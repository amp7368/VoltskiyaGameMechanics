package voltskiya.apple.game_mechanics.decay.storage;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import voltskiya.apple.game_mechanics.decay.config.database.DecayBlockDatabase;
import voltskiya.apple.game_mechanics.decay.config.template.DecayBlockTemplate;
import voltskiya.apple.game_mechanics.decay.config.template.DecayBlockTemplateRequiredType;
import voltskiya.apple.game_mechanics.decay.config.template.DecayInto;
import voltskiya.apple.game_mechanics.decay.storage.deciders.DecayBlockContext;
import voltskiya.apple.game_mechanics.decay.storage.deciders.DecayBlockDecider;
import voltskiya.apple.game_mechanics.decay.storage.deciders.DecayBlockDeciderRequirements;
import voltskiya.apple.game_mechanics.decay.storage.deciders.DecayBlockRequirementAbstract;
import voltskiya.apple.game_mechanics.tmw.sql.SqlVariableNames.Decay;
import voltskiya.apple.game_mechanics.tmw.sql.VerifyDatabaseTmw;
import voltskiya.apple.game_mechanics.tmw.tmw_world.util.SimpleWorldDatabase;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Random;
import java.util.UUID;

@Table(name = Decay.TABLE_DECAY_BLOCK, uniqueConstraints = {@UniqueConstraint(columnNames = {Decay.X, Decay.Y, Decay.Z, Decay.WORLD_UID})})
@Entity(name = Decay.TABLE_DECAY_BLOCK)
public class DecayBlock implements DecayBlockRequirementAbstract<Material> {
    @Transient
    private transient final Random random = new Random();
    @Id
    @Column(name = Decay.BLOCK_UID, nullable = false)
    public long blockUid = VerifyDatabaseTmw.getCurrentDecayBlockUid();
    @Column(name = Decay.X, nullable = false)
    public int x;
    @Column(name = Decay.Y, nullable = false)
    public int y;
    @Column(name = Decay.Z, nullable = false)
    public int z;
    @Column(name = Decay.WORLD_UID, nullable = false)
    public int myWorldUid;
    @Column(name = Decay.ORIGINAL_MATERIAL, nullable = false)
    public int oldMaterialUid;
    @Column(name = Decay.CURRENT_MATERIAL, nullable = false)
    public int currentMaterialUid;
    @Column(name = Decay.IS_DECAY)
    public boolean isDecay;
    @Column(name = Decay.DAMAGE)
    public int damage;
    @Transient
    private transient Material oldMaterial = null;
    @Transient
    private transient Material currentMaterial = null;
    @Transient
    private transient UUID world = null;
    @Transient
    private transient DecayBlockDecider decider = null;
    @Transient
    private transient Material estimate;

    public DecayBlock(Material oldMaterial, Material currentMaterial, int x, int y, int z, UUID world) {
        this.oldMaterial = oldMaterial;
        this.currentMaterial = currentMaterial;
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
        this.oldMaterialUid = MaterialDatabase.get(oldMaterial);
        this.currentMaterialUid = MaterialDatabase.get(currentMaterial);
        this.myWorldUid = SimpleWorldDatabase.getWorld(world);
        this.isDecay = true;
        this.damage = 0;
    }

    public DecayBlock() {
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DecayBlock b) {
            return b.x == this.x && b.y == this.y && b.z == this.z && b.myWorldUid == this.myWorldUid;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (int) ((x + (long) y + z) % Integer.MAX_VALUE);
    }


    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public int getMyWorldUid() {
        return myWorldUid;
    }

    public Material getOldMaterial() {
        if (oldMaterial == null) oldMaterial = MaterialDatabase.get(oldMaterialUid);
        return oldMaterial;
    }

    public Material getCurrentMaterial() {
        if (currentMaterial == null) currentMaterial = MaterialDatabase.get(currentMaterialUid);
        return currentMaterial;
    }

    @NotNull
    public UUID getWorld() {
        if (world == null) world = SimpleWorldDatabase.getWorld(myWorldUid);
        return world;
    }

    public void explosion(float damage, double chanceOfFire) {
        this.damage += damage;
        DecayBlockTemplate template = DecayBlockDatabase.getBlock(this.getCurrentMaterial());
        while (template != null && this.damage > template.getSettings().getDurability()) {
            doBreak(template);
            template = DecayBlockDatabase.getBlock(this.getCurrentMaterial());
        }
        if (template == null) {
            this.currentMaterial = Material.AIR;
        }
        this.currentMaterialUid = MaterialDatabase.get(getCurrentMaterial());
        if (template == null) {
            if (chanceOfFire == 0) {
                this.decider = DecayBlockDecider.AIR;
            } else {
                this.decider = new DecayBlockDeciderRequirements(Material.AIR)
                        .addChance(DecayBlockTemplateRequiredType.BLOCK_BELOW, (c, x, y, z) -> chanceOfFire, Material.FIRE);
            }
            this.estimate = Material.AIR;
        } else {
            this.decider = template.toDecider();
            this.estimate = decider.estimate();
        }
    }

    private void doBreak(DecayBlockTemplate template) {
        this.damage -= template.getSettings().getDurability();
        HashSet<DecayInto> decayInto = template.getSettings().getDecayInto();
        if (decayInto.isEmpty()) {
            this.currentMaterial = null;
        }
        int i = 0;
        for (DecayInto m : decayInto) {
            i += m.getChance();
        }
        i = random.nextInt(decayInto.size());
        for (DecayInto m : decayInto) {
            if ((i -= m.getChance()) == 0) {
                this.currentMaterial = m.getMaterial();
                break;
            }
        }
    }

    @Override
    public Material decide(DecayBlockContext blocksToDecay, int x, int y, int z) {
        return this.decider.decide(blocksToDecay, x, y, z);
    }

    @Nullable
    public Material estimate() {
        if (this.decider == null) return this.currentMaterial;
        return this.estimate;
    }
}
