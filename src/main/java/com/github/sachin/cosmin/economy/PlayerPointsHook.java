package com.github.sachin.cosmin.economy;

import java.util.concurrent.ExecutionException;

import com.github.sachin.cosmin.Cosmin;
import com.github.sachin.cosmin.armor.CosmeticSet;
import com.github.sachin.cosmin.armor.CosminArmor;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.entity.Player;

public class PlayerPointsHook implements CosminEconomy{

    private PlayerPointsAPI ppa;
    private Cosmin plugin;

    public PlayerPointsHook(Cosmin plugin){
        this.plugin = plugin;
        ppa = PlayerPoints.getInstance().getAPI();
        if(ppa != null){
            plugin.isEconomyEnabled = true;
            plugin.getLogger().info("PlayerPoints integration successfully registered");
        }
    }

    @Override
    public void withDraw(Player player, int amount) {
        ppa.takeAsync(player.getUniqueId(), amount);
    }

    @Override
    public double getBalance(Player player) {
        try {
            return Double.valueOf(ppa.lookAsync(player.getUniqueId()).get());
        } catch (InterruptedException e) {
            return 0;
        } catch (ExecutionException e) {
            return 0;
        }
    }

    @Override
    public void deposit(Player player, int amount) {
        ppa.giveAsync(player.getUniqueId(), amount);
    }

    
    
}
