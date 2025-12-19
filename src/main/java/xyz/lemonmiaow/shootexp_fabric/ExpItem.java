package xyz.lemonmiaow.shootexp_fabric;

import xyz.lemonmiaow.shootexp_fabric.config.Language;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemLore;

import java.util.List;
import java.util.stream.Collectors;

public class ExpItem {
    private static final String TAG_OWNER = "shootexp_owner";
    private static final String TAG_RECIPIENT = "shootexp_recipient";
    private static final String TAG_AMOUNT = "shootexp_amount";
    private static final String TAG_MARKER = "shootexp_item";

    public static ItemStack create(String owner, String recipient, int amount) {
        ItemStack stack = new ItemStack(Items.BONE_MEAL);


        CompoundTag tag = new CompoundTag();
        tag.putBoolean(TAG_MARKER, true);
        tag.putString(TAG_OWNER, owner);
        tag.putString(TAG_RECIPIENT, recipient);
        tag.putInt(TAG_AMOUNT, amount);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));

        String itemName = Language.getString("shootexp.item.name")
                .replace("%OWNER%", owner)
                .replace("%RECIPIENT%", recipient)
                .replace("%AMOUNT%", String.valueOf(amount));
        stack.set(DataComponents.ITEM_NAME, Component.literal(itemName));

        List<String> loreStrings = Language.getStringList("shootexp.item.lore");
        List<Component> loreComponents = loreStrings.stream()
                .map(line -> line
                        .replace("%OWNER%", owner)
                        .replace("%RECIPIENT%", recipient)
                        .replace("%AMOUNT%", String.valueOf(amount)))
                .map(Component::literal)
                .collect(Collectors.toList());
        stack.set(DataComponents.LORE, new ItemLore(loreComponents));

        return stack;
    }

    public static boolean isExpItem(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) {
            return false;
        }
        return customData.copyTag().getBoolean(TAG_MARKER);
    }

    public static String getOwner(ItemStack stack) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) {
            return "UNKNOWN";
        }
        return customData.copyTag().getString(TAG_OWNER);
    }

    public static String getRecipient(ItemStack stack) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) {
            return "UNKNOWN";
        }
        return customData.copyTag().getString(TAG_RECIPIENT);
    }

    public static int getAmount(ItemStack stack) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) {
            return 0;
        }
        return customData.copyTag().getInt(TAG_AMOUNT);
    }
}
