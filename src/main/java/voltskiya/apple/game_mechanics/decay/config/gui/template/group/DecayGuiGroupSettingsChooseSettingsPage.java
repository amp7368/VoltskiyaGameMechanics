package voltskiya.apple.game_mechanics.decay.config.gui.template.group;

import apple.mc.utilities.inventory.gui.acd.page.InventoryGuiPageScrollableACD;
import apple.mc.utilities.inventory.gui.acd.page.ScrollableSectionACD;
import java.util.List;
import org.bukkit.Material;
import voltskiya.apple.game_mechanics.decay.config.database.DecayBlockSettingsDatabase;
import voltskiya.apple.game_mechanics.decay.config.gui.DecayGui;
import voltskiya.apple.game_mechanics.decay.config.template.DecayBlockTemplateGrouping;
import voltskiya.apple.game_mechanics.decay.config.template.DecayBlockTemplateGroupingSettings;

public class DecayGuiGroupSettingsChooseSettingsPage extends
    InventoryGuiPageScrollableACD<DecayGui> {

    private final DecayBlockTemplateGrouping builder;

    public DecayGuiGroupSettingsChooseSettingsPage(DecayGui parent,
        DecayBlockTemplateGrouping builder) {
        super(parent, false);
        this.builder = builder;
    }

    @Override
    public void initialize() {
        addSection(new ScrollableSectionACD("", 18, size()));
        setSlot(slotDoNothing(
                makeItem(Material.OAK_SAPLING, 1, "To make a new settings",
                    List.of("Go to the home navigation and choose settings"))),
            4);
        setSlot(slotImpl(e -> parentRemoveSubPage(),
            makeItem(Material.RED_TERRACOTTA, "Discard choice")), 0);
        setSlot(blackGlassDoNothing(), 9, 10, 11, 12, 13, 14, 15);
        this.setSlot(slotImpl((e) -> {
            this.getSection("").scroll(-1);
        }, makeItem(Material.REDSTONE_TORCH, 1, "Up", List.of("Scroll up"))), 17);
        this.setSlot(slotImpl((e) -> {
            this.getSection("").scroll(1);
        }, makeItem(Material.LEVER, 1, "Down", List.of("Scroll down"))), 16);
    }

    @Override
    public void refreshPageItems() {
        clear();
        for (DecayBlockTemplateGroupingSettings settings : DecayBlockSettingsDatabase.getAll()) {
            this.add(slotImpl((e) -> {
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
