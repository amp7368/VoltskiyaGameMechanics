package voltskiya.apple.game_mechanics.decay.config.template;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import voltskiya.apple.game_mechanics.decay.config.database.DecayBlockSettingsDatabase;
import voltskiya.apple.game_mechanics.decay.storage.deciders.DecayBlockDecider;
import voltskiya.apple.game_mechanics.decay.storage.deciders.DecayBlockDeciderRequirements;

import java.util.HashMap;
import java.util.UUID;

public class DecayBlockTemplate {
    private HashMap<Material, MaterialVariant> decayIntoThis;
    private Material icon;
    private UUID settingsUUID;
    private transient DecayBlockTemplateGroupingSettings settings = null;

    public DecayBlockTemplate(DecayBlockBuilderTemplate builder) {
        this.icon = builder.icon;
        this.decayIntoThis = builder.decayIntoThis;
        this.settingsUUID = builder.settingsUUID;
    }

    public DecayBlockTemplate() {
    }

    public Material getIcon() {
        return icon;
    }

    public HashMap<Material, MaterialVariant> getMaterials() {
        return decayIntoThis;
    }

    public DecayBlockTemplateGroupingSettings getSettings() {
        validateSettings();
        return settings;
    }

    private void validateSettings() {
        if (this.settings == null) {
            this.settings = DecayBlockSettingsDatabase.get(this.settingsUUID);
        }
    }

    public DecayBlockBuilderTemplate toBuilder() {
        return new DecayBlockBuilderTemplate(this);
    }

    public DecayBlockDecider toDecider() {
        DecayBlockDeciderRequirements decider = new DecayBlockDeciderRequirements(this.getIcon());
        for (MaterialVariant block : decayIntoThis.values()) {
            decider.addChance(block.requirementsType::getAsRequirement, (c, x, y, z) -> block.chance, block.material);
        }
        return decider;
    }

    public static class DecayBlockBuilderTemplate {
        public HashMap<Material, MaterialVariant> decayIntoThis;
        public Material icon;
        public UUID settingsUUID;
        public transient DecayBlockTemplateGroupingSettings settings = null;

        public DecayBlockBuilderTemplate(DecayBlockTemplate real) {
            this.icon = real.icon;
            this.decayIntoThis = real.decayIntoThis;
            this.settingsUUID = real.settingsUUID;
            this.settings = DecayBlockSettingsDatabase.get(this.settingsUUID);
        }

        public DecayBlockBuilderTemplate(UUID settingsUUID, ItemStack item) {
            this.decayIntoThis = new HashMap<>();
            this.icon = item.getType();
            this.settingsUUID = settingsUUID;
        }

        public DecayBlockTemplate build() {
            return new DecayBlockTemplate(this);
        }
    }
}
