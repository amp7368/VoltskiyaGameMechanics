package voltskiya.apple.game_mechanics.decay.config.gui.template.group;

import org.bukkit.Material;
import voltskiya.apple.game_mechanics.decay.config.database.DecayBlockSettingsDatabase;
import voltskiya.apple.game_mechanics.decay.config.gui.DecayGui;
import voltskiya.apple.game_mechanics.decay.config.template.DecayBlockTemplateGrouping;
import voltskiya.apple.game_mechanics.decay.config.template.DecayBlockTemplateGroupingSettings;
import voltskiya.apple.utilities.util.gui.acd.page.InventoryGuiPageScrollableACD;
import voltskiya.apple.utilities.util.gui.acd.page.ScrollableSectionACD;
import voltskiya.apple.utilities.util.gui.acd.slot.InventoryGuiButtonTemplate;
import voltskiya.apple.utilities.util.gui.acd.slot.InventoryGuiSlotDoNothingACD;
import voltskiya.apple.utilities.util.gui.acd.slot.InventoryGuiSlotImplACD;
import voltskiya.apple.utilities.util.minecraft.InventoryUtils;

import java.util.List;

public class DecayGuiGroupSettingsChooseSettingsPage extends InventoryGuiPageScrollableACD<DecayGui> {
    private DecayBlockTemplateGrouping builder;

    public DecayGuiGroupSettingsChooseSettingsPage(DecayGui parent, DecayBlockTemplateGrouping builder) {
        super(parent, false);
        this.builder = builder;
    }

    @Override
    public void initialize() {
        addSection(new ScrollableSectionACD("", 18, size()));
        setSlot(new InventoryGuiSlotDoNothingACD(
                        InventoryUtils.makeItem(Material.OAK_SAPLING, 1, "To make a new settings", List.of("Go to the home navigation and choose settings"))),
                4);
        setSlot(new InventoryGuiSlotImplACD(e -> parentRemoveSubPage(),
                InventoryUtils.makeItem(Material.RED_TERRACOTTA, "Discard choice")), 0);
        setSlot(InventoryGuiButtonTemplate.blackGlassDoNothing(), 9, 10, 11, 12, 13, 14, 15);
        this.setSlot(new InventoryGuiSlotImplACD((e) -> {
            this.getSection("").scroll(-1);
        }, InventoryUtils.makeItem(Material.REDSTONE_TORCH, 1, "Up", List.of("Scroll up"))), 17);
        this.setSlot(new InventoryGuiSlotImplACD((e) -> {
            this.getSection("").scroll(1);
        }, InventoryUtils.makeItem(Material.LEVER, 1, "Down", List.of("Scroll down"))), 16);
    }

    @Override
    public void refreshPageItems() {
        clear();
        for (DecayBlockTemplateGroupingSettings settings : DecayBlockSettingsDatabase.getAll()) {
            this.add(new InventoryGuiSlotImplACD((e) -> {
                builder.setSettings(settings);
                parentRemoveSubPage();
            }, settings.getIcon()));
        }
    }

    @Override
    public String getName() {
        return "Choose settings";
    }

    @Override
    public int size() {
        return 54;
    }
}
