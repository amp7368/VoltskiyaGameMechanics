package voltskiya.apple.game_mechanics.decay.config.gui.template;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import voltskiya.apple.game_mechanics.decay.config.database.DecayBlockDatabase;
import voltskiya.apple.game_mechanics.decay.config.gui.DecayGui;
import voltskiya.apple.game_mechanics.decay.config.gui.template.group.DecayGuiGroupSettingsChooseSettingsPage;
import voltskiya.apple.game_mechanics.decay.config.template.DecayBlockTemplate;
import voltskiya.apple.game_mechanics.decay.config.template.DecayBlockTemplateGrouping;
import voltskiya.apple.game_mechanics.decay.config.template.DecayInto;
import voltskiya.apple.utilities.util.gui.acd.page.InventoryGuiPageScrollableACD;
import voltskiya.apple.utilities.util.gui.acd.page.ScrollableSectionACD;
import voltskiya.apple.utilities.util.gui.acd.slot.InventoryGuiButtonTemplate;
import voltskiya.apple.utilities.util.gui.acd.slot.InventoryGuiSlotDoNothingACD;
import voltskiya.apple.utilities.util.gui.acd.slot.InventoryGuiSlotImplACD;
import voltskiya.apple.utilities.util.minecraft.InventoryUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static voltskiya.apple.utilities.util.minecraft.InventoryUtils.makeItem;

public class DecayGuiGroupSettingsPage extends InventoryGuiPageScrollableACD<DecayGui> {
    private final DecayBlockTemplateGrouping builder;

    public DecayGuiGroupSettingsPage(DecayGui parent, DecayBlockTemplateGrouping builder) {
        super(parent, false);
        this.builder = builder;
    }

    @Override
    public void initialize() {
        addSection(new ScrollableSectionACD("blocks_in_group", 18, 49));
        addSection(new ScrollableSectionACD("into", 23, 54));
        addBlocks();
        addInto();
        scrollButtons();

        setSlot(new InventoryGuiSlotImplACD(e -> parentRemoveSubPage(), makeItem(Material.RED_TERRACOTTA, "Discard Changes")), 0);
        setSlot(new InventoryGuiSlotDoNothingACD(builder.getIcon().getItem()), 1);
        setSlot(new InventoryGuiSlotDoNothingACD(makeItem(Material.OAK_SAPLING, 1, "To add a block",
                Collections.singletonList("LEFT click a block in your inventory"))), 11);
        setSlot(new InventoryGuiSlotDoNothingACD(makeItem(Material.SPRUCE_SAPLING, 1, "To add a block to decay into",
                Collections.singletonList("RIGHT click a block in your inventory"))), 15);
        setSlot(InventoryGuiButtonTemplate.blackGlassDoNothing(), 12, 13, 14, 22, 31, 40, 49);
        setSlot(new InventoryGuiSlotImplACD(e -> {
            DecayBlockDatabase.addBlock(builder);
            parentRemoveSubPage();
        }, makeItem(Material.GREEN_TERRACOTTA, "Save Changes")), 8);
        setSlot(new InventoryGuiSlotImplACD(e -> {
            parentAddSubPage(new DecayGuiGroupSettingsChooseSettingsPage(parent, this.builder));
        }, makeItem(Material.COMPARATOR, "Set settings")), 3);
    }

    private void scrollButtons() {
        this.setSlot(new InventoryGuiSlotImplACD((e) -> {
            this.getSection("blocks_in_group").scroll(-1);
        }, InventoryUtils.makeItem(Material.REDSTONE_TORCH, 1, "Up", List.of("Scroll up"))), 10);
        this.setSlot(new InventoryGuiSlotImplACD((e) -> {
            this.getSection("blocks_in_group").scroll(1);
        }, InventoryUtils.makeItem(Material.LEVER, 1, "Down", List.of("Scroll down"))), 9);
        this.setSlot(new InventoryGuiSlotImplACD((e) -> {
            this.getSection("into").scroll(-1);
        }, InventoryUtils.makeItem(Material.REDSTONE_TORCH, 1, "Up", List.of("Scroll up"))), 17);
        this.setSlot(new InventoryGuiSlotImplACD((e) -> {
            this.getSection("into").scroll(1);
        }, InventoryUtils.makeItem(Material.LEVER, 1, "Down", List.of("Scroll down"))), 16);
    }


    @Override
    public void refreshPageItems() {
        addBlocks();
        addInto();
        setSlot(new InventoryGuiSlotDoNothingACD(builder.getSettings().getIcon()), 4);
    }

    private void addBlocks() {
        getSection("blocks_in_group").clear();
        for (DecayBlockTemplate block : builder.getBlocks().values()) {
            add("blocks_in_group", new InventoryGuiSlotImplACD(
                    e -> parentAddSubPage(new DecayGuiBlockTemplatePage(parent, this.builder, block.toBuilder())),
                    makeItem(block.getIcon(), "Block in Group")
            ));
        }
    }

    private void addInto() {
        getSection("into").clear();
        for (DecayInto block : builder.getDecayInto().values()) {
            add("into", new InventoryGuiSlotImplACD(
                    e -> parentAddSubPage(new DecayGuiDecayIntoSettingsPage(parent, this.builder, block.copy())),
                    makeItem(block.getMaterial(), "Block in Group")
            ));
        }
    }

    @Override
    public void onPlayerInventory(InventoryClickEvent event) {
        @Nullable ItemStack item = event.getCurrentItem();
        if (item != null && item.getType().isBlock()) {
            if (event.getClick().isLeftClick()) {
                DecayBlockTemplate blockOld = this.builder.getBlock(item.getType());
                DecayBlockTemplate.DecayBlockBuilderTemplate block =
                        blockOld == null ?
                                new DecayBlockTemplate.DecayBlockBuilderTemplate(this.builder.getSettingsUUID(), item) :
                                blockOld.toBuilder();
                parentAddSubPage(new DecayGuiBlockTemplatePage(parent, this.builder, block));
            } else if (event.getClick().isRightClick()) {
                DecayInto decayOld = this.builder.getDecay(item.getType());
                DecayInto decayInto = Objects.requireNonNullElseGet(decayOld, () -> new DecayInto(item));
                parentAddSubPage(new DecayGuiDecayIntoSettingsPage(parent, this.builder, decayInto));
            }
        }
    }

    @Override
    public String getName() {
        return "Block Grouping";
    }

    @Override
    public int size() {
        return 54;
    }
}
