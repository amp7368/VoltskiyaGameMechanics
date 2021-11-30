package voltskiya.apple.game_mechanics.tmw.tmw_world.biomes;

import ycm.yml.manager.fields.YcmField;

public class ScanWorldConfig {
    private static ScanWorldConfig instance;

    @YcmField
    public double maxTps = 15;
    @YcmField
    public int limitVariation = 20; // _ work change per timeUnit
    @YcmField
    public int maxExecutorSize = 100;
    @YcmField
    public int ticksPerScheduleWork = 5;
    @YcmField
    public int timeWatchTpsFor = 60 * 20;

    public ScanWorldConfig() {
        instance = this;
    }

    public static ScanWorldConfig get() {
        return instance;
    }

    public int getWatchTpsSize() {
        return timeWatchTpsFor / ticksPerScheduleWork;
    }
}
