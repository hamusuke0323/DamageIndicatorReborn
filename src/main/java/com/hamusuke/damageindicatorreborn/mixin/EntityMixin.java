package com.hamusuke.damageindicatorreborn.mixin;

import com.hamusuke.damageindicatorreborn.invoker.EntityInvoker;
import com.hamusuke.damageindicatorreborn.network.DamageIndicatorPacket;
import com.hamusuke.damageindicatorreborn.network.NetworkManager;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityInvoker {
    @Shadow
    public World world;

    @Shadow
    public abstract int getId();

    @Shadow
    protected boolean firstUpdate;
    @Shadow
    public int age;
    @Unique
    protected boolean crit;

    @Override
    public void sendDamageValue(float value, String source, boolean crit, boolean isSourcePlayer) {
        if (this.world instanceof ServerWorld serverWorld) {
            var packet = new DamageIndicatorPacket(this.getId(), value, source, crit, isSourcePlayer);
            serverWorld.getPlayers().forEach(player -> player.networkHandler.sendPacket(new CustomPayloadS2CPacket(NetworkManager.DAMAGE_PACKET_ID, packet.write(PacketByteBufs.create()))));
        }
    }

    @Override
    public void setCrit(boolean crit) {
        this.crit = crit;
    }

    @Override
    public boolean isCrit() {
        return this.crit;
    }

    @Override
    public boolean isFirstTick() {
        return this.firstUpdate || this.age < 10;
    }
}
