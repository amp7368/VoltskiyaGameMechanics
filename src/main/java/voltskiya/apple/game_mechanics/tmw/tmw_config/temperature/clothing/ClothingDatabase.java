package voltskiya.apple.game_mechanics.tmw.tmw_config.temperature.clothing;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import voltskiya.apple.game_mechanics.tmw.PluginTMW;

public class ClothingDatabase {

    private static final String CLOTHINGS_FOLDER = "clothing";
    private static final File clothingsFile;
    private static final Gson gson;
    private static ClothingDatabase instance;

    static {
        final GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();

        // get the clothings from our db
        File clothingsFolder = new File(PluginTMW.get().getDataFolder(), CLOTHINGS_FOLDER);
        clothingsFolder.mkdirs();
        clothingsFile = new File(clothingsFolder, "clothingsDB.json");
        try {
            if (clothingsFile.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(clothingsFile))) {
                    instance = gson.fromJson(reader, ClothingDatabase.class);
                }
            } else {
                clothingsFile.createNewFile();
                instance = new ClothingDatabase();
                save();
            }
        } catch (IOException e) {
            instance = null;
            e.printStackTrace();
        }
    }

    private final HashMap<String, ClothingType> clothings = new HashMap<>();

    public synchronized static void addClothing(ClothingType clothing) {
        get().clothings.put(clothing.getName(), clothing);
        save();
    }

    public static ClothingDatabase get() {
        return instance;
    }

    private static void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(clothingsFile))) {
            gson.toJson(get(), writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<ClothingType> getAll() {
        return new ArrayList<>(get().clothings.values());
    }

    @Nullable
    public static ClothingType get(@NotNull ItemStack item) {
        String name = ClothingType.getName(item);
        return get().clothings.get(name);
    }
}
