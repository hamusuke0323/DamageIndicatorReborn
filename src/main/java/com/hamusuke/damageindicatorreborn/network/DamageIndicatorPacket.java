package com.hamusuke.damageindicatorreborn.network;

import net.minecraft.network.PacketByteBuf;

public record DamageIndicatorPacket(int entityId, float value, String source, boolean crit, boolean isSourcePlayer) {
    public DamageIndicatorPacket(PacketByteBuf buf) {
        this(buf.readVarInt(), buf.readFloat(), buf.readString(), buf.readBoolean(), buf.readBoolean());
    }

    public PacketByteBuf write(PacketByteBuf packetByteBuf) {
        packetByteBuf.writeVarInt(this.entityId);
        packetByteBuf.writeFloat(this.value);
        packetByteBuf.writeString(this.source);
        packetByteBuf.writeBoolean(this.crit);
        packetByteBuf.writeBoolean(this.isSourcePlayer);
        return packetByteBuf;
    }
}
