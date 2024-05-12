package com.hamusuke.damageindicatorreborn.network.packet.clientbound;

import com.hamusuke.damageindicatorreborn.client.DamageIndicatorRebornClient;
import com.hamusuke.damageindicatorreborn.network.packet.Packet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent.Context;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public record ACKRsp() implements Packet {
    private static final Logger LOGGER = LogManager.getLogger();

    public ACKRsp(FriendlyByteBuf buf) {
        this();
    }

    @Override
    public boolean handleInEnqueued(Context ctx) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            if (DamageIndicatorRebornClient.SERVER_SIDED.get()) {
                return;
            }

            DamageIndicatorRebornClient.SERVER_SIDED.set(true);
            LOGGER.info("This mod is installed on the server! You may retrieve accurate damage value!");
        });
        return true;
    }
}
