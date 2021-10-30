package voltskiya.apple.game_mechanics.tmw.sql;


import voltskiya.apple.game_mechanics.tmw.sql.SqlVariableNames.ChunkSql;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = ChunkSql.TABLE_CHUNK)
public class TmwChunkEntity {
    @Id
    @Column(name = ChunkSql.CHUNK_UID)
    public long chunkUid;
    @Column(name = ChunkSql.BIOME_GUESS_UID, columnDefinition = "TINYINT")
    public int biome;
}
