package archives.tater.segfault.client.render.widget;

import archives.tater.segfault.client.render.pip.GuiStructureRenderer;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.network.chat.CommonComponents;

import static java.lang.Math.clamp;
import static java.lang.Math.min;
import static net.minecraft.util.Mth.sqrt;
import static net.minecraft.util.Mth.square;

public class StructureWidget extends AbstractWidget {

    private final BlockModelRenderState[][][] structure;
    private final float scale;
    private double yRot = 45;
    private double xRot = 35;
    private boolean grabbed = false;

    public StructureWidget(BlockModelRenderState[][][] structure, int x, int y, int width, int height) {
        super(x, y, width, height, CommonComponents.EMPTY);
        this.structure = structure;
        var dx = structure.length;
        var dy = structure[0].length;
        var dz = structure[0][0].length;
        scale = min(
                width / sqrt(square(dx) + square(dz)),
                height / sqrt(square(dx) + square(dy) + square(dz))
        );
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        graphics.guiRenderState.addPicturesInPictureState(new GuiStructureRenderer.State(
                structure,
                (int) yRot,
                (int) xRot,
                getX(),
                getY(),
                getRight(),
                getBottom(),
                scale,
                graphics.scissorStack.peek()
        ));
        handleCursor(graphics);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        grabbed = true;
        return true;
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (grabbed) {
            grabbed = false;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
        if (grabbed) {
            xRot = clamp(xRot + 2 * dy, -90, 90);
            yRot -= 2 * dx;
            return true;
        }
        return false;
    }

    @Override
    protected void handleCursor(GuiGraphicsExtractor graphics) {
        if (grabbed || isHovered())
            graphics.requestCursor(CursorTypes.RESIZE_ALL);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {

    }
}
