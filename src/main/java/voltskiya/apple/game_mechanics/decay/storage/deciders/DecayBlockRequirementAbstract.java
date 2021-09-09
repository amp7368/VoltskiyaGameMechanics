package voltskiya.apple.game_mechanics.decay.storage.deciders;


@FunctionalInterface
public interface DecayBlockRequirementAbstract<R> {
    R decide(DecayBlockContext context, int x, int y, int z);
}
