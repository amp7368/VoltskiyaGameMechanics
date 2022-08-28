package voltskiya.apple.game_mechanics.tmw.tmw_world.util;

import apple.utilities.database.SaveFileable;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.HashMap;
import java.util.UUID;

public class SimpleWorldDatabase implements SaveFileable {

    private static SimpleWorldDatabase instance;
    private final HashMap<UUID, Integer> realToMyWorldSaved = new HashMap<>();
    private transient BiMap<UUID, Integer> realToMyWorld = null;

    public SimpleWorldDatabase() {
        instance = this;
    }

    private static SimpleWorldDatabase get() {
        return instance;
    }

    public synchronized static UUID getWorld(int uid) {
        verifyMap();
        return get().realToMyWorld.inverse().get(uid);
    }

    private static void verifyMap() {
        if (get().realToMyWorld == null) {
            get().realToMyWorld = HashBiMap.create(get().realToMyWorldSaved);
        }
    }

    public synchronized static int getWorld(UUID uid) {
        verifyMap();
        Integer myUid = get().realToMyWorld.get(uid);
        if (myUid == null) {
            myUid = get().realToMyWorld.values().stream().max(Integer::compareTo).orElse(0);
            myUid++;
            get().realToMyWorld.put(uid, myUid);
            get().realToMyWorldSaved.put(uid, myUid);
            WorldDatabaseManager.get().save(get());
        }
        return myUid;
    }

    @Override
    public String getSaveFileName() {
        return "world_list.json";
    }


}
