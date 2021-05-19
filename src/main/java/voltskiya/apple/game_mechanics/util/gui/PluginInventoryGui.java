package voltskiya.apple.game_mechanics.util.gui;


import voltskiya.apple.game_mechanics.VoltskiyaModule;

public class PluginInventoryGui extends VoltskiyaModule {
    @Override
    public void enable() {
        new InventoryGuiListener();
    }

    @Override
    public String getName() {
        return "inventory_gui";
    }
}
