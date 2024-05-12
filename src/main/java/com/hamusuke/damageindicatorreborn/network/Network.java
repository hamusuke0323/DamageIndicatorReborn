package com.hamusuke.damageindicatorreborn.network;

import com.hamusuke.damageindicatorreborn.network.packet.Packet;
import com.hamusuke.damageindicatorreborn.network.packet.clientbound.ACKRsp;
import com.hamusuke.damageindicatorreborn.network.packet.clientbound.IndicatorNotify;
import com.hamusuke.damageindicatorreborn.network.packet.serverbound.SYNReq;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry.ChannelBuilder;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static com.hamusuke.damageindicatorreborn.DamageIndicatorReborn.MOD_ID;

public class Network {
    private static final String PROTOCOL_VERSION = "1";
    private static final SimpleChannel MAIN = ChannelBuilder
            .named(new ResourceLocation(MOD_ID, "main"))
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .simpleChannel();
    private static final AtomicInteger COUNTER = new AtomicInteger(0);
    private static final Supplier<Integer> ID = COUNTER::getAndIncrement;

    public static void registerPackets() {
        registerServerboundPackets();
        registerClientboundPackets();
    }

    private static void registerServerboundPackets() {
        MAIN.registerMessage(ID.get(), SYNReq.class, Packet::write, SYNReq::new, Packet::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
    }

    private static void registerClientboundPackets() {
        MAIN.registerMessage(ID.get(), ACKRsp.class, Packet::write, ACKRsp::new, Packet::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        MAIN.registerMessage(ID.get(), IndicatorNotify.class, Packet::write, IndicatorNotify::new, Packet::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    public static void sendToServer(Packet packet) {
        MAIN.sendToServer(packet);
    }

    public static void sendToClient(ServerPlayer player, Packet packet) {
        MAIN.sendTo(packet, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }
}
