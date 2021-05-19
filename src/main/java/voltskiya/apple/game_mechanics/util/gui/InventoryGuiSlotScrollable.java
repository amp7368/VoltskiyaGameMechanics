package voltskiya.apple.game_mechanics.util.gui;

public class InventoryGuiSlotScrollable extends InventoryGuiSlotDoNothing {
    private static InventoryGuiSlotDoNothing instance = null;

    public static InventoryGuiSlotDoNothing get() {
        if (instance == null) instance = new InventoryGuiSlotScrollable();
        return instance;
    }
}
