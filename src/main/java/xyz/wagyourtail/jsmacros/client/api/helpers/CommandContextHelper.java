package xyz.wagyourtail.jsmacros.client.api.helpers;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.block.Block;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

/**
 * @since 1.4.2
 */
public class CommandContextHelper extends BaseHelper<CommandContext<ICommandSender>> {
    public CommandContextHelper(CommandContext<ICommandSender> base) {
        super(base);
    }

    /**
     * @param name
     *
     * @return
     * @since 1.4.2
     * @throws CommandSyntaxException
     */
    public Object getArg(String name) throws CommandSyntaxException {
        Object arg = base.getArgument(name, Object.class);
        if (arg instanceof Block) {
            arg = Block.blockRegistry.getNameForObject((Block) arg).toString();
        } else if (arg instanceof ResourceLocation) {
            arg = arg.toString();
        } else if (arg instanceof Item) {
            arg = new ItemStackHelper(new ItemStack((Item) arg, 1));
        } else if (arg instanceof NBTBase) {
            arg = NBTElementHelper.resolve((NBTBase) arg);
        } else if (arg instanceof IChatComponent) {
            arg = new TextHelper((IChatComponent) arg);
        }
        return arg;
    }

    public CommandContextHelper getChild() {
        return new CommandContextHelper(base.getChild());
    }

    public StringRange getRange() {
        return base.getRange();
    }

    public String getInput() {
        return base.getInput();
    }
}
