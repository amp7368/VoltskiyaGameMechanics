package voltskiya.apple.game_mechanics.decay.config.gui.template.variant;

import apple.mc.utilities.inventory.gui.acd.page.InventoryGuiPageImplACD;
import apple.mc.utilities.inventory.gui.acd.slot.ItemGuiSlotCycleACD;
import java.util.List;
import org.bukkit.Material;
import voltskiya.apple.game_mechanics.decay.config.gui.DecayGui;
import voltskiya.apple.game_mechanics.decay.config.template.DecayBlockTemplate;
import voltskiya.apple.game_mechanics.decay.config.template.MaterialVariant;

public class DecayGuiVariantSettingsPage extends InventoryGuiPageImplACD<DecayGui> {

    private final DecayBlockTemplate.DecayBlockBuilderTemplate parentBlock;
    private final MaterialVariant variant;

    public DecayGuiVariantSettingsPage(DecayGui decayGui,
        DecayBlockTemplate.DecayBlockBuilderTemplate parentBlock, MaterialVariant variant) {
        super(decayGui);
        this.parentBlock = parentBlock;
        this.variant = variant;
    }

    @Override
    public void initialize() {
        setSlot(slotDoNothing(makeItem(this.variant.material)), 4);
        setSlot(slotImpl(e -> parentRemoveSubPage(),
            makeItem(Material.RED_TERRACOTTA, "Discard Changes")), 0);
        setSlot(slotImpl(e -> {
            parentBlock.decayIntoThis.remove(variant.material);
            parentRemoveSubPage();
        }, makeItem(Material.RED_CONCRETE_POWDER, "DELETE")), 1);
        setSlot(slotImpl(e -> {
            parentBlock.decayIntoThis.put(variant.material, variant);
            parentRemoveSubPage();
        }, makeItem(Material.GREEN_TERRACOTTA, "Save")), 8);
        setSlot(
            slotImpl(e -> parentAddSubPage(new DecayGuiVariantRequirementsType(parent, variant)),
                makeItem(Material.GLASS, "Requirements to decay")), 9);
    }

    @Override
    public void refreshPageItems() {
        setSlot(slotImpl((e) -> {
            variant.chance += switch (e.getClick()) {
                case SHIFT_LEFT -> .1;
                case LEFT -> -1;
                case SHIFT_RIGHT -> -.1;
                case RIGHT -> 1;
                default -> 0;
            };
        }, makeItem(Material.REDSTONE, 1,
            String.format("%.1f chance to decay into", variant.chance), List.of(
                "Typically between 1 and 10",
                "Normal Left: -1",
                "Shift Left: -0.1",
                "Normal Right: 1",
                "Shift Right: 0.1"
            ))), 12);
        setSlot(new ItemGuiSlotCycleACD<>(variant::getBlockSize, variant::setBlockSize), 14);
        setSlot(new ItemGuiSlotCycleACD<>(variant::getMaterialVariantType,
            variant::setMaterialVariantType), 17);

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
