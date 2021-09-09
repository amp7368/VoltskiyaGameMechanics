package voltskiya.apple.game_mechanics.decay.storage.deciders;

import org.bukkit.Material;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface DecayBlockDecider extends DecayBlockRequirementAbstract<Material> {
    DecayBlockDecider AIR = given(Material.AIR);

    @Contract(pure = true)
    static @NotNull DecayBlockDecider given(Material material) {
        return (c, x, y, z) -> material;
    }

    @NotNull
    default Material estimate() {
        return decide(DecayBlockContext.EMPTY, -1, -1, -1);
    }
}
