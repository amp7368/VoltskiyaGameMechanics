package voltskiya.apple.game_mechanics.decay.config.template;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import voltskiya.apple.game_mechanics.decay.config.database.DecayBlockSettingsDatabase;
import voltskiya.apple.utilities.util.minecraft.ItemSerializable;

import java.util.HashMap;
import java.util.UUID;

public class DecayBlockTemplateGrouping {
    private transient DecayBlockTemplateGroupingSettings settings = null;
    private ItemSerializable icon;
    private HashMap<Material, DecayBlockTemplate> blocksInGroup;
    private UUID settingsUUID;

    public DecayBlockTemplateGrouping() {
    }

    public DecayBlockTemplateGrouping(DecayBlockTemplateGrouping other) {
        this.icon = other.icon.copy();
        this.blocksInGroup = new HashMap<>(other.blocksInGroup);
        this.settingsUUID = other.settingsUUID;
    }

    public DecayBlockTemplateGrouping(ItemStack currentItem) {
        this.icon = new ItemSerializable(currentItem, true);
        this.blocksInGroup = new HashMap<>();
        this.settings = DecayBlockTemplateGroupingSettings.createDefault();
        this.settingsUUID = settings.getUuid();
    }

    public ItemSerializable getIcon() {
        return icon;
    }

    public HashMap<Material, DecayBlockTemplate> getBlocks() {
        return blocksInGroup;
    }

    public DecayBlockTemplate getBlock(Material name) {
        return blocksInGroup.get(name);
    }

    public UUID getSettingsUUID() {
        return settingsUUID;
    }

    public void addBlock(DecayBlockTemplate block) {
        blocksInGroup.put(block.getIcon(), block);
    }

    public DecayBlockTemplateGrouping copy() {
        return new DecayBlockTemplateGrouping(this);
    }

    public DecayBlockTemplateGroupingSettings getSettings() {
        if (settings == null) {
            this.settings = DecayBlockSettingsDatabase.get(settingsUUID);
        }
        return settings;
    }
}
