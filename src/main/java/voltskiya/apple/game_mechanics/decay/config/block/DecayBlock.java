package voltskiya.apple.game_mechanics.decay.config.block;

import org.bukkit.Material;

import java.util.HashSet;
import java.util.Set;

public class DecayBlock {
    private int damage;
    private Set<Material> materials;
    private Material material;
    private Material decayInto;
    private int durability;

    public DecayBlock(DecayBlockBuilder builder) {
        this.material = builder.icon;
        this.decayInto = builder.decayInto;
        this.durability = builder.durability;
        this.materials = builder.materials;
        this.damage = builder.damage;
    }

    public DecayBlock() {

    }

    public Material getMaterial() {
        return material;
    }

    public static class DecayBlockBuilder {
        public Material icon;
        public Set<Material> materials;
        public Material decayInto = null;
        private int durability;
        private int damage = 0;

        public DecayBlockBuilder(DecayBlock real) {
            this.icon = real.material;
            this.decayInto = real.decayInto;
            this.durability = real.durability;
            this.materials = real.materials;
            this.damage = real.damage;
        }

        public DecayBlockBuilder(Material icon) {
            this.icon = icon;
            this.materials = new HashSet<>();
            this.materials.add(icon);
            this.durability = DecayBlockDatabase.getDurability();
        }

        public DecayBlock build() {
            return new DecayBlock(this);
        }

        public void incrementDurability(int i) {
            durability += i;
        }

        public void incrementDamage(int i) {
            damage += i;
        }

        public int getDurability() {
            return durability;
        }

        public int getDamage() {
            return damage;
        }

        public void addMaterial(Material type) {
            this.materials.add(type);
        }

        public Material getIcon() {
            return icon;
        }
    }
}
