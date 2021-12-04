package com.github.sachin.cosmin.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.github.sachin.cosmin.Cosmin;
import com.github.sachin.cosmin.database.PlayerData;
import com.github.sachin.cosmin.player.CosminPlayer;
import com.github.sachin.cosmin.utils.InventoryUtils;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;

public class SpawnPlayerPacketListener extends PacketAdapter{

    private Cosmin plugin;


    public SpawnPlayerPacketListener(Cosmin plugin) {
        super(plugin, PacketType.Play.Server.NAMED_ENTITY_SPAWN);
        this.plugin = plugin;
    }

    @Override
    public void onPacketSending(PacketEvent e) {
        PacketContainer packet = e.getPacket();
        if(plugin.getEntityIdMap().keySet().contains(packet.getIntegers().read(0))){
            Player player = plugin.getEntityIdMap().get(packet.getIntegers().read(0));
            if(plugin.getPlayerManager().containsPlayer(player)){
                CosminPlayer cPlayer = plugin.getPlayerManager().getPlayer(player);
                equipCPlayer(cPlayer);
            }
            else if(plugin.getConfigUtils().isMySQLEnabled()){
                if(!plugin.MySQL().isConnected()) return;
                PlayerData playerData = new PlayerData(player.getUniqueId());
                if(playerData.playerExists() && !plugin.getPlayerManager().containsPlayer(player)){
                    CosminPlayer cPlayer = new CosminPlayer(player.getUniqueId(),Arrays.asList(InventoryUtils.base64ToItemStackArray(playerData.getPlayerData())));
                    // cPlayer.computeAndPutEquipmentPairList();
                    plugin.getPlayerManager().addPlayer(cPlayer);
                    equipCPlayer(cPlayer);
                }
            }
        }
    }

    private void equipCPlayer(CosminPlayer cPlayer){
        new BukkitRunnable(){
            @Override
            public void run() {
                if(cPlayer.getBukkitPlayer().isPresent()){
                    cPlayer.sendPacketWithinRange(60);
                }
            }
        }.runTaskLater(plugin, 5);
    }
    
}
