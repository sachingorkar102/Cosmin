package com.github.sachin.cosmin.commands;

import com.github.sachin.cosmin.armor.CosminArmor;
import com.github.sachin.cosmin.utils.CItemSlot;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;

public class ImportItemCommand extends SubCommands
{

    @Override
    public String getName() {
        return "import";
    }

    @Override
    public String getPermission() {
        return "cosmin.command.import";
    }

    @Override
    public String getUsage() {
        return "&3/cosmin &fimport &9[slot_type] [item_name]";
    }

    @Override
    public String getDescription() {
        return "Imports the item player is holding in main hand as a cosmin item in imported-items.yml.";
    }
    
    @Override
    public int getMaxArgs() {
        return 3;
    }
    @Override
    public void perform(CommandSender sender, String[] args) {
        if(sender instanceof Player){
            Player player = (Player) sender;
            ItemStack item = player.getInventory().getItemInMainHand();
            
            if(item == null || item.getType() == Material.AIR){
                player.sendMessage(ChatColor.RED+"Cannot import empty item");
                return;
            }
            CItemSlot slot = CItemSlot.valueOf(args[1]);
            if(slot == null){
                player.sendMessage(ChatColor.RED+"Specify a valid slot type");
                return;
            }
            String itemName = args[2];
            if(plugin.getArmorManager().getArmor(itemName) != null){
                player.sendMessage(ChatColor.RED+"Item with that name already exsists");
                return;
            }
            File file = new File(plugin.getDataFolder(),"items/imported-items.yml");
            if(!file.exists()){
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            config.options().copyDefaults(true);
            config.addDefault(itemName+".item", item);
            config.addDefault(itemName+".type", slot.toString());
            
            try {
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            CosminArmor armor = new CosminArmor(item,itemName,slot);
            plugin.getArmorManager().addArmor(armor);
            player.sendMessage(ChatColor.YELLOW+itemName+ChatColor.GOLD+" saved successfully..");
            player.sendMessage(ChatColor.GOLD+"do not alter/change the item option under "+ChatColor.YELLOW+itemName+ChatColor.GOLD+" in imported-items.yml");
            

        }
        else{
           plugin.getLogger().info("Requires player to execute command"); 
        }
    }

    
}
