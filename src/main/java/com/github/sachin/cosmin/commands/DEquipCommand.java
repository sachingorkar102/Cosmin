package com.github.sachin.cosmin.commands;

import com.github.sachin.cosmin.Cosmin;
import com.github.sachin.cosmin.player.CosminPlayer;
import com.github.sachin.cosmin.utils.CItemSlot;
import com.github.sachin.cosmin.utils.CosminConstants;
import com.google.common.base.Enums;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DEquipCommand extends SubCommands{

    private Cosmin plugin;

    public DEquipCommand(Cosmin plugin){
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "dequip";
    }

    @Override
    public String getPermission() {
        return CosminConstants.PERM_COMMAND_DEQUP;
    }

    @Override
    public String getUsage() {
        return "&3/cosmin &fdequip &9[slot_type] [player_name]";
    }

    @Override
    public String getDescription() {
        return "removes the fake armor player is wearing at the given slot";
    }

    @Override
    public int getMaxArgs() {
        return 3;
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if(args.length >= 3){
            String slotName = args[1];
            CItemSlot slotType = Enums.getIfPresent(CItemSlot.class, slotName).orNull();
            Player player = Bukkit.getPlayer(args[2]);
            if(player == null){
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', CosminConstants.MESSAGE_PREFIX+"player is offline or dosnt exists"));
                return;
            }
            if(slotType == null && !slotName.equalsIgnoreCase("ALL")){
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', CosminConstants.MESSAGE_PREFIX+"valid slot type should be specified"));
                return;
            }

            CosminPlayer cPlayer = plugin.getPlayerManager().containsPlayer(player) ? plugin.getPlayerManager().getPlayer(player) : plugin.getPlayerManager().createCosminPlayer(player);
            if(slotName.equalsIgnoreCase("ALL")){
                for (CItemSlot cSlot : CItemSlot.values()) {
                    cPlayer.getCosminInvContents().set(cSlot.getFakeSlotId(), null);
                }
            }
            else{
                cPlayer.getCosminInvContents().set(slotType.getFakeSlotId(), null);
            }
            cPlayer.computeAndPutEquipmentPairList();
            cPlayer.setFakeSlotItems();
            cPlayer.sendPacketWithinRange(60);
        }
        
    }


    
}
