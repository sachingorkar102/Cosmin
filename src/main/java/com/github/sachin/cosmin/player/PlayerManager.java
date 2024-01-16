package com.github.sachin.cosmin.player;

import com.github.sachin.cosmin.Cosmin;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class PlayerManager {

    private final Map<UUID,CosminPlayer> cosminPlayers = new HashMap<>();

    public void addPlayer(CosminPlayer player){
        cosminPlayers.put(player.getUuid(), player);
    }

    public void removePlayer(CosminPlayer player){
        cosminPlayers.remove(player.getUuid());
    }

    public void removePlayer(UUID uuid){
        cosminPlayers.remove(uuid);
    }


    public CosminPlayer getPlayer(Player player){
       return cosminPlayers.get(player.getUniqueId());
    }

    public CosminPlayer getPlayer(UUID uuid){
        return cosminPlayers.get(uuid);
    }

    public boolean containsPlayer(Player player){
        return cosminPlayers.keySet().contains(player.getUniqueId());
    }

    public Collection<CosminPlayer> getCosminPlayers(){
       return Collections.unmodifiableCollection(cosminPlayers.values());
    }

    public Collection<UUID> getUuids(){
        return cosminPlayers.keySet();
    }

    public void clear(){
        cosminPlayers.clear();
    }

    public CosminPlayer createCosminPlayer(Player player){
        List<ItemStack> starterItems = Cosmin.getInstance().guiManager.getMainGuiTemplate(); 
        CosminPlayer cPlayer = new CosminPlayer(player.getUniqueId(),starterItems);
        cPlayer.computeAndPutEquipmentPairList();
        addPlayer(cPlayer);
        return cPlayer;
    }
    
}
