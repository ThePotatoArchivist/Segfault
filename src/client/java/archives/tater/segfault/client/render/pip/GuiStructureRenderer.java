package archives.tater.segfault.client.render.pip;

import archives.tater.segfault.Segfault;

import net.fabricmc.fabric.api.client.rendering.v1.PictureInPictureRendererRegistry;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import org.jspecify.annotations.Nullable;

public class GuiStructureRenderer extends PictureInPictureRenderer<GuiStructureRenderer.State> {

    private static final BlockDisplayContext DISPLAY_CONTEXT = BlockDisplayContext.create();

    private final Minecraft minecraft;

    public GuiStructureRenderer(PictureInPictureRendererRegistry.Context context) {
        this.minecraft = context.minecraft();
    }

    @Override
    public Class<State> getRenderStateClass() {
        return State.class;
    }

    @Override
    protected void renderToTexture(State renderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector) {
        minecraft.gameRenderer.lighting().setupFor(Lighting.Entry.ENTITY_IN_UI);

        poseStack.translate(0, (renderState.y0 - renderState.y1) / renderState.scale / 2f, 0);
        poseStack.scale(-1, -1, 1);
        poseStack.rotate(Axis.XP.rotationDegrees(-renderState.xRot));
        poseStack.rotate(Axis.YP.rotationDegrees(-renderState.yRot));
        poseStack.translate(
                -renderState.blockStates.length / 2f,
                -renderState.blockStates[0].length / 2f,
                -renderState.blockStates[0][0].length / 2f
        );

        poseStack.pushPose();
        for (var slice : renderState.blockStates) {
            poseStack.pushPose();
            for (var line : slice) {
                poseStack.pushPose();
                for (var state : line) {
                    if (state != null)
                        state.submit(poseStack, submitNodeCollector, LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0);

                    poseStack.translate(0, 0, 1);
                }
                poseStack.popPose();
                poseStack.translate(0, 1, 0);
            }
            poseStack.popPose();
            poseStack.translate(1, 0, 0);
        }
        poseStack.popPose();
    }

    @Override
    protected String getTextureLabel() {
        return Segfault.MOD_ID + ":structure";
    }

    public static @Nullable BlockModelRenderState[][][] extract(BlockModelResolver blockModelResolver, BlockGetter level, BoundingBox box) {
        var xSpan = box.getXSpan();
        var ySpan = box.getYSpan();
        var zSpan = box.getZSpan();
        var states = new BlockModelRenderState[zSpan][ySpan][xSpan];
        var pos = new BlockPos.MutableBlockPos();
        for (var x = 0; x < xSpan; x++) {
            pos.setX(box.minX() + x);
            for (var y = 0; y < ySpan; y++) {
                pos.setY(box.minY() + y);
                for (var z = 0; z < zSpan; z++) {
                    pos.setZ(box.minZ() + z);

                    var state = level.getBlockState(pos);
                    if (state.isAir()) continue;
                    var renderState = new BlockModelRenderState();
                    blockModelResolver.update(renderState, state, DISPLAY_CONTEXT);
                    states[x][y][z] = renderState;
                }
            }
        }

        return states;
    }

    public record State(
            @Nullable BlockModelRenderState[][][] blockStates,
            float yRot,
            float xRot,
            int x0,
            int y0,
            int x1,
            int y1,
            float scale,
            @Nullable ScreenRectangle scissorArea,
            @Nullable ScreenRectangle bounds
    ) implements PictureInPictureRenderState {
        public State(
                @Nullable BlockModelRenderState[][][] blockStates,
                float yRot,
                float xRot,
                int x0,
                int y0,
                int x1,
                int y1,
                float scale,
                @Nullable ScreenRectangle scissorArea
        ) {
            this(blockStates, yRot, xRot, x0, y0, x1, y1, scale, scissorArea, PictureInPictureRenderState.getBounds(x0, y0, x1, y1, scissorArea));
        }
    }
}
