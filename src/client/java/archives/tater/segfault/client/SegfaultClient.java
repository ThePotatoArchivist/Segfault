package archives.tater.segfault.client;

import archives.tater.segfault.registry.SegfaultModComponents;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.item.v1.ItemComponentTooltipProviderRegistry;

public class SegfaultClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		ItemComponentTooltipProviderRegistry.addFirst(SegfaultModComponents.POINTER);
	}
}