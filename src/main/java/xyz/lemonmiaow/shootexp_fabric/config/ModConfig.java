package xyz.lemonmiaow.shootexp_fabric.config;

import xyz.lemonmiaow.shootexp_fabric.ShootexpFabric;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ModConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_DIR = FabricLoader.getInstance().getConfigDir().resolve(ShootexpFabric.MOD_ID);
    private static final Path CONFIG_FILE = CONFIG_DIR.resolve("config.json");

    private static ConfigData config = new ConfigData();

    public static void load() {
        try {
            Files.createDirectories(CONFIG_DIR);

            if (Files.exists(CONFIG_FILE)) {
                try (Reader reader = Files.newBufferedReader(CONFIG_FILE, StandardCharsets.UTF_8)) {
                    config = GSON.fromJson(reader, ConfigData.class);
                    if (config == null) {
                        config = new ConfigData();
                    }
                }
            } else {
                save();
            }
        } catch (IOException e) {
            ShootexpFabric.LOGGER.error("Failed to load config", e);
            config = new ConfigData();
        }
    }

    public static void save() {
        try {
            Files.createDirectories(CONFIG_DIR);
            try (Writer writer = Files.newBufferedWriter(CONFIG_FILE, StandardCharsets.UTF_8)) {
                GSON.toJson(config, writer);
            }
        } catch (IOException e) {
            ShootexpFabric.LOGGER.error("Failed to save config", e);
        }
    }

    public static void reload() {
        load();
        Language.load();
    }

    public static String getLang() { return config.lang; }
    public static boolean isPrivateMessage() { return config.privateMessage; }
    public static int getMaxStock() { return config.maxStock; }
    public static String getRequiredAttackTimes() { return config.requiredAttackTimes; }
    public static String getShootAmount() { return config.shootAmount; }
    public static List<String> getEntityTypes() { return config.entityTypes; }
    public static double getAttackDistance() { return config.attackDistance; }
    public static int getAttackTimeout() { return config.attackTimeout; }
    public static int getRestoreShootPeriod() { return config.restoreShootPeriod; }
    public static int getRestoreShootAmount() { return config.restoreShootAmount; }
    public static int getRestoreStockPeriod() { return config.restoreStockPeriod; }
    public static int getRestoreStockAmount() { return config.restoreStockAmount; }
    public static boolean isCustomModelDataEnabled() { return config.customModelDataEnable; }
    public static int getCustomModelDataValue() { return config.customModelDataValue; }
    public static String getSoundAttack() { return config.soundAttack; }
    public static String getSoundShoot() { return config.soundShoot; }
    public static String getSoundShootNoExp() { return config.soundShootNoExp; }
    public static String getSoundEat() { return config.soundEat; }

    public static class ConfigData {
        public String lang = "zh_CN";
        public boolean privateMessage = false;
        public int maxStock = 1000;
        public String requiredAttackTimes = "1.618^SHOOT + 10";
        public String shootAmount = "STOCK / 2";
        public List<String> entityTypes = List.of("Player", "LivingEntity");
        public double attackDistance = 2.0;
        public int attackTimeout = 100;
        public int restoreShootPeriod = 6000;
        public int restoreShootAmount = 1;
        public int restoreStockPeriod = 6000;
        public int restoreStockAmount = 200;
        public boolean customModelDataEnable = false;
        public int customModelDataValue = 0;
        public String soundAttack = "minecraft:entity.parrot.imitate.slime";
        public String soundShoot = "minecraft:block.slime_block.step";
        public String soundShootNoExp = "minecraft:entity.llama.eat";
        public String soundEat = "minecraft:entity.generic.drink";
    }
}
