package voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.gui;

import apple.mc.utilities.inventory.gui.acd.page.InventoryGuiPageImplACD;
import apple.mc.utilities.inventory.gui.acd.slot.base.ItemGuiSlotACD;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;
import voltskiya.apple.game_mechanics.tmw.tmw_config.TMWGui;
import voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.BiomeType;
import voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.BiomeType.BiomeTypeBuilder;
import voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.BiomeType.TemperatureInfo;
import voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.BiomeTypeDatabase;

public class BiomeTypeGuiPageSettings extends InventoryGuiPageImplACD<TMWGui> {

    private final BiomeType.BiomeTypeBuilder biome;

    BiomeTypeGuiPageSettings(TMWGui parent, BiomeTypeBuilder biome) {
        super(parent);
        this.biome = biome;
    }

    @Override
    public void initialize() {
        setSlot(new BiomeNameSlot(), 0);
        setSlot(new SaveSlot(), 4);
        setSlot(slotImpl((e) -> parentNext(1),
            makeItem(Material.GREEN_TERRACOTTA, 1, "Next Page", null)), 8);
        setSlot(new SpawnRateSlot(), 45);
        setSlot(new RegisterTimeTempSlot(BiomeType.TemperatureTime.MORNING), 9);
        setSlot(new RegisterTimeTempSlot(BiomeType.TemperatureTime.NOON), 10);
        setSlot(new RegisterTimeTempSlot(BiomeType.TemperatureTime.EVENING), 11);
        setSlot(new RegisterTimeTempSlot(BiomeType.TemperatureTime.MIDNIGHT), 12);
        setSlot(new RegisterWindSlot(), 18);
        setSlot(new DeleteSlot(), 2);
        setSlot(slotImpl((e) -> this.parentPrev(), backItem()), 6);
    }

    @Override
    public String getName() {
        return "Biome Settings";
    }

    @Override
    public int size() {
        return 54;
    }


    private class BiomeNameSlot implements ItemGuiSlotACD {

        @Override
        public void dealWithClick(InventoryClickEvent event) {
            parentAddSubPage(new BiomeNamePage(BiomeTypeGuiPageSettings.this));
        }

        @Override
        public ItemStack getItem() {
            return biome.getIconItem();
        }

        private class BiomeNamePage extends InventoryGuiPageImplACD<BiomeTypeGuiPageSettings> {

            public BiomeNamePage(BiomeTypeGuiPageSettings parent) {
                super(parent);
                setSlot(slotImpl(e -> {
                }, biome.getIconItem()), 0);
                setSlot(slotImpl(e -> this.parent.parentRemoveSubPage(),
                    makeItem(Material.GREEN_TERRACOTTA, 1, "Save", null)), 8);
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
            public void onPlayerInventory(InventoryClickEvent event) {
                @Nullable ItemStack item = event.getCurrentItem();
                if (item == null) {
                    return;
                }
                // the material of the ItemStack
                Material material = item.getType();
                ItemMeta im = item.getItemMeta();

                // get the name and lore of the item
                String name = im.getDisplayName();
                List<String> lore = im.getLore();

                biome.setIcon(new BiomeType.BiomeTypeBuilder.BiomeIcon(name, material, lore));
                setSlot(slotImpl(e -> {
                }, biome.getIconItem()), 0);
                refresh();
            }
        }
    }

    private class SaveSlot implements ItemGuiSlotACD {

        @Override
        public void dealWithClick(InventoryClickEvent event) {
            BiomeTypeDatabase.addBiome(biome.build());
            parentRemoveSubPage();
        }

        @Override
        public ItemStack getItem() {
            return makeItem(Material.LIME_CONCRETE, 1, "Save",
                Collections.singletonList("list of things needed before saving"));
        }
    }

    private class SpawnRateSlot implements ItemGuiSlotACD {

        @Override
        public void dealWithClick(InventoryClickEvent event) {
            if (event.getClick().isLeftClick()) {
                biome.incrementSpawnRate(1);
            } else {
                biome.incrementSpawnRate(-1);
            }
            refresh();
        }

        @Override
        public ItemStack getItem() {
            return makeItem(Material.GHAST_TEAR, 1, "Spawn Rate",
                Collections.singletonList(String.valueOf(biome.getSpawnRate())));
        }
    }

    private class RegisterTimeTempSlot implements ItemGuiSlotACD {

        private final BiomeType.TemperatureTime time;

        public RegisterTimeTempSlot(BiomeType.TemperatureTime time) {
            this.time = time;
        }

        @Override
        public void dealWithClick(InventoryClickEvent inventoryClickEvent) {
            parentAddSubPage(new TimeTempBuilderGuiPage(BiomeTypeGuiPageSettings.this,
                biome.getDailyTemperatures().get(time)));
        }

        @Override
        public ItemStack getItem() {
            HashMap<BiomeType.TemperatureTime, BiomeType.TemperatureInfo> temperatures = biome.getDailyTemperatures();
            BiomeType.TemperatureInfo temperatureInfo = temperatures.get(time);
            return makeItem(Material.YELLOW_STAINED_GLASS_PANE, 1,
                "Temperature " + time.name().toLowerCase(Locale.ROOT), Collections.singletonList(
                    temperatureInfo == null ? "???"
                        : String.format("%d degrees", temperatureInfo.degrees)));
        }

        private class TimeTempBuilderGuiPage extends
            InventoryGuiPageImplACD<BiomeTypeGuiPageSettings> {

            private final BiomeType.TemperatureInfo temperatureInfo;

            public TimeTempBuilderGuiPage(BiomeTypeGuiPageSettings parent,
                TemperatureInfo temperatureInfo) {
                super(parent);
                this.temperatureInfo = temperatureInfo;

            }

            @Override
            public void initialize() {
                setSlot(slotImpl(e -> {
                    temperatureInfo.degrees += e.getClick().isLeftClick() ? 1 : 5;
                }, makeItem(Material.REDSTONE, 1, "Temperature increase",
                    Arrays.asList("Left click - increase temperature by 1",
                        "Right click - increase temperature by 5"))), 1);
                setSlot(slotImpl(e -> {
                    temperatureInfo.degrees -= e.getClick().isLeftClick() ? 1 : 5;
                }, makeItem(Material.GUNPOWDER, 1, "Temperature decrease",
                    Arrays.asList("Left click - decrease temperature by 1",
                        "Right click - decrease temperature by 5"))), 2);
                setSlot(slotImpl(e -> {
                    parent.parentRemoveSubPage();
                }, makeItem(Material.GREEN_TERRACOTTA, 1, "Save", null)), 8);
            }

            @Override
            public void refreshPageItems() {
                setSlot(slotImpl(e -> {
                }, makeItem(Material.YELLOW_STAINED_GLASS_PANE, 1, "Temperature",
                    Collections.singletonList(
                        String.format("%d degrees", temperatureInfo.degrees)))), 0);
                super.finalizePageItems();
            }

            @Override
            public String getName() {
                return "Temperature";
            }

            @Override
            public int size() {
                return 9;
            }
        }
    }

    private class RegisterWindSlot implements ItemGuiSlotACD {

        @Override
        public void dealWithClick(InventoryClickEvent inventoryClickEvent) {
            parentAddSubPage(new WindGuiPage(parent));
        }

        @Override
        public ItemStack getItem() {
            return makeItem(Material.BLACK_STAINED_GLASS_PANE, 1, "Wind",
                Arrays.asList(String.format("%d kph (min)", biome.getWind().kphMin),
                    String.format("%d kph (max)", biome.getWind().kphMax)));
        }

        private class WindGuiPage extends InventoryGuiPageImplACD<TMWGui> {

            final BiomeType.WindInfo wind = biome.getWind();

            public WindGuiPage(TMWGui parent) {
                super(parent);
            }

            private void fixWindMinMax() {
                if (wind.kphMin > wind.kphMax) {
                    int temp = wind.kphMax;
                    wind.kphMax = wind.kphMin;
                    wind.kphMin = temp;
                }
                wind.kphMin = Math.max(0, wind.kphMin);
                wind.kphMax = Math.max(0, wind.kphMax);
                refresh();
            }

            @Override
            public void initialize() {
                setSlot(slotImpl(e -> {
                    if (e.getClick().isLeftClick()) {
                        wind.kphMin += 1;
                    } else {
                        wind.kphMin += 5;
                    }
                    fixWindMinMax();
                }, makeItem(Material.REDSTONE, 1, "Wind increase (min)",
                    Arrays.asList("Left click - increase kph by 1",
                        "Right click - increase kph by 5"))), 1);
                setSlot(slotImpl(e -> {
                    if (e.getClick().isLeftClick()) {
                        wind.kphMax += 1;
                    } else {
                        wind.kphMax += 5;
                    }
                    fixWindMinMax();
                }, makeItem(Material.REDSTONE, 1, "Wind increase (max)",
                    Arrays.asList("Left click - increase kph by 1",
                        "Right click - increase kph by 5"))), 2);

                setSlot(slotImpl(e -> {
                    if (e.getClick().isLeftClick()) {
                        wind.kphMin -= 1;
                    } else {
                        wind.kphMin -= 5;
                    }
                    fixWindMinMax();
                }, makeItem(Material.GUNPOWDER, 1, "Wind decrease (min)",
                    Arrays.asList("Left click - decrease kph by 1",
                        "Right click - decrease kph by 5"))), 3);
                setSlot(slotImpl(e -> {
                    if (e.getClick().isLeftClick()) {
                        wind.kphMax -= 1;
                    } else {
                        wind.kphMax -= 5;
                    }
                    fixWindMinMax();
                }, makeItem(Material.GUNPOWDER, 1, "Wind decrease (max)",
                    Arrays.asList("Left click - decrease kph by 1",
                        "Right click - decrease kph by 5"))), 4);

                setSlot(slotImpl(e -> {
                    this.parentRemoveSubPage();
                }, makeItem(Material.GREEN_TERRACOTTA, 1, "Save", null)), 8);
                setSlot(slotImpl(e -> {
                }, makeItem(Material.BLACK_STAINED_GLASS_PANE, 1, "Wind",
                    Arrays.asList(String.format("%d kph (min)", wind.kphMin),
                        String.format("%d kph (max)", wind.kphMax)))), 0);
            }

            @Override
            public String getName() {
                return "Wind";
            }

            @Override
            public int size() {
                return 9;
            }
        }
    }

    private class DeleteSlot implements ItemGuiSlotACD {

        private int deleteCount = 5;

        @Override
        public void dealWithClick(InventoryClickEvent event) {
            if (deleteCount-- == 0) {
                BiomeTypeDatabase.removeBiome(biome);
                parentRemoveSubPage();
            }
        }

        @Override
        public ItemStack getItem() {
            return makeItem(Material.RED_TERRACOTTA, 1, "CLICK 5 TIMES TO DELETE",
                List.of("Note: to simply not save changes,", "just exit your inventory."));
        }
    }
}
