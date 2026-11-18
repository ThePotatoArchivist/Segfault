package archives.tater.segfault.allocation;

public sealed interface GridNode {
    Free FREE = new Free();

    final class Free implements GridNode {}

    record Used(GridRegion allocation) implements GridNode {}

    final class HorizontalSplit implements GridNode {
        public GridNode left = FREE;
        public GridNode right = FREE;
        public final int sizeLeft;

        public HorizontalSplit(int sizeLeft) {
            this.sizeLeft = sizeLeft;
        }
    }

    final class VerticalSplit implements GridNode {
        public GridNode up = FREE;
        public GridNode down = FREE;
        public final int sizeUp;

        public VerticalSplit(int sizeUp) {
            this.sizeUp = sizeUp;
        }
    }
}
