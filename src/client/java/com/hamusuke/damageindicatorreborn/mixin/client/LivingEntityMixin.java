package com.hamusuke.damageindicatorreborn.mixin.client;

import com.hamusuke.damageindicatorreborn.DamageIndicatorRebornClient;
import com.hamusuke.damageindicatorreborn.mixin.EntityMixin;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends EntityMixin {
    @Shadow
    public abstract float getHealth();

    @Inject(method = "setHealth", at = @At("HEAD"))
    private void setHealth(float p_21154_, CallbackInfo ci) {
        if (this.world.isClient && !DamageIndicatorRebornClient.SERVER_SIDED.get() && !this.isFirstTick()) {
            float value = this.getHealth() - p_21154_;
            if (value <= 0.0F) {
                return;
            }

            DamageIndicatorRebornClient.addRenderer(this.getId(), value, "generic", false);
        }
    }
}
