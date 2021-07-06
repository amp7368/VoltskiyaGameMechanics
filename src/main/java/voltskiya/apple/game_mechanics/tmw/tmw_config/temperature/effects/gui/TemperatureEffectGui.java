package voltskiya.apple.game_mechanics.tmw.tmw_config.temperature.effects.gui;

import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import voltskiya.apple.game_mechanics.tmw.tmw_config.TMWGui;
import voltskiya.apple.game_mechanics.tmw.tmw_config.temperature.effects.TemperatureEffect;
import voltskiya.apple.game_mechanics.tmw.tmw_config.temperature.effects.TemperatureEffectsDatabase;
import voltskiya.apple.utilities.util.gui.*;
import voltskiya.apple.utilities.util.minecraft.InventoryUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.DoubleSupplier;

public class TemperatureEffectGui extends InventoryGuiPageSimple {
    private final TemperatureEffect.TemperatureEffectBuilder temperatureEffect;
    private TMWGui tmwGui;

    public TemperatureEffectGui(TMWGui tmwGui, TemperatureEffect.TemperatureEffectBuilder temperatureEffect) {
        super(tmwGui);
        this.tmwGui = tmwGui;
        this.temperatureEffect = temperatureEffect;
        setSlot(new InventoryGuiSlotGeneric(e -> {
            TemperatureEffectsDatabase.addEffect(temperatureEffect.build());
            tmwGui.setTempInventory(null);
        }, InventoryUtils.makeItem(Material.LIME_TERRACOTTA, 1, "Save", null)), 4);
        setSlots();
    }

    private void setSlots() {
        setSlot(new PotionEffectTypeSlot(), 13);
        setSlot(new DisplayIncrementSlot(
                temperatureEffect::getTemperatureStart,
                temperatureEffect::incrementTemperatureStart,
                1, 5, Material.MELON_SEEDS, "Temperature Start"
        ), 9);
        setSlot(new DisplayIncrementSlot(
                temperatureEffect::getLevel,
                temperatureEffect::incrementLevel,
                1, 5, Material.BEETROOT_SEEDS, "Potion Level"
        ), 10);
        setSlot(new DisplayIncrementSlot(
                temperatureEffect::getInterval,
                temperatureEffect::incrementInterval,
                1, 5, Material.WHEAT_SEEDS, "Effect Interval"
        ), 11);
        setSlot(new DisplayIncrementSlot(
                temperatureEffect::getLasting,
                temperatureEffect::incrementLasting,
                1, 5, Material.PUMPKIN_SEEDS, "Effect Lasting"
        ), 12);
    }

    @Override
    public void fillInventory() {
        super.fillInventory();
        setSlots();
    }

    @Override
    public String getName() {
        return "Temperature Effects Settings";
    }

    @Override
    public int size() {
        return 54;
    }

    private class DisplayIncrementSlot implements InventoryGui.InventoryGuiSlot {
        private final DoubleSupplier supplier;
        private final Consumer<Integer> increment;
        private final int normal;
        private final int shift;
        private final Material material;
        private final String name;

        public DisplayIncrementSlot(DoubleSupplier supplier, Consumer<Integer> increment, int normal, int shift, Material material, String name) {
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
            update();
        }

        @Override
        public ItemStack getItem() {
            return InventoryUtils.makeItem(material, 1, String.format("%s - %.1f", name, this.supplier.getAsDouble()),
                    Arrays.asList(
                            String.format("Left Click - increment by %d", 1),
                            String.format("Left Shift Click - increment by %d", 5),
                            String.format("Right Click - decrement by %d", -1),
                            String.format("Right Shift Click - decrement by %d", -5)
                    )
            );
        }
    }

    private class PotionEffectTypeSlot implements InventoryGui.InventoryGuiSlot {
        @Override
        public void dealWithClick(InventoryClickEvent inventoryClickEvent) {
            tmwGui.setTempInventory(new PotionEffectGui());
        }

        @Override
        public ItemStack getItem() {
            PotionEffectType potionEffectType = temperatureEffect.getPotionEffectType();
            return InventoryUtils.makeItem(Material.POTION, 1, potionEffectType == null ? "not set" : potionEffectType.getName(), null);
        }
    }

    private class PotionEffectGui extends InventoryGuiPageScrollable {
        public PotionEffectGui() {
            super(tmwGui);
            addPotions();
            setSlots();
        }

        private void addPotions() {
            List<PotionEffectType> effectTypes = new ArrayList<>(Arrays.asList(PotionEffectType.values()));
            effectTypes.sort(Comparator.comparing(PotionEffectType::getName));
            for (PotionEffectType potionType : effectTypes) {
                add(new InventoryGuiSlotGenericScrollable(e -> {
                    temperatureEffect.setPotionType(potionType);
                    tmwGui.setTempInventory(TemperatureEffectGui.this);
                }, InventoryUtils.makeItem(Material.POTION, 1, potionType.getName(), null)));
            }
        }

        @Override
        protected int getScrollIncrement() {
            return 8;
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
