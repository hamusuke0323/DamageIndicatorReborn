package com.hamusuke.damageindicatorreborn.invoker;

public interface EntityInvoker {
    void sendDamageValue(float value, String source, boolean crit);

    void setCrit(boolean crit);

    boolean isCrit();

    boolean isFirstTick();
}
