package dev.abidux.moreautomation.gui;

import dev.abidux.moreautomation.MoreAutomationMod;
import dev.abidux.moreautomation.gui.autoworkbench.AutoWorkbenchMenu;
import dev.abidux.moreautomation.gui.placer.PlacerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {

    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MoreAutomationMod.MOD_ID);

    public static final RegistryObject<MenuType<AutoWorkbenchMenu>> AUTO_WORKBENCH = MENU_TYPES.register("auto_workbench",
            () -> IForgeMenuType.create(AutoWorkbenchMenu::new));

    public static final RegistryObject<MenuType<PlacerMenu>> PLACER = MENU_TYPES.register("placer",
            () -> IForgeMenuType.create(PlacerMenu::new));

}