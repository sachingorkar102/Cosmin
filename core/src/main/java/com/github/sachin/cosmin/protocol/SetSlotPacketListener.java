package com.github.sachin.cosmin.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.github.sachin.cosmin.Cosmin;
import com.github.sachin.cosmin.player.CosminPlayer;
import com.github.sachin.cosmin.utils.CItemSlot;
import com.github.sachin.cosmin.utils.CosminConstants;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class SetSlotPacketListener extends PacketAdapter{


    private Cosmin plugin;

    private Map<Integer,CItemSlot> equipSlotMap = new HashMap<>();

    public SetSlotPacketListener(Cosmin plugin) {
        super(plugin, PacketType.Play.Server.SET_SLOT);
        this.plugin = plugin;
        equipSlotMap.clear();
        for(CItemSlot cSlot:CItemSlot.values()){
            equipSlotMap.put(cSlot.getEquipmentSlotId(), cSlot);
        }
    }

    @Override
    public void onPacketSending(PacketEvent e) {
        Player player = e.getPlayer();
        PacketContainer packet = e.getPacket();
        if(!plugin.getPlayerManager().containsPlayer(player) || packet.getIntegers().read(0) != 0) return;
        CosminPlayer cosminPlayer = plugin.getPlayerManager().getPlayer(player);
        int affectedSlot = packet.getIntegers().read(plugin.is1_17_1() ? 2 : 1);
        if(cosminPlayer.getInventoryOpen()) return;
        if(affectedSlot == 0 || affectedSlot == -1){
            cosminPlayer.setFakeSlotItems();
            return;
        }
        if(!CosminConstants.EQUIPMENT_SLOTS.contains(affectedSlot)) return;
        if(cosminPlayer.preventSetSlotPacket()){
            cosminPlayer.preventSetSlotPacket(false);
            return;
        }
        CItemSlot cSlot = equipSlotMap.get(affectedSlot);
        if(!cosminPlayer.getOrignalArmorMap().get(cSlot)){

            packet.getItemModifier().write(0, cosminPlayer.getSlotItem(cSlot));
        }
    }
}
