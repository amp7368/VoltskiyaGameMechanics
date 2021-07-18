package voltskiya.apple.game_mechanics.electricity.piping.simple;

import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import voltskiya.apple.game_mechanics.electricity.FluidTickable;
import voltskiya.apple.game_mechanics.electricity.FluidTickableLogistics;
import voltskiya.apple.utilities.util.minecraft.LocationCoords;
import voltskiya.apple.utilities.util.storage.GetNameable;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class ElectricitySimpleLine implements GetNameable, FluidTickable {
    private String name;
    private LocationCoords inletLoc;
    private LocationCoords outletLoc;
    private FluidTickable outlet;
    private List<FluidTickableLogistics> tick = null;

    public ElectricitySimpleLine(LocationCoords inletLoc, LocationCoords outletLoc) {
        this.inletLoc = inletLoc;
        this.outletLoc = outletLoc;
        this.name = UUID.randomUUID().toString();
    }

    /**
     * only used by gson
     */
    public ElectricitySimpleLine() {
    }


    @Override
    public String getName() {
        return name;
    }

    @Override
    public void reset() {
        this.tick = null;
    }

    @Override
    public LocationCoords[] getInlets() {
        return new LocationCoords[]{inletLoc};
    }

    @Override
    public @NotNull List<FluidTickableLogistics> tick(double maxWatts, double maxPerc) {
        if (this.tick == null) {
            if (outlet == null) {
                return Collections.emptyList();
            } else {
                return outlet.tick(maxWatts, maxPerc);
            }
        } else {
            return this.tick;
        }
    }

    @Override
    public FluidTickable create(Vector direction) {
        return null;
    }
}
