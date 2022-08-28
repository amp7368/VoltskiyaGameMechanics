package voltskiya.apple.game_mechanics.decay.storage;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;
import java.util.UUID;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import voltskiya.apple.game_mechanics.decay.config.database.DecayBlockDatabase;
import voltskiya.apple.game_mechanics.decay.config.template.DecayBlockTemplate;
import voltskiya.apple.game_mechanics.decay.config.template.DecayBlockTemplateGrouping;
import voltskiya.apple.game_mechanics.decay.config.template.DecayBlockTemplateGroupingSettings;
import voltskiya.apple.game_mechanics.decay.config.template.DecayBlockTemplateRequiredType;
import voltskiya.apple.game_mechanics.decay.config.template.DecayInto;
import voltskiya.apple.game_mechanics.decay.storage.deciders.DecayBlockContext;
import voltskiya.apple.game_mechanics.decay.storage.deciders.DecayBlockDecider;
import voltskiya.apple.game_mechanics.decay.storage.deciders.DecayBlockDeciderRequirements;
import voltskiya.apple.game_mechanics.decay.storage.deciders.DecayBlockRequirementAbstract;
import voltskiya.apple.game_mechanics.tmw.sql.SqlVariableNames.Decay;
import voltskiya.apple.game_mechanics.tmw.sql.VerifyDatabaseTmw;
import voltskiya.apple.game_mechanics.tmw.tmw_world.util.SimpleWorldDatabase;

@Table(name = Decay.TABLE_DECAY_BLOCK, uniqueConstraints = {
    @UniqueConstraint(columnNames = {Decay.X, Decay.Y, Decay.Z, Decay.WORLD_UID})})
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

    public DecayBlock(Material oldMaterial, Material currentMaterial, int x, int y, int z,
        UUID world) {
        this.oldMaterial = oldMaterial;
        this.currentMaterial = currentMaterial;
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
        this.oldMaterialUid = MaterialDatabase.get(oldMaterial);
        this.currentMaterialUid = MaterialDatabase.get(currentMaterial);
        this.myWorldUid = SimpleWorldDatabase.getWorld(world);
        this.damage = 0;
    }

    public DecayBlock() {
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DecayBlock b) {
            return b.x == this.x && b.y == this.y && b.z == this.z
                && b.myWorldUid == this.myWorldUid;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return x << 16 + z << 8 + y;
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
        if (oldMaterial == null) {
            oldMaterial = MaterialDatabase.get(oldMaterialUid);
        }
        return oldMaterial;
    }

    public Material getCurrentMaterial() {
        if (currentMaterial == null) {
            currentMaterial = MaterialDatabase.get(currentMaterialUid);
        }
        return currentMaterial;
    }

    private void setCurrentMaterial(Material material) {
        this.currentMaterial = material;
        this.currentMaterialUid = MaterialDatabase.get(material);
    }

    @NotNull
    public UUID getWorld() {
        if (world == null) {
            world = SimpleWorldDatabase.getWorld(myWorldUid);
        }
        return world;
    }

    public void explosion(float damage, double chanceOfFire) {
        this.damage += damage;
        @Nullable DecayBlockTemplateGrouping group = DecayBlockDatabase.getGroup(
            this.getCurrentMaterial());
        @Nullable DecayBlockTemplate template = DecayBlockDatabase.getBlock(group,
            this.getCurrentMaterial());
        boolean isOriginal = true;
        while (template != null && this.damage > template.getSettings().getDurability()) {
            isOriginal = false;
            doBreak(group, template);
            group = DecayBlockDatabase.getGroup(this.getCurrentMaterial());
            template = DecayBlockDatabase.getBlock(group, this.getCurrentMaterial());
        }
        if (isOriginal) {
            this.decider = DecayBlockDecider.given(this.getCurrentMaterial());
            this.estimate = this.getCurrentMaterial();
            return;
        }
        if (template == null) {
            // make it air
            this.decider = new DecayBlockDeciderRequirements(Material.AIR)
                .addChance(DecayBlockTemplateRequiredType.BLOCK_BELOW, (c, x, y, z) -> chanceOfFire,
                    Material.FIRE)
                .addChance(DecayBlockTemplateRequiredType.NONE, (c, x, y, z) -> 1 - chanceOfFire,
                    Material.AIR);
            this.estimate = Material.AIR;
        } else {
            this.decider = template.toDecider();
            this.estimate = decider.estimate();
        }
    }

    private void doBreak(DecayBlockTemplateGrouping group, DecayBlockTemplate template) {
        this.damage -= template.getSettings().getDurability();
        Collection<DecayInto> decayInto =
            group == null ? Collections.emptyList() : group.getDecayInto().values();
        if (decayInto.isEmpty()) {
            setCurrentMaterial(null);
            return;
        }
        double i = 0;
        for (DecayInto m : decayInto) {
            i += m.getChance();
        }
        i = random.nextDouble() * i;
        for (DecayInto m : decayInto) {
            if ((i -= m.getChance()) <= 0) {
                setCurrentMaterial(m.getMaterial());
                return;
            }
        }
        setCurrentMaterial(null);
    }

    @Override
    public Material decide(DecayBlockContext blocksToDecay, int x, int y, int z) {
        Material decided = this.decider.decide(blocksToDecay, x, y, z);
        setCurrentMaterial(decided);
        return decided;
    }

    @Nullable
    public Material estimate() {
        if (this.estimate == null) {
            return this.currentMaterial;
        }
        return this.estimate;
    }

    public float getResistance() {
        DecayBlockTemplate template = DecayBlockDatabase.getBlock(this.getCurrentMaterial());
        if (template == null) {
            return 0;
        } else {
            DecayBlockTemplateGroupingSettings settings = template.getSettings();
            return Math.min(settings.getResistance(), settings.getDurability() - this.damage);
        }
    }
}
