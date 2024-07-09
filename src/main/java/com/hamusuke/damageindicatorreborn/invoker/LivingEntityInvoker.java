package com.hamusuke.damageindicatorreborn.invoker;

import net.minecraft.entity.data.TrackedData;

public interface LivingEntityInvoker extends EntityInvoker {
    TrackedData<Float> getDataHealth();
}
