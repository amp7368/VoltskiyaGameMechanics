package voltskiya.apple.game_mechanics.tmw.tmw_config;

import org.bukkit.entity.Player;
import voltskiya.apple.utilities.util.gui.InventoryGui;

public class TMWGui extends InventoryGui {
    private final Player player;

    public TMWGui(Player player) {
        this.player = player;
        addPage(
                new TMWGuiMobsPage(this),
                new TMWGuiBiomesPage(this),
                new TMWGuiWeatherPage(this)
        );
    }

    public Player getPlayer() {
        return player;
    }
}
