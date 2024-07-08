package com.hamusuke.damageindicatorreborn.network;

import com.hamusuke.damageindicatorreborn.network.packet.Packet;
import com.hamusuke.damageindicatorreborn.network.packet.clientbound.ACKRsp;
import com.hamusuke.damageindicatorreborn.network.packet.clientbound.IndicatorNotify;
import com.hamusuke.damageindicatorreborn.network.packet.serverbound.SYNReq;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.SimpleChannel;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static com.hamusuke.damageindicatorreborn.DamageIndicatorReborn.MOD_ID;

public class Network {
    private static final int PROTOCOL_VERSION = 1;
    private static final SimpleChannel MAIN = ChannelBuilder
            .named(new ResourceLocation(MOD_ID, "main"))
            .networkProtocolVersion(PROTOCOL_VERSION)
            .serverAcceptedVersions((status, i) -> true)
            .clientAcceptedVersions((status, i) -> true)
            .simpleChannel();
    private static final AtomicInteger COUNTER = new AtomicInteger(0);
    private static final Supplier<Integer> ID = COUNTER::getAndIncrement;

    public static void registerPackets() {
        registerServerboundPackets();
        registerClientboundPackets();
    }

    private static void registerServerboundPackets() {
        MAIN.messageBuilder(SYNReq.class, ID.get(), NetworkDirection.PLAY_TO_SERVER)
                .encoder(Packet::write)
                .decoder(SYNReq::new)
                .consumerNetworkThread(Packet::handle)
                .add();
    }

    private static void registerClientboundPackets() {
        MAIN.messageBuilder(ACKRsp.class, ID.get(), NetworkDirection.PLAY_TO_CLIENT)
                .encoder(Packet::write)
                .decoder(ACKRsp::new)
                .consumerNetworkThread(Packet::handle)
                .add();
        MAIN.messageBuilder(IndicatorNotify.class, ID.get(), NetworkDirection.PLAY_TO_CLIENT)
                .encoder(Packet::write)
                .decoder(IndicatorNotify::new)
                .consumerNetworkThread(Packet::handle)
                .add();
    }

    public static void sendToServer(Packet packet) {
        if (Minecraft.getInstance().getConnection() == null) {
            return;
        }

        MAIN.send(packet, Minecraft.getInstance().getConnection().getConnection());
    }

    public static void sendToClient(ServerPlayer player, Packet packet) {
        MAIN.send(packet, player.connection.getConnection());
    }
}
