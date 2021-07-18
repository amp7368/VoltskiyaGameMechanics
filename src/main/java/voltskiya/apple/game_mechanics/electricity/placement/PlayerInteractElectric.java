package voltskiya.apple.game_mechanics.electricity.placement;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import voltskiya.apple.game_mechanics.VoltskiyaPlugin;
import voltskiya.apple.utilities.util.minecraft.MaterialUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerInteractElectric implements Listener {
    public static final NamespacedKey WRENCH_KEY = new NamespacedKey(VoltskiyaPlugin.get(), "wrench");
    private final Map<UUID, LivingEntity> attachments = new HashMap<>();

    public PlayerInteractElectric() {
        Bukkit.getPluginManager().registerEvents(this, VoltskiyaPlugin.get());
    }

    @EventHandler(ignoreCancelled = true)
    public void interact(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (event.getHand() == EquipmentSlot.HAND && action == Action.RIGHT_CLICK_BLOCK) {
            @Nullable ItemStack item = event.getItem();
            if (item != null) {
                @NotNull PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
                if (container.has(WRENCH_KEY, PersistentDataType.BYTE)) {
                    Block clickedBlock = event.getClickedBlock();
                    if (clickedBlock != null && MaterialUtils.isFence(clickedBlock.getType())) {
                        ElectricityInteractablesAll.dealWith(clickedBlock.getLocation());
                        Player player = event.getPlayer();
                        LivingEntity attatchment = attachments.get(player.getUniqueId());
                        if (attatchment == null) {
                            player.getWorld().spawnEntity(clickedBlock.getLocation().add(0.5, 0, 0.5), EntityType.CHICKEN, CreatureSpawnEvent.SpawnReason.CUSTOM, e -> {
                                Rabbit chicken = (Rabbit) e;
                                chicken.setAI(false);
                                chicken.setGravity(false);
                                chicken.setAdult();
                                chicken.setInvulnerable(true);
                                chicken.setSilent(true);
                                chicken.setPersistent(true);
                                chicken.setLeashHolder(player);
                                chicken.setAgeLock(true);
                                chicken.setInvisible(true);
                                attachments.put(player.getUniqueId(), chicken);
                            });
                        } else {
                            player.getWorld().spawnEntity(clickedBlock.getLocation().add(0.5, 0, 0.5), EntityType.LEASH_HITCH, CreatureSpawnEvent.SpawnReason.CUSTOM, e -> {
                                LeashHitch chicken = (LeashHitch) e;
                                chicken.setGravity(false);
                                chicken.setInvulnerable(true);
                                chicken.setSilent(true);
                                chicken.setPersistent(true);
                                attatchment.setLeashHolder(e);
                            });
                            attachments.remove(player.getUniqueId());
                        }
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
}
