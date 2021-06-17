package com.github.sachin.cosmin.economy;

import com.github.sachin.cosmin.Cosmin;

import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.economy.Economy;

public class EconomyManager {

    private Economy economy;
    private boolean isVaultEnabled;

    public EconomyManager(Cosmin plugin){
        isVaultEnabled = false;
        if(!plugin.getServer().getPluginManager().isPluginEnabled("Vault")){
            // isVaultEnabled = false;
            plugin.getLogger().info("Vault not found disabling economy features");
            return;
        }
        RegisteredServiceProvider<Economy> provider = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if(provider == null){
            plugin.getLogger().info("Error occured while loading Economy provider from vault");
            return;
        }
        economy = provider.getProvider();
        isVaultEnabled = true;
        plugin.getLogger().info("Vault integration successfully registered");
    }

    public boolean isVaultEnabled() {
        return isVaultEnabled;
    }

    public void withdraw(Player player,int amount){
        economy.withdrawPlayer(player, amount);
    }

    public void deposit(Player player,int amount){
        economy.depositPlayer(player, amount);
    }

    public double getBalance(Player player){
        return economy.getBalance(player);
    }
    
}
