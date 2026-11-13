package archives.tater.segfault.client;

import archives.tater.segfault.client.screen.SingleSlotScreen;
import archives.tater.segfault.registry.SegfaultModComponents;
import archives.tater.segfault.registry.SegfaultModMenus;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.item.v1.ItemComponentTooltipProviderRegistry;

import net.minecraft.client.gui.screens.MenuScreens;

public class SegfaultClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		ItemComponentTooltipProviderRegistry.addFirst(SegfaultModComponents.POINTER);
		MenuScreens.register(SegfaultModMenus.SINGLE_SLOT, SingleSlotScreen::new);
	}
}