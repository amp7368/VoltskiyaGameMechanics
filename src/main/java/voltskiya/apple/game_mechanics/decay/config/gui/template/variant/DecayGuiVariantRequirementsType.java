package voltskiya.apple.game_mechanics.decay.config.gui.template.variant;

import apple.utilities.util.Pretty;
import org.bukkit.Material;
import voltskiya.apple.game_mechanics.decay.config.gui.DecayGui;
import voltskiya.apple.game_mechanics.decay.config.template.DecayBlockTemplateRequiredType;
import voltskiya.apple.game_mechanics.decay.config.template.DecayBlockTemplateRequiredTypeJoined;
import voltskiya.apple.game_mechanics.decay.config.template.MaterialVariant;
import voltskiya.apple.utilities.util.gui.acd.page.InventoryGuiPageScrollableACD;
import voltskiya.apple.utilities.util.gui.acd.page.ScrollableSectionACD;
import voltskiya.apple.utilities.util.gui.acd.slot.InventoryGuiSlotDoNothingACD;
import voltskiya.apple.utilities.util.gui.acd.slot.InventoryGuiSlotImplACD;
import voltskiya.apple.utilities.util.gui.acd.slot.cycle.InventoryGuiSlotCycleACD;
import voltskiya.apple.utilities.util.gui.acd.slot.cycle.SlotCycleable;

import java.util.List;

import static voltskiya.apple.utilities.util.minecraft.InventoryUtils.makeItem;

public class DecayGuiVariantRequirementsType extends InventoryGuiPageScrollableACD<DecayGui> {
    private final DecayBlockTemplateRequiredTypeJoined requirementsType;
    private final MaterialVariant parentBlock;
    private final ScrollableSectionACD section;
    private RequiredTypeWrapper tempRequirementType = RequiredTypeWrapper.get(DecayBlockTemplateRequiredType.NONE);

    public DecayGuiVariantRequirementsType(DecayGui parent, MaterialVariant parentBlock) {
        super(parent, false);
        this.parentBlock = parentBlock;
        this.requirementsType = parentBlock.requirementsType.copy();
        section = new ScrollableSectionACD("requirements", 9, size());
        addSection(section);
    }

    @Override
    public void initialize() {
        addRequirements();
        setSlot(new InventoryGuiSlotDoNothingACD(makeItem(this.parentBlock.material)), 4);
        setSlot(new InventoryGuiSlotImplACD(e -> parentRemoveSubPage(), makeItem(Material.RED_TERRACOTTA, "Discard Changes")), 0);
        setSlot(new InventoryGuiSlotImplACD(e -> {
            parentBlock.requirementsType = requirementsType;
            parentRemoveSubPage();
        }, makeItem(Material.GREEN_TERRACOTTA, "Save")), 8);
        setSlot(new InventoryGuiSlotImplACD(
                e -> {
                    this.requirementsType.addEnum(this.tempRequirementType.wrapped());
                    parentRefresh();
                },
                makeItem(Material.MAGENTA_GLAZED_TERRACOTTA, "Add the requirement")
        ), 7);
    }

    @Override
    public void refreshPageItems() {
        addRequirements();
        setSlot(new InventoryGuiSlotCycleACD<>(
                () -> RequirementsOrValue.get(requirementsType.getIsOr()),
                r -> this.requirementsType.setIsOr(r.isOr())
        ), 2);
        setSlot(new InventoryGuiSlotCycleACD<>(() -> this.tempRequirementType, (r) -> this.tempRequirementType = r), 6);
    }

    private void addRequirements() {
        section.clear();
        for (DecayBlockTemplateRequiredType required : this.requirementsType.getMyEnums()) {
            section.addItem(new InventoryGuiSlotImplACD(
                    e -> requirementsType.removeEnum(required),
                    makeItem(Material.QUARTZ, RequiredTypeWrapper.get(required).itemName())
            ));
        }
    }

    @Override
    public String getName() {
        return "Requirements to Decay";
    }

    @Override
    public int size() {
        return 27;
    }

    private enum RequirementsOrValue implements SlotCycleable<RequirementsOrValue> {
        OR(Material.COMPARATOR),
        AND(Material.REPEATER);

        private final Material material;

        RequirementsOrValue(Material material) {
            this.material = material;
        }

        public static RequirementsOrValue get(boolean isOr) {
            return isOr ? OR : AND;
        }

        @Override
        public RequirementsOrValue[] valuesList() {
            return values();
        }

        @Override
        public Material itemMaterial() {
            return material;
        }

        @Override
        public String itemName() {
            return name();
        }

        @Override
        public List<String> itemLore() {
            return List.of(
                    "The operator that joins the requirements",
                    "'AND' means the requirements all need to be true",
                    "'OR' means only one requirements needs to be true"
            );
        }

        public boolean isOr() {
            return this == OR;
        }
    }

    private record RequiredTypeWrapper(DecayBlockTemplateRequiredType wrapped)
            implements SlotCycleable<RequiredTypeWrapper> {
        private static final RequiredTypeWrapper[] values = new RequiredTypeWrapper[DecayBlockTemplateRequiredType.values().length];

        static {
            DecayBlockTemplateRequiredType[] decayBlockTemplateRequiredTypes = DecayBlockTemplateRequiredType.values();
            for (int i = 0; i < decayBlockTemplateRequiredTypes.length; i++) {
                values[i] = new RequiredTypeWrapper(decayBlockTemplateRequiredTypes[i]);
            }
        }

        public static RequiredTypeWrapper get(DecayBlockTemplateRequiredType required) {
            return values[required.ordinal()];
        }

        @Override
        public int ordinal() {
            return wrapped.ordinal();
        }

        @Override
        public RequiredTypeWrapper[] valuesList() {
            return values;
        }

        @Override
        public Material itemMaterial() {
            return Material.TARGET;
        }

        @Override
        public String name() {
            return "Requirement: " + Pretty.upperCaseFirst(wrapped.name().replace("_", " "));
        }
    }
}
