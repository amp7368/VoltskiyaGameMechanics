package voltskiya.apple.game_mechanics.tmw;


import apple.file.yml.YcmField;

public class TmwWatchConfig {

    private static TmwWatchConfig instance;

    public ConsoleOutput consoleOutput = new ConsoleOutput();
    public CheckInterval checkInterval = new CheckInterval();

    public TmwWatchConfig() {
        instance = this;
    }

    public static CheckInterval getCheckInterval() {
        return get().checkInterval;
    }

    public static TmwWatchConfig get() {
        return instance;
    }

    public static class CheckInterval {

        public int mobWatchPlayer = 60;
        public int biomeWatchPlayer = 80;
        @YcmField(inlineComment = "does not impact speed of cooling down or heating up")
        public int temperatureWatchPlayer = 20;
        @YcmField(inlineComment = "lower doubles are slower heat transfer")
        public double heatTransferConstant = 0.005;
    }

    public static class ConsoleOutput {

        @YcmField
        public boolean showSummonMob = true;
        @YcmField
        public boolean showCreateMob = true;
    }
}
