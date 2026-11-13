package archives.tater.segfault.client.screen;

import archives.tater.segfault.Segfault;
import archives.tater.segfault.menu.AllocatorMenu;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class AllocatorScreen extends AbstractContainerScreen<AllocatorMenu> {
    private static final Identifier CONTAINER_LOCATION = Segfault.id("textures/gui/container/allocator.png");

    public AllocatorScreen(AllocatorMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 176, 155);
        inventoryLabelY = imageHeight - 94;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        var xo = (width - imageWidth) / 2;
        var yo = (height - imageHeight) / 2;
        graphics.blit(RenderPipelines.GUI_TEXTURED, CONTAINER_LOCATION, xo, yo, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
    }
}
