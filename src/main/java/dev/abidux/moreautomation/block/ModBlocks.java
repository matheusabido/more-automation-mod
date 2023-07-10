package dev.abidux.moreautomation.block;

import dev.abidux.moreautomation.MoreAutomationMod;
import dev.abidux.moreautomation.block.custom.*;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MoreAutomationMod.MOD_ID);

    public static final RegistryObject<Block> AUTO_WORKBENCH = BLOCKS.register("auto_workbench", AutoWorkbenchBlock::new);
    public static final RegistryObject<Block> PLACER = BLOCKS.register("placer", PlacerBlock::new);
    public static final RegistryObject<Block> HARVESTER = BLOCKS.register("harvester", HarvesterBlock::new);
    public static final RegistryObject<Block> TRANSPORTER = BLOCKS.register("transporter", TransporterBlock::new);
    public static final RegistryObject<Block> FILTER = BLOCKS.register("filter", FilterBlock::new);

}