package archives.tater.segfault.client.screen;

import archives.tater.segfault.Segfault;
import archives.tater.segfault.client.mixin.EntityRenderDispatcherAccessor;
import archives.tater.segfault.client.render.pip.GuiStructureRenderer;
import archives.tater.segfault.client.render.widget.StructureWidget;
import archives.tater.segfault.menu.StructDesignerMenu;
import archives.tater.segfault.network.SetStructNamePayload;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.ItemStack;

import org.jspecify.annotations.Nullable;

public class StructDesignerScreen extends AbstractContainerScreen<StructDesignerMenu> implements ContainerListener {
    private static final Identifier CONTAINER_LOCATION = Segfault.id("textures/gui/container/struct_designer.png");
    private static final WidgetSprites BUTTON_SPRITES = new WidgetSprites(
            Segfault.id("container/struct_designer/button"),
            Segfault.id("container/struct_designer/button_disabled"),
            Segfault.id("container/struct_designer/button_highlighted")
    );
    private static final Identifier TEXT_FIELD_SPRITE = Segfault.id("container/struct_designer/text_field");
    private static final Identifier TEXT_FIELD_DISABLED_SPRITE = Segfault.id("container/struct_designer/text_field_disabled");
    public static final Component PLACEHOLDER_TEXT = Component.translatable("screen.segfault.struct_designer.name");
    public static final Component NOT_FOUND_TEXT = Component.translatable("screen.segfault.struct_designer.none");
    public static final String DISALLOWED_PATH_CHARACTERS = "[^a-z0-9_./-]";

    private final Player player;
    @SuppressWarnings("NotNullFieldNotInitialized")
    private EditBox name;
    private final @Nullable BlockModelRenderState @Nullable[][][] structure;
    private @Nullable StructureWidget structureWidget;
    private @Nullable AbstractButton button;

    public StructDesignerScreen(StructDesignerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 175, 178);
        player = inventory.player;
        inventoryLabelY = 86;

        if (menu.box != null)
            structure = GuiStructureRenderer.extract(
                    ((EntityRenderDispatcherAccessor) minecraft.getEntityRenderDispatcher()).getBlockModelResolver(),
                    minecraft.level,
                    menu.box
            );
        else
            structure = null;
    }

    @Override
    protected void init() {
        super.init();
        name = addRenderableWidget(createNameEdit(leftPos + 80, topPos + 20));
        button = addRenderableWidget(createButton(leftPos + 113, topPos + 60));
        if (structure != null)
            structureWidget = addRenderableWidget(new StructureWidget(structure, leftPos + 8, topPos + 17, 64, 64, 2, 2, structureWidget));
        menu.addSlotListener(this);
    }

    private EditBox createNameEdit(int x, int y) {
        var name = new EditBox(font, x, y, 81, 12, this.name, PLACEHOLDER_TEXT);
        name.setCanLoseFocus(false);
        name.setTextColor(0xFFFFFFFF);
        name.setTextColorUneditable(0xFFFFFFFF);
        name.setInvertHighlightedTextColor(false);
        name.setBordered(false);
        name.setMaxLength(50);
        name.setResponder(this::onNameChanged);
        name.setEditable(menu.getSlot(0).hasItem());
        return name;
    }

    private AbstractButton createButton(int x, int y) {
        var button = new AbstractButton(x, y, 22, 22, CommonComponents.EMPTY) {
            @Override
            protected void updateWidgetNarration(NarrationElementOutput output) {
                defaultButtonNarrationText(output);
            }

            @Override
            public void onPress(InputWithModifiers input) {
                if (menu.clickMenuButton(player, StructDesignerMenu.BUTTON_CREATE)) {
                    minecraft.gameMode.handleInventoryButtonClick(menu.containerId, StructDesignerMenu.BUTTON_CREATE);
                    name.setValue("");
                }
            }

            @Override
            protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BUTTON_SPRITES.get(isActive(), isHoveredOrFocused()), x, y, 22, 22);
            }

            @Override
            public boolean isActive() {
                return super.isActive();
            }
        };
        button.active = menu.canSaveStruct();
        return button;
    }

    @Override
    public void removed() {
        super.removed();
        menu.removeSlotListener(this);
    }

    @Override
    public boolean keyPressed(final KeyEvent event) {
        if (event.isEscape()) {
            minecraft.player.closeContainer();
            return true;
        }
        return name.keyPressed(event) || name.canConsumeInput() || super.keyPressed(event);
    }

    private void onNameChanged(String name) {
        var validatedName = name.trim().replaceAll(DISALLOWED_PATH_CHARACTERS, "");
        if (!name.equals(validatedName)) {
            this.name.setValue(validatedName);
            return;
        }
        menu.setStructName(name);
        ClientPlayNetworking.send(new SetStructNamePayload(name));
        updateButton();
    }

    @Override
    protected void setInitialFocus() {
        setInitialFocus(name);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int xm, int ym) {
        super.extractLabels(graphics, xm, ym);
        var box = menu.box;
        if (box == null) {
            var text = NOT_FOUND_TEXT;
            graphics.text(font, text, 8 + 32 - font.width(text) / 2, 17 + 32 - font.lineHeight / 2, 0xffff6060);
        }
//        else
//            graphics.text(font, Component.translatable("screen.segfault.struct_designer.dimensions", box.getXSpan(), box.getYSpan(), box.getZSpan()), leftPos, topPos + 24, 0xFFFFFFFF);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        graphics.blit(RenderPipelines.GUI_TEXTURED, CONTAINER_LOCATION, leftPos, topPos, 0f, 0f, imageWidth, imageHeight, 256, 256);
        graphics.blitSprite(
                RenderPipelines.GUI_TEXTURED,
                menu.getSlot(0).hasItem() ? TEXT_FIELD_SPRITE : TEXT_FIELD_DISABLED_SPRITE,
                leftPos + 77,
                topPos + 16,
                92,
                16
        );
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);
    }

    @Override
    public void slotChanged(AbstractContainerMenu container, int slotIndex, ItemStack itemStack) {
        if (slotIndex != 0) return;

        if (container.getSlot(0).hasItem()) {
            name.setEditable(true);
        } else {
            name.setEditable(false);
            name.setValue("");
        }
        setFocused(this.name);
        updateButton();
    }

    private void updateButton() {
        if (button != null)
            button.active = menu.canSaveStruct();
    }

    @Override
    public void dataChanged(AbstractContainerMenu container, int id, int value) {

    }
}
