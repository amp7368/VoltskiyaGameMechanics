package voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.gui;

import apple.nms.decoding.world.DecodeBiome;
import net.minecraft.resources.MinecraftKey;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import voltskiya.apple.game_mechanics.tmw.tmw_config.TMWGui;
import voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.BiomeType;
import voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.BiomeTypeDatabase;
import voltskiya.apple.utilities.util.gui.InventoryGui;
import voltskiya.apple.utilities.util.gui.InventoryGuiPageScrollable;
import voltskiya.apple.utilities.util.gui.InventoryGuiSlotGeneric;
import voltskiya.apple.utilities.util.gui.InventoryGuiSlotScrollable;
import voltskiya.apple.utilities.util.minecraft.InventoryUtils;

import java.util.Comparator;
import java.util.List;

public class BiomeTypeGuiPageBiomes extends InventoryGuiPageScrollable {
    private final BiomeTypeGui biomeTypeGui;
    private final BiomeType.BiomeTypeBuilder biome;

    public BiomeTypeGuiPageBiomes(BiomeTypeGui biomeTypeGui, BiomeType.BiomeTypeBuilder biome, TMWGui callbackGui) {
        super(biomeTypeGui);
        this.biomeTypeGui = biomeTypeGui;
        this.biome = biome;
        addMinecraftBiomes();
        setSlots();
    }

    @Override
    public void fillInventory() {
        addMinecraftBiomes();
        super.fillInventory();
    }

    private void addMinecraftBiomes() {
        clear();
        final List<MinecraftKey> biomes = biome.getMinecraftBiomes();
        biomes.sort(Comparator.comparing(MinecraftKey::toString, String.CASE_INSENSITIVE_ORDER));
        for (MinecraftKey minecraftBiome : biomes) {
            add(new MinecraftBiomeSlot(minecraftBiome));
        }
    }

    @Override
    public void setSlots() {
        super.setSlots();
        setSlot(new InventoryGuiSlotGeneric((e1) -> biomeTypeGui.nextPage(-1),
                InventoryUtils.makeItem(Material.GREEN_TERRACOTTA, 1, "Previous Page", null)), 0);
        setSlot(new InventoryGuiSlotGeneric((e1) -> biomeTypeGui.nextPage(1), InventoryUtils.makeItem(Material.GREEN_TERRACOTTA, 1, "Next Page", null)
        ), 8);
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

    @Override
    protected int getScrollIncrement() {
        return 8;
    }

    private class MinecraftBiomeSlot extends InventoryGuiSlotScrollable {
        private final MinecraftKey minecraftBiome;

        public MinecraftBiomeSlot(MinecraftKey minecraftBiome) {
            this.minecraftBiome = minecraftBiome;
        }

        @Override
        public void dealWithClick(InventoryClickEvent event) {
            BiomeTypeDatabase.get().removeMapping(minecraftBiome);
            setSlots();
            update();
        }

        @Override
        public ItemStack getItem() {
            return InventoryUtils.makeItem(Material.VINE, 1, minecraftBiome.toString(), List.of("Click to remove mapping"));
        }
    }

    private class AddBiomeSlot implements InventoryGui.InventoryGuiSlot {
        private int clicks = 0;
        private String lastBiome = null;

        @Override
        public void dealWithClick(InventoryClickEvent event) {
            Location location = event.getWhoClicked().getLocation();
            @Nullable MinecraftKey minecraft = DecodeBiome.getBiomeKeyAt(location.getWorld(), location.getX(), location.getY(), location.getZ());
            if (minecraft == null) return;
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
                update();
            }
        }

        @Override
        public ItemStack getItem() {
            if (clicks == 0) {
                return InventoryUtils.makeItem(Material.DARK_OAK_SAPLING, "Add current biome");
            }
            return InventoryUtils.makeItem(Material.DARK_OAK_SAPLING,
                    1, "Are you sure?",
                    List.of(
                            "The biome you just tried to add is",
                            String.format("already mapped to %s", lastBiome)
                    )
            );
        }
    }
}
