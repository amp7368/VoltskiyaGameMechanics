package voltskiya.apple.game_mechanics.decay.config.gui.template;

import apple.mc.utilities.inventory.gui.acd.page.InventoryGuiPageScrollableACD;
import apple.mc.utilities.inventory.gui.acd.page.ScrollableSectionACD;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import voltskiya.apple.game_mechanics.decay.config.gui.DecayGui;
import voltskiya.apple.game_mechanics.decay.config.gui.template.variant.DecayGuiVariantSettingsPage;
import voltskiya.apple.game_mechanics.decay.config.template.DecayBlockTemplate;
import voltskiya.apple.game_mechanics.decay.config.template.DecayBlockTemplateGrouping;
import voltskiya.apple.game_mechanics.decay.config.template.MaterialVariant;

public class DecayGuiBlockTemplatePage extends InventoryGuiPageScrollableACD<DecayGui> {

    private final DecayBlockTemplateGrouping parentGrouping;
    private final DecayBlockTemplate.DecayBlockBuilderTemplate builder;

    public DecayGuiBlockTemplatePage(DecayGui decayGui, DecayBlockTemplateGrouping parentGrouping,
        DecayBlockTemplate.DecayBlockBuilderTemplate builder) {
        super(decayGui, false);
        this.parentGrouping = parentGrouping;
        this.builder = builder;
    }

    @Override
    public void initialize() {
        addSection(new ScrollableSectionACD("variants", 18, size()));
        setSlot(slotImpl(
            e -> parentRemoveSubPage(),
            makeItem(Material.RED_TERRACOTTA, "Discard Changes")
        ), 0);
        setSlot(slotDoNothing(makeItem(builder.icon, "Block")
        ), 4);
        setSlot(slotImpl(
            e -> {
                parentGrouping.addBlock(builder.build());
                parentRemoveSubPage();
            },
            makeItem(Material.GREEN_TERRACOTTA, "Save Changes")
        ), 8);
        this.setSlot(slotImpl((e) -> {
            this.getSection("variants").scroll(-1);
        }, makeItem(Material.REDSTONE_TORCH, 1, "Up", List.of("Scroll up"))), 17);
        this.setSlot(slotImpl((e) -> {
            this.getSection("variants").scroll(1);
        }, makeItem(Material.LEVER, 1, "Down", List.of("Scroll down"))), 16);
        setSlot(blackGlassDoNothing(), 9, 10, 11, 12, 13, 14, 15);
    }

    @Override
    public void refreshPageItems() {
        addVarients();
    }

    private void addVarients() {
        getSection("variants").clear();
        for (MaterialVariant variant : builder.decayIntoThis.values()) {
            add("variants", slotImpl(e -> {
                    parentAddSubPage(new DecayGuiVariantSettingsPage(parent, builder, variant.copy()));
                }, makeItem(variant.material)
            ));
        }
    }


    @Override
    public void onPlayerInventory(InventoryClickEvent event) {
        ItemStack currentItem = event.getCurrentItem();
        if (currentItem != null && currentItem.getType().isBlock()) {
            MaterialVariant variant = builder.decayIntoThis.get(currentItem.getType());
            if (variant == null) {
                variant = new MaterialVariant(currentItem);
            }
            parentAddSubPage(new DecayGuiVariantSettingsPage(parent, builder, variant));
        }
    }

    @Override
    public String getName() {
        return "Block Settings";
    }

    @Override
    public int size() {
        return 54;
    }
}
