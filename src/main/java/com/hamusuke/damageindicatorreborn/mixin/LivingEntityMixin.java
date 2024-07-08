package com.hamusuke.damageindicatorreborn.mixin;

import com.hamusuke.damageindicatorreborn.client.DamageIndicatorRebornClient;
import com.hamusuke.damageindicatorreborn.invoker.LivingEntityInvoker;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends EntityMixin implements LivingEntityInvoker {
    @Shadow
    @Final
    private static EntityDataAccessor<Float> DATA_HEALTH_ID;

    @Shadow
    public abstract float getHealth();

    @Override
    public EntityDataAccessor<Float> getDataHealth() {
        return DATA_HEALTH_ID;
    }

    @Inject(method = "setHealth", at = @At("HEAD"))
    private void setHealth(float p_21154_, CallbackInfo ci) {
        if (this.level().isClientSide && !DamageIndicatorRebornClient.SERVER_SIDED.get() && !this.isFirstTick()) {
            float value = this.getHealth() - p_21154_;
            if (value <= 0.0F) {
                return;
            }

            DamageIndicatorRebornClient.getInstance().addRenderer(this.getId(), value, "generic", false);
        }
    }
}
