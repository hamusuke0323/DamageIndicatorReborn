package com.hamusuke.damageindicatorreborn;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.hamusuke.damageindicatorreborn.command.DamageIndicatorRebornCommand;
import com.hamusuke.damageindicatorreborn.config.ClientConfig;
import com.hamusuke.damageindicatorreborn.gui.screen.ConfigScreen;
import com.hamusuke.damageindicatorreborn.math.MthH;
import com.hamusuke.damageindicatorreborn.network.DamageIndicatorPacket;
import com.hamusuke.damageindicatorreborn.network.NetworkManager;
import com.hamusuke.damageindicatorreborn.renderer.IndicatorRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.hamusuke.damageindicatorreborn.DamageIndicatorReborn.MOD_ID;

@Environment(EnvType.CLIENT)
public class DamageIndicatorRebornClient implements ClientModInitializer {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final Queue<IndicatorRenderer> queue = Queues.newLinkedBlockingDeque();
    public static final ClientConfig clientConfig = new ClientConfig(FabricLoader.getInstance().getConfigDir().resolve(MOD_ID + "/config.json").toFile());
    private static final KeyBinding OPEN_CONFIG = KeyBindingHelper.registerKeyBinding(new KeyBinding("key." + MOD_ID + ".openConfig", GLFW.GLFW_KEY_V, "key." + MOD_ID + ".category.indicator"));
    private static final KeyBinding HIDE_INDICATOR = KeyBindingHelper.registerKeyBinding(new KeyBinding("key." + MOD_ID + ".hideIndicator.desc", GLFW.GLFW_KEY_B, "key." + MOD_ID + ".category.indicator"));
    private static final Text HOW_TO_OPEN_CONFIG_SCREEN = Text.translatable(MOD_ID + ".howToOpenConfigScreen");
    public static final AtomicBoolean SERVER_SIDED = new AtomicBoolean();
    private static final float MAX_DAMAGE = 9999999.0F;

    @Override
    public void onInitializeClient() {
        clientConfig.load();

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> DamageIndicatorRebornCommand.register(dispatcher));

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (!clientConfig.disableMessageWhenJoined.get()) {
                client.player.sendMessage(HOW_TO_OPEN_CONFIG_SCREEN);
            }

            SERVER_SIDED.set(false);
            handler.sendPacket(new CustomPayloadC2SPacket(NetworkManager.SYN_PACKET_ID, PacketByteBufs.empty()));
        });

        ClientPlayNetworking.registerGlobalReceiver(NetworkManager.ACK_PACKET_ID, (client, handler, buf, responseSender) -> {
            if (SERVER_SIDED.get()) {
                return;
            }

            SERVER_SIDED.set(true);
            LOGGER.info("This mod is installed on the server! You may retrieve accurate damage value!");
        });

        ClientPlayNetworking.registerGlobalReceiver(NetworkManager.DAMAGE_PACKET_ID, (client, handler, buf, responseSender) -> {
            var packet = new DamageIndicatorPacket(buf);
            if (!packet.isSourcePlayer() && clientConfig.onlyRenderDamagePlayersAttacked.get()) {
                return;
            }

            client.send(() -> addRenderer(packet.entityId(), packet.value(), packet.source(), packet.crit()));
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!client.isPaused()) {
                List<IndicatorRenderer> list = Lists.newArrayList();
                queue.forEach(indicatorRenderer -> {
                    indicatorRenderer.tick();
                    if (!indicatorRenderer.isAlive()) {
                        list.add(indicatorRenderer);
                    }
                });

                queue.removeAll(list);
            }

            while (OPEN_CONFIG.wasPressed()) {
                client.setScreen(new ConfigScreen(client.currentScreen));
            }

            while (HIDE_INDICATOR.wasPressed()) {
                clientConfig.hideIndicator.toggle();
                clientConfig.save();
            }
        });
    }

    public static void addRenderer(int entityId, float value, String source, boolean crit) {
        var mc = MinecraftClient.getInstance();
        if (mc.world != null && mc.player != null && mc.getEntityRenderDispatcher().camera != null) {
            var clientEntity = mc.world.getEntityById(entityId);

            if (clientEntity instanceof LivingEntity livingEntity) {
                double x = clientEntity.getParticleX(0.5D);
                double y = clientEntity.getY() + MathHelper.nextDouble(livingEntity.getRandom(), 0.0D, 1.0D);
                double z = clientEntity.getParticleZ(0.5D);
                var vec3 = new Vec3d(x, y, z);
                float distance = (float) mc.getEntityRenderDispatcher().camera.getPos().distanceTo(vec3);
                var result = mc.world.raycast(new RaycastContext(mc.player.getPos(), vec3, ShapeType.COLLIDER, FluidHandling.NONE, mc.player));
                if ((clientConfig.forciblyRenderIndicator.get() || result.getType() == HitResult.Type.MISS) && distance <= (float) clientConfig.renderDistance.get()) {
                    value = Math.min(value, MAX_DAMAGE);
                    queue.add(new IndicatorRenderer(x, y, z, (source.equalsIgnoreCase("heal") ? "+" : "") + (clientConfig.showFloatingPoint.get() ? String.format("%.1f", value) : String.format("%d", MthH.toInt(value))), source, crit, distance));
                }
            }
        }
    }
}
