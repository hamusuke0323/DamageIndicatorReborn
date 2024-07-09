package com.hamusuke.damageindicatorreborn;

import com.hamusuke.damageindicatorreborn.invoker.EntityInvoker;
import com.hamusuke.damageindicatorreborn.network.NetworkManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;

public class DamageIndicatorReborn implements ModInitializer {
    public static final String MOD_ID = "damageindicatorreborn";

    @Override
    public void onInitialize() {
        ServerPlayNetworking.registerGlobalReceiver(NetworkManager.SYN_PACKET_ID, (server, player, handler, buf, responseSender) -> {
            player.networkHandler.sendPacket(new CustomPayloadS2CPacket(NetworkManager.ACK_PACKET_ID, PacketByteBufs.empty()));
        });

        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
            if (!entity.world.isClient && entity instanceof EntityInvoker invoker) {
                boolean crit = invoker.isCrit();
                if (source.getSource() instanceof PersistentProjectileEntity arrow) {
                    crit = arrow.isCritical();
                }

                invoker.setCrit(false);
                invoker.sendDamageValue(amount, source.getName(), crit, source.getAttacker() instanceof PlayerEntity);
            }

            return true;
        });
    }
}
