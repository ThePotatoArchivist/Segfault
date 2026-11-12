package archives.tater.segfault.mixin;

import archives.tater.segfault.registry.SegfaultModDimensionTypeTags;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

@Mixin(Level.class)
public abstract class LevelMixin {

	@Shadow
	public abstract DimensionType dimensionType();

	@Shadow
	public abstract RegistryAccess registryAccess();

	@ModifyReturnValue(
			method = "canHaveWeather",
			at = @At("RETURN")
	)
	private boolean checkAttribute(boolean original) {
		return original && !registryAccess().lookupOrThrow(Registries.DIMENSION_TYPE).wrapAsHolder(dimensionType()).is(SegfaultModDimensionTypeTags.HAS_NO_WEATHER);
	}
}