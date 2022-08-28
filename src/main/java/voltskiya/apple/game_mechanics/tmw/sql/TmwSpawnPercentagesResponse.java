package voltskiya.apple.game_mechanics.tmw.sql;

import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityResult;
import jakarta.persistence.FieldResult;
import jakarta.persistence.Id;
import jakarta.persistence.SqlResultSetMapping;
import voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.BiomeType;

@SqlResultSetMapping(name = "tmw_spawn_percentages",
    entities = @EntityResult(
        entityClass = TmwSpawnPercentagesResponse.class,
        fields = {
            @FieldResult(name = "chunkUid", column = "c" + SqlVariableNames.ChunkSql.CHUNK_UID),
            @FieldResult(name = "biome", column = SqlVariableNames.ChunkSql.BIOME_GUESS_UID),
            @FieldResult(name = "chunkX", column = SqlVariableNames.Contour.CHUNK_X),
            @FieldResult(name = "chunkZ", column = SqlVariableNames.Contour.CHUNK_Z),
            @FieldResult(name = "worldMyUid", column = SqlVariableNames.WORLD_MY_UID),
            @FieldResult(name = "middleX", column = SqlVariableNames.Contour.MIDDLE_X),
            @FieldResult(name = "middleY", column = SqlVariableNames.Contour.MIDDLE_Y),
            @FieldResult(name = "middleZ", column = SqlVariableNames.Contour.MIDDLE_Z),
            @FieldResult(name = "mobName", column = SqlVariableNames.MOB_UNIQUE_NAME),
            @FieldResult(name = "mobCount", column = "mob_count"),
        }
    ))
@Entity
public class TmwSpawnPercentagesResponse {

    @Id
    public long chunkUid;
    @Convert(converter = VerifyDatabaseTmw.BiomeTypeConverter.class)
    public BiomeType biome;
    public int chunkX;
    public int chunkZ;
    public int worldMyUid;
    public int middleX;
    public int middleY;
    public int middleZ;
    public String mobName;
    public int mobCount;
}
