package voltskiya.apple.game_mechanics.decay.config.gui;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import voltskiya.apple.game_mechanics.decay.config.gui.variant.DecayGuiVariantSettingsPage;
import voltskiya.apple.game_mechanics.decay.config.template.DecayBlockTemplate;
import voltskiya.apple.game_mechanics.decay.config.template.DecayBlockTemplateGrouping;
import voltskiya.apple.game_mechanics.decay.config.template.MaterialVariant;
import voltskiya.apple.utilities.util.gui.acd.page.InventoryGuiPageScrollableACD;
import voltskiya.apple.utilities.util.gui.acd.page.ScrollableSectionACD;
import voltskiya.apple.utilities.util.gui.acd.slot.InventoryGuiButtonTemplate;
import voltskiya.apple.utilities.util.gui.acd.slot.InventoryGuiSlotDoNothingACD;
import voltskiya.apple.utilities.util.gui.acd.slot.InventoryGuiSlotImplACD;
import voltskiya.apple.utilities.util.minecraft.InventoryUtils;

import java.util.List;

import static voltskiya.apple.utilities.util.minecraft.InventoryUtils.makeItem;

public class DecayGuiBlockSettingsPage extends InventoryGuiPageScrollableACD<DecayGui> {
    private final DecayBlockTemplateGrouping parentGrouping;
    private final DecayBlockTemplate.DecayBlockBuilderTemplate builder;

    public DecayGuiBlockSettingsPage(DecayGui decayGui, DecayBlockTemplateGrouping parentGrouping, DecayBlockTemplate.DecayBlockBuilderTemplate builder) {
        super(decayGui, false);
        this.parentGrouping = parentGrouping;
        this.builder = builder;
    }

    @Override
    public void initialize() {
        addSection(new ScrollableSectionACD("variants", 18, size()));
        setSlot(new InventoryGuiSlotImplACD(
                e -> parentRemoveSubPage(),
                makeItem(Material.RED_TERRACOTTA, "Discard Changes")
        ), 0);
        setSlot(new InventoryGuiSlotDoNothingACD(makeItem(builder.icon, "Block")
        ), 4);
        setSlot(new InventoryGuiSlotImplACD(
                e -> {
                    parentGrouping.addBlock(builder.build());
                    parentRemoveSubPage();
                },
                makeItem(Material.GREEN_TERRACOTTA, "Save Changes")
        ), 8);
        this.setSlot(new InventoryGuiSlotImplACD((e) -> {
            this.getSection("variants").scroll(-1);
        }, InventoryUtils.makeItem(Material.REDSTONE_TORCH, 1, "Up", List.of("Scroll up"))), 17);
        this.setSlot(new InventoryGuiSlotImplACD((e) -> {
            this.getSection("variants").scroll(1);
        }, InventoryUtils.makeItem(Material.LEVER, 1, "Down", List.of("Scroll down"))), 16);
        setSlot(InventoryGuiButtonTemplate.blackGlassDoNothing(), 9, 10, 11, 12, 13, 14, 15);
    }

    @Override
    public void refreshPageItems() {
        addVarients();
    }

    private void addVarients() {
        getSection("variants").clear();
        for (MaterialVariant variant : builder.decayIntoThis.values()) {
            add("variants", new InventoryGuiSlotImplACD(e -> {
                parentAddSubPage(new DecayGuiVariantSettingsPage(parent, builder, variant.copy()));
            }, makeItem(variant.material)
            ));
        }
    }


    @Override
    public void onPlayerInventory(InventoryClickEvent event) {
        ItemStack currentItem = event.getCurrentItem();
        if (currentItem != null && currentItem.getType().isBlock()) {
            MaterialVariant variant = builder.decayIntoThis.get(currentItem.getType());
            if (variant == null) variant = new MaterialVariant(currentItem);
            parentAddSubPage(new DecayGuiVariantSettingsPage(parent, builder, variant));
        }
    }

    @Override
    public String getName() {
        return "Block Settings";
    }

    @Override
    public int size() {
        return 54;
    }
}
