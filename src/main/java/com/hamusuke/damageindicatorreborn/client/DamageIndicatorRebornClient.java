package com.hamusuke.damageindicatorreborn.client;

import com.google.common.collect.Lists;
import com.hamusuke.damageindicatorreborn.client.config.Config;
import com.hamusuke.damageindicatorreborn.client.indicator.renderer.IndicatorRenderer;
import com.hamusuke.damageindicatorreborn.math.MthH;
import com.hamusuke.damageindicatorreborn.network.Network;
import com.hamusuke.damageindicatorreborn.network.packet.serverbound.SYNReq;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent.LoggingIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent.LoggingOut;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.hamusuke.damageindicatorreborn.DamageIndicatorReborn.MOD_ID;

@OnlyIn(Dist.CLIENT)
public final class DamageIndicatorRebornClient {
    private static DamageIndicatorRebornClient INSTANCE;
    private static final Minecraft mc = Minecraft.getInstance();
    public static final AtomicBoolean SERVER_SIDED = new AtomicBoolean();
    private static final List<IndicatorRenderer> renderers = Collections.synchronizedList(Lists.newArrayList());
    private static final KeyMapping hideIndicator = new KeyMapping("key." + MOD_ID + ".hideIndicator.desc", -1, "key." + MOD_ID + ".category.indicator");
    private static final float MAX_DAMAGE = 9999999.0F;

    private DamageIndicatorRebornClient() {
        INSTANCE = this;
        FMLJavaModLoadingContext.get().getModEventBus().register(DamageIndicatorRebornClient.class);
    }

    @SubscribeEvent
    public void onClientPlayerLoggingOut(final LoggingOut e) {
        renderers.clear();
    }

    @SubscribeEvent
    public void onClientPlayerLoggingIn(final LoggingIn e) {
        SERVER_SIDED.set(false);
        Network.sendToServer(new SYNReq());
    }

    @SubscribeEvent
    public static void onConfigReload(final ModConfigEvent event) {
        if (event.getConfig().getModId().equals(MOD_ID)) {
            renderers.forEach(IndicatorRenderer::syncIndicatorColor);
        }

        event.setResult(Result.ALLOW);
    }

    @SubscribeEvent
    public static void onRegisterKeyMappings(final RegisterKeyMappingsEvent event) {
        event.register(hideIndicator);
    }

    @SubscribeEvent
    public void onEndTick(ClientTickEvent event) {
        if (!mc.isPaused() && event.phase == Phase.END) {
            if (hideIndicator.consumeClick()) {
                Config.CLIENT.hideIndicator.set(!Config.CLIENT.hideIndicator.get());
            }

            renderers.forEach(IndicatorRenderer::tick);
            renderers.removeIf(IndicatorRenderer::isDead);
        }
    }

    @SubscribeEvent
    public void onRenderLevelStage(final RenderLevelStageEvent event) {
        if (!Config.CLIENT.hideIndicator.get() && event.getStage() == RenderLevelStageEvent.Stage.AFTER_WEATHER && !renderers.isEmpty()) {
            mc.getProfiler().push("damage indicator rendering");
            var bufferSource = mc.renderBuffers().bufferSource();
            renderers.forEach(indicatorRenderer -> indicatorRenderer.render(bufferSource, mc.getEntityRenderDispatcher().camera, event.getPartialTick()));
            bufferSource.endBatch();
            mc.getProfiler().pop();
        }
    }

    public void addRenderer(int entityId, float value, String source, boolean crit) {
        if (mc.level != null && mc.player != null && mc.getEntityRenderDispatcher().camera != null) {
            var clientEntity = mc.level.getEntity(entityId);

            if (clientEntity instanceof LivingEntity livingEntity) {
                double x = clientEntity.getRandomX(0.5D);
                double y = clientEntity.getY() + Mth.nextDouble(livingEntity.getRandom(), 0.0D, 1.0D);
                double z = clientEntity.getRandomZ(0.5D);
                var vec3 = new Vec3(x, y, z);
                float distance = (float) mc.getEntityRenderDispatcher().camera.getPosition().distanceTo(vec3);
                var result = mc.level.clip(new ClipContext(mc.player.position(), vec3, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null));
                if ((Config.CLIENT.forceIndicatorRendering.get() || result.getType() == HitResult.Type.MISS) && distance <= (float) Config.CLIENT.renderDistance.get()) {
                    value = Math.min(value, MAX_DAMAGE);
                    renderers.add(new IndicatorRenderer(x, y, z, (source.equalsIgnoreCase("heal") ? "+" : "") + (Config.CLIENT.showFloatingPoint.get() ? String.format("%.1f", value) : String.format("%d", MthH.toInt(value))), source, crit, distance));
                }
            }
        }
    }

    public static DamageIndicatorRebornClient getInstance() {
        if (INSTANCE == null) {
            new DamageIndicatorRebornClient();
        }

        return INSTANCE;
    }
}
