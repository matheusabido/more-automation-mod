package dev.abidux.moreautomation.item;

import dev.abidux.moreautomation.MoreAutomationMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MoreAutomationMod.MOD_ID);

    public static final RegistryObject<CreativeModeTab> MORE_AUTOMATION_TAB = CREATIVE_MODE_TABS.register("more_automation_tab",
            () -> CreativeModeTab.builder()
                    .icon(() -> ModItems.AUTO_WORKBENCH.get().getDefaultInstance())
                    .title(Component.literal("More Automation Mod"))
                    .displayItems((p, output) -> {
                        output.accept(ModItems.AUTO_WORKBENCH::get);
                        output.accept(ModItems.PLACER::get);
                        output.accept(ModItems.HARVESTER::get);
                        output.accept(ModItems.TRANSPORTER::get);
                        output.accept(ModItems.FILTER::get);
                    })
                    .build());

}