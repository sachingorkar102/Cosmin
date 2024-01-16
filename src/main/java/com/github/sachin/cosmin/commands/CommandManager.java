package com.github.sachin.cosmin.commands;


import java.util.ArrayList;

import com.github.sachin.cosmin.Cosmin;
import com.github.sachin.cosmin.utils.CosminConstants;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class CommandManager implements CommandExecutor {


    private ArrayList<SubCommands> subcommands = new ArrayList<>();
    private Cosmin plugin;
    

    public CommandManager(Cosmin plugin){
        this.plugin = plugin;
    }

    public void registerSubCommands(){
        subcommands.add(new ReloadCommand());
        subcommands.add(new GiveCommand(plugin));
        subcommands.add(new ViewCommand(plugin));
        subcommands.add(new ClearCommand(plugin));
        subcommands.add(new HelpCommand(plugin));
        subcommands.add(new EquipCommand(plugin));
        subcommands.add(new DEquipCommand(plugin));
        subcommands.add(new TakeCommand(plugin));
        subcommands.add(new BuyCommand(plugin));
        subcommands.add(new GeneratePackCommand());
        subcommands.add(new ImportItemCommand());
        subcommands.add(new OpenWardrobeCommand());
        subcommands.add(new OpenStoreCommand());
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length > 0){
            for (int i = 0; i < getSubcommands().size(); i++){
                if(!args[0].equals(getSubcommands().get(i).getName())) continue;
                SubCommands sub = getSubcommands().get(i);
                if(sub.getMaxArgs() > args.length){
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', sub.getUsage()));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', sub.getDescription()));
                }
                else if((sender instanceof Player)){
                    Player p = (Player) sender;
                    if(p.hasPermission(sub.getPermission()) || p.hasPermission(CosminConstants.PERM_COMMAND_ALL)){
                        sub.perform(sender, args);
                    }
                    else{
                        plugin.getMessageManager().sendMessage(CosminConstants.M_NO_PERM, p);
                    }
                }
                else{
                    sub.perform(sender, args);
                }
                break;
            }
        }
        else{
            if(sender.hasPermission(CosminConstants.PERM_COMMAND_HELP) || sender.hasPermission(CosminConstants.PERM_COMMAND_ALL)){
                getSubcommands().get(4).perform(sender, args);
            }
        }
        
        return true;
    }

    public ArrayList<SubCommands> getSubcommands(){
        return subcommands;
    }

    
}