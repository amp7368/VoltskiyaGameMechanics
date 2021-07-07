package voltskiya.apple.game_mechanics.tmw.tmw_config;

import org.bukkit.Material;
import voltskiya.apple.game_mechanics.tmw.tmw_config.temperature.effects.TemperatureEffect;
import voltskiya.apple.game_mechanics.tmw.tmw_config.temperature.effects.TemperatureEffectsDatabase;
import voltskiya.apple.game_mechanics.tmw.tmw_config.temperature.effects.gui.TemperatureEffectGui;
import voltskiya.apple.utilities.util.gui.InventoryGuiPageScrollable;
import voltskiya.apple.utilities.util.gui.InventoryGuiSlotGeneric;
import voltskiya.apple.utilities.util.gui.InventoryGuiSlotGenericScrollable;
import voltskiya.apple.utilities.util.minecraft.InventoryUtils;

public class TMWGuiEffectsPage extends InventoryGuiPageScrollable {
    private TMWGui tmwGui;

    public TMWGuiEffectsPage(TMWGui tmwGui) {
        super(tmwGui);
        this.tmwGui = tmwGui;
        addEffects();
        setSlots();
    }

    private void addEffects() {
        clear();
        for (TemperatureEffect effect : TemperatureEffectsDatabase.getAll()) {
            add(new InventoryGuiSlotGenericScrollable(e -> {
                tmwGui.setTempInventory(new TemperatureEffectGui(tmwGui, effect.toBuilder()));
            }, effect.toItem()));
        }
    }

    @Override
    public void setSlots() {
        super.setSlots();
        setSlot(new InventoryGuiSlotGeneric((e1) -> tmwGui.nextPage(1), InventoryUtils.makeItem(Material.GREEN_TERRACOTTA, 1, "Next Page", null)
        ), 8);
        setSlot(new InventoryGuiSlotGeneric(e -> tmwGui.setTempInventory(new TemperatureEffectGui(tmwGui, new TemperatureEffect.TemperatureEffectBuilder())),
                InventoryUtils.makeItem(Material.DARK_OAK_SAPLING, 1, "Add an effect", null)), 4);
    }

    @Override
    public void fillInventory() {
        addEffects();
        super.fillInventory();
    }

    @Override
    protected int getScrollIncrement() {
        return 8;
    }

    @Override
    public String getName() {
        return "Temperature Effects";
    }

    @Override
    public int size() {
        return 54;
    }
}
