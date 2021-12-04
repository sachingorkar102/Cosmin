package com.github.sachin.cosmin.economy;

import org.bukkit.entity.Player;


public interface CosminEconomy {


    public void withDraw(Player player,int amount);

    public double getBalance(Player player);

    public void deposit(Player player,int amount);


    
}
