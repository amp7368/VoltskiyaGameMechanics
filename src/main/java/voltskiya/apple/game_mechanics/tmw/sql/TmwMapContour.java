package voltskiya.apple.game_mechanics.tmw.sql;

import javax.persistence.Column;


public class TmwMapContour {
    public long chunk_uid;
    public int chunk_x;
    public int chunk_z;
    @Column(name = SqlVariableNames.Contour.BRIDGE_X_POS, nullable = false)
    public long bride_x_pos;
    @Column(name = SqlVariableNames.Contour.BRIDGE_X_NEG, nullable = false)
    public long bride_x_neg;
    @Column(name = SqlVariableNames.Contour.BRIDGE_Z_POS, nullable = false)
    public long bride_z_pos;
    @Column(name = SqlVariableNames.Contour.BRIDGE_Z_NEG, nullable = false)
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
