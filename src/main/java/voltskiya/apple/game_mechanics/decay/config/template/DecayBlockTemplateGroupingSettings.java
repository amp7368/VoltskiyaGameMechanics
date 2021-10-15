package voltskiya.apple.game_mechanics.decay.config.template;

import voltskiya.apple.game_mechanics.decay.config.database.DecayBlockDefaultsDatabase;

import java.util.HashSet;
import java.util.UUID;

public class DecayBlockTemplateGroupingSettings {
    private UUID uuid;
    private HashSet<DecayInto> decayInto;
    private int durability;
    private int resistance;

    private DecayBlockTemplateGroupingSettings() {
    }

    public DecayBlockTemplateGroupingSettings(boolean ignored) {
        uuid = UUID.randomUUID();
        decayInto = new HashSet<>();
        this.durability = DecayBlockDefaultsDatabase.getDurability();
        this.resistance = DecayBlockDefaultsDatabase.getDefaultResistance();
    }

    public static DecayBlockTemplateGroupingSettings createDefault() {
        return new DecayBlockTemplateGroupingSettings(true);
    }

    public UUID getUuid() {
        return uuid;
    }

    public HashSet<DecayInto> getDecayInto() {
        return decayInto;
    }

    public int getDurability() {
        return durability;
    }

    public int getResistance() {
        return resistance;
    }

    public void incrementDurability(int i) {
        this.durability += i;
    }

    public void incrementResistance(int i) {
        this.resistance += i;

    }
}
