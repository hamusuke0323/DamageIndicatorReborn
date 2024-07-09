package com.hamusuke.damageindicatorreborn.mixin;

import com.hamusuke.damageindicatorreborn.invoker.EntityInvoker;
import com.hamusuke.damageindicatorreborn.invoker.LivingEntityInvoker;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(PlayerEntity.class)
public abstract class PlayerMixin extends LivingEntityMixin implements LivingEntityInvoker {
    @ModifyVariable(method = "attack", at = @At("STORE"), ordinal = 2)
    private boolean attack$bl3(boolean bl3, Entity entity) {
        if (bl3 && entity instanceof EntityInvoker invoker) {
            invoker.setCrit(true);
        }

        return bl3;
    }
}
