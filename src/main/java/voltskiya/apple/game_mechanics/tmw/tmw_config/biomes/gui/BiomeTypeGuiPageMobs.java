package voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.gui;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import voltskiya.apple.game_mechanics.tmw.tmw_config.TMWGui;
import voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.BiomeType;
import voltskiya.apple.game_mechanics.tmw.tmw_config.mobs.MobType;
import voltskiya.apple.game_mechanics.tmw.tmw_config.mobs.gui.MobTypeGui;
import voltskiya.apple.utilities.util.gui.InventoryGuiPageScrollable;
import voltskiya.apple.utilities.util.gui.InventoryGuiSlotGeneric;
import voltskiya.apple.utilities.util.gui.InventoryGuiSlotScrollable;
import voltskiya.apple.utilities.util.minecraft.InventoryUtils;

public class BiomeTypeGuiPageMobs extends InventoryGuiPageScrollable {
    private final BiomeTypeGui biomeTypeGui;
    private final BiomeType.BiomeTypeBuilder biome;
    private final TMWGui tmwGui;

    public BiomeTypeGuiPageMobs(BiomeTypeGui biomeTypeGui, BiomeType.BiomeTypeBuilder biome, TMWGui tmwGui) {
        super(biomeTypeGui);
        this.biomeTypeGui = biomeTypeGui;
        this.biome = biome;
        this.tmwGui = tmwGui;
        addMobs();
        setSlots();
    }

    private void addMobs() {
        for (MobType mob : biome.getMobs().keySet()) {
            add(new MobTypeBiomeSlot(mob));
        }
    }

    @Override
    public void setSlots() {
        setSlot(new InventoryGuiSlotGeneric((e) -> {
                    biomeTypeGui.setTempInventory(new MobPageGet(this, biomeTypeGui));
                }, InventoryUtils.makeItem(Material.DARK_OAK_SAPLING, 1, "Add a mob", null))
                , 4);
        setSlot(new InventoryGuiSlotGeneric((e1) -> biomeTypeGui.nextPage(-1),
                InventoryUtils.makeItem(Material.GREEN_TERRACOTTA, 1, "Previous Page", null)), 0);
        setSlot(new InventoryGuiSlotGeneric((e1) -> biomeTypeGui.nextPage(1), InventoryUtils.makeItem(Material.GREEN_TERRACOTTA, 1, "Next Page", null)
        ), 8);
        super.setSlots();
    }

    @Override
    public void fillInventory() {
        clear();
        addMobs();
        super.fillInventory();
    }

    @Override
    public String getName() {
        return "Biome Mobs";
    }

    @Override
    public int size() {
        return 54;
    }

    @Override
    protected int getScrollIncrement() {
        return 8;
    }

    public void addMob(MobType mob) {
        this.biome.addMob(mob);
        update();
    }

    public boolean containsMob(MobType mob) {
        return this.biome.getMob(mob) != null;
    }

    private class MobTypeBiomeSlot extends InventoryGuiSlotScrollable {
        private final MobType mob;

        public MobTypeBiomeSlot(MobType mob) {
            this.mob = mob;
        }

        @Override
        public void dealWithClick(InventoryClickEvent event) {
            if (event.getClick().isShiftClick()) {
                if (event.getClick().isLeftClick()) {
                    biome.incrementMob(mob, 1);
                } else {
                    biome.incrementMob(mob, -1);
                }
                update();
            } else {
                tmwGui.getPlayer().openInventory(new MobTypeGui(tmwGui, mob.toBuilder()).getInventory());
            }
        }

        @Override
        public ItemStack getItem() {
            final ItemStack item = mob.toItem();
            item.setAmount(biome.getMob(mob));
            return item;
        }
    }
}
