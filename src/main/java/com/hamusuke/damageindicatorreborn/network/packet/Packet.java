package com.hamusuke.damageindicatorreborn.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

import java.util.function.Supplier;

public interface Packet {
    default void write(FriendlyByteBuf buf) {
    }

    default void handle(Supplier<Context> context) {
        context.get().setPacketHandled(this.handleInEnqueued(context.get()));
    }

    boolean handleInEnqueued(Context ctx);
}
