package com.github.sachin.cosmin.economy;

import com.github.sachin.cosmin.Cosmin;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.entity.Player;

import java.util.concurrent.ExecutionException;

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
