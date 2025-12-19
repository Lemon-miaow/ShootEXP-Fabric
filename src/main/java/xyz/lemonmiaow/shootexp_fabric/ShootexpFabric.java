package xyz.lemonmiaow.shootexp_fabric;

import xyz.lemonmiaow.shootexp_fabric.config.ModConfig;
import xyz.lemonmiaow.shootexp_fabric.config.Language;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShootexpFabric implements ModInitializer {
    public static final String MOD_ID = "shootexp_fabric";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("ShootEXP Fabric initializing...");

        ModConfig.load();
        Language.load();

        ServerTickEvents.END_SERVER_TICK.register(SneakDetector::onServerTick);

        UseBlockCallback.EVENT.register(ExpItemHandler::onUseBlock);

        UseItemCallback.EVENT.register(ExpItemHandler::onUseItem);

        CommandRegistrationCallback.EVENT.register(ModCommands::register);

        LOGGER.info("ShootEXP Fabric initialized!");
    }
}
