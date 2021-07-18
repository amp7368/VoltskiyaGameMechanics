package voltskiya.apple.game_mechanics.electricity;

import org.jetbrains.annotations.Nullable;
import voltskiya.apple.utilities.util.minecraft.LocationCoords;

import java.util.HashMap;
import java.util.Map;

public class ElectricityInlets {
    private static final Map<LocationCoords, FluidTickable> inlets = new HashMap<>();

    public static void addTickable(FluidTickable tickable) {
        for (LocationCoords inlet : tickable.getInlets()) {
            inlets.put(inlet, tickable);
        }
    }

    @Nullable
    public static FluidTickable getInlet(LocationCoords coords) {
        return inlets.get(coords);
    }
}
