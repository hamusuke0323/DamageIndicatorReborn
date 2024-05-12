package com.hamusuke.damageindicatorreborn.invoker;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.LivingEntity;

public interface LivingEntityInvoker extends EntityInvoker {
    EntityDataAccessor<Float> getDataHealth();

    static LivingEntityInvoker from(LivingEntity entity) {
        return (LivingEntityInvoker) entity;
    }
}
