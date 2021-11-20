package voltskiya.apple.game_mechanics.electricity;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface FluidTickable {
    void reset();

    Location[] getInlets();

    @NotNull List<FluidTickableLogistics> tick(double maxWatts, double maxPerc);

    FluidTickable create(Vector direction);
}
