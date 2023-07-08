package dev.abidux.moreautomation.item;

import dev.abidux.moreautomation.MoreAutomationMod;
import dev.abidux.moreautomation.block.ModBlocks;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MoreAutomationMod.MOD_ID);

    public static final RegistryObject<Item> AUTO_WORKBENCH = ITEMS.register("auto_workbench", () -> new BlockItem(ModBlocks.AUTO_WORKBENCH.get(), new Item.Properties()));
    public static final RegistryObject<Item> PLACER = ITEMS.register("placer", () -> new BlockItem(ModBlocks.PLACER.get(), new Item.Properties()));
    public static final RegistryObject<Item> HARVESTER = ITEMS.register("harvester", () -> new BlockItem(ModBlocks.HARVESTER.get(), new Item.Properties()));

}