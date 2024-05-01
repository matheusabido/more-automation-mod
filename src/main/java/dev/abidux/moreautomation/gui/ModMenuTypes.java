package dev.abidux.moreautomation.gui;

import dev.abidux.moreautomation.MoreAutomationMod;
import dev.abidux.moreautomation.gui.armor.ArmorMenu;
import dev.abidux.moreautomation.gui.autoworkbench.AutoWorkbenchMenu;
import dev.abidux.moreautomation.gui.filter.FilterMenu;
import dev.abidux.moreautomation.gui.harvester.HarvesterMenu;
import dev.abidux.moreautomation.gui.placer.PlacerMenu;
import dev.abidux.moreautomation.gui.portabletranspositioner.PortableTranspositionerMenu;
import dev.abidux.moreautomation.gui.treecutter.TreeCutterMenu;
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

    public static final RegistryObject<MenuType<HarvesterMenu>> HARVESTER = MENU_TYPES.register("harvester",
            () -> IForgeMenuType.create(HarvesterMenu::new));

    public static final RegistryObject<MenuType<FilterMenu>> FILTER = MENU_TYPES.register("filter",
            () -> IForgeMenuType.create(FilterMenu::new));

    public static final RegistryObject<MenuType<PortableTranspositionerMenu>> PORTABLE_TRANSPOSITIONER = MENU_TYPES.register("portable_transpositioner",
            () -> IForgeMenuType.create(PortableTranspositionerMenu::new));

    public static final RegistryObject<MenuType<ArmorMenu>> ARMOR = MENU_TYPES.register("armor",
            () -> IForgeMenuType.create(ArmorMenu::new));

    public static final RegistryObject<MenuType<TreeCutterMenu>> TREE_CUTTER = MENU_TYPES.register("tree_cutter",
            () -> IForgeMenuType.create(TreeCutterMenu::new));

}