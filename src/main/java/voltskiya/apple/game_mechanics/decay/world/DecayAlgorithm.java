package voltskiya.apple.game_mechanics.decay.world;

import apple.utilities.util.BooleanUtils;
import apple.utilities.util.NumberUtils;
import voltskiya.apple.game_mechanics.decay.config.block.DecayBlockDatabase;
import voltskiya.apple.game_mechanics.decay.config.block.DecayBlockTemplate;
import voltskiya.apple.game_mechanics.decay.storage.DecayBlock;
import voltskiya.apple.game_mechanics.decay.storage.deciders.DecayBlockContext;

import java.util.Arrays;
import java.util.function.BiConsumer;

public class DecayAlgorithm {
    /**
     * @param blocksToDecay cubic matrix with blocks to decay
     * @param force         the force of the explosion
     * @return damage that should be applied to blocksToDecay
     */
    public static float[][][] explosion(DecayBlock[][][] blocksToDecay, int force) {
        int totalLength = blocksToDecay.length - 2; // remember the + 2 for the extra context in the rim
        float[][][] blocksResistance = new float[totalLength][totalLength][totalLength];
        for (int x = 0; x < totalLength; x++) {
            for (int y = 0; y < totalLength; y++) {
                for (int z = 0; z < totalLength; z++) {
                    DecayBlockTemplate template = DecayBlockDatabase.getBlock(blocksToDecay[x + 1][y + 1][z + 1].getOldMaterial());
                    blocksResistance[x][y][z] = template == null ? 0 : template.getResistance();
                }
            }
        }
        BiConsumer<DecayBlock, Float> doExplosionDmg = (db, dmg) -> db.explosion(dmg, 0.1f);
        float[][][] damage = explosionInternal(blocksResistance, force);
        for (int x = 0; x < totalLength; x++) {
            for (int y = 0; y < totalLength; y++) {
                for (int z = 0; z < totalLength; z++) {
                    doExplosionDmg.accept(blocksToDecay[x + 1][y + 1][z + 1], damage[x][y][z]);
                }
            }
        }
        DecayBlockContext context = new DecayBlockContext(blocksToDecay);
        for (int x = 0; x < totalLength; x++) {
            for (int y = 0; y < totalLength; y++) {
                for (int z = 0; z < totalLength; z++) {
                    blocksToDecay[x + 1][y + 1][z + 1].decide(context, x, y, z);
                }
            }
        }
        return damage;
    }

    public static float[][][] explosionInternal(float[][][] blocksResistance, int force) {
        int totalLength = blocksResistance.length;

        float starter;
        if (totalLength <= 1) {
            starter = force;
        } else {
            starter = (float) ((Math.pow(Math.E, 3) + Math.pow(totalLength, 3)) * force);
        }
        float[][][] damage = new float[][][]{{{starter}}};

        for (int explosionIteration = 2; explosionIteration <= totalLength; explosionIteration++) {
            float[][][] newDamage = new float[explosionIteration][explosionIteration][explosionIteration];
            // expand the previous iteration of the explosion out to "newDamage" size
            // [2 * 2^3]  =>  [ [[ 2 , 2 ] , [[ 2 , 2 ] ]
            //                   [ 2 , 2 ]] , [ 2 , 2 ]] ]
            for (int xi = 0; xi < explosionIteration; xi++) {
                for (int yi = 0; yi < explosionIteration; yi++) {
                    for (int zi = 0; zi < explosionIteration; zi++) {
                        // do a small loop to sum all the numbers nearby in the last array
                        int xiiStart = xi == 0 ? 0 : -1;
                        int yiiStart = yi == 0 ? 0 : -1;
                        int ziiStart = zi == 0 ? 0 : -1;
                        for (int xii = xiiStart; xii < 1; xii++) {
                            for (int yii = yiiStart; yii < 1; yii++) {
                                for (int zii = ziiStart; zii < 1; zii++) {
                                    // x,y,z is where you're coming from in "damage"
                                    int x = xi + xii;
                                    int y = yi + yii;
                                    int z = zi + zii;
                                    // if we won't have an ArrayIndexOutOfBounds
                                    if (NumberUtils.between(0, x, damage.length) &&
                                            NumberUtils.between(0, y, damage.length) &&
                                            NumberUtils.between(0, z, damage.length)
                                    ) {
                                        // if we're not adding anything, just skip this
                                        if (damage[x][y][z] == 0) continue;

                                        // if we are heading out from the central explosion,
                                        boolean xNeg = xii < 0 && xi > explosionIteration / 2 - 1;
                                        boolean xPos = xii >= 0 && xi < explosionIteration / 2 - 1;
                                        boolean yNeg = yii < 0 && yi > explosionIteration / 2 - 1;
                                        boolean yPos = yii >= 0 && yi < explosionIteration / 2 - 1;
                                        boolean zNeg = zii < 0 && zi > explosionIteration / 2 - 1;
                                        boolean zPos = zii >= 0 && zi < explosionIteration / 2 - 1;
                                        if (BooleanUtils.isAtLeastN(2, xNeg, yNeg, zNeg) ||
                                                BooleanUtils.isAtLeastN(2, xPos, yPos, zPos)
                                        ) {
                                            // do a block check and reduce the damage going out
                                            int half = (totalLength - explosionIteration + 1) / 2;
                                            newDamage[xi][yi][zi] += Math.max(
                                                    0,
                                                    damage[x][y][z] / 8 -
                                                            blocksResistance[xi + half]
                                                                    [yi + half]
                                                                    [zi + half]
                                            );
                                        } else {
                                            if (xii == 0 && yii == 0 && zii == 0) {
                                                newDamage[xi][yi][zi] += damage[x][y][z] / 8; // split it (divide by 2^3)
                                            } else {
                                                newDamage[xi][yi][zi] += damage[x][y][z]; // this is bounce back basically
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        // end the small loop to sum all the numbers nearby in the last array
                    }
                }
            }
            damage = newDamage;
        }
        return damage;
    }

    public static void main(String[] args) {
        int length = 5;
        DecayBlock[][][] decayBlocks = new DecayBlock[length][length][length];
        float[][][] dmg = new float[length - 2][length - 2][length - 2];
        for (int xi = 0; xi < length; xi++) {
            for (int yi = 0; yi < length; yi++) {
                for (int zi = 0; zi < length; zi++) {
                    decayBlocks[xi][yi][zi] = null;
                    if (xi == 0 || yi == 0 || zi == 0 || xi == length - 1 || yi == length - 1 || zi == length - 1)
                        continue;
                    dmg[xi - 1][yi - 1][zi - 1] = 0;
                }
            }
        }
        float[][][] s = explosionInternal(dmg, 5);
        System.out.println(Arrays.deepToString(s)
                .replace("]],", "]],\n")
                .replace("],", "],\n")
        );
    }
}
