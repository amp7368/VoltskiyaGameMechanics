package voltskiya.apple.game_mechanics.tmw.tmw_config;

import apple.mc.utilities.inventory.gui.acd.InventoryGuiACD;
import org.bukkit.entity.Player;

public class TMWGui extends InventoryGuiACD {

    private final Player player;

    public TMWGui(Player player) {
        this.player = player;
        addPage(
            new TMWGuiMobsPage(this),
            new TMWGuiBiomesPage(this),
            new TMWGuiWeatherPage(this),
            new TMWGuiBlocksPage(this),
            new TMWGuiClothingPage(this),
            new TMWGuiEffectsPage(this)
        );
    }

    public Player getPlayer() {
        return player;
    }
}
