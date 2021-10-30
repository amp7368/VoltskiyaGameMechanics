package voltskiya.apple.game_mechanics.tmw;

public class MobConfigPerWorld {
    private boolean isMobSpawning = false;

    public MobConfigPerWorld() {
    }

    public boolean isMobSpawning() {
        return isMobSpawning;
    }

    public void setMobSpawning(boolean mobSpawning) {
        isMobSpawning = mobSpawning;
    }
}
