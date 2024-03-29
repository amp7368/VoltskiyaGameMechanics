package voltskiya.apple.game_mechanics.decay.storage.deciders;

import apple.utilities.util.ObjectUtilsFormatting;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public class DecayBlockDeciderRequirements implements DecayBlockDecider {

    private final List<DecayBlockPossibilityRequirement> choices = new ArrayList<>();
    private final Random random = new Random();
    private final Material defaultChoice;
    private Material decided = null;

    public DecayBlockDeciderRequirements(@NotNull Material defaultChoice) {
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
        choices.removeIf(choice -> !choice.requirements(context, x, y, z));
        double chanceSum = 0;
        for (DecayBlockPossibilityRequirement choice : choices) {
            chanceSum += choice.chanceDecider(context, x, y, z);
        }
        if (chanceSum <= 0) {
            decided = defaultChoice;
            return decided;
        }
        double choiceIndex = random.nextDouble() * chanceSum;
        for (DecayBlockPossibilityRequirement choice : choices) {
            choiceIndex -= choice.chanceDecider(context, x, y, z);
            if (choiceIndex <= 0) {
                return decided = choice.materialIfChance();
            }
        }
        return decided;
    }

    public DecayBlockDeciderRequirements addChance(
        DecayBlockRequirementAbstract<Boolean> requirements,
        DecayBlockRequirementAbstract<Double> chanceDecider, Material materialIfChance) {
        this.choices.add(
            new DecayBlockPossibilityRequirement(requirements, chanceDecider, materialIfChance));
        return this;
    }

    private static final class DecayBlockPossibilityRequirement {

        private final DecayBlockRequirementAbstract<Boolean> requirements;
        private final DecayBlockRequirementAbstract<Double> chanceDecider;
        private final Material materialIfChance;
        private Double chance;
        private Boolean requirement;

        private DecayBlockPossibilityRequirement(
            DecayBlockRequirementAbstract<Boolean> requirements,
            DecayBlockRequirementAbstract<Double> chanceDecider,
            Material materialIfChance) {
            this.requirements = requirements;
            this.chanceDecider = chanceDecider;
            this.materialIfChance = materialIfChance;
            this.chance = null;
            this.requirement = null;
        }

        public Boolean requirements(DecayBlockContext context, int x, int y, int z) {
            return this.requirement == null ? this.requirement = requirements.decide(context, x, y,
                z) : this.requirement;
        }

        public Double chanceDecider(DecayBlockContext context, int x, int y, int z) {
            return this.chance == null ? this.chance = chanceDecider.decide(context, x, y, z)
                : this.chance;
        }

        public Material materialIfChance() {
            return materialIfChance;
        }

    }
}
