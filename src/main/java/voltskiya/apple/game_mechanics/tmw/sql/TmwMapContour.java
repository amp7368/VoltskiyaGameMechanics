package voltskiya.apple.game_mechanics.tmw.sql;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Table(name = SqlVariableNames.Contour.TABLE_CONTOUR)
@Entity(name = SqlVariableNames.Contour.TABLE_CONTOUR)
public class TmwMapContour {
    @Id
    @Column(name = SqlVariableNames.Contour.CHUNK_UID, nullable = false)
    public long chunk_uid;
    @Column(name = SqlVariableNames.WORLD_MY_UID, columnDefinition = "TINYINT")
    public int worldMyUid;
    @Column(name = SqlVariableNames.Contour.CHUNK_X, nullable = false)
    public int chunk_x;
    @Column(name = SqlVariableNames.Contour.CHUNK_Z, nullable = false)
    public int chunk_z;
    @Column(name = SqlVariableNames.Contour.BRIDGE_X_POS)
    public Long bride_x_pos;
    @Column(name = SqlVariableNames.Contour.BRIDGE_X_NEG)
    public Long bride_x_neg;
    @Column(name = SqlVariableNames.Contour.BRIDGE_Z_POS)
    public Long bride_z_pos;
    @Column(name = SqlVariableNames.Contour.BRIDGE_Z_NEG)
    public Long bride_z_neg;
    @Column(name = SqlVariableNames.Contour.MIDDLE_X, nullable = false)
    public int middle_x;
    @Column(name = SqlVariableNames.Contour.MIDDLE_Y, nullable = false)
    public int middle_y;
    @Column(name = SqlVariableNames.Contour.MIDDLE_Z, nullable = false)
    public int middle_z;

    public TmwMapContour() {
    }

    public TmwMapContour(long chunk_uid, int worldMyUid, int chunk_x, int chunk_z, Long bride_x_pos, Long bride_x_neg, Long bride_z_pos, Long bride_z_neg, int middle_x, int middle_y, int middle_z) {
        this.chunk_uid = chunk_uid;
        this.worldMyUid = worldMyUid;
        this.chunk_x = chunk_x;
        this.chunk_z = chunk_z;
        this.bride_x_pos = bride_x_pos;
        this.bride_x_neg = bride_x_neg;
        this.bride_z_pos = bride_z_pos;
        this.bride_z_neg = bride_z_neg;
        this.middle_x = middle_x;
        this.middle_y = middle_y;
        this.middle_z = middle_z;
    }
}
