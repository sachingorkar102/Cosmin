package com.github.sachin.cosmin.commands;

import com.github.sachin.cosmin.Cosmin;
import com.github.sachin.cosmin.utils.CosminConstants;
import org.bukkit.command.CommandSender;

public class ReloadCommand extends SubCommands{

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getPermission() {
        return CosminConstants.PERM_COMMAND_RELOAD;
    }

    @Override
    public String getUsage() {
        return "&3/cosmin &freload";
    }

    @Override
    public String getDescription() {
        return "Reloads all cosmin config files";
    }

    @Override
    public int getMaxArgs() {
        return 1;
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        Cosmin.getInstance().reloadAllConfigs();
        plugin.getMessageManager().sendMessage(CosminConstants.M_RELOADED, sender);
    }
    
}
