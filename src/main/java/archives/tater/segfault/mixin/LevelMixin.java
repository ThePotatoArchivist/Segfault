package archives.tater.segfault.mixin;

import archives.tater.segfault.registry.SegfaultModDimensionTypeTags;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.core.Holder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

@Mixin(Level.class)
public abstract class LevelMixin {

	@Shadow
	public abstract Holder<DimensionType> dimensionTypeRegistration();

	@ModifyReturnValue(
			method = "canHaveWeather",
			at = @At("RETURN")
	)
	private boolean checkAttribute(boolean original) {
		return original && !dimensionTypeRegistration().is(SegfaultModDimensionTypeTags.HAS_NO_WEATHER);
	}
}