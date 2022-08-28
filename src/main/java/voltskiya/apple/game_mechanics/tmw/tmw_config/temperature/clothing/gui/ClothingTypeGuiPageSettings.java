package voltskiya.apple.game_mechanics.tmw.tmw_config.temperature.clothing.gui;

import apple.mc.utilities.inventory.gui.acd.page.InventoryGuiPageImplACD;
import apple.mc.utilities.inventory.gui.acd.slot.base.ItemGuiSlotACD;
import apple.mc.utilities.item.material.ArmorType;
import apple.mc.utilities.item.material.MaterialUtils;
import java.util.Arrays;
import java.util.Collections;
import java.util.function.Consumer;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import voltskiya.apple.game_mechanics.tmw.tmw_config.TMWGui;
import voltskiya.apple.game_mechanics.tmw.tmw_config.temperature.clothing.ClothingDatabase;
import voltskiya.apple.game_mechanics.tmw.tmw_config.temperature.clothing.ClothingType;

public class ClothingTypeGuiPageSettings extends InventoryGuiPageImplACD<TMWGui> {

    private final ClothingType.ClothingTypeBuilder clothing;

    public ClothingTypeGuiPageSettings(TMWGui parent, ClothingType.ClothingTypeBuilder clothing) {
        super(parent);
        this.clothing = clothing;
        setSlot(slotImpl(e -> {
            if (clothing.canBuild()) {
                ClothingDatabase.addClothing(clothing.build());
                parentRemoveSubPage();
            }
        }, makeItem(Material.GREEN_TERRACOTTA, 1, "Save", null)), 4);
        setSlot(slotImpl((e1) -> parentRemoveSubPage(),
            makeItem(Material.LIGHT_BLUE_TERRACOTTA, 1, "Back", null)), 6);

        setSlot(new IncrementSlot(clothing::incrementWindProtection, .1, 1, Material.REDSTONE,
            "Increase"), 10);
        setSlot(new IncrementSlot(clothing::incrementWindProtection, -.1, -1, Material.GUNPOWDER,
            "Decrease"), 11);
        setSlot(new IncrementSlot(clothing::incrementWetProtection, .1, 1, Material.REDSTONE,
            "Increase"), 19);
        setSlot(new IncrementSlot(clothing::incrementWetProtection, -.1, -1, Material.GUNPOWDER,
            "Decrease"), 20);
        setSlot(new IncrementSlot(clothing::incrementColdResistance, .1, 1, Material.REDSTONE,
            "Increase"), 28);
        setSlot(new IncrementSlot(clothing::incrementColdResistance, -.1, -1, Material.GUNPOWDER,
            "Decrease"), 29);
        setSlot(new IncrementSlot(clothing::incrementHeatResistance, .1, 1, Material.REDSTONE,
            "Increase"), 37);
        setSlot(new IncrementSlot(clothing::incrementHeatResistance, -.1, -1, Material.GUNPOWDER,
            "Decrease"), 38);
    }

    @Override
    public void refreshPageItems() {
        setSlot(slotImpl(e -> {
        }, clothing.toItem()), 0);
        setSlot(slotImpl(e -> {
        }, makeItem(Material.BLACK_STAINED_GLASS_PANE, 1, "Wind protection",
            Collections.singletonList(String.valueOf(this.clothing.getWindProtection())))), 9);
        setSlot(slotImpl(e -> {
        }, makeItem(Material.WET_SPONGE, 1, "Wet protection",
            Collections.singletonList(String.valueOf(this.clothing.getWetProtection())))), 18);
        setSlot(slotImpl(e -> {
        }, makeItem(Material.CAMPFIRE, 1, "Cold resistance",
            Collections.singletonList(String.valueOf(this.clothing.getColdResistance())))), 27);
        setSlot(slotImpl(e -> {
        }, makeItem(Material.SOUL_CAMPFIRE, 1, "Heat resistance",
            Collections.singletonList(String.valueOf(this.clothing.getHeatResistance())))), 36);
    }

    @Override
    public String getName() {
        return "Clothing Type";
    }

    @Override
    public void onPlayerInventory(InventoryClickEvent event) {
        final ItemStack currentItem = event.getCurrentItem();
        if (currentItem != null) {
            ArmorType armorType = MaterialUtils.getArmorType(currentItem.getType());
            if (armorType != null) {
                clothing.setIcon(currentItem, armorType);
            }
        }
    }

    @Override
    public int size() {
        return 54;
    }

    private class IncrementSlot implements ItemGuiSlotACD {

        private final Consumer<Double> increment;
        private final double left;
        private final double right;
        private final Material material;
        private final String name;

        public IncrementSlot(Consumer<Double> increment, double left, double right,
            Material material, String name) {
            this.increment = increment;
            this.left = left;
            this.right = right;
            this.material = material;
            this.name = name;
        }

        @Override
        public void dealWithClick(InventoryClickEvent event) {
            if (event.getClick().isLeftClick()) {
                increment.accept(left);
            } else {
                increment.accept(right);
            }
            refresh();
        }

        @Override
        public ItemStack getItem() {
            return makeItem(material, 1, name,
                Arrays.asList(String.format("Left click - %s by %.1f", name, left),
                    String.format("Right click - %s by %.1f", name, right)));
        }
    }
}
