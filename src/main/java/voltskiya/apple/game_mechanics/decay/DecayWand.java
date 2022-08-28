package voltskiya.apple.game_mechanics.decay;

import apple.mc.utilities.player.wand.Wand;
import apple.mc.utilities.world.vector.VectorUtils;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.Nullable;
import voltskiya.apple.game_mechanics.VoltskiyaPlugin;
import voltskiya.apple.game_mechanics.decay.storage.DecayBlock;
import voltskiya.apple.game_mechanics.decay.world.DecayErosionType;


public class DecayWand extends Wand {

    public static final NamespacedKey WAND_KEY = new NamespacedKey(VoltskiyaPlugin.get(),
        "decay_wand");
    private int radius = 10;
    private int force = 40;

    public DecayWand(Player player) {
        super(player);
    }


    @Override
    public void onEvent(PlayerInteractEvent event) {
        if (!actionIsLeft(event.getAction())) {
            return;
        }
        Player player = event.getPlayer();
        Location location = player.getLocation();
        World world = player.getWorld();
        //todo choose the type of decay to do
        DecayErosionType decayErosionType = DecayErosionType.EXPLOSION;
        @Nullable Location hitLocation = VectorUtils.getHitLocation(location, 200,
            FluidCollisionMode.ALWAYS, true);
        if (hitLocation == null) {
            return;
        }
        int blocksLength = this.radius * 2 + 1;
        DecayBlock[][][] blocksToDecay = new DecayBlock[blocksLength][blocksLength][blocksLength];
        for (int xi = -this.radius; xi <= this.radius; xi++) {
            for (int yi = -this.radius; yi <= this.radius; yi++) {
                for (int zi = -this.radius; zi <= this.radius; zi++) {
                    int x = hitLocation.getBlockX() + xi;
                    int y = hitLocation.getBlockY() + yi;
                    int z = hitLocation.getBlockZ() + zi;
                    Block irlBlock = world.getBlockAt(x, y, z);
                    Material material = irlBlock.getType();
                    blocksToDecay[xi + this.radius][yi + this.radius][zi
                        + this.radius] = new DecayBlock(material, material, x, y, z,
                        world.getUID());
                }
            }
        }
        float[][][] damage = decayErosionType.doDecay(blocksToDecay, force);
        for (int xi = -this.radius; xi <= this.radius; xi++) {
            for (int yi = -this.radius; yi <= this.radius; yi++) {
                for (int zi = -this.radius; zi <= this.radius; zi++) {
                    if (VectorUtils.magnitude(xi, yi, zi) > this.radius) {
                        continue;
                    }
                    DecayBlock block = blocksToDecay[xi + this.radius][yi + this.radius][zi
                        + this.radius];
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

    public void setSize(int size) {
        // + 2 because I want to include an outer-rim
        // that will not be impacted, just for context to the explosion
        this.radius = size + 2;
    }

    public void setForce(int force) {
        this.force = force;
    }
}
