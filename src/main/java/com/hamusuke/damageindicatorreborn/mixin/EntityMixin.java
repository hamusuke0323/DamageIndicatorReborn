package com.hamusuke.damageindicatorreborn.mixin;

import com.hamusuke.damageindicatorreborn.invoker.EntityInvoker;
import com.hamusuke.damageindicatorreborn.network.Network;
import com.hamusuke.damageindicatorreborn.network.packet.clientbound.IndicatorNotify;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityInvoker {
    @Shadow
    private Level level;

    @Shadow
    public abstract int getId();

    @Shadow
    protected boolean firstTick;

    @Shadow
    public abstract Level level();

    @Shadow
    public int tickCount;
    @Unique
    protected boolean crit;

    @Override
    public void sendDamageValue(float value, String source, boolean crit) {
        if (this.level instanceof ServerLevel serverLevel) {
            var packet = new IndicatorNotify(this.getId(), value, source, crit);
            serverLevel.players().forEach(player -> Network.sendToClient(player, packet));
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
        return this.firstTick || this.tickCount < 10;
    }
}
