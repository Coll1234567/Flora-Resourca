package com.jishunamatata.floraresourca.core;

import com.jishunamatata.floraresourca.core.registry.FloraResourcaBlocks;
import com.jishunamatata.floraresourca.core.registry.FloraResourcaItems;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(FloraResourca.MODID)
public class FloraResourca {

	public static final String MODID = "flora_resourca";

	public FloraResourca() {
		FloraResourcaBlocks.BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
		FloraResourcaItems.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
	}

}
