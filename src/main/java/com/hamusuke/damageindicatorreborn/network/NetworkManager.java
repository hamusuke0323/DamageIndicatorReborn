package com.hamusuke.damageindicatorreborn.network;

import com.hamusuke.damageindicatorreborn.DamageIndicatorReborn;
import net.minecraft.util.Identifier;

public class NetworkManager {
    public static final Identifier SYN_PACKET_ID = new Identifier(DamageIndicatorReborn.MOD_ID, "syn");
    public static final Identifier ACK_PACKET_ID = new Identifier(DamageIndicatorReborn.MOD_ID, "ack");
    public static final Identifier DAMAGE_PACKET_ID = new Identifier(DamageIndicatorReborn.MOD_ID, "damage_packet");
}
