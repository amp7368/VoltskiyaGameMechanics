package voltskiya.apple.game_mechanics.electricity.piping.simple;

import voltskiya.apple.game_mechanics.electricity.ElectricityInlets;
import voltskiya.apple.game_mechanics.electricity.PluginElectricity;
import voltskiya.apple.utilities.util.storage.MappedGsonFolder;

import java.io.File;

public class ElectricitySimpleLinesAll extends MappedGsonFolder<ElectricitySimpleLine> {
    private static final ElectricitySimpleLinesAll instance = new ElectricitySimpleLinesAll();

    public ElectricitySimpleLinesAll() {
        super(ElectricitySimpleLine.class);
    }

    public static ElectricitySimpleLinesAll getInstance() {
        return instance;
    }

    public static void initialize() {
        for (ElectricitySimpleLine line : instance.getAll()) {
            ElectricityInlets.addTickable(line);
        }
    }

    @Override
    protected File getFolder() {
        return new File(new File(PluginElectricity.get().getDataFolder(), "pipes"), "simple");
    }
}
