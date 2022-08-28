package voltskiya.apple.game_mechanics.decay.config.template;

import apple.utilities.structures.expressions.EnumJoinedExpressionSimple;
import apple.utilities.structures.expressions.EnumOperatorType;
import java.util.List;
import java.util.function.IntFunction;
import org.jetbrains.annotations.NotNull;
import voltskiya.apple.game_mechanics.decay.storage.deciders.DecayBlockContext;
import voltskiya.apple.game_mechanics.decay.storage.deciders.DecayBlockRequirementAbstract;

public class DecayBlockTemplateRequiredTypeJoined extends
    EnumJoinedExpressionSimple<DecayBlockTemplateRequiredType> implements
    DecayBlockRequirementAbstract<Boolean> {

    public DecayBlockTemplateRequiredTypeJoined() {
        super(EnumOperatorType.OR);
    }

    @Override
    public IntFunction<DecayBlockTemplateRequiredType[]> getEnumArrayConverter() {
        return DecayBlockTemplateRequiredType[]::new;
    }

    public Boolean decide(@NotNull DecayBlockContext context, int x, int y, int z) {
        if (getMyEnums().length == 0) {
            return true;
        }
        return operate(context.getGivenAsRequiredTypes(x, y, z)
            .toArray(DecayBlockTemplateRequiredType[]::new));
    }

    public DecayBlockTemplateRequiredTypeJoined copy() {
        DecayBlockTemplateRequiredTypeJoined other = new DecayBlockTemplateRequiredTypeJoined();
        other.addEnums(List.of(getMyEnums()));
        other.setOperator(getOperatorType());
        return other;
    }
}
