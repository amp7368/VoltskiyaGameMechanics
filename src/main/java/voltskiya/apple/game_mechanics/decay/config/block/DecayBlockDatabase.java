package voltskiya.apple.game_mechanics.decay.config.block;

import com.google.gson.Gson;
import org.bukkit.Material;
import voltskiya.apple.game_mechanics.tmw.PluginTMW;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DecayBlockDatabase {
    private static final String BLOCKS_FOLDER = "decayBlocks";
    private static final File blocksFile;
    private static DecayBlockDatabase instance;

    static {
        // get the blocks from our db
        File blocksFolder = new File(PluginTMW.get().getDataFolder(), BLOCKS_FOLDER);
        blocksFolder.mkdirs();
        blocksFile = new File(blocksFolder, "decayBlocksDB.json");
        try {
            if (blocksFile.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(blocksFile))) {
                    instance = new Gson().fromJson(reader, DecayBlockDatabase.class);
                }
            } else {
                blocksFile.createNewFile();
                instance = new DecayBlockDatabase();
                save();
            }
        } catch (IOException e) {
            instance = null;
            e.printStackTrace();
        }
    }

    private final HashMap<Material, DecayBlock> blocks = new HashMap<>();
    private int decayRate = 10;
    private int durability = 10;

    public static DecayBlockDatabase get() {
        return instance;
    }

    public synchronized static void addBlock(DecayBlock block) {
        get().blocks.put(block.getMaterial(), block);
        save();
    }

    public static List<DecayBlock> getAll() {
        return new ArrayList<>(get().blocks.values());
    }

    private static void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(blocksFile))) {
            new Gson().toJson(get(), writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static DecayBlock getBlock(Material name) {
        return get().blocks.get(name);
    }

    public static void incrementDecayRate(int i) {
        get().decayRate += i;
        save();
    }

    public static void incrementDurability(int i) {
        get().durability += i;
        save();
    }

    public static int getDecayRate() {
        return get().decayRate;
    }

    public static int getDurability() {
        return get().durability;
    }
}