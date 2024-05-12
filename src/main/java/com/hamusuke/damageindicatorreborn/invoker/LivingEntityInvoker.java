package com.hamusuke.damageindicatorreborn.invoker;

import net.minecraft.network.syncher.EntityDataAccessor;

public interface LivingEntityInvoker extends EntityInvoker {
    EntityDataAccessor<Float> getDataHealth();
}
