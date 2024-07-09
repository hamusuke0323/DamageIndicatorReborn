package com.hamusuke.damageindicatorreborn.command;

import com.hamusuke.damageindicatorreborn.gui.screen.ConfigScreen;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

@Environment(EnvType.CLIENT)
public class DamageIndicatorRebornCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
                literal("damageindicatorreborn")
                        .then(literal("config")
                                .executes(DamageIndicatorRebornCommand::openConfig))
        );
    }

    private static int openConfig(CommandContext<FabricClientCommandSource> context) {
        var client = context.getSource().getClient();
        if (client.currentScreen instanceof ConfigScreen) { // should never happen.
            return 0;
        }

        client.send(() -> client.setScreen(new ConfigScreen(client.currentScreen)));
        return 1;
    }
}
