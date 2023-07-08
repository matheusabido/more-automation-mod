package dev.abidux.moreautomation.block.entities;

import dev.abidux.moreautomation.MoreAutomationMod;
import dev.abidux.moreautomation.block.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MoreAutomationMod.MOD_ID);

    public static final RegistryObject<BlockEntityType<AutoWorkbenchBlockEntity>> AUTO_WORKBENCH = BLOCK_ENTITIES.register("auto_workbench",
            () -> BlockEntityType.Builder.of(AutoWorkbenchBlockEntity::new, ModBlocks.AUTO_WORKBENCH.get()).build(null));

    public static final RegistryObject<BlockEntityType<PlacerBlockEntity>> PLACER = BLOCK_ENTITIES.register("placer",
            () -> BlockEntityType.Builder.of(PlacerBlockEntity::new, ModBlocks.PLACER.get()).build(null));

    public static final RegistryObject<BlockEntityType<HarvesterBlockEntity>> HARVESTER = BLOCK_ENTITIES.register("harvester",
            () -> BlockEntityType.Builder.of(HarvesterBlockEntity::new, ModBlocks.HARVESTER.get()).build(null));

}