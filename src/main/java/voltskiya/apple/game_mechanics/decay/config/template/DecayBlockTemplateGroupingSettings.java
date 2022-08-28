package voltskiya.apple.game_mechanics.decay.config.template;

import apple.mc.utilities.inventory.item.InventoryUtils;
import apple.mc.utilities.inventory.item.ItemSerial;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import voltskiya.apple.game_mechanics.decay.config.database.DecayBlockDefaultsDatabase;

public class DecayBlockTemplateGroupingSettings {

    public static final DecayBlockTemplateGroupingSettings DEFAULT = createDefault(
        InventoryUtils.get().makeItem(Material.COMMAND_BLOCK, "DEFAULT"));

    static {
        DEFAULT.uuid = UUID.fromString("68a069a5-5a9e-4422-a71c-f3fc2c8f6e0c");
    }

    private UUID uuid;
    private int durability;
    private int resistance;
    private ItemSerial icon;

    private DecayBlockTemplateGroupingSettings() {
    }

    public DecayBlockTemplateGroupingSettings(ItemSerial icon) {
        uuid = UUID.randomUUID();
        this.icon = icon;
        this.durability = DecayBlockDefaultsDatabase.getDurability();
        this.resistance = DecayBlockDefaultsDatabase.getDefaultResistance();
    }

    public DecayBlockTemplateGroupingSettings(DecayBlockTemplateGroupingSettings other) {
        this.uuid = other.uuid;
        this.durability = other.durability;
        this.resistance = other.resistance;
        this.icon = other.icon;
    }

    public static DecayBlockTemplateGroupingSettings createDefault(ItemStack item) {
        return new DecayBlockTemplateGroupingSettings(new ItemSerial(item, true));
    }

    public UUID getUuid() {
        return uuid;
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

    public ItemStack getIcon() {
        return this.icon.getItem();
    }

    public DecayBlockTemplateGroupingSettings copy() {
        return new DecayBlockTemplateGroupingSettings(this);
    }
}
