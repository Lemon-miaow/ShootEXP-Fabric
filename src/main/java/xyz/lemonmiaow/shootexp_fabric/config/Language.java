package xyz.lemonmiaow.shootexp_fabric.config;

import xyz.lemonmiaow.shootexp_fabric.ShootexpFabric;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Language {
    private static final Gson GSON = new Gson();
    private static final String LANG_PATH = "/assets/shootexp_fabric/lang/";

    private static Map<String, Object> messages = new HashMap<>();

    public static void load() {
        String lang = ModConfig.getLang().toLowerCase();
        if (!loadFromResource(lang)) {
            if (!loadFromResource("en_us")) {
                ShootexpFabric.LOGGER.error("Failed to load any language file");
                messages = new HashMap<>();
            }
        }
    }

    private static boolean loadFromResource(String lang) {
        String path = LANG_PATH + lang + ".json";
        try (InputStream is = Language.class.getResourceAsStream(path)) {
            if (is == null) {
                return false;
            }
            try (Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                Type type = new TypeToken<Map<String, Object>>(){}.getType();
                messages = GSON.fromJson(reader, type);
                if (messages == null) {
                    messages = new HashMap<>();
                }
                return true;
            }
        } catch (IOException e) {
            ShootexpFabric.LOGGER.error("Failed to load language file: " + lang, e);
            return false;
        }
    }

    public static String getString(String key) {
        Object value = messages.get(key);
        if (value instanceof String) {
            return (String) value;
        }
        return key;
    }

    @SuppressWarnings("unchecked")
    public static List<String> getStringList(String key) {
        Object value = messages.get(key);
        if (value instanceof List) {
            return (List<String>) value;
        }
        return List.of();
    }
}
