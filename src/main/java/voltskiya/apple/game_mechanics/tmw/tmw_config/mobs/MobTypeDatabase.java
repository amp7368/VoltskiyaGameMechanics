package voltskiya.apple.game_mechanics.tmw.tmw_config.mobs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MobTypeDatabase {
    private static final Map<String, MobType> mobs = new HashMap<>();

    static {
        // get the mobs from our db
    }

    public synchronized static void addMob(MobType mob) {
        mobs.put(mob.getName(), mob);
    }

    public static List<MobType> getAll() {
        return new ArrayList<>(mobs.values());
    }
}
