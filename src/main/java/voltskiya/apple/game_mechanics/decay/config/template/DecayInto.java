package voltskiya.apple.game_mechanics.decay.config.template;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import voltskiya.apple.game_mechanics.decay.config.database.DecayBlockDatabase;

public final class DecayInto {
    private Material material;
    private transient MaterialVariant variant = null;
    private transient boolean isDeleted;

    public DecayInto() {
        this.isDeleted = false;
    }

    public DecayInto(ItemStack item) {
        this.isDeleted = true;
        this.material = item.getType();
    }

    public DecayInto(DecayInto other) {
        this.isDeleted = true;
        this.material = other.material;
    }

    public Material getMaterial() {
        return material;
    }

    public DecayInto copy() {
        return new DecayInto(this);
    }

    public double getChance() {
        verifyMaterialVariant();
        return variant == null ? 1 : variant.chance;
    }

    private void verifyMaterialVariant() {
        if (this.variant == null) {
            this.variant = DecayBlockDatabase.getMaterialVariant(material);
        } else if (this.variant.isDeleted()) {
            this.variant = null;
        }
    }

    public void setDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
}
