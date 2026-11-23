package archives.tater.segfault.client;

import archives.tater.segfault.Segfault;
import archives.tater.segfault.client.render.debug.AllocationDebugRenderer;
import archives.tater.segfault.client.screen.ConstructorScreen;
import archives.tater.segfault.client.screen.DestructorScreen;
import archives.tater.segfault.client.screen.SingleSlotScreen;
import archives.tater.segfault.registry.SegfaultModComponents;
import archives.tater.segfault.registry.SegfaultModDebug;
import archives.tater.segfault.registry.SegfaultModMenus;
import archives.tater.segfault.registry.SegfaultModNetworking;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.debug.v1.ClientDebugSubscriptionRegistry;
import net.fabricmc.fabric.api.client.debug.v1.renderer.DebugRendererRegistry;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.item.v1.ItemComponentTooltipProviderRegistry;
import net.fabricmc.loader.api.FabricLoader;

import net.minecraft.client.gui.components.debug.DebugEntryNoop;
import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;

public class SegfaultClient implements ClientModInitializer {
	public static final Identifier DEBUG_CHUNK_GRID = DebugScreenEntries.register(Segfault.id("chunk_grid"), new DebugEntryNoop());

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		ItemComponentTooltipProviderRegistry.addFirst(SegfaultModComponents.STORED_POINTER);
		ItemComponentTooltipProviderRegistry.addFirst(SegfaultModComponents.POINTER);
		ItemComponentTooltipProviderRegistry.addFirst(SegfaultModComponents.STRUCT_TEMPLATE);
		MenuScreens.register(SegfaultModMenus.SINGLE_SLOT, SingleSlotScreen::new);
		MenuScreens.register(SegfaultModMenus.CONSTRUCTOR, ConstructorScreen::new);
		MenuScreens.register(SegfaultModMenus.DEREFERENCER, SingleSlotScreen::new);
		MenuScreens.register(SegfaultModMenus.DESTRUCTOR, DestructorScreen::new);

		ClientPlayNetworking.registerGlobalReceiver(SegfaultModNetworking.TELEPORT_SOUND, (_, context) -> {
			context.client().getSoundManager().play(SimpleSoundInstance.forLocalAmbience(SoundEvents.PLAYER_TELEPORT, context.player().getRandom().nextFloat() * 0.4F + 0.8F, 1));
		});

		var renderAllocations = FabricLoader.getInstance().isDevelopmentEnvironment();
		ClientDebugSubscriptionRegistry.register(SegfaultModDebug.ALLOCATION, renderAllocations);
		DebugRendererRegistry.register(SegfaultModDebug.ALLOCATION, _ -> new AllocationDebugRenderer(), renderAllocations);
	}
}