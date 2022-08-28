package voltskiya.apple.game_mechanics.decay.config.gui.template;

import apple.mc.utilities.inventory.gui.acd.page.InventoryGuiPageImplACD;
import org.bukkit.Material;
import voltskiya.apple.game_mechanics.decay.config.gui.DecayGui;
import voltskiya.apple.game_mechanics.decay.config.template.DecayBlockTemplate;
import voltskiya.apple.game_mechanics.decay.config.template.DecayBlockTemplateGrouping;
import voltskiya.apple.game_mechanics.decay.config.template.DecayInto;

public class DecayGuiDecayIntoSettingsPage extends InventoryGuiPageImplACD<DecayGui> {

    private final DecayBlockTemplateGrouping parentGrouping;
    private final DecayInto decayInto;
    private DecayBlockTemplate.DecayBlockBuilderTemplate block;

    public DecayGuiDecayIntoSettingsPage(DecayGui parent, DecayBlockTemplateGrouping parentGrouping,
        DecayInto decayInto) {
        super(parent);
        this.parentGrouping = parentGrouping;
        this.decayInto = decayInto;
    }

    @Override
    public void initialize() {
        setSlot(slotImpl(
            e -> parentRemoveSubPage(),
            makeItem(Material.RED_TERRACOTTA, "Discard Changes")
        ), 0);
        setSlot(slotImpl(
            e -> {
                parentGrouping.removeDecayInto(decayInto.getMaterial());
                parentRemoveSubPage();
            },
            makeItem(Material.RED_CONCRETE_POWDER, "DELETE")
        ), 1);
        setSlot(slotDoNothing(makeItem(decayInto.getMaterial(), "Decay Into")
        ), 4);
        setSlot(slotImpl(
            e -> {
                parentGrouping.addDecayInto(decayInto);
                parentRemoveSubPage();
            },
            makeItem(Material.GREEN_TERRACOTTA, "Save Changes")
        ), 8);
    }

    @Override
    public String getName() {
        return "Variant Settings";
    }

    @Override
    public int size() {
        return 9;
    }
}
