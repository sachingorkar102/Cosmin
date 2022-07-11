package com.github.sachin.cosmin.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.github.sachin.cosmin.Cosmin;
import com.github.sachin.cosmin.player.CosminPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.logging.Level;

public class PlayerUseItemPacketListener extends PacketAdapter {


    private final Cosmin plugin;

    public PlayerUseItemPacketListener(Cosmin plugin) {
        super(plugin, PacketType.Play.Client.USE_ENTITY);
        this.plugin = plugin;
    }

    @Override
    public void onPacketReceiving(PacketEvent e) {
        PacketContainer packet = e.getPacket();
        Player player = e.getPlayer();
        CosminPlayer cPlayer = plugin.getPlayerManager().getPlayer(player);
        if(cPlayer==null) return;
//        if(packet.getEntityUseActions().read(0)!= EnumWrappers.EntityUseAction.ATTACK) return;
//        System.out.println("Fire");
//        cPlayer.computeAndPutEquipmentPairList();
//        cPlayer.setFakeSlotItems();
        new BukkitRunnable(){
            @Override
            public void run() {
                cPlayer.computeAndPutEquipmentPairList();
                cPlayer.setFakeSlotItems();

            }
        }.runTaskLater(plugin,1);
    }
}
