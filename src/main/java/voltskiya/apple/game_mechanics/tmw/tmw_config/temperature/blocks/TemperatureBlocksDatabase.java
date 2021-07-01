package voltskiya.apple.game_mechanics.tmw.tmw_config.temperature.blocks;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;
import voltskiya.apple.game_mechanics.tmw.PluginTMW;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TemperatureBlocksDatabase {
    private static final String TempBlockS_FOLDER = "tempBlock";
    private static final File tempBlocksFile;
    private static final Gson gson;
    private static TemperatureBlocksDatabase instance;

    static {
        final GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();

        // get the tempBlocks from our db
        File tempBlocksFolder = new File(PluginTMW.get().getDataFolder(), TempBlockS_FOLDER);
        tempBlocksFolder.mkdirs();
        tempBlocksFile = new File(tempBlocksFolder, "tempBlocksDB.json");
        try {
            if (tempBlocksFile.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(tempBlocksFile))) {
                    instance = gson.fromJson(reader, TemperatureBlocksDatabase.class);
                }
            } else {
                tempBlocksFile.createNewFile();
                instance = new TemperatureBlocksDatabase();
                save();
            }
        } catch (IOException e) {
            instance = null;
            e.printStackTrace();
        }
    }

    private final HashMap<String, TempBlockType> tempBlocks = new HashMap<>();

    public synchronized static void addTempBlock(TempBlockType tempBlock) {
        get().tempBlocks.put(tempBlock.getName(), tempBlock);
        save();
    }

    public static TemperatureBlocksDatabase get() {
        return instance;
    }

    private static void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempBlocksFile))) {
            gson.toJson(get(), writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<TempBlockType> getAll() {
        return new ArrayList<>(get().tempBlocks.values());
    }

    @Nullable
    public static TempBlockType get(Material type) {
        return get().tempBlocks.get(type.name());
    }
}

