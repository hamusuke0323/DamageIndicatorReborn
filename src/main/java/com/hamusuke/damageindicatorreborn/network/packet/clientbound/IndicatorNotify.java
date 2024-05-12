package com.hamusuke.damageindicatorreborn.network.packet.clientbound;

import com.hamusuke.damageindicatorreborn.client.DamageIndicatorRebornClient;
import com.hamusuke.damageindicatorreborn.network.packet.Packet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent.Context;

public record IndicatorNotify(int entityId, float value, String source, boolean crit) implements Packet {
    public IndicatorNotify(FriendlyByteBuf buf) {
        this(buf.readVarInt(), buf.readFloat(), buf.readUtf(), buf.readBoolean());
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(this.entityId);
        buf.writeFloat(this.value);
        buf.writeUtf(this.source);
        buf.writeBoolean(this.crit);
    }

    @Override
    public boolean handleInEnqueued(Context ctx) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            if (!DamageIndicatorRebornClient.SERVER_SIDED.get()) {
                DamageIndicatorRebornClient.SERVER_SIDED.set(true);
            }

            DamageIndicatorRebornClient.getInstance().addRenderer(this.entityId, this.value, this.source, this.crit);
        });
        return true;
    }
}
