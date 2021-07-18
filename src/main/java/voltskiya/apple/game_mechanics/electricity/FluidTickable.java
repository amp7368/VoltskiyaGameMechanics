package voltskiya.apple.game_mechanics.electricity;

import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import voltskiya.apple.utilities.util.minecraft.LocationCoords;

import java.util.List;

public interface FluidTickable {
    void reset();

    LocationCoords[] getInlets();

    @NotNull List<FluidTickableLogistics> tick(double maxWatts, double maxPerc);

    FluidTickable create(Vector direction);
}
