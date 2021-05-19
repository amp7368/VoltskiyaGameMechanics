package voltskiya.apple.game_mechanics.tmw.tmw_config;

import voltskiya.apple.game_mechanics.util.gui.InventoryGui;

public class TMWGui extends InventoryGui {
    public TMWGui() {
        addPage(
                new TMWGuiMobsPage(this),
                new TMWGuiBiomesPage(this),
                new TMWGuiWeatherPage(this)
        );
    }
}
