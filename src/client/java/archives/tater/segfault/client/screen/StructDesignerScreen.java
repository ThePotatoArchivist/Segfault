package archives.tater.segfault.client.screen;

import archives.tater.segfault.Segfault;
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
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.ItemStack;

public class StructDesignerScreen extends AbstractContainerScreen<StructDesignerMenu> implements ContainerListener {
    private static final Identifier CONTAINER_LOCATION = Segfault.id("textures/gui/container/struct_designer.png");
    private static final WidgetSprites BUTTON_SPRITES = new WidgetSprites(
            Segfault.id("container/struct_designer/button"),
            Segfault.id("container/struct_designer/button_disabled"),
            Segfault.id("container/struct_designer/button_focused")
    );
    public static final Component PLACEHOLDER_TEXT = Component.translatable("screen.segfault.struct_designer.name");
    public static final Component NOT_FOUND_TEXT = Component.translatable("screen.segfault.struct_designer.none");
    private final Player player;
    @SuppressWarnings("NotNullFieldNotInitialized")
    private EditBox name;

    public StructDesignerScreen(StructDesignerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        player = inventory.player;
    }

    @Override
    protected void init() {
        super.init();
        var xo = (width - imageWidth) / 2;
        var yo = (height - imageHeight) / 2;
        name = addRenderableWidget(createNameEdit(xo + 62, yo + 24));
        addRenderableWidget(createButton(xo + 165, yo + 24));
        menu.addSlotListener(this);
    }

    private EditBox createNameEdit(int x, int y) {
        var name = new EditBox(font, x, y, 103, 12, this.name, PLACEHOLDER_TEXT);
        name.setCanLoseFocus(false);
        name.setTextColor(0xFFFFFFFF);
        name.setTextColorUneditable(0xFFFFFFFF);
        name.setInvertHighlightedTextColor(false);
        name.setBordered(false);
        name.setMaxLength(50);
        name.setResponder(this::onNameChanged);
        name.setValue("");
        name.setEditable(menu.getSlot(0).hasItem());
        return name;
    }

    private AbstractButton createButton(int x, int y) {
        return new AbstractButton(x, y, 22, 22, CommonComponents.EMPTY) {
            @Override
            protected void updateWidgetNarration(NarrationElementOutput output) {
                defaultButtonNarrationText(output);
            }

            @Override
            public void onPress(InputWithModifiers input) {
                if (menu.clickMenuButton(player, StructDesignerMenu.BUTTON_CREATE))
                    minecraft.gameMode.handleInventoryButtonClick(menu.containerId, StructDesignerMenu.BUTTON_CREATE);
            }

            @Override
            protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BUTTON_SPRITES.get(isActive(), isHoveredOrFocused()), x, y, 22, 22);
            }
        };
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
        menu.setStructName(name);
        ClientPlayNetworking.send(new SetStructNamePayload(name));
    }

    @Override
    protected void setInitialFocus() {
        setInitialFocus(name);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int xm, int ym) {
        super.extractLabels(graphics, xm, ym);
        var xo = (width - imageWidth) / 2;
        var yo = (height - imageHeight) / 2;
        var box = menu.box;
        if (box != null)
            graphics.text(font, Component.translatable("screen.segfault.struct_designer.dimensions", box.getXSpan(), box.getYSpan(), box.getZSpan()), xo, yo + 24, 0xFFFFFFFF);
        else
            graphics.text(font, NOT_FOUND_TEXT, xo, yo + 24, 0xffff6060);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        graphics.blit(RenderPipelines.GUI_TEXTURED, CONTAINER_LOCATION, leftPos, topPos, 0f, 0f, imageWidth, imageHeight, 256, 256);
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
    }

    @Override
    public void dataChanged(AbstractContainerMenu container, int id, int value) {

    }
}
