package archives.tater.segfault.client;

import folk.sisby.kaleido.api.WrappedConfig;
import folk.sisby.kaleido.lib.quiltconfig.api.annotations.ChangeWarning;
import folk.sisby.kaleido.lib.quiltconfig.api.annotations.Comment;

import static folk.sisby.kaleido.lib.quiltconfig.api.metadata.ChangeWarning.Type;

public class SegfaultClientConfig extends WrappedConfig {
    public Section client = new Section();
    public static class Section implements WrappedConfig.Section {
        @Comment("Enable the allocation region debug renderer")
        @Comment("Requires restart")
        @ChangeWarning(Type.RequiresRestart)
        public boolean allocationDebugRenderer = false;
    }
}
