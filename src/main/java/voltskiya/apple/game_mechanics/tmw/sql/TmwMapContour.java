package voltskiya.apple.game_mechanics.tmw.sql;

import javax.persistence.*;


@Table(name = SqlVariableNames.Contour.TABLE_CONTOUR,
        uniqueConstraints = {
                @UniqueConstraint(columnNames =
                        {SqlVariableNames.WORLD_MY_UID, SqlVariableNames.Contour.CHUNK_X, SqlVariableNames.Contour.CHUNK_Z}
                )
        })
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
    public long bride_x_pos;
    @Column(name = SqlVariableNames.Contour.BRIDGE_X_NEG)
    public long bride_x_neg;
    @Column(name = SqlVariableNames.Contour.BRIDGE_Z_POS)
    public long bride_z_pos;
    @Column(name = SqlVariableNames.Contour.BRIDGE_Z_NEG)
    public long bride_z_neg;
    @Column(name = SqlVariableNames.Contour.MIDDLE_X, nullable = false)
    public int middle_x;
    @Column(name = SqlVariableNames.Contour.MIDDLE_Y, nullable = false)
    public int middle_y;
    @Column(name = SqlVariableNames.Contour.MIDDLE_Z, nullable = false)
    public int middle_z;

    public TmwMapContour() {
    }
}
