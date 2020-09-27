package com.jishunamatata.floraresourca.core.registry;

import java.util.function.Supplier;

import com.jishunamatata.floraresourca.common.blocks.DoubleCropBlock;
import com.jishunamatata.floraresourca.core.FloraResourca;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class FloraResourcaBlocks {

	public static DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, FloraResourca.MODID);

	public static final RegistryObject<Block> COAL_CATTAIL = registerBlock("coal_cattail",
			() -> new DoubleCropBlock(Block.Properties.from(Blocks.TALL_GRASS)), ItemGroup.DECORATIONS, true);

	public static <B extends Block> RegistryObject<B> registerBlock(String name, Supplier<? extends B> supplier,
			ItemGroup itemGroup, boolean item) {
		RegistryObject<B> block = BLOCKS.register(name, supplier);
		if (item)
			FloraResourcaItems.ITEMS.register(name,
					() -> new BlockItem(block.get(), new Item.Properties().group(itemGroup)));
		return block;
	}

}
