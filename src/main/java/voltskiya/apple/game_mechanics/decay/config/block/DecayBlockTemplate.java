package voltskiya.apple.game_mechanics.decay.config.block;

import org.bukkit.Material;
import voltskiya.apple.game_mechanics.decay.storage.deciders.DecayBlockDecider;
import voltskiya.apple.game_mechanics.decay.storage.deciders.DecayBlockDeciderRequirements;
import voltskiya.apple.game_mechanics.decay.storage.deciders.DecayBlockRequirementAbstract;

import java.util.*;

public class DecayBlockTemplate {
    public static final DecayBlockTemplate DEFAULT_TEMPLATE = new DecayBlockTemplate(
            new HashSet<>(1),
            null,
            new HashSet<>(List.of(Material.AIR)),
            DecayBlockDatabase.getDurability(),
            DecayBlockDatabase.getDefaultResistance());
    private final HashMap<DecayBlockTemplateRequiredTypeJoined, HashSet<DecayBlockTemplateRequired>> decayIntoMaterialsWithRequirements = new HashMap<>();
    private HashSet<Material> materials;
    private Material material;
    private HashSet<Material> decayInto;
    private int durability;
    private int resistance;

    public DecayBlockTemplate(DecayBlockBuilderTemplate builder) {
        this.material = builder.icon;
        this.decayInto = builder.decayInto;
        this.durability = builder.durability;
        this.materials = builder.materials;
        this.resistance = builder.resistance;
    }

    public DecayBlockTemplate(HashSet<Material> materials, Material material, HashSet<Material> decayInto, int durability, int resistance) {
        this.materials = materials;
        this.material = material;
        this.decayInto = decayInto;
        this.durability = durability;
        this.resistance = resistance;
    }

    public DecayBlockTemplate() {

    }

    public Material getIcon() {
        return material;
    }

    public Set<Material> getMaterials() {
        return materials;
    }

    public HashSet<Material> getDecayInto() {
        return decayInto;
    }

    public int getDurability() {
        return durability;
    }

    public int getResistance() {
        return resistance;
    }

    public DecayBlockBuilderTemplate toBuilder() {
        return new DecayBlockBuilderTemplate(this);
    }

    public DecayBlockDecider toDecider() {
        DecayBlockDeciderRequirements decider = new DecayBlockDeciderRequirements(this.getIcon());
        for (Map.Entry<DecayBlockTemplateRequiredTypeJoined, HashSet<DecayBlockTemplateRequired>> requirement : this.decayIntoMaterialsWithRequirements.entrySet()) {
            DecayBlockRequirementAbstract<Boolean> requirementFunction = (context, x1, y1, z1) -> requirement.getKey().getAsRequirement(context, x1, y1, z1);
            for (DecayBlockTemplateRequired block : requirement.getValue()) {
                decider.addChance(requirementFunction, (c, x, y, z) -> 1d, block.material);
            }
        }
        return decider;
    }

    public static class DecayBlockBuilderTemplate {
        public Material icon;
        public HashSet<Material> materials;
        public HashSet<Material> decayInto = new HashSet<>();
        private int durability;
        private int resistance;

        public DecayBlockBuilderTemplate(DecayBlockTemplate real) {
            this.icon = real.material;
            this.decayInto = real.decayInto;
            this.durability = real.durability;
            this.materials = real.materials;
            this.resistance = real.resistance;
        }

        public DecayBlockBuilderTemplate(Material icon) {
            this.icon = icon;
            this.materials = new HashSet<>();
            this.materials.add(icon);
            this.durability = DecayBlockDatabase.getDurability();
        }

        public DecayBlockTemplate build() {
            return new DecayBlockTemplate(this);
        }

        public void incrementDurability(int i) {
            durability += i;
        }

        public void incrementResistance(int i) {
            resistance += i;
        }

        public int getDurability() {
            return durability;
        }

        public void addMaterial(Material type) {
            this.materials.add(type);
        }

        public Material getIcon() {
            return icon;
        }
    }
}
