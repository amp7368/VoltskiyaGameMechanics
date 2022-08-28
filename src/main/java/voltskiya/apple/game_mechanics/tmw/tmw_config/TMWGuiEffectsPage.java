package voltskiya.apple.game_mechanics.tmw.tmw_config;

import apple.mc.utilities.inventory.gui.acd.page.InventoryGuiPageScrollableACD;
import org.bukkit.Material;
import voltskiya.apple.game_mechanics.tmw.tmw_config.temperature.effects.TemperatureEffect;
import voltskiya.apple.game_mechanics.tmw.tmw_config.temperature.effects.TemperatureEffectsDatabase;
import voltskiya.apple.game_mechanics.tmw.tmw_config.temperature.effects.gui.TemperatureEffectGui;

public class TMWGuiEffectsPage extends InventoryGuiPageScrollableACD<TMWGui> {


    public TMWGuiEffectsPage(TMWGui parent) {
        super(parent);
    }

    @Override
    public void refreshPageItems() {
        addEffects();
        setSlot(slotImpl((e1) -> parentNext(),
            makeItem(Material.GREEN_TERRACOTTA, 1, "Next Page", null)), 8);
        setSlot(slotImpl(e -> parentAddSubPage(
                new TemperatureEffectGui(parent, new TemperatureEffect.TemperatureEffectBuilder())),
            makeItem(Material.DARK_OAK_SAPLING, 1, "Add an effect", null)), 4);
    }

    private void addEffects() {
        clear();
        for (TemperatureEffect effect : TemperatureEffectsDatabase.getAll()) {
            add(slotImpl(
                e -> parentAddSubPage(new TemperatureEffectGui(parent, effect.toBuilder())),
                effect.toItem()));
        }
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
