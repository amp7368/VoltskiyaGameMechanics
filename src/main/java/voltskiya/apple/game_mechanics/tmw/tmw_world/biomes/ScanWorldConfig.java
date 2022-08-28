package voltskiya.apple.game_mechanics.tmw.tmw_world.biomes;

public class ScanWorldConfig {

    private static ScanWorldConfig instance;

    public double maxTps = 15;
    public int limitVariation = 20; // _ work change per timeUnit
    public int maxExecutorSize = 100;
    public int ticksPerScheduleWork = 5;
    public int timeWatchTpsFor = 60 * 20;
    public double limitShouldBe = 80d;

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
