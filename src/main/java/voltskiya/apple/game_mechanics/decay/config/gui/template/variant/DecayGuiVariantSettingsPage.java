package voltskiya.apple.game_mechanics.decay.config.gui.template.variant;

import org.bukkit.Material;
import voltskiya.apple.game_mechanics.decay.config.gui.DecayGui;
import voltskiya.apple.game_mechanics.decay.config.template.DecayBlockTemplate;
import voltskiya.apple.game_mechanics.decay.config.template.MaterialVariant;
import voltskiya.apple.utilities.util.gui.acd.page.InventoryGuiPageImplACD;
import voltskiya.apple.utilities.util.gui.acd.slot.InventoryGuiSlotDoNothingACD;
import voltskiya.apple.utilities.util.gui.acd.slot.InventoryGuiSlotImplACD;
import voltskiya.apple.utilities.util.gui.acd.slot.InventoryGuiSlotSplitImplACD;
import voltskiya.apple.utilities.util.gui.acd.slot.cycle.InventoryGuiSlotCycleACD;

import java.util.List;

import static voltskiya.apple.utilities.util.minecraft.InventoryUtils.makeItem;

public class DecayGuiVariantSettingsPage extends InventoryGuiPageImplACD<DecayGui> {
    private final DecayBlockTemplate.DecayBlockBuilderTemplate parentBlock;
    private final MaterialVariant variant;

    public DecayGuiVariantSettingsPage(DecayGui decayGui, DecayBlockTemplate.DecayBlockBuilderTemplate parentBlock, MaterialVariant variant) {
        super(decayGui);
        this.parentBlock = parentBlock;
        this.variant = variant;
    }

    @Override
    public void initialize() {
        setSlot(new InventoryGuiSlotDoNothingACD(makeItem(this.variant.material)), 4);
        setSlot(new InventoryGuiSlotImplACD(e -> parentRemoveSubPage(), makeItem(Material.RED_TERRACOTTA, "Discard Changes")), 0);
        setSlot(new InventoryGuiSlotImplACD(e -> {
            parentBlock.decayIntoThis.remove(variant.material);
            parentRemoveSubPage();
        }, makeItem(Material.RED_CONCRETE_POWDER, "DELETE")), 1);
        setSlot(new InventoryGuiSlotImplACD(e -> {
            parentBlock.decayIntoThis.put(variant.material, variant);
            parentRemoveSubPage();
        }, makeItem(Material.GREEN_TERRACOTTA, "Save")), 8);
        setSlot(new InventoryGuiSlotImplACD(e -> parentAddSubPage(new DecayGuiVariantRequirementsType(parent, variant)),
                makeItem(Material.GLASS, "Requirements to decay")), 9);
    }

    @Override
    public void refreshPageItems() {
        setSlot(new InventoryGuiSlotSplitImplACD(
                        makeItem(Material.REDSTONE, 1, String.format("%.1f chance to decay into", variant.chance), List.of(
                                "Typically between 1 and 10",
                                "Normal Left: -1",
                                "Shift Left: -0.1",
                                "Normal Right: 1",
                                "Shift Right: 0.1"
                        )))
                        .withNoShiftLeftClick(e -> variant.chance -= 1)
                        .withShiftLeftClick(e -> variant.chance -= .1)
                        .withNoShiftRightClick(e -> variant.chance += 1)
                        .withShiftRightClick(e -> variant.chance += .1)
                , 12);
        setSlot(new InventoryGuiSlotCycleACD<>(variant::getBlockSize, variant::setBlockSize), 14);
        setSlot(new InventoryGuiSlotCycleACD<>(variant::getMaterialVariantType, variant::setMaterialVariantType), 17);

    }

    @Override
    public String getName() {
        return "Material Variant";
    }

    @Override
    public int size() {
        return 18;
    }
}
