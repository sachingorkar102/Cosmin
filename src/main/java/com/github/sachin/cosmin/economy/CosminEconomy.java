package com.github.sachin.cosmin.economy;

import com.github.sachin.cosmin.Cosmin;

import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;


public interface CosminEconomy {


    public void withDraw(Player player,int amount);

    public double getBalance(Player player);

    public void deposit(Player player,int amount);
    
}
