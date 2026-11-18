package archives.tater.segfault.allocation;

import com.mojang.serialization.Codec;

import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Math.max;

public final class GridMap {
    public static final Codec<GridMap> REGIONS_CODEC = GridRegion.CODEC.listOf().xmap(GridMap::new, GridMap::regions);

    private final List<GridRegion> regions = new ArrayList<GridRegion>();
    private int maxX;
    private int maxZ;
    private final Map<GridPos, GridRegion> originCache = new HashMap<GridPos, GridRegion>();
    private final Map<GridPos, GridRegion> regionCache = new HashMap<GridPos, GridRegion>();

    public GridMap(List<GridRegion> regions) {
        this.regions.addAll(regions);
        maxX = regions.stream().mapToInt(GridRegion::sizeX).max().orElse(0);
        maxZ = regions.stream().mapToInt(GridRegion::sizeZ).max().orElse(0);
        rebuildCache();
    }

    public int maxX() {
        return maxX;
    }

    public int maxZ() {
        return maxZ;
    }

    public List<GridRegion> regions() {
        return regions;
    }

    @Nullable
    public GridRegion get(GridPos pos) {
        return regionCache.get(pos);
    }

    @Nullable
    public GridRegion getByOrigin(GridPos pos) {
        return originCache.get(pos);
    }

    public void add(GridRegion region) {
        resize(region.end().x(), region.end().z());

        regions.add(region);
        addToCache(region);
    }

    public void remove(GridRegion region) {
        regions.remove(region);
        removeFromCache(region);
    }

    private void resize(int maxX, int maxZ) {
        if (maxX <= this.maxX && maxZ <= this.maxZ) return;

        this.maxX = max(this.maxX, maxX);
        this.maxZ = max(this.maxZ, maxZ);

        rebuildCache();
    }

    private void rebuildCache() {
        originCache.clear();
        regionCache.clear();
        for (var region : regions)
            addToCache(region);
    }

    private void addToCache(GridRegion region) {
        originCache.put(region.start(), region);
        for (GridPos pos : region)
            regionCache.put(pos, region);
    }

    private void removeFromCache(GridRegion region) {
        originCache.remove(region.start(), region);
        for (GridPos pos : region)
            regionCache.remove(pos, region);
    }
}