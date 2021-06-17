package com.github.sachin.cosmin.commands;


import org.bukkit.command.CommandSender;


public abstract class SubCommands {

  
    public abstract String getName();

    public abstract String getPermission();

    public abstract String getUsage();

    public abstract String getDescription();

    public abstract void perform(CommandSender sender,String[] args);

}