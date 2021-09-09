package voltskiya.apple.game_mechanics.decay.storage.deciders;

import voltskiya.apple.game_mechanics.decay.config.block.DecayBlockTemplateRequiredType;
import voltskiya.apple.game_mechanics.decay.storage.DecayBlock;

import java.util.ArrayList;
import java.util.Collection;

public class DecayBlockContext {
    public static final DecayBlockContext EMPTY = new DecayBlockContext();
    private final DecayBlock[][][] blocksToDecay;
    private Collection<DecayBlockTemplateRequiredType> given = null;

    public DecayBlockContext(DecayBlock[][][] blocksToDecay) {
        this.blocksToDecay = blocksToDecay;
    }

    private DecayBlockContext() {
        this.blocksToDecay = new DecayBlock[0][0][0];
    }

    public Collection<DecayBlockTemplateRequiredType> getGivenAsRequiredTypes(int x, int y, int z) {
        if (given == null) {
            given = new ArrayList<>();
            for (DecayBlockTemplateRequiredType requiredType : DecayBlockTemplateRequiredType.values()) {
                if (requiredType.decide(this, x, y, z))
                    given.add(requiredType);
            }
        }
        return given;
    }

    public DecayBlock[][][] getBlocksToDecay() {
        return blocksToDecay;
    }
}
