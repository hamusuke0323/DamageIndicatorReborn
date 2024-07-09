package com.hamusuke.damageindicatorreborn.mixin;

import com.hamusuke.damageindicatorreborn.invoker.EntityInvoker;
import com.hamusuke.damageindicatorreborn.invoker.LivingEntityInvoker;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.TrackedData;
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
    private static TrackedData<Float> HEALTH;

    @Shadow
    public abstract float getMaxHealth();

    @Shadow
    public abstract float getHealth();

    @Shadow
    public abstract boolean isDead();

    @Override
    public TrackedData<Float> getDataHealth() {
        return HEALTH;
    }

    @Inject(method = "heal", at = @At("HEAD"))
    private void heal(float amount, CallbackInfo ci) {
        if (!this.isDead() && (LivingEntity) (Object) this instanceof EntityInvoker invoker) {
            amount = Math.min(this.getMaxHealth() - this.getHealth(), amount);
            if (!this.world.isClient && amount > 0.0F) {
                invoker.sendDamageValue(amount, "heal", false, true);
            }
        }
    }
}
