package voltskiya.apple.game_mechanics.decay.config.block;

import org.bukkit.Material;

public class DecayBlock {
    private Material material;
    private Material decayInto;
    private int durability;

    public DecayBlock(DecayBlockBuilder builder) {
        this.material = builder.material;
        this.decayInto = builder.decayInto;
        this.durability = builder.durability;
    }

    public DecayBlock() {

    }

    public Material getMaterial() {
        return material;
    }

    public static class DecayBlockBuilder {
        public Material material;
        public Material decayInto = null;
        private int durability;

        public DecayBlockBuilder(DecayBlock real) {
            this.material = real.material;
            this.decayInto = real.decayInto;
            this.durability = real.durability;
        }

        public DecayBlockBuilder(Material material) {
            this.material = material;
            this.durability = DecayBlockDatabase.getDurability();
        }

        public DecayBlock build() {
            return new DecayBlock(this);
        }

        public void incrementDurability(int i) {
            durability += i;
        }

        public int getDurability() {
            return durability;
        }
    }
}
