package com.hamusuke.damageindicatorreborn.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent.Context;

public interface Packet {
    default void write(FriendlyByteBuf buf) {
    }

    default void handle(Context context) {
        context.setPacketHandled(this.handleInEnqueued(context));
    }

    boolean handleInEnqueued(Context ctx);
}
