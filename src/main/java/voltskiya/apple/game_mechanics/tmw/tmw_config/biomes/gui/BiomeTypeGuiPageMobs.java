package voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.gui;

import apple.mc.utilities.inventory.gui.acd.page.InventoryGuiPageScrollableACD;
import apple.mc.utilities.inventory.gui.acd.slot.base.ItemGuiSlotACD;
import java.util.Arrays;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import voltskiya.apple.game_mechanics.tmw.tmw_config.TMWGui;
import voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.BiomeType;
import voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.BiomeType.BiomeTypeBuilder;
import voltskiya.apple.game_mechanics.tmw.tmw_config.mobs.MobType;
import voltskiya.apple.game_mechanics.tmw.tmw_config.mobs.gui.MobTypeGui;

public class BiomeTypeGuiPageMobs extends InventoryGuiPageScrollableACD<TMWGui> {

    private final BiomeType.BiomeTypeBuilder biome;

    public BiomeTypeGuiPageMobs(TMWGui parent, BiomeTypeBuilder biome) {
        super(parent);
        this.biome = biome;
    }

    @Override
    public void refreshPageItems() {
        clear();
        for (MobType mob : biome.getMobs().keySet()) {
            add(new MobTypeBiomeSlot(mob));
        }
        setSlot(slotImpl((e) -> {
            parentAddSubPage(new MobPageGet(parent, biome));
        }, makeItem(Material.DARK_OAK_SAPLING, 1, "Add a mob", null)), 4);
        setSlot(slotImpl((e1) -> parentPrev(),
            makeItem(Material.GREEN_TERRACOTTA, 1, "Previous Page", null)), 0);
        setSlot(slotImpl((e1) -> parentNext(),
            makeItem(Material.GREEN_TERRACOTTA, 1, "Next Page", null)), 8);
    }

    @Override
    public String getName() {
        return "Biome Mobs";
    }

    @Override
    public int size() {
        return 54;
    }

    public boolean containsMob(MobType mob) {
        return this.biome.getMob(mob) != null;
    }

    private class MobTypeBiomeSlot implements ItemGuiSlotACD {

        private final MobType mob;

        public MobTypeBiomeSlot(MobType mob) {
            this.mob = mob;
        }

        @Override
        public void dealWithClick(InventoryClickEvent event) {
            ClickType click = event.getClick();
            if (click.isKeyboardClick()) {
                biome.removeMob(mob);
                refresh();
                return;
            }
            if (click.isShiftClick()) {
                if (click.isLeftClick()) {
                    biome.incrementMob(mob, 1);
                } else {
                    biome.incrementMob(mob, -1);
                }
                refresh();
            } else {
                parentAddSubPage(new MobTypeGui(parent, mob.toBuilder()));
            }
        }

        @Override
        public ItemStack getItem() {
            final ItemStack item = mob.toItem();
            item.setAmount(Math.max(1, biome.getMob(mob)));
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setLore(Arrays.asList("Shift left click - increment mob ratio",
                "Shift right click - decrement mob ratio", "Keyboard click - remove mob",
                "Normal click - access mob info"));
            item.setItemMeta(itemMeta);
            return item;
        }
    }
}
