package voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.gui;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;
import voltskiya.apple.game_mechanics.tmw.tmw_config.TMWGui;
import voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.BiomeType;
import voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.BiomeTypeDatabase;
import voltskiya.apple.utilities.util.gui.InventoryGui;
import voltskiya.apple.utilities.util.gui.InventoryGuiPageSimple;
import voltskiya.apple.utilities.util.gui.InventoryGuiSlotGeneric;
import voltskiya.apple.utilities.util.minecraft.InventoryUtils;

import java.util.*;

public class BiomeTypeGuiPageSettings extends InventoryGuiPageSimple {
    private final BiomeTypeGui biomeTypeGui;
    private final BiomeType.BiomeTypeBuilder biome;
    private final TMWGui callbackGui;

    BiomeTypeGuiPageSettings(BiomeTypeGui biomeTypeGui, BiomeType.BiomeTypeBuilder biome, TMWGui callbackGui) {
        super(biomeTypeGui);
        this.biomeTypeGui = biomeTypeGui;
        this.biome = biome;
        this.callbackGui = callbackGui;
        setSlots();
    }

    private void setSlots() {
        setSlot(new BiomeNameSlot(), 0);
        setSlot(new SaveSlot(), 4);
        setSlot(new InventoryGuiSlotGeneric((e) -> biomeTypeGui.nextPage(1), InventoryUtils.makeItem(Material.GREEN_TERRACOTTA, 1, "Next Page", null)
        ), 8);
        setSlot(new SpawnRateSlot(), 45);
        setSlot(new RegisterTimeTempSlot(BiomeType.TemperatureTime.MORNING), 9);
        setSlot(new RegisterTimeTempSlot(BiomeType.TemperatureTime.NOON), 10);
        setSlot(new RegisterTimeTempSlot(BiomeType.TemperatureTime.EVENING), 11);
        setSlot(new RegisterTimeTempSlot(BiomeType.TemperatureTime.MIDNIGHT), 12);
        setSlot(new RegisterWindSlot(), 18);
        setSlot(new DeleteSlot(), 2);
        setSlot(new InventoryGuiSlotGeneric((e1) -> e1.getWhoClicked().openInventory(callbackGui.getInventory()), InventoryUtils.makeItem(Material.LIGHT_BLUE_TERRACOTTA, 1, "Back", null)
        ), 6);
    }

    @Override
    public void fillInventory() {
        setSlots();
        super.fillInventory();
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
                setSlot(new InventoryGuiSlotGeneric(e -> biomeTypeGui.setTempInventory(null),
                        InventoryUtils.makeItem(Material.GREEN_TERRACOTTA, 1, "Save", null)), 8);
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

                biome.setIcon(new BiomeType.BiomeTypeBuilder.BiomeIcon(name, material, lore));
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

    private class SpawnRateSlot implements InventoryGui.InventoryGuiSlot {
        @Override
        public void dealWithClick(InventoryClickEvent event) {
            if (event.getClick().isLeftClick())
                biome.incrementSpawnRate(1);
            else
                biome.incrementSpawnRate(-1);
            update();
        }

        @Override
        public ItemStack getItem() {
            return InventoryUtils.makeItem(Material.GHAST_TEAR, 1, "Spawn Rate", Collections.singletonList(String.valueOf(biome.getSpawnRate())));
        }
    }

    private class RegisterTimeTempSlot implements InventoryGui.InventoryGuiSlot {
        private final BiomeType.TemperatureTime time;

        public RegisterTimeTempSlot(BiomeType.TemperatureTime time) {
            this.time = time;
        }

        @Override
        public void dealWithClick(InventoryClickEvent inventoryClickEvent) {
            biomeTypeGui.setTempInventory(new TimeTempBuilderGuiPage(biomeTypeGui, biome.getDailyTemperatures().get(time)));
        }

        @Override
        public ItemStack getItem() {
            HashMap<BiomeType.TemperatureTime, BiomeType.TemperatureInfo> temperatures = biome.getDailyTemperatures();
            BiomeType.TemperatureInfo temperatureInfo = temperatures.get(time);
            return InventoryUtils.makeItem(
                    Material.YELLOW_STAINED_GLASS_PANE,
                    1,
                    "Temperature " + time.name().toLowerCase(Locale.ROOT),
                    Collections.singletonList(
                            temperatureInfo == null ? "???" : String.format("%d degrees", temperatureInfo.degrees)
                    )
            );
        }

        private class TimeTempBuilderGuiPage extends InventoryGuiPageSimple {
            private final BiomeType.TemperatureInfo temperatureInfo;

            public TimeTempBuilderGuiPage(BiomeTypeGui holder, BiomeType.TemperatureInfo temperatureInfo) {
                super(holder);
                this.temperatureInfo = temperatureInfo;
                setSlot(new InventoryGuiSlotGeneric(e -> {
                    if (e.getClick().isLeftClick()) temperatureInfo.degrees += 1;
                    else temperatureInfo.degrees += 5;
                    holder.update(null);
                }, InventoryUtils.makeItem(
                        Material.REDSTONE, 1, "Temperature increase", Arrays.asList(
                                "Left click - increase temperature by 1",
                                "Right click - increase temperature by 5"
                        )
                )), 1);
                setSlot(new InventoryGuiSlotGeneric(e -> {
                    if (e.getClick().isLeftClick()) temperatureInfo.degrees -= 1;
                    else temperatureInfo.degrees -= 5;
                    holder.update(null);
                }, InventoryUtils.makeItem(
                        Material.GUNPOWDER, 1, "Temperature decrease", Arrays.asList(
                                "Left click - decrease temperature by 1",
                                "Right click - decrease temperature by 5"
                        )
                )), 2);
                setSlot(new InventoryGuiSlotGeneric(e -> {
                    holder.setTempInventory(null);
                    holder.update(null);
                }, InventoryUtils.makeItem(Material.GREEN_TERRACOTTA, 1, "Save", null)), 8);
                setSlots();
            }

            private void setSlots() {
                setSlot(new InventoryGuiSlotGeneric(e -> {
                }, () -> InventoryUtils.makeItem(Material.YELLOW_STAINED_GLASS_PANE,
                        1,
                        "Temperature",
                        Collections.singletonList(
                                String.format("%d degrees", temperatureInfo.degrees)
                        )
                )), 0);
            }

            @Override
            public void fillInventory() {
                super.fillInventory();
                setSlots();
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

    private class RegisterWindSlot implements InventoryGui.InventoryGuiSlot {

        public RegisterWindSlot() {
        }

        @Override
        public void dealWithClick(InventoryClickEvent inventoryClickEvent) {
            biomeTypeGui.setTempInventory(new WindGuiPage());
        }

        @Override
        public ItemStack getItem() {
            return InventoryUtils.makeItem(Material.BLACK_STAINED_GLASS_PANE, 1, "Wind", Arrays.asList(
                    String.format("%d kph (min)", biome.getWind().kphMin), String.format("%d kph (max)", biome.getWind().kphMax)
            ));
        }

        private class WindGuiPage extends InventoryGuiPageSimple {
            final BiomeType.WindInfo wind = biome.getWind();

            public WindGuiPage() {
                super(biomeTypeGui);
                setSlot(new InventoryGuiSlotGeneric(e -> {
                    if (e.getClick().isLeftClick()) wind.kphMin += 1;
                    else wind.kphMin += 5;
                    fixWindMinMax();
                }, InventoryUtils.makeItem(
                        Material.REDSTONE, 1, "Wind increase (min)", Arrays.asList(
                                "Left click - increase kph by 1",
                                "Right click - increase kph by 5"
                        )
                )), 1);
                setSlot(new InventoryGuiSlotGeneric(e -> {
                    if (e.getClick().isLeftClick()) wind.kphMax += 1;
                    else wind.kphMax += 5;
                    fixWindMinMax();
                }, InventoryUtils.makeItem(
                        Material.REDSTONE, 1, "Wind increase (max)", Arrays.asList(
                                "Left click - increase kph by 1",
                                "Right click - increase kph by 5"
                        )
                )), 2);


                setSlot(new InventoryGuiSlotGeneric(e -> {
                    if (e.getClick().isLeftClick()) wind.kphMin -= 1;
                    else wind.kphMin -= 5;
                    fixWindMinMax();
                }, InventoryUtils.makeItem(
                        Material.GUNPOWDER, 1, "Wind decrease (min)", Arrays.asList(
                                "Left click - decrease kph by 1",
                                "Right click - decrease kph by 5"
                        )
                )), 3);
                setSlot(new InventoryGuiSlotGeneric(e -> {
                    if (e.getClick().isLeftClick()) wind.kphMax -= 1;
                    else wind.kphMax -= 5;
                    fixWindMinMax();
                }, InventoryUtils.makeItem(
                        Material.GUNPOWDER, 1, "Wind decrease (max)", Arrays.asList(
                                "Left click - decrease kph by 1",
                                "Right click - decrease kph by 5"
                        )
                )), 4);


                setSlot(new InventoryGuiSlotGeneric(e -> {
                    biomeTypeGui.setTempInventory(null);
                    biomeTypeGui.update(null);
                }, InventoryUtils.makeItem(Material.GREEN_TERRACOTTA, 1, "Save", null)), 8);
                setSlots();
            }

            private void fixWindMinMax() {
                if (wind.kphMin > wind.kphMax) {
                    int temp = wind.kphMax;
                    wind.kphMax = wind.kphMin;
                    wind.kphMin = temp;
                }
                wind.kphMin = Math.max(0, wind.kphMin);
                wind.kphMax = Math.max(0, wind.kphMax);
                update();
            }

            private void setSlots() {
                setSlot(new InventoryGuiSlotGeneric(e -> {
                }, InventoryUtils.makeItem(Material.BLACK_STAINED_GLASS_PANE, 1, "Wind", Arrays.asList(
                        String.format("%d kph (min)", wind.kphMin), String.format("%d kph (max)", wind.kphMax)
                ))
                ), 0);
            }

            @Override
            public void fillInventory() {
                super.fillInventory();
                setSlots();
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

    private class DeleteSlot implements InventoryGui.InventoryGuiSlot {
        private int deleteCount = 5;

        @Override
        public void dealWithClick(InventoryClickEvent event) {
            if (deleteCount-- == 0) {
                BiomeTypeDatabase.removeBiome(biome);
                callbackGui.update(null);
                event.getWhoClicked().openInventory(callbackGui.getInventory());
            }
        }

        @Override
        public ItemStack getItem() {
            return InventoryUtils.makeItem(Material.RED_TERRACOTTA, 1, "CLICK 5 TIMES TO DELETE", List.of(
                    "Note: to simply not save changes,", "just exit your inventory."
            ));
        }
    }
}
