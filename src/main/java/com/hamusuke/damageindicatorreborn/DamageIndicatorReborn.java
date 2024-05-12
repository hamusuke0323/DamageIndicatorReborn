package com.hamusuke.damageindicatorreborn;

import com.hamusuke.damageindicatorreborn.client.DamageIndicatorRebornClient;
import com.hamusuke.damageindicatorreborn.client.config.Config;
import com.hamusuke.damageindicatorreborn.client.gui.screen.ConfigScreen;
import com.hamusuke.damageindicatorreborn.invoker.EntityInvoker;
import com.hamusuke.damageindicatorreborn.network.Network;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import static com.hamusuke.damageindicatorreborn.DamageIndicatorReborn.MOD_ID;

@Mod(MOD_ID)
public final class DamageIndicatorReborn {
    public static final String MOD_ID = "damageindicatorreborn";

    public DamageIndicatorReborn() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CONFIG);
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory(ConfigScreen::new));
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> MinecraftForge.EVENT_BUS.register(DamageIndicatorRebornClient.getInstance()));
        FMLJavaModLoadingContext.get().getModEventBus().addListener((final FMLCommonSetupEvent event) -> Network.registerPackets());
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onHeal(final LivingHealEvent event) {
        LivingEntity livingEntity = event.getEntity();
        if (!event.isCanceled() && livingEntity instanceof EntityInvoker invoker) {
            float amount = Math.min(livingEntity.getMaxHealth() - livingEntity.getHealth(), event.getAmount());
            if (!livingEntity.level.isClientSide && amount > 0.0F) {
                invoker.sendDamageValue(amount, "heal", false);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onCrit(final CriticalHitEvent e) {
        if (e.getTarget() instanceof EntityInvoker invoker) {
            invoker.setCrit(e.isVanillaCritical());
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onDamageLast(final LivingDamageEvent event) {
        var livingEntity = event.getEntity();
        if (!livingEntity.level.isClientSide && livingEntity instanceof EntityInvoker invoker) {
            var source = event.getSource();
            boolean crit = invoker.isCrit();
            if (source.getDirectEntity() instanceof AbstractArrow arrow) {
                crit = arrow.isCritArrow();
            }

            invoker.setCrit(false);
            invoker.sendDamageValue(event.getAmount(), source.getMsgId(), crit);
        }
    }
}
