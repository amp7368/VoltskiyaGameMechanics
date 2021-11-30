package voltskiya.apple.game_mechanics.decay.config.gui.settings;

import org.bukkit.Material;
import voltskiya.apple.game_mechanics.decay.config.database.DecayBlockSettingsDatabase;
import voltskiya.apple.game_mechanics.decay.config.gui.DecayGui;
import voltskiya.apple.game_mechanics.decay.config.template.DecayBlockTemplateGroupingSettings;
import voltskiya.apple.utilities.util.gui.acd.page.InventoryGuiPageImplACD;
import voltskiya.apple.utilities.util.gui.acd.slot.InventoryGuiButtonTemplate;
import voltskiya.apple.utilities.util.gui.acd.slot.InventoryGuiSlotImplACD;

import java.util.Collections;

import static voltskiya.apple.utilities.util.minecraft.InventoryUtils.makeItem;

public class DecayGuiBlockSettingsPage extends InventoryGuiPageImplACD<DecayGui> {
    private DecayBlockTemplateGroupingSettings settings;

    public DecayGuiBlockSettingsPage(DecayGui parent, DecayBlockTemplateGroupingSettings settings) {
        super(parent);
        this.settings = settings;
    }

    @Override
    public void initialize() {
        setSlot(new InventoryGuiSlotImplACD(e -> parentRemoveSubPage(), makeItem(Material.RED_TERRACOTTA, "Discard Changes")), 0);
        setSlot(new InventoryGuiSlotImplACD(e -> {
            parentRemoveSubPage();
        }, makeItem(Material.RED_TERRACOTTA, "Discard Changes")), 0);
        setSlot(new InventoryGuiSlotImplACD(e -> {
            DecayBlockSettingsDatabase.add(settings);
            parentRemoveSubPage();
        }, makeItem(Material.GREEN_TERRACOTTA, "Save Changes")), 8);
    }

    @Override
    public void refreshPageItems() {
        settings();
    }

    public void settings() {
        setSlot(new InventoryGuiSlotImplACD(e -> {
            settings.incrementDurability(e.getClick().isLeftClick() ? 1 : -1);
        }, InventoryGuiButtonTemplate.configNamed(Material.BRICKS, "Durability",
                Collections.singletonList("How much durability does this item have before it breaks"),
                settings::getDurability, 1)
        ), 3);

        setSlot(new InventoryGuiSlotImplACD(e -> {
            settings.incrementResistance(e.getClick().isLeftClick() ? 1 : -1);
        }, InventoryGuiButtonTemplate.configNamed(Material.TNT, "Resistance",
                Collections.singletonList("How much resistance does this item have against decay"),
                settings::getResistance, 1)
        ), 2);
    }

    @Override
    public String getName() {
        return "Settings for blocks";
    }

    @Override
    public int size() {
        return 27;
    }
}
