package voltskiya.apple.game_mechanics.tmw.tmw_world.mobs;

import apple.nms.decoding.entity.DecodeEntity;
import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.jetbrains.annotations.NotNull;
import voltskiya.apple.game_mechanics.VoltskiyaPlugin;
import voltskiya.apple.game_mechanics.tmw.sql.MobSqlStorage;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MobListener implements Listener {

    public static final String DESPAWN_AT_TIME = "despawnAtTime.";

    public MobListener() {
        Bukkit.getPluginManager().registerEvents(this, VoltskiyaPlugin.get());
    }

    @EventHandler
    public void unloadChunk(ChunkUnloadEvent event) {
        @NotNull Entity[] entitiesInChunk = event.getChunk().getEntities();
        List<MobSqlStorage.StoredMob> mobsToSave = new ArrayList<>();
        for (Entity entity : entitiesInChunk) {
            for (String tag : entity.getScoreboardTags()) {
                if (tag.startsWith(DESPAWN_AT_TIME) && tag.length() > DESPAWN_AT_TIME.length()) {
                    long despawnAtTime;
                    try {
                        despawnAtTime = Long.parseLong(tag.substring(DESPAWN_AT_TIME.length()));
                    } catch (NumberFormatException e) {
                        continue;
                    }
                    Location location = entity.getLocation();
                    int x = location.getBlockX();
                    int y = location.getBlockY();
                    int z = location.getBlockZ();
                    NBTTagCompound entityNbt = DecodeEntity.saveWithId(((CraftEntity) entity).getHandle());
                    try {
                        mobsToSave.add(new MobSqlStorage.StoredMob(x, y, z, location.getWorld().getUID(), entityNbt, despawnAtTime));
                        entity.remove();
                        System.out.println("de3spawned");
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        MobSqlStorage.insertMob(mobsToSave);
    }
}