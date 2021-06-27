package voltskiya.apple.game_mechanics.tmw.tmw_config.clothing;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import voltskiya.apple.game_mechanics.tmw.PluginTMW;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClothingDatabase {
    private static final String CLOTHINGS_FOLDER = "clothing";
    private static final File clothingsFile;
    private static Gson gson;
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
}
