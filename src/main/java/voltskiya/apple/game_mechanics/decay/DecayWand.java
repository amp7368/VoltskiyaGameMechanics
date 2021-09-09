package voltskiya.apple.game_mechanics.decay;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.RayTraceResult;
import org.jetbrains.annotations.Nullable;
import voltskiya.apple.game_mechanics.VoltskiyaPlugin;
import voltskiya.apple.game_mechanics.decay.storage.DecayBlock;
import voltskiya.apple.game_mechanics.decay.world.DecayErosionType;
import voltskiya.apple.utilities.util.wand.WandPlayer;
import voltskiya.apple.utilities.util.wand.WandUse;


public class DecayWand implements WandPlayer {
    public static final NamespacedKey WAND_KEY = new NamespacedKey(VoltskiyaPlugin.get(), "decay_wand");
    private int radius = 20;
    private int force = 10;

    public DecayWand(Player player) {
    }

    @Override
    @WandUse(action = {Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK})
    public void dealWithUse(PlayerInteractEvent event, String wandItemMetaz) {
        Player player = event.getPlayer();
        Location location = player.getLocation();
        World world = player.getWorld();

        //todo choose the type of decay to do
        DecayErosionType decayErosionType = DecayErosionType.EXPLOSION;
        @Nullable RayTraceResult raytrace = world.rayTraceBlocks(location, location.getDirection(), 200, FluidCollisionMode.ALWAYS);
        if (raytrace != null) {
            Block hitBlock = raytrace.getHitBlock();
            if (hitBlock != null) {
                int blocksLength = this.radius * 2 + 1;
                DecayBlock[][][] blocksToDecay = new DecayBlock[blocksLength][blocksLength][blocksLength];
                Location hitLocation = hitBlock.getLocation();
                for (int xi = -this.radius; xi <= this.radius; xi++) {
                    for (int yi = -this.radius; yi <= this.radius; yi++) {
                        for (int zi = -this.radius; zi <= this.radius; zi++) {
                            int x = hitLocation.getBlockX() + xi;
                            int y = hitLocation.getBlockY() + yi;
                            int z = hitLocation.getBlockZ() + zi;
                            Block block = world.getBlockAt(z, y, z);
                            Material material = block.getType();
                            blocksToDecay[xi + this.radius][yi + this.radius][zi + this.radius] = new DecayBlock(material, material, x, y, z, world.getUID());
                        }
                    }
                }
                float[][][] damage = decayErosionType.doDecay(blocksToDecay, force);
                for (int xi = -this.radius; xi <= this.radius; xi++) {
                    for (int yi = -this.radius; yi <= this.radius; yi++) {
                        for (int zi = -this.radius; zi <= this.radius; zi++) {
                            DecayBlock block = blocksToDecay[xi + this.radius][yi + this.radius][zi + this.radius];
                            if (block.getOldMaterial() != block.getCurrentMaterial()) {
                                int x = hitLocation.getBlockX() + xi;
                                int y = hitLocation.getBlockY() + yi;
                                int z = hitLocation.getBlockZ() + zi;
                                Block irlBlock = world.getBlockAt(x, y, z);
                                irlBlock.setType(block.getCurrentMaterial());
                            }
                        }
                    }
                }
            }
        }
    }

    public void setSize(int size) {
        // + 2 because I want to include an outer-rim
        // that will not be impacted, just for context to the explosion
        this.radius = size + 2;
    }

    public void setForce(int force) {
        this.force = force;
    }
}
