package com.hamusuke.damageindicatorreborn.mixin.client;

import com.hamusuke.damageindicatorreborn.DamageIndicatorRebornClient;
import com.hamusuke.damageindicatorreborn.invoker.LivingEntityInvoker;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.DataTracker.Entry;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(DataTracker.class)
public abstract class DataTrackerMixin {
    @Shadow
    @Final
    private Entity trackedEntity;

    @Inject(method = "copyToFrom", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/data/DataTracker$Entry;set(Ljava/lang/Object;)V", shift = Shift.BEFORE))
    private <T> void assignValue(Entry<T> _to, Entry<?> from, CallbackInfo ci) {
        if (from.get() instanceof Float f && this.trackedEntity instanceof LivingEntity living && this.trackedEntity.world.isClient && this.trackedEntity instanceof LivingEntityInvoker invoker && invoker.getDataHealth().getId() == from.getData().getId() && !DamageIndicatorRebornClient.SERVER_SIDED.get()) {
            if (invoker.isFirstTick() || living.isDead()) {
                return;
            }

            float value = living.getHealth() - f;
            if (value == 0.0F) {
                return;
            }

            boolean heal = value < 0.0F;
            DamageIndicatorRebornClient.addRenderer(this.trackedEntity.getId(), MathHelper.abs(value), heal ? "heal" : "generic", false);
        }
    }
}
