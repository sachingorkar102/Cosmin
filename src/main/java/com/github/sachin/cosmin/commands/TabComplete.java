package com.github.sachin.cosmin.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.sachin.cosmin.Cosmin;
import com.github.sachin.cosmin.utils.CItemSlot;
import com.github.sachin.cosmin.utils.CosminConstants;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;


public class TabComplete implements TabCompleter {

    private Cosmin plugin;

    public TabComplete(Cosmin plugin){
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> arguments = new ArrayList<>();
        if(args.length == 1){
            plugin.getCommandManager().getSubcommands().forEach(s -> {
                if(sender.hasPermission(s.getPermission()) || sender.hasPermission(CosminConstants.PERM_COMMAND_ALL)){
                    arguments.add(s.getName());
                }
            });
            return getBetterArgs(arguments, args[0]);
        }
        else if(args.length == 2){
            if(args[0].equalsIgnoreCase("give")){
                plugin.getArmorManager().getInternalNames().forEach(s -> arguments.add(s));
                return getBetterArgs(arguments, args[1]);
            }
            if(args[0].equalsIgnoreCase("equip") || args[0].equalsIgnoreCase("dequip") || args[0].equalsIgnoreCase("import") || args[0].equalsIgnoreCase("openwardrobe") || args[0].equalsIgnoreCase("openstore")){
                Arrays.asList(CItemSlot.values()).forEach(s -> arguments.add(s.toString()));
                if(args[0].equalsIgnoreCase("openwardrobe") || args[0].equalsIgnoreCase("openstore")){
                    arguments.add("SET");
                }
                if(args[0].equalsIgnoreCase("dequip")){
                    arguments.add("ALL");
                }
                return getBetterArgs(arguments, args[1]);
            }   
        }
        else if(args.length == 3){
            if(args[0].equalsIgnoreCase("equip") || args[0].equalsIgnoreCase("take") || args[0].equalsIgnoreCase("buy")){
                plugin.getArmorManager().getInternalNames().forEach(s -> arguments.add(s));
                plugin.getArmorManager().getCosmeticSets().keySet().forEach(s -> arguments.add(s));
                return getBetterArgs(arguments, args[2]);
            }
            if(args[0].equalsIgnoreCase("generate")){
                return getBetterArgs(new ArrayList<>(Arrays.asList("true","false")), args[2]);
            }
        }
        return null;
    }

    public List<String> getBetterArgs(List<String> normalArgs,String currentArg){
        List<String> betterArgs = new ArrayList<>();
        normalArgs.forEach(s -> {
            if(s.startsWith(currentArg)){
                betterArgs.add(s);
            }
        });
        if(betterArgs.isEmpty()){
            return normalArgs;
        }
        else{
            return betterArgs;
        }
    }



}   