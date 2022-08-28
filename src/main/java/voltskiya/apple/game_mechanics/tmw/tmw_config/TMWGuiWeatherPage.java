package voltskiya.apple.game_mechanics.tmw.tmw_config;

import apple.mc.utilities.inventory.gui.acd.page.InventoryGuiPageScrollableACD;
import org.bukkit.Material;

public class TMWGuiWeatherPage extends InventoryGuiPageScrollableACD<TMWGui> {
    public TMWGuiWeatherPage(TMWGui tmwGui) {
        super(tmwGui);
    }

    private void addWeathers() {
        clear();
    }

    @Override
    public void initialize() {
        
        setSlot(slotImpl((e1) -> parentPrev(), backItem()), 0);
        setSlot(slotImpl((e1) -> parentNext(),
            makeItem(Material.GREEN_TERRACOTTA, 1, "Next Page", null)), 8);
    }

    @Override
    public void refreshPageItems() {
        addWeathers();
    }

    @Override
    public String getName() {
        return "All Weather Events";
    }

    @Override
    public int size() {
        return 54;
    }
}
