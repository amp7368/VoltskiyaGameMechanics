package voltskiya.apple.game_mechanics.tmw.tmw_config.mobs;

import org.bukkit.inventory.ItemStack;
import voltskiya.apple.game_mechanics.tmw.tmw_config.mobs.gui.MobTypeBuilder;

public class MobType {
    private final MobTypeBuilder.MobIcon icon;

    public MobType(MobTypeBuilder.MobIcon icon) {
        this.icon = icon;
    }

    public ItemStack toItem() {
        return icon.toItem();
    }

    public MobTypeBuilder toBuilder() {
        return new MobTypeBuilder(this);
    }

    public String getName() {
        return icon.getName();
    }

    public MobTypeBuilder.MobIcon getIcon() {
        return icon;
    }
}
