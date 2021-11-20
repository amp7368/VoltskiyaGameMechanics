package voltskiya.apple.game_mechanics.electricity;

import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ElectricityInlets {
    private static final Map<Location, FluidTickable> inlets = new HashMap<>();

    public static void addTickable(FluidTickable tickable) {
        for (Location inlet : tickable.getInlets()) {
            inlets.put(inlet, tickable);
        }
    }

    @Nullable
    public static FluidTickable getInlet(Location coords) {
        return inlets.get(coords);
    }
}
