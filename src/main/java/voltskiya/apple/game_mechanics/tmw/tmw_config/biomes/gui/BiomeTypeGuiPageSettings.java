package voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.gui;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;
import voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.BiomeTypeDatabase;
import voltskiya.apple.game_mechanics.util.gui.InventoryGui;
import voltskiya.apple.game_mechanics.util.gui.InventoryGuiPageSimple;
import voltskiya.apple.game_mechanics.util.gui.InventoryGuiSlotGeneric;
import voltskiya.apple.game_mechanics.util.minecraft.InventoryUtils;

import java.util.Collections;
import java.util.List;

public class BiomeTypeGuiPageSettings extends InventoryGuiPageSimple {
    private final BiomeTypeGui biomeTypeGui;
    private final BiomeTypeBuilder biome;
    private final InventoryGui callbackGui;

    public BiomeTypeGuiPageSettings(BiomeTypeGui biomeTypeGui, BiomeTypeBuilder biome, InventoryGui callbackGui) {
        super(biomeTypeGui);
        this.biomeTypeGui = biomeTypeGui;
        this.biome = biome;
        this.callbackGui = callbackGui;

        setSlot(new BiomeNameSlot(), 0);
        setSlot(new SaveSlot(), 4);
        setSlot(new InventoryGuiSlotGeneric((e) -> biomeTypeGui.nextPage(1), InventoryUtils.makeItem(Material.GREEN_TERRACOTTA, 1, "Next Page", null)
        ), 8);
        setSlot(new HighestYSlot(), 17);
        setSlot(new HeightVarianceSlot(), 25);
        setSlot(new TypicalYSlot(), 26);
        setSlot(new LowestYSlot(), 35);
    }

    @Override
    public String getName() {
        return "Biome Settings";
    }

    @Override
    public int size() {
        return 54;
    }


    private class BiomeNameSlot implements InventoryGui.InventoryGuiSlot {
        @Override
        public void dealWithClick(InventoryClickEvent event) {
            biomeTypeGui.setTempInventory(new BiomeTypeGuiPageSettings.BiomeNameSlot.BiomeNamePage());
        }

        @Override
        public ItemStack getItem() {
            return biome.getIconItem();
        }

        private class BiomeNamePage extends InventoryGuiPageSimple {
            public BiomeNamePage() {
                super(biomeTypeGui);
                setSlot(new InventoryGuiSlotGeneric(e -> {
                }, biome.getIconItem()), 0);
                setSlot(new InventoryGuiSlotGeneric(e -> {
                    biomeTypeGui.setTempInventory(null);
                }, new ItemStack(Material.GREEN_TERRACOTTA)), 8);
            }

            @Override
            public String getName() {
                return "Insert item representation";
            }

            @Override
            public int size() {
                return 9;
            }

            @Override
            public void dealWithPlayerInventoryClick(InventoryClickEvent event) {
                @Nullable ItemStack item = event.getCurrentItem();
                if (item == null) return;
                // the material of the ItemStack
                Material material = item.getType();
                ItemMeta im = item.getItemMeta();

                // get the name and lore of the item
                String name = im.getDisplayName();
                List<String> lore = im.getLore();

                biome.setIcon(new BiomeTypeBuilder.BiomeIcon(name, material, lore));

                setSlot(new InventoryGuiSlotGeneric(e -> {
                }, biome.getIconItem()), 0);
                update();
            }
        }
    }

    private class SaveSlot implements InventoryGui.InventoryGuiSlot {
        @Override
        public void dealWithClick(InventoryClickEvent event) {
            BiomeTypeDatabase.addBiome(biome.build());
            callbackGui.update(null);
            event.getWhoClicked().openInventory(callbackGui.getInventory());
        }

        @Override
        public ItemStack getItem() {
            return InventoryUtils.makeItem(Material.LIME_CONCRETE, 1, "Save", Collections.singletonList("list of things needed before saving"));
        }
    }

    private class HighestYSlot implements InventoryGui.InventoryGuiSlot {
        @Override
        public void dealWithClick(InventoryClickEvent event) {
        }

        @Override
        public ItemStack getItem() {
            final int highestY = biome.getHighestY();
            String name = highestY < 0 ? "not set" : String.valueOf(highestY);
            return InventoryUtils.makeItem(Material.GRASS_BLOCK, 1, "Highest Y", Collections.singletonList(name));
        }
    }

    private class LowestYSlot implements InventoryGui.InventoryGuiSlot {
        @Override
        public void dealWithClick(InventoryClickEvent event) {
        }

        @Override
        public ItemStack getItem() {
            final int lowestY = biome.getLowestY();
            String name = lowestY < 0 ? "not set" : String.valueOf(lowestY);
            return InventoryUtils.makeItem(Material.BEDROCK, 1, "Lowest Y", Collections.singletonList(name));
        }
    }

    private class HeightVarianceSlot implements InventoryGui.InventoryGuiSlot {
        @Override
        public void dealWithClick(InventoryClickEvent event) {
        }

        @Override
        public ItemStack getItem() {
            final double heightVariance = biome.getHeightVariance();
            String name = heightVariance < 0 ? "not set" : String.format("%.2f", heightVariance);
            return InventoryUtils.makeItem(Material.GRANITE, 1, "Height Variance", Collections.singletonList(name));
        }
    }

    private class TypicalYSlot implements InventoryGui.InventoryGuiSlot {
        @Override
        public void dealWithClick(InventoryClickEvent event) {
        }

        @Override
        public ItemStack getItem() {
            final double typicalY = biome.getTypicalY();
            String name = typicalY < 0 ? "not set" : String.format("%.2f", typicalY);
            return InventoryUtils.makeItem(Material.STONE, 1, "Typical Y", Collections.singletonList(name));
        }
    }
}
