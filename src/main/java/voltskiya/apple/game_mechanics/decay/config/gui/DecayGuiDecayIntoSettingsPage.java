package voltskiya.apple.game_mechanics.decay.config.gui;

import voltskiya.apple.game_mechanics.decay.config.template.DecayBlockTemplate;
import voltskiya.apple.game_mechanics.decay.config.template.DecayBlockTemplateGrouping;
import voltskiya.apple.game_mechanics.decay.config.template.DecayInto;
import voltskiya.apple.utilities.util.gui.acd.page.InventoryGuiPageImplACD;

public class DecayGuiDecayIntoSettingsPage extends InventoryGuiPageImplACD<DecayGui> {
    private DecayBlockTemplate.DecayBlockBuilderTemplate block;

    public DecayGuiDecayIntoSettingsPage(DecayGui parent, DecayBlockTemplateGrouping builder, DecayInto decayInto) {
        super(parent);
    }

    @Override
    public void initialize() {
        super.initialize();
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
