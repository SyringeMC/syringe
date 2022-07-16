package dev.uten2c.syringe.io;

import com.google.gson.Gson;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class SaveDataManager {
    private static final String FILE_NAME = "syringe.json";
    private static final Gson GSON = new Gson();
    private static final Path CONFIG_DIR = FabricLoader.getInstance().getConfigDir();
    private static final Path SAVE_FILE_PATH = createFileIfNotExists(CONFIG_DIR.resolve(FILE_NAME));
    private static Map<String, Object> dataMap = new HashMap<>();

    static {
        try {
            var string = Files.readString(SAVE_FILE_PATH);
            if (string.isEmpty()) {
                Files.writeString(SAVE_FILE_PATH, "{}");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private SaveDataManager() {
    }

    public static void setString(@NotNull String key, @NotNull String value) {
        dataMap.put(key, value);
    }

    public static void setInt(@NotNull String key, int value) {
        dataMap.put(key, value);
    }

    public static void remove(@NotNull String key) {
        dataMap.remove(key);
    }

    public static Optional<String> getString(@NotNull String key) {
        try {
            var value = dataMap.get(key);
            return value == null ? Optional.empty() : Optional.of((String) value);
        } catch (ClassCastException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public static Optional<Integer> getInt(@NotNull String key) {
        try {
            var value = dataMap.get(key);
            return value == null ? Optional.empty() : Optional.of((int) (double) value);
        } catch (ClassCastException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public static void save() {
        var json = GSON.toJson(dataMap);
        try {
            Files.writeString(SAVE_FILE_PATH, json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void load() {
        try {
            var string = Files.readString(SAVE_FILE_PATH);
            dataMap = GSON.<Map<String, Object>>fromJson(string, Map.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Path createFileIfNotExists(Path path) {
        if (Files.notExists(path)) {
            try {
                Files.createFile(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return path;
    }
}
