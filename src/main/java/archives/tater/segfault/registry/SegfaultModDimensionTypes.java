package archives.tater.segfault.registry;

import archives.tater.segfault.Segfault;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.attribute.BedRule;
import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.level.CardinalLighting;
import net.minecraft.world.level.dimension.DimensionType;

import java.util.Optional;

import static net.minecraft.util.ARGB.vector3fFromRGB24;

public interface SegfaultModDimensionTypes {
    private static ResourceKey<DimensionType> create(String path) {
        return ResourceKey.create(Registries.DIMENSION_TYPE, Segfault.id(path));
    }

    ResourceKey<DimensionType> MEMORY = create("memory");

    static void bootstrap(BootstrapContext<DimensionType> context) {
        var blocks = context.lookup(Registries.BLOCK);
        var timelines = context.lookup(Registries.TIMELINE);

        context.register(MEMORY, new DimensionType(
                true,
                true,
                false,
                false,
                1,
                0,
                256,
                256,
                blocks.getOrThrow(BlockTags.INFINIBURN_OVERWORLD),
                0,
                new DimensionType.MonsterSettings(ConstantInt.of(0), 0),
                DimensionType.Skybox.NONE,
                CardinalLighting.Type.NETHER,
                EnvironmentAttributeMap.builder()
                        .set(EnvironmentAttributes.FOG_COLOR, vector3fFromRGB24(0xff0a0a0a))
                        .set(EnvironmentAttributes.FOG_START_DISTANCE, 0f)
                        .set(EnvironmentAttributes.FOG_END_DISTANCE, 128f)
                        .set(EnvironmentAttributes.SKY_COLOR, vector3fFromRGB24(0xff0a0a0a))
                        .set(EnvironmentAttributes.SKY_LIGHT_FACTOR, 0f)
                        .set(EnvironmentAttributes.AMBIENT_LIGHT_COLOR, vector3fFromRGB24(0xff0a0a0a))
                        .set(EnvironmentAttributes.BED_RULE, new BedRule(BedRule.Rule.NEVER, BedRule.Rule.NEVER, false, false, Optional.of(Component.translatable("block.minecraft.bed.no_sleep"))))
                        .set(EnvironmentAttributes.STRAW_BED_RULE, BedRule.DESTROY_ON_LEAVE)
                        .set(EnvironmentAttributes.PIGLINS_ZOMBIFY, false)
                        .build(),
                timelines.getOrThrow(SegfaultModTimelineTags.IN_MEMORY),
                Optional.empty()
        ));
    }
}
