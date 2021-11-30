package voltskiya.apple.game_mechanics.tmw;

import ycm.yml.manager.fields.YcmField;
import ycm.yml.manager.fields.YcmInlineComment;

public class TmwWatchConfig {
    private static TmwWatchConfig instance;

    @YcmField
    public ConsoleOutput consoleOutput = new ConsoleOutput();
    @YcmField
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
        @YcmField
        public int mobWatchPlayer = 60;
        @YcmField
        public int biomeWatchPlayer = 80;
        @YcmField
        @YcmInlineComment("does not impact speed of cooling down or heating up")
        public int temperatureWatchPlayer = 20;
        @YcmInlineComment("lower doubles are slower heat transfer")
        @YcmField
        public double heatTransferConstant = 0.005;
    }

    public static class ConsoleOutput {
        @YcmField
        public boolean showSummonMob = true;
        @YcmField
        public boolean showCreateMob = true;
    }
}
