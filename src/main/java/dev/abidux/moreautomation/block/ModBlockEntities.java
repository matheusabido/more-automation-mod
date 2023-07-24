package dev.abidux.moreautomation.block;

import dev.abidux.moreautomation.MoreAutomationMod;
import dev.abidux.moreautomation.block.ModBlocks;
import dev.abidux.moreautomation.block.entities.*;
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
    public static final RegistryObject<BlockEntityType<TransporterBlockEntity>> TRANSPORTER = BLOCK_ENTITIES.register("transporter",
            () -> BlockEntityType.Builder.of(TransporterBlockEntity::new, ModBlocks.TRANSPORTER.get()).build(null));

    public static final RegistryObject<BlockEntityType<FilterBlockEntity>> FILTER = BLOCK_ENTITIES.register("filter",
            () -> BlockEntityType.Builder.of(FilterBlockEntity::new, ModBlocks.FILTER.get()).build(null));

    public static final RegistryObject<BlockEntityType<TranspositionerBlockEntity>> TRANSPOSITIONER = BLOCK_ENTITIES.register("transpositioner",
            () -> BlockEntityType.Builder.of(TranspositionerBlockEntity::new, ModBlocks.TRANSPOSITIONER.get()).build(null));

}