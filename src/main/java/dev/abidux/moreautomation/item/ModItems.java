package dev.abidux.moreautomation.item;

import dev.abidux.moreautomation.MoreAutomationMod;
import dev.abidux.moreautomation.block.ModBlocks;
import dev.abidux.moreautomation.item.custom.PortableTranspositionerItem;
import dev.abidux.moreautomation.item.custom.TransporterRemoteItem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MoreAutomationMod.MOD_ID);

    public static final RegistryObject<Item> AUTO_WORKBENCH = registerBlockItem("auto_workbench", ModBlocks.AUTO_WORKBENCH, "tooltip.auto_workbench_guide");
    public static final RegistryObject<Item> PLACER = registerBlockItem("placer", ModBlocks.PLACER, "tooltip.placer_guide");
    public static final RegistryObject<Item> HARVESTER = registerBlockItem("harvester", ModBlocks.HARVESTER, "tooltip.harvester_guide");
    public static final RegistryObject<Item> TRANSPORTER = registerBlockItem("transporter", ModBlocks.TRANSPORTER, "tooltip.transporter_guide");
    public static final RegistryObject<Item> FILTER = registerBlockItem("filter", ModBlocks.FILTER, "tooltip.filter_guide");
    public static final RegistryObject<Item> USER = registerBlockItem("user", ModBlocks.USER, "tooltip.user_guide");
    public static final RegistryObject<Item> TRANSPOSITIONER = registerBlockItem("transpositioner", ModBlocks.TRANSPOSITIONER, "tooltip.transpositioner_guide");
    public static final RegistryObject<Item> TRANSPOSITIONER_REMOTE = ITEMS.register("transpositioner_remote", TransporterRemoteItem::new);
    public static final RegistryObject<Item> PORTABLE_TRANSPOSITIONER = ITEMS.register("portable_transpositioner", PortableTranspositionerItem::new);

    private static RegistryObject<Item> registerBlockItem(String name, RegistryObject<Block> block, String translatable) {
        return ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()) {
            @Override
            public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> tooltip, TooltipFlag pFlag) {
                if (Screen.hasShiftDown()) {
                    String text = Component.translatable(translatable).getString();
                    Arrays.stream(text.split("\n")).map(Component::literal).forEach(tooltip::add);
                } else tooltip.add(Component.translatable("tooltip.press_shift_for_more").withStyle(ChatFormatting.YELLOW));
            }
        });
    }

}