package voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.gui;

import apple.mc.utilities.inventory.gui.acd.page.InventoryGuiPageScrollableACD;
import apple.mc.utilities.inventory.gui.acd.slot.base.ItemGuiSlotACD;
import apple.mc.utilities.inventory.item.InventoryUtils;
import apple.nms.decoding.world.DecodeBiome;
import java.util.Comparator;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import voltskiya.apple.game_mechanics.tmw.tmw_config.TMWGui;
import voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.BiomeType;
import voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.BiomeType.BiomeTypeBuilder;
import voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.BiomeTypeDatabase;

public class BiomeTypeGuiPageBiomes extends InventoryGuiPageScrollableACD<TMWGui> {

    private final BiomeType.BiomeTypeBuilder biome;

    public BiomeTypeGuiPageBiomes(TMWGui parent, BiomeTypeBuilder biome) {
        super(parent);
        this.biome = biome;
        addMinecraftBiomes();
    }

    @Override
    public void refreshPageItems() {
        addMinecraftBiomes();
    }

    private void addMinecraftBiomes() {
        clear();
        final List<ResourceLocation> biomes = biome.getMinecraftBiomes();
        biomes.sort(
            Comparator.comparing(ResourceLocation::toString, String.CASE_INSENSITIVE_ORDER));
        for (ResourceLocation minecraftBiome : biomes) {
            add(new MinecraftBiomeSlot(minecraftBiome));
        }
    }

    @Override
    public void initialize() {

        setSlot(slotImpl((e1) -> parentPrev(),
            makeItem(Material.GREEN_TERRACOTTA, 1, "Previous Page", null)), 0);
        setSlot(slotImpl((e1) -> parentNext(),
            makeItem(Material.GREEN_TERRACOTTA, 1, "Next Page", null)), 8);
        setSlot(new AddBiomeSlot(), 4);
    }

    @Override
    public String getName() {
        return "Biome Blocks";
    }

    @Override
    public int size() {
        return 54;
    }

    private class MinecraftBiomeSlot implements ItemGuiSlotACD {

        private final ResourceLocation minecraftBiome;

        public MinecraftBiomeSlot(ResourceLocation minecraftBiome) {
            this.minecraftBiome = minecraftBiome;
        }

        @Override
        public void dealWithClick(InventoryClickEvent event) {
            BiomeTypeDatabase.get().removeMapping(minecraftBiome);
            refresh();
        }

        @Override
        public ItemStack getItem() {
            return InventoryUtils.get().makeItem(Material.VINE, 1, minecraftBiome.toString(),
                List.of("Click to remove mapping"));
        }
    }

    private class AddBiomeSlot implements ItemGuiSlotACD {

        private int clicks = 0;
        private String lastBiome = null;

        @Override
        public void dealWithClick(InventoryClickEvent event) {
            Location location = event.getWhoClicked().getLocation();
            ResourceLocation biomeKey = DecodeBiome.getBiomeKey(location.getWorld(),
                location.getBlockX(), location.getBlockY(), location.getBlockZ()).location();
            addBiome(biomeKey);
        }

        private void addBiome(ResourceLocation minecraft) {
            if (minecraft == null) {
                return;
            }
            boolean shouldAddMapping = clicks >= 1;
            if (!shouldAddMapping) {
                @Nullable BiomeType biome = BiomeTypeDatabase.getBiome(minecraft);
                if (biome == null) {
                    shouldAddMapping = true;
                } else {
                    lastBiome = biome.getName();
                }
                clicks++;
            }
            if (shouldAddMapping) {
                clicks = 0;
                BiomeTypeDatabase.get().addBiomeMapping(minecraft, biome.getName());
                refresh();
            }
        }

        @Override
        public ItemStack getItem() {
            if (clicks == 0) {
                return makeItem(Material.DARK_OAK_SAPLING, "Add current biome");
            }
            return makeItem(Material.DARK_OAK_SAPLING, 1, "Are you sure?",
                List.of("The biome you just tried to add is",
                    String.format("already mapped to %s", lastBiome)));
        }
    }
}
