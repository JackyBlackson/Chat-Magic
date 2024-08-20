package com.jackyblackson.modfabric;

import com.jackyblackson.modfabric.bstats.BstatsMetrics;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

public class ChatMagicClient implements ClientModInitializer {


	@Override
	public void onInitializeClient() {
		System.out.println("==========================");
		System.out.println("[CHAT MAGIC] Hello, world!");
		System.out.println("[CHAT MAGIC] Hello, world!");
		System.out.println("[CHAT MAGIC] Hello, world!");
		System.out.println("[CHAT MAGIC] Hello, world!");
		System.out.println("[CHAT MAGIC] Hello, world!");
		System.out.println("==========================");

		int pluginId = 23086; // <-- Replace with the id of your plugin!
		BstatsMetrics metrics = new BstatsMetrics("chatmagic", pluginId, true, true, true);
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
	}
}