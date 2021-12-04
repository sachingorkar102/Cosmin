package com.github.sachin.cosmin.economy;

import com.github.sachin.cosmin.Cosmin;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultHook implements CosminEconomy{

    private Economy economy;

    public VaultHook(Cosmin plugin){
        RegisteredServiceProvider<Economy> provider = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if(provider == null){
            plugin.getLogger().info("Error occured while loading economy provider from vault");
            return;
        }
        economy = provider.getProvider();
        plugin.isEconomyEnabled = true;
        plugin.getLogger().info("Vault integration successfully registered");
    }

    @Override
    public void withDraw(Player player, int amount) {
        economy.withdrawPlayer(player, amount);
    }

    @Override
    public double getBalance(Player player) {
        return economy.getBalance(player);
    }

    @Override
    public void deposit(Player player, int amount) {
        economy.depositPlayer(player,amount);
    }


  
    
}
