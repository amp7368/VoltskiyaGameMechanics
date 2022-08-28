package voltskiya.apple.game_mechanics.decay.config.template;

import apple.mc.utilities.inventory.item.ItemSerial;
import java.util.HashMap;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import voltskiya.apple.game_mechanics.decay.config.database.DecayBlockSettingsDatabase;

public class DecayBlockTemplateGrouping {

    private transient DecayBlockTemplateGroupingSettings settings = null;
    private ItemSerial icon;
    private HashMap<Material, DecayBlockTemplate> blocksInGroup;
    private HashMap<Material, DecayInto> decayInto;
    private UUID settingsUUID;

    public DecayBlockTemplateGrouping() {
    }

    public DecayBlockTemplateGrouping(DecayBlockTemplateGrouping other) {
        this.icon = other.icon.copy();
        this.blocksInGroup = new HashMap<>(other.blocksInGroup);
        this.decayInto = new HashMap<>(other.decayInto);
        this.settingsUUID = other.settingsUUID;
    }

    public DecayBlockTemplateGrouping(ItemStack currentItem) {
        this.icon = new ItemSerial(currentItem, true);
        this.blocksInGroup = new HashMap<>();
        this.decayInto = new HashMap<>();
        this.settings = null;
        this.settingsUUID = null;
    }

    public ItemSerial getIcon() {
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
        validateSettings();
        if (settings == null) {
            return DecayBlockTemplateGroupingSettings.DEFAULT;
        }
        return settings;
    }

    public void setSettings(DecayBlockTemplateGroupingSettings settings) {
        this.settings = settings;
        this.settingsUUID = settings.getUuid();
        for (DecayBlockTemplate block : this.blocksInGroup.values()) {
            block.setSettings(settings);
        }
    }

    private void validateSettings() {
        if (this.settings == null) {
            this.settings = DecayBlockSettingsDatabase.get(this.settingsUUID);
        }
    }

    public void addDecayInto(DecayInto decayInto) {
        this.decayInto.put(decayInto.getMaterial(), decayInto);
    }

    public void removeDecayInto(Material material) {
        this.decayInto.remove(material);
    }

    public DecayInto getDecay(Material material) {
        return this.decayInto.get(material);
    }

    public void setDeleted(boolean isDeleted) {
        for (DecayBlockTemplate block : this.blocksInGroup.values()) {
            block.setDeleted(isDeleted);
        }
        for (DecayInto decay : this.decayInto.values()) {
            decay.setDeleted(isDeleted);
        }
    }

    public HashMap<Material, DecayInto> getDecayInto() {
        return decayInto;
    }
}
