package voltskiya.apple.game_mechanics.tmw.tmw_config.mobs;

import com.google.gson.*;
import org.bukkit.inventory.ItemStack;
import voltskiya.apple.game_mechanics.tmw.tmw_config.mobs.gui.MobTypeBuilder;

import java.lang.reflect.Type;

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

    @Override
    public int hashCode() {
        return this.icon.getName().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof MobType && this.icon.getName().equals(((MobType) obj).getName());
    }

    @Override
    public String toString() {
        return this.icon.getName();
    }

    public static class MobTypeSerializer implements JsonSerializer<MobType>{
        @Override
        public JsonElement serialize(MobType mobType, Type type, JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(mobType.icon.getName());
        }
    }
    public static class MobTypeDeSerializer implements  JsonDeserializer<MobType> {
        @Override
        public MobType deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return MobTypeDatabase.getMob(jsonElement.getAsString());
        }
    }
}
