package dev.abidux.moreautomation;

import dev.abidux.moreautomation.block.ModBlocks;
import dev.abidux.moreautomation.block.entities.ModBlockEntities;
import dev.abidux.moreautomation.gui.ModMenuTypes;
import dev.abidux.moreautomation.gui.autoworkbench.AutoWorkbenchScreen;
import dev.abidux.moreautomation.gui.harvester.HarvesterMenu;
import dev.abidux.moreautomation.gui.harvester.HarvesterScreen;
import dev.abidux.moreautomation.gui.placer.PlacerScreen;
import dev.abidux.moreautomation.item.ModCreativeTabs;
import dev.abidux.moreautomation.item.ModItems;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(MoreAutomationMod.MOD_ID)
public class MoreAutomationMod {

    // TODO
    // adapt for melon, pumpkin, berry bush...

    public static final String MOD_ID = "moreautomationmod";
    public MoreAutomationMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);

        ModItems.ITEMS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModMenuTypes.MENU_TYPES.register(modEventBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::addCreative);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(ModItems.AUTO_WORKBENCH::get);
        } else if (event.getTabKey() == CreativeModeTabs.REDSTONE_BLOCKS) {
            event.accept(ModItems.PLACER::get);
            event.accept(ModItems.HARVESTER::get);
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            MenuScreens.register(ModMenuTypes.AUTO_WORKBENCH.get(), AutoWorkbenchScreen::new);
            MenuScreens.register(ModMenuTypes.PLACER.get(), PlacerScreen::new);
            MenuScreens.register(ModMenuTypes.HARVESTER.get(), HarvesterScreen::new);
        }
    }
}
