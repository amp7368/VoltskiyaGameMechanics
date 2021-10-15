package voltskiya.apple.game_mechanics.decay.config.template;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public final class DecayInto {
    private int chance;
    private Material material;

    public DecayInto() {
    }

    public DecayInto(ItemStack item) {
        this.chance = 1;
        this.material = item.getType();
    }

    public DecayInto(DecayInto other) {
        this.chance = other.chance;
        this.material = other.material;
    }

    public int getChance() {
        return chance;
    }

    public Material getMaterial() {
        return material;
    }

    public DecayInto copy() {
        return new DecayInto(this);
    }
}
