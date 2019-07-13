package me.falsehonesty.asmhelpermod;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.common.DummyModContainer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AsmHelperMod extends DummyModContainer {
    public AsmHelperMod() {
        ClientCommandHandler.instance.registerCommand(new ICommand() {
            @Override
            public String getCommandName() {
                return "dummy";
            }

            @Override
            public String getCommandUsage(ICommandSender sender) {
                return null;
            }

            @Override
            public List<String> getCommandAliases() {
                return new ArrayList();
            }

            @Override
            public void processCommand(ICommandSender sender, String[] args) throws CommandException {
                Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(
                        new ChatComponentText("lmaoooo"),
                        1337
                );
            }

            @Override
            public boolean canCommandSenderUseCommand(ICommandSender sender) {
                return true;
            }

            @Override
            public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
                return null;
            }

            @Override
            public boolean isUsernameIndex(String[] args, int index) {
                return false;
            }

            @Override
            public int compareTo(@NotNull ICommand o) {
                return 0;
            }
        });
    }

    @Override
    public String getModId() {
        return "asmhelpermod";
    }

    @Override
    public String getName() {
        return "Asm Helper Mod";
    }

    @Override
    public String getVersion() {
        return "1";
    }
}
