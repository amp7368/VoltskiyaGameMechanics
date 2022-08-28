package voltskiya.apple.game_mechanics.tmw.sql;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = SqlVariableNames.ChunkSql.TABLE_CHUNK)
public class TmwChunkEntity {

    @Id
    @Column(name = SqlVariableNames.ChunkSql.CHUNK_UID)
    public long chunkUid;
    @Column(name = SqlVariableNames.ChunkSql.BIOME_GUESS_UID, columnDefinition = "TINYINT")
    public int biome;

    public TmwChunkEntity(long chunkUid, int biome) {
        this.chunkUid = chunkUid;
        this.biome = biome;
    }

    public TmwChunkEntity() {
    }
}
