package voltskiya.apple.game_mechanics.decay.config.template;


import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import voltskiya.apple.game_mechanics.decay.config.gui.template.variant.BlockSizeType;

public class MaterialVariant {

    public DecayBlockTemplateRequiredTypeJoined requirementsType;
    public Material material;
    public double chance;
    public BlockSizeType blockSize;
    public MaterialVariantType materialVariantType;
    private transient boolean isDeleted;

    public MaterialVariant() {
        this.isDeleted = false;
    }

    public MaterialVariant(ItemStack item) {
        this.isDeleted = true;
        this.material = item.getType();
        this.requirementsType = new DecayBlockTemplateRequiredTypeJoined();
        this.chance = 1;
        this.blockSize = BlockSizeType.FULL;
        this.materialVariantType = MaterialVariantType.NORMAL;
    }

    public MaterialVariant(MaterialVariant other) {
        this.isDeleted = true;
        this.requirementsType = other.requirementsType.copy();
        this.material = other.material;
        this.chance = other.chance;
        this.blockSize = other.blockSize;
        this.materialVariantType = other.materialVariantType;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof MaterialVariant other && other.material.equals(this.material);
    }

    @Override
    public int hashCode() {
        return material.hashCode();
    }

    public MaterialVariant copy() {
        return new MaterialVariant(this);
    }

    public BlockSizeType getBlockSize() {
        return this.blockSize;
    }

    public void setBlockSize(BlockSizeType blockSize) {
        this.blockSize = blockSize;
    }

    public MaterialVariantType getMaterialVariantType() {
        return this.materialVariantType;
    }

    public void setMaterialVariantType(MaterialVariantType materialVariantType) {
        this.materialVariantType = materialVariantType;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
}
