package com.github.sachin.cosmin.commands;

import com.github.sachin.cosmin.Cosmin;
import com.github.sachin.cosmin.player.CosminPlayer;
import com.github.sachin.cosmin.utils.CosminConstants;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TakeCommand extends SubCommands{

    private Cosmin plugin;

    public TakeCommand(Cosmin plugin){
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "take";
    }

    @Override
    public String getPermission() {
        return CosminConstants.PERM_COMMAND_TAKE;
    }

    @Override
    public String getUsage() {
        return "&3/cosmin &ftake &9[player_name] [item-name/set-name]";
    }

    @Override
    public String getDescription() {
        return "removes a purchased item or set from player";
    }

    @Override
    public int getMaxArgs() {
        return 3;
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if(args.length < 3) return;
        Player targetPlayer = Bukkit.getPlayerExact(args[1]);
        if(targetPlayer == null){
            plugin.getMessageManager().sendMessage(CosminConstants.M_OFFLINE_PLAYER, sender);
            return;
        }
        String name = args[2];
        if(!plugin.getPlayerManager().containsPlayer(targetPlayer)) return;
        CosminPlayer cPlayer = plugin.getPlayerManager().getPlayer(targetPlayer);
        if(plugin.getArmorManager().containsArmor(name) && cPlayer.getPurchasedItems().contains(name)){
            cPlayer.getPurchasedItems().remove(name);

        }else if(plugin.getArmorManager().containsSet(name) && cPlayer.getPurchasedSets().contains(name)){
            cPlayer.getPurchasedSets().remove(name);
        }
        cPlayer.clearNonExsistantArmorItems();
        cPlayer.computeAndPutEquipmentPairList();
        cPlayer.setFakeSlotItems();
        cPlayer.sendPacketWithinRange(60);
    }
    
}
