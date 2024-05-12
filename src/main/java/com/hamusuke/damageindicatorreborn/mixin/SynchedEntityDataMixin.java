package com.hamusuke.damageindicatorreborn.mixin;

import com.hamusuke.damageindicatorreborn.client.DamageIndicatorRebornClient;
import com.hamusuke.damageindicatorreborn.invoker.LivingEntityInvoker;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.DataItem;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SynchedEntityData.class)
public abstract class SynchedEntityDataMixin {
    @Shadow
    @Final
    private Entity entity;

    @Inject(method = "assignValue", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/syncher/SynchedEntityData$DataItem;setValue(Ljava/lang/Object;)V", shift = Shift.BEFORE))
    private <T> void assignValue(DataItem<T> p_135376_, DataItem<?> p_135377_, CallbackInfo ci) {
        if (p_135377_.getValue() instanceof Float f && this.entity instanceof LivingEntity living && this.entity.level.isClientSide && this.entity instanceof LivingEntityInvoker invoker && invoker.getDataHealth().getId() == p_135377_.getAccessor().getId() && !DamageIndicatorRebornClient.SERVER_SIDED.get()) {
            if (invoker.isFirstTick() || living.isDeadOrDying()) {
                return;
            }

            float value = living.getHealth() - f;
            if (value == 0.0F) {
                return;
            }

            boolean heal = value < 0.0F;
            DamageIndicatorRebornClient.getInstance().addRenderer(this.entity.getId(), Mth.abs(value), heal ? "heal" : "generic", false);
        }
    }
}
