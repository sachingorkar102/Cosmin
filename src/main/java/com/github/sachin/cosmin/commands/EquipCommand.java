package com.github.sachin.cosmin.commands;

import com.github.sachin.cosmin.Cosmin;
import com.github.sachin.cosmin.armor.CosminArmor;
import com.github.sachin.cosmin.player.CosminPlayer;
import com.github.sachin.cosmin.utils.CItemSlot;
import com.github.sachin.cosmin.utils.CosminConstants;
import com.github.sachin.cosmin.utils.ItemBuilder;
import com.google.common.base.Enums;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

// this command is under progress
public class EquipCommand extends SubCommands{

    private Cosmin plugin;

    public EquipCommand(Cosmin plugin){
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "equip";
    }

    @Override
    public String getPermission() {
        return CosminConstants.PERM_COMMAND_EQUIP;
    }

    @Override
    public String getUsage() {
        return "&3/cosmin &fequip &9[slot_type] [item_name] [player_name] (forceequip) (replace)";
    }

    @Override
    public String getDescription() {
        return "equips a item for specified player which cant be removed if forceequip is set to true, if replace is true it will replace already existing armor";
    }

    @Override
    public int getMaxArgs() {
        return 4;
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if(args.length >= 4){
            String armorName = args[2];
            boolean invalidArmor = false;
            boolean isSet = false;
            boolean forceequip = false;
            boolean replace = true;
            if(plugin.getArmorManager().containsArmor(armorName)){
                CosminArmor armor = plugin.getArmorManager().getArmor(armorName);

            }
            else if(plugin.getArmorManager().containsSet(armorName)){
                isSet = true;
            }
            else{
                invalidArmor = true;
            }
            CItemSlot slotType = Enums.getIfPresent(CItemSlot.class, args[1]).orNull();
            CosminArmor cArmor = plugin.getArmorManager().getArmor(args[2]);
            Player player = Bukkit.getPlayer(args[3]);
            if(args.length >= 5){
                if(args[4].equalsIgnoreCase("true")){
                    forceequip = true;
                }
            }
            if(args.length == 6){
                if(args[5].equalsIgnoreCase("true")){
                    replace = true;
                }   
            }
            if(player == null){
                plugin.getMessageManager().sendMessage(CosminConstants.M_OFFLINE_PLAYER, sender);
                return;
            }
            if(invalidArmor){
                plugin.getMessageManager().sendMessage(CosminConstants.M_INVALID_ITEM, sender);
                return;
            }
            if(slotType == null){
                plugin.getMessageManager().sendMessage(CosminConstants.M_INVALID_SLOT, sender);
                return;
            }
            CosminPlayer cPlayer = plugin.getPlayerManager().containsPlayer(player) ? plugin.getPlayerManager().getPlayer(player) : plugin.getPlayerManager().createCosminPlayer(player);
            List<ItemStack> items = cPlayer.getCosminInvContents();
            
            if(isSet){
                cPlayer.updateArmorSets(armorName, forceequip);
            }
            else{
                ItemStack item = forceequip ? ItemBuilder.setForcedItem(cArmor.getItem(), false) : cArmor.getItem();
                if(replace){
                    if(ItemBuilder.isHatItem(items.get(slotType.getFakeSlotId()))|| items.get(slotType.getFakeSlotId()) == null){
                        items.set(slotType.getFakeSlotId(), item);
                        items.set(slotType.getToggleSlotId(), plugin.miscItems.getEnableItem());
                    }
                }
            }
            cPlayer.computeAndPutEquipmentPairList();
            cPlayer.setFakeSlotItems();
            cPlayer.sendPacketWithinRange(60);
        }
        
    }
    
}
