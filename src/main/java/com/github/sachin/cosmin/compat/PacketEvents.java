package com.github.sachin.cosmin.compat;


import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketListener;
import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketListenerPriority;
import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketSendEvent;
import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetSlot;
import ac.grim.grimac.shaded.io.github.retrooper.packetevents.util.SpigotConversionUtil;
import com.github.sachin.cosmin.Cosmin;
import com.github.sachin.cosmin.player.CosminPlayer;
import com.github.sachin.cosmin.utils.CItemSlot;
import com.github.sachin.cosmin.utils.CosminConstants;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import sun.security.provider.ConfigFile;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class PacketEvents implements PacketListener {

    private final Cosmin plugin;

    private Map<Integer,CItemSlot> equipSlotMap = new HashMap<>();

    public PacketEvents(){
        this.plugin = Cosmin.getInstance();
        equipSlotMap.clear();
        for(CItemSlot cSlot:CItemSlot.values()){
            equipSlotMap.put(cSlot.getEquipmentSlotId(), cSlot);
        }
    }



    @Override
    public void onPacketSend(PacketSendEvent e) {
        if(e.getPacketType()== PacketType.Play.Server.SET_SLOT){
            WrapperPlayServerSetSlot packet = new WrapperPlayServerSetSlot(e);
            Player player = (Player) e.getPlayer();
            if(!plugin.getPlayerManager().containsPlayer(player) || packet.getWindowId() != 0) return;
            CosminPlayer cosminPlayer = plugin.getPlayerManager().getPlayer(player);
            int affectedSlot = packet.getSlot();
            if(cosminPlayer.getInventoryOpen()) return;
            if(affectedSlot == 0 || affectedSlot == -1){
                e.setCancelled(true);
                cosminPlayer.setFakeSlotItems();
                return;
            }
            if(!CosminConstants.EQUIPMENT_SLOTS.contains(affectedSlot)) return;

            CItemSlot cSlot = equipSlotMap.get(affectedSlot);
            if(!cosminPlayer.getOrignalArmorMap().get(cSlot)){
                e.setCancelled(true);
                setFakeSlotItem(cosminPlayer,affectedSlot,cosminPlayer.getSlotItem(cSlot));
            }
        }
    }

    public static void setFakeSlotItem(CosminPlayer cosminPlayer, int slot, ItemStack item){
        WrapperPlayServerSetSlot packet = new WrapperPlayServerSetSlot(0,0,slot,SpigotConversionUtil.fromBukkitItemStack(item));
        cosminPlayer.preventSetSlotPacket(true);
        Bukkit.getServer().getScheduler().runTaskAsynchronously(Cosmin.getInstance(), new Runnable() {
            @Override
            public void run() {
                ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents.getAPI().getPlayerManager().sendPacketSilently(cosminPlayer.getBukkitPlayer().get(),packet);
            }
        });
    }


    public static void enablePacketEvents(){
        ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents.getAPI().getEventManager().registerListener(new PacketEvents(),
                PacketListenerPriority.HIGHEST);
        ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents.getAPI().init();
    }

    public static void disablePacketEvents(){
        ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents.getAPI().terminate();
    }
}
