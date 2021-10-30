package voltskiya.apple.game_mechanics.tmw.sql;

public class SqlVariableNames {
    public static final String X = "x";
    public static final String Y = "y";
    public static final String Z = "z";
    public static final String MOB_UNIQUE_NAME = "unique_name";
    public static final String DESPAWN_TIME = "despawn_time";
    public static final String TABLE_STORED_MOB = "stored_mob";
    public static final String WORLD_MY_UID = "world_my_uid";
    public static final String WORLD_UUID = "world_uuid";
    public static final String TABLE_WORLD = "world";
    public static final String MOB_MY_UID = "mob_my_uid";
    public static final String MATERIAL = "name";
    public static final String MATERIAL_MY_UID = "material_my_uid";
    public static final String TABLE_MATERIAL = "material";

    public static class ChunkSql {
        public static final String TABLE_CHUNK = "chunk";
        public static final String CHUNK_UID = "chunk_uid";
        public static final String BIOME_GUESS_UID = "biome_guess_uid";
    }

    public static class Weather {

        public static final String WEATHER_UID = "weather_uid";
        public static final String IMPACT = "impact";
        public static final String IMPACT_VELOCITY = "impact_velocity";
        public static final String IMPACT_ACCELERATION = "impact_acceleration";
        public static final String TABLE_WEATHER = "weather";
    }

    public static class Contour {
        public static final String TABLE_CONTOUR = "contour";
        public static final String CHUNK_UID = "chunk_uid";
        public static final String CHUNK_X = "chunk_x";
        public static final String CHUNK_Z = "chunk_z";
        public static final String BRIDGE_X_POS = "bridge_x_pos";
        public static final String BRIDGE_X_NEG = "bridge_x_neg";
        public static final String BRIDGE_Z_POS = "bridge_z_pos";
        public static final String BRIDGE_Z_NEG = "bridge_z_neg";
        public static final String MIDDLE_X = "middle_x";
        public static final String MIDDLE_Y = "middle_y";
        public static final String MIDDLE_Z = "middle_z";
    }

    public static class Kills {
        public static final String TABLE_CHUNK_KILL = "chunk_kill";
        public static final String TIME = "time_of_kill";
    }

    public static class Decay {
        // table
        public static final String TABLE_DECAY_BLOCK = "decay_block";

        // location
        public static final String X = "x";
        public static final String Y = "y";
        public static final String Z = "z";
        public static final String WORLD_UID = "world";

        // material
        public static final String ORIGINAL_MATERIAL = "original_material";
        public static final String CURRENT_MATERIAL = "new_material";

        // fields
        public static final String DAMAGE = "damage";
        public static final String BLOCK_UID = "block_uid";
    }
}
