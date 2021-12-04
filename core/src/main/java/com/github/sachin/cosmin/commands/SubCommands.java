package com.github.sachin.cosmin.commands;


import com.github.sachin.cosmin.Cosmin;
import org.bukkit.command.CommandSender;


public abstract class SubCommands {

    protected Cosmin plugin;

    public SubCommands(){
        this.plugin = Cosmin.getInstance();
    }

    public SubCommands(Cosmin plugin){
        this.plugin = plugin;
    }

  
    public abstract String getName();

    public abstract String getPermission();

    public abstract String getUsage();

    public abstract String getDescription();

    public abstract void perform(CommandSender sender,String[] args);

    public abstract int getMaxArgs();

}