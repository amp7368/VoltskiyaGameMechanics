package voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.gui;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import voltskiya.apple.game_mechanics.tmw.tmw_config.TMWGui;
import voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.BiomeType;
import voltskiya.apple.utilities.util.gui.InventoryGuiPageScrollable;
import voltskiya.apple.utilities.util.gui.InventoryGuiSlotGeneric;
import voltskiya.apple.utilities.util.gui.InventoryGuiSlotScrollable;
import voltskiya.apple.utilities.util.minecraft.InventoryUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class BiomeTypeGuiPageBlocks extends InventoryGuiPageScrollable {
    private final BiomeTypeGui biomeTypeGui;
    private final BiomeType.BiomeTypeBuilder biome;
    private final TMWGui callbackGui;

    public BiomeTypeGuiPageBlocks(BiomeTypeGui biomeTypeGui, BiomeType.BiomeTypeBuilder biome, TMWGui callbackGui) {
        super(biomeTypeGui);
        this.biomeTypeGui = biomeTypeGui;
        this.biome = biome;
        this.callbackGui = callbackGui;
        addBlocks();
        setSlots();
    }

    @Override
    public void fillInventory() {
        addBlocks();
        super.fillInventory();
    }

    private void addBlocks() {
        clear();
        final Map<Material, Double> materials = biome.getMaterials();
        System.out.println(materials);
        if (materials == null) return;
        System.out.println(materials.size());
        List<Map.Entry<Material, Double>> blockTypes = new ArrayList<>(materials.entrySet());
        blockTypes.sort((o1, o2) -> (int) (o2.getValue() - o1.getValue()));
        for (Map.Entry<Material, Double> blockType : blockTypes) {
            add(new BlockTypeSlot(blockType));
        }
    }

    @Override
    public void setSlots() {
        super.setSlots();
        setSlot(new InventoryGuiSlotGeneric((e1) -> biomeTypeGui.nextPage(-1),
                InventoryUtils.makeItem(Material.GREEN_TERRACOTTA, 1, "Previous Page", null)), 0);
        setSlot(new InventoryGuiSlotGeneric((e1) -> biomeTypeGui.nextPage(1), InventoryUtils.makeItem(Material.GREEN_TERRACOTTA, 1, "Next Page", null)
        ), 8);
    }

    @Override
    public String getName() {
        return "Biome Blocks";
    }

    @Override
    public int size() {
        return 54;
    }

    @Override
    protected int getScrollIncrement() {
        return 8;
    }

    private class BlockTypeSlot extends InventoryGuiSlotScrollable {
        private final Map.Entry<Material, Double> blockType;

        public BlockTypeSlot(Map.Entry<Material, Double> blockType) {
            this.blockType = blockType;
        }

        @Override
        public void dealWithClick(InventoryClickEvent event) {
        }

        @Override
        public ItemStack getItem() {
            if (blockType.getKey().isItem()) {
                return InventoryUtils.makeItem(blockType.getKey(), 1, (String) null, Collections.singletonList(String.format("%.2f%%", blockType.getValue() * 100)));
            } else {                // there is no item of that type
                return InventoryUtils.makeItem(Material.BLACK_CONCRETE, 1, blockType.getKey().name(), Collections.singletonList(String.format("%.2f%%", blockType.getValue() * 100)));
            }
        }
    }
}
