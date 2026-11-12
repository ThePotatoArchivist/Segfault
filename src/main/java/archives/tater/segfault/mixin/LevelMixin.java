package archives.tater.segfault.mixin;

import archives.tater.segfault.registry.SegfaultModEnvironment;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.world.attribute.EnvironmentAttributeSystem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

@Mixin(Level.class)
public abstract class LevelMixin {
	@Shadow
	public abstract EnvironmentAttributeSystem environmentAttributes();

	@Shadow
	public abstract DimensionType dimensionType();

	@ModifyReturnValue(
			method = "canHaveWeather",
			at = @At("RETURN")
	)
	private boolean checkAttribute(boolean original) {
		if (!original) return false;

		var attributes = environmentAttributes();
		if (attributes != null) return attributes.getDimensionValue(SegfaultModEnvironment.CAN_HAVE_WEATHER);
		// canHaveWeather is invoked before environmentAttributes is initialized >:(

		var modifier = dimensionType().attributes().get(SegfaultModEnvironment.CAN_HAVE_WEATHER);
		if (modifier == null) return true;

		return modifier.applyModifier(true);
	}
}