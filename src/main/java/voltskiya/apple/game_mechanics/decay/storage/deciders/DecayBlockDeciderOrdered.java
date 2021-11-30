package voltskiya.apple.game_mechanics.decay.storage.deciders;

import apple.utilities.util.ObjectUtilsFormatting;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class DecayBlockDeciderOrdered implements DecayBlockDecider {
    private final Random random = new Random();
    private final Material defaultChoice;
    private List<DecayBlockPossibility> choices;
    private Material decided = null;

    public DecayBlockDeciderOrdered(Material defaultChoice) {
        this.defaultChoice = defaultChoice;
    }

    public DecayBlockDeciderOrdered addChance(DecayBlockRequirementAbstract<Double> chanceDecider, Material materialIfChance) {
        this.choices.add(new DecayBlockPossibility(Integer.MAX_VALUE, chanceDecider, materialIfChance));
        return this;
    }

    public DecayBlockDeciderOrdered addChance(DecayBlockRequirementAbstract<Double> chanceDecider, Material materialIfChance, int priority) {
        this.choices.add(new DecayBlockPossibility(priority, chanceDecider, materialIfChance));
        return this;
    }

    @Override
    public @NotNull Material decide(DecayBlockContext context, int x, int y, int z) {
        choices.sort(Comparator.comparingInt(DecayBlockPossibility::priority));
        for (DecayBlockPossibility possibility : choices) {
            if (random.nextDouble() <= possibility.chanceDecider.decide(context, x, y, z)) {
                decided = possibility.materialIfChance;
            }
        }
        if (decided == null) {
            decided = defaultChoice;
        }
        return decided;
    }

    @Override
    public @NotNull Material estimate() {
        return ObjectUtilsFormatting.requireNonNullElseElse(Material.AIR, decided, defaultChoice);
    }

    private record DecayBlockPossibility(int priority,
                                         DecayBlockRequirementAbstract<Double> chanceDecider,
                                         Material materialIfChance) {
    }
}
