package com.github.sachin.cosmin.commands;

import com.github.sachin.cosmin.Cosmin;
import com.github.sachin.cosmin.utils.CosminConstants;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class HelpCommand extends SubCommands{

    private Cosmin plugin;

    public HelpCommand(Cosmin plugin){
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getPermission() {
        return CosminConstants.PERM_COMMAND_HELP;
    }

    @Override
    public String getUsage() {
        return "&3/cosmin &fhelp";
    }

    @Override
    public String getDescription() {
        return "Shows list of all commands present in cosmin";
    }

    @Override
    public int getMaxArgs() {
        return 1;
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        List<SubCommands> list = plugin.getCommandManager().getSubcommands();
        StringBuffer buffer = new StringBuffer();
        buffer.append("\n");
        buffer.append(ChatColor.translateAlternateColorCodes('&', "&7--------&eCosmin&7--------\n"));
        buffer.append(ChatColor.DARK_AQUA+"/"+plugin.getConfigUtils().getCommandAliases().get(0)+"\n");
        buffer.append(ChatColor.AQUA+"["+CosminConstants.PERM_COMMAND_COSMETICS+"]\n");
        buffer.append(ChatColor.GRAY+""+ChatColor.ITALIC+"opens cosmetic gui"+"\n");
        for (SubCommands subCommands : list) {
            buffer.append(ChatColor.translateAlternateColorCodes('&', subCommands.getUsage())+"\n");
            buffer.append(ChatColor.AQUA+"["+subCommands.getPermission()+"]\n");
            buffer.append(ChatColor.GRAY+""+ChatColor.ITALIC+""+subCommands.getDescription()+"\n");
        }
        buffer.append(ChatColor.GRAY+"----------------------\n");
        sender.sendMessage(buffer.toString());
    }
    
}
