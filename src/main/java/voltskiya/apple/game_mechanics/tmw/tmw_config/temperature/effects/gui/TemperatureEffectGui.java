package voltskiya.apple.game_mechanics.tmw.tmw_config.temperature.effects.gui;

import apple.mc.utilities.inventory.gui.acd.page.InventoryGuiPageImplACD;
import apple.mc.utilities.inventory.gui.acd.page.InventoryGuiPageScrollableACD;
import apple.mc.utilities.inventory.gui.acd.slot.base.ItemGuiSlotACD;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.DoubleSupplier;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import voltskiya.apple.game_mechanics.tmw.tmw_config.TMWGui;
import voltskiya.apple.game_mechanics.tmw.tmw_config.temperature.effects.TemperatureEffect;
import voltskiya.apple.game_mechanics.tmw.tmw_config.temperature.effects.TemperatureEffectsDatabase;

public class TemperatureEffectGui extends InventoryGuiPageImplACD<TMWGui> {

    private final TemperatureEffect.TemperatureEffectBuilder temperatureEffect;

    public TemperatureEffectGui(TMWGui parent,
        TemperatureEffect.TemperatureEffectBuilder temperatureEffect) {
        super(parent);
        this.temperatureEffect = temperatureEffect;
        setSlot(slotImpl(e -> {
            TemperatureEffectsDatabase.addEffect(temperatureEffect.build());
            parentRemoveSubPage();
        }, makeItem(Material.LIME_TERRACOTTA, 1, "Save", null)), 4);
        setSlot(slotImpl((e1) -> parentRemoveSubPage(),
            makeItem(Material.LIGHT_BLUE_TERRACOTTA, 1, "Back", null)), 6);
    }

    @Override
    public void refreshPageItems() {
        setSlot(new PotionEffectTypeSlot(), 13);
        setSlot(new DisplayIncrementSlot(temperatureEffect::getTemperatureStart,
            temperatureEffect::incrementTemperatureStart, 1, 5, Material.MELON_SEEDS,
            "Temperature Start"), 9);
        setSlot(
            new DisplayIncrementSlot(temperatureEffect::getLevel, temperatureEffect::incrementLevel,
                1, 5, Material.BEETROOT_SEEDS, "Potion Level"), 10);
        setSlot(new DisplayIncrementSlot(temperatureEffect::getInterval,
                temperatureEffect::incrementInterval, 1, 5, Material.WHEAT_SEEDS, "Effect Interval"),
            11);
        setSlot(new DisplayIncrementSlot(temperatureEffect::getLasting,
                temperatureEffect::incrementLasting, 1, 5, Material.PUMPKIN_SEEDS, "Effect Lasting"),
            12);
    }


    @Override
    public String getName() {
        return "Temperature Effects Settings";
    }

    @Override
    public int size() {
        return 54;
    }

    private class DisplayIncrementSlot implements ItemGuiSlotACD {

        private final DoubleSupplier supplier;
        private final Consumer<Integer> increment;
        private final int normal;
        private final int shift;
        private final Material material;
        private final String name;

        public DisplayIncrementSlot(DoubleSupplier supplier, Consumer<Integer> increment,
            int normal, int shift, Material material, String name) {
            this.supplier = supplier;
            this.increment = increment;
            this.normal = normal;
            this.shift = shift;
            this.material = material;
            this.name = name;
        }

        @Override
        public void dealWithClick(InventoryClickEvent inventoryClickEvent) {
            ClickType click = inventoryClickEvent.getClick();
            if (click.isLeftClick()) {
                if (click.isShiftClick()) {
                    increment.accept(shift);
                } else {
                    increment.accept(normal);
                }
            } else {
                if (click.isShiftClick()) {
                    increment.accept(-shift);
                } else {
                    increment.accept(-normal);
                }
            }
        }

        @Override
        public ItemStack getItem() {
            return makeItem(material, 1,
                String.format("%s - %.1f", name, this.supplier.getAsDouble()),
                Arrays.asList(String.format("Left Click - increment by %d", 1),
                    String.format("Left Shift Click - increment by %d", 5),
                    String.format("Right Click - decrement by %d", -1),
                    String.format("Right Shift Click - decrement by %d", -5)));
        }
    }

    private class PotionEffectTypeSlot implements ItemGuiSlotACD {

        @Override
        public void dealWithClick(InventoryClickEvent inventoryClickEvent) {
            parent.parentAddSubPage(new PotionEffectGui(parent));
        }

        @Override
        public ItemStack getItem() {
            PotionEffectType potionEffectType = temperatureEffect.getPotionEffectType();
            return makeItem(Material.POTION, 1,
                potionEffectType == null ? "not set" : potionEffectType.getName(), null);
        }
    }

    private class PotionEffectGui extends InventoryGuiPageScrollableACD<TMWGui> {

        public PotionEffectGui(TMWGui parent) {
            super(parent);
        }

        @Override
        public void refreshPageItems() {
            List<PotionEffectType> effectTypes = new ArrayList<>(
                Arrays.asList(PotionEffectType.values()));
            effectTypes.sort(Comparator.comparing(PotionEffectType::getName));
            for (PotionEffectType potionType : effectTypes) {
                add(slotImpl(e -> {
                    temperatureEffect.setPotionType(potionType);
                    parentRemoveSubPage();
                }, makeItem(Material.POTION, 1, potionType.getName(), null)));
            }
        }

        @Override
        public String getName() {
            return "Potions Types";
        }

        @Override
        public int size() {
            return 54;
        }
    }
}
