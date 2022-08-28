package voltskiya.apple.game_mechanics.decay.storage.deciders;

import apple.utilities.util.ObjectUtilsFormatting;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public class DecayBlockDeciderEqualRequirements implements DecayBlockDecider {

    private final Material defaultChoice;
    private final List<DecayBlockPossibilityRequirement> choices = new ArrayList<>();
    private final Random random = new Random();
    private Material decided = null;

    public DecayBlockDeciderEqualRequirements(@NotNull Material defaultChoice) {
        this.defaultChoice = defaultChoice;
    }

    @Override
    @NotNull
    public Material estimate() {
        return ObjectUtilsFormatting.requireNonNullElseElse(Material.AIR, decided, defaultChoice);
    }

    @Override
    @NotNull
    public Material decide(DecayBlockContext context, int x, int y, int z) {
        choices.removeIf(choice -> !choice.requirements().decide(context, x, y, z));
        if (choices.isEmpty()) {
            return defaultChoice;
        }
        return decided = choices.get(random.nextInt(choices.size())).materialIfChance;
    }

    public DecayBlockDeciderEqualRequirements addChance(
        DecayBlockRequirementAbstract<Boolean> requirements, Material materialIfChance) {
        this.choices.add(new DecayBlockPossibilityRequirement(requirements, materialIfChance));
        return this;
    }

    private record DecayBlockPossibilityRequirement(
        DecayBlockRequirementAbstract<Boolean> requirements,
        Material materialIfChance) {

    }
}
