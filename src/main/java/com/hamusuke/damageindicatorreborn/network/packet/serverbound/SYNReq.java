package com.hamusuke.damageindicatorreborn.network.packet.serverbound;

import com.hamusuke.damageindicatorreborn.network.Network;
import com.hamusuke.damageindicatorreborn.network.packet.Packet;
import com.hamusuke.damageindicatorreborn.network.packet.clientbound.ACKRsp;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public record SYNReq() implements Packet {
    public SYNReq(FriendlyByteBuf buf) {
        this();
    }

    @Override
    public boolean handleInEnqueued(Context ctx) {
        ctx.enqueueWork(() -> {
            var sender = ctx.getSender();
            if (sender == null) {
                return;
            }

            Network.sendToClient(sender, new ACKRsp());
        });

        return true;
    }
}
