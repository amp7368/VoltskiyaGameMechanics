package voltskiya.apple.game_mechanics.decay.config.template;

import apple.utilities.structures.EnumJoinedExpressionImpl;
import org.jetbrains.annotations.NotNull;
import voltskiya.apple.game_mechanics.decay.storage.deciders.DecayBlockContext;

import java.util.List;
import java.util.function.IntFunction;

public class DecayBlockTemplateRequiredTypeJoined extends EnumJoinedExpressionImpl<DecayBlockTemplateRequiredTypeJoined, DecayBlockTemplateRequiredType> {
    @Override
    public IntFunction<DecayBlockTemplateRequiredType[]> getEnumArrayConverter() {
        return DecayBlockTemplateRequiredType[]::new;
    }

    public boolean getAsRequirement(@NotNull DecayBlockContext context, int x, int y, int z) {
        return isContained(context.getGivenAsRequiredTypes(x, y, z).toArray(DecayBlockTemplateRequiredType[]::new));
    }

    public DecayBlockTemplateRequiredTypeJoined copy() {
        DecayBlockTemplateRequiredTypeJoined other = new DecayBlockTemplateRequiredTypeJoined();
        other.addEnums(List.of(getMyEnums()));
        other.setIsOr(getIsOr());
        return other;
    }
}
