package com.github.sachin.cosmin.protocol;

import java.util.ArrayList;
import java.util.List;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.Pair;
import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;
import com.github.sachin.cosmin.Cosmin;
import com.github.sachin.cosmin.player.CosminPlayer;
import com.github.sachin.cosmin.utils.CItemSlot;
import com.github.sachin.cosmin.utils.CosminConstants;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EntityEquipmentPacketListener extends PacketAdapter{

    private Cosmin plugin;

    public EntityEquipmentPacketListener(Cosmin plugin) {
        super(plugin, PacketType.Play.Server.ENTITY_EQUIPMENT);
        this.plugin = plugin;
    }


    @Override
    public void onPacketSending(PacketEvent e) {

        PacketContainer packet = e.getPacket();
        int entityId = packet.getIntegers().read(0);
        if(!plugin.getEntityIdMap().keySet().contains(entityId)) return;
        Player player = plugin.getEntityIdMap().get(entityId);
        if(!plugin.getPlayerManager().containsPlayer(player) || e.getPlayer().hasPermission(CosminConstants.PERM_REALARMOR)) return;
        CosminPlayer cosminPlayer = plugin.getPlayerManager().getPlayer(player);
        if(cosminPlayer.preventEntityEquipPacket()){
            cosminPlayer.preventEntityEquipPacket(false);

            return;
        }
        CItemSlot affectedSlot;
        if(plugin.postNetherUpdate){
            List<Pair<ItemSlot,ItemStack>> newPairs = new ArrayList<>();
            for (CItemSlot slot: CItemSlot.values()) {
                if(cosminPlayer.getOrignalArmorMap().get(slot)){
                    newPairs.add(new Pair<>(slot.getProtocolSlot(),player.getInventory().getItem(slot.getAltSlotId())));
                }
                else{
                    newPairs.add(new Pair<>(slot.getProtocolSlot(),cosminPlayer.getEquipmentMap().get(slot)));
                }
            }
            newPairs.add(new Pair<>(ItemSlot.MAINHAND,player.getInventory().getItemInMainHand()));
            packet.getSlotStackPairLists().write(0, newPairs);
            return;
        }

        if(packet.getItemSlots().read(0) == ItemSlot.MAINHAND) return;
        affectedSlot = CItemSlot.valueOf(packet.getItemSlots().read(0).toString());
        
        if(!cosminPlayer.getOrignalArmorMap().get(affectedSlot)){
            packet.getItemModifier().write(0, cosminPlayer.getEquipmentMap().get(affectedSlot));
        }
    }

    

    
}
