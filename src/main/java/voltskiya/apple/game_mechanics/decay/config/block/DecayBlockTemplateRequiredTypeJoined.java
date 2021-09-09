package voltskiya.apple.game_mechanics.decay.config.block;

import apple.utilities.structures.EnumJoinedExpression;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import voltskiya.apple.game_mechanics.decay.storage.deciders.DecayBlockContext;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class DecayBlockTemplateRequiredTypeJoined implements EnumJoinedExpression<DecayBlockTemplateRequiredTypeJoined, DecayBlockTemplateRequiredType> {
    private final Set<DecayBlockTemplateRequiredType> myEnums = new HashSet<>();
    private boolean isOr = false;

    @Contract(pure = true)
    public static @NotNull JsonDeserializer<DecayBlockTemplateRequiredTypeJoined> getThisDeserializer() {
        return EnumJoinedExpression.getDeserializer(DecayBlockTemplateRequiredTypeJoined.class);
    }

    @Contract(pure = true)
    public static @NotNull JsonSerializer<DecayBlockTemplateRequiredTypeJoined> getThisSerializer() {
        return EnumJoinedExpression.getSerializer();
    }

    @Override
    public DecayBlockTemplateRequiredType[] getMyEnums() {
        return myEnums.toArray(DecayBlockTemplateRequiredType[]::new);
    }

    @Override
    public boolean getIsOr() {
        return isOr;
    }

    @Override
    public void setEnums(Collection<DecayBlockTemplateRequiredType> collection) {
        this.myEnums.addAll(collection);
    }

    @Override
    public Function<String, DecayBlockTemplateRequiredType> getEnumConverter() {
        return DecayBlockTemplateRequiredType::valueOf;
    }

    public boolean getAsRequirement(DecayBlockContext context, int x, int y, int z) {
        return isContained(context.getGivenAsRequiredTypes(x, y, z).toArray(DecayBlockTemplateRequiredType[]::new));
    }

}
