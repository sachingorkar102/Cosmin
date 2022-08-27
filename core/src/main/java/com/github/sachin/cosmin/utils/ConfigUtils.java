package com.github.sachin.cosmin.utils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.sachin.cosmin.Cosmin;
import com.github.sachin.cosmin.armor.ArmorManager;
import com.github.sachin.cosmin.armor.CosmeticSet;
import com.github.sachin.cosmin.armor.CosminArmor;
import com.google.common.base.Enums;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import me.clip.placeholderapi.PlaceholderAPI;


public class ConfigUtils {
    
    private Cosmin plugin;

    private File exampleItems;
    private File itemsDirectory;

    private final Map<Integer,Boolean> externalArmorMap = new HashMap<>();
    private final Map<CItemSlot,List<String>> blackListMaterials = new HashMap<>();
    private final List<ClickType> hotKeysList = new ArrayList<>();
    private final List<ConfigurationSection> cosmeticSetSections = new ArrayList<>();

    public ConfigUtils(Cosmin plugin){
        this.plugin = plugin;
    }

    public void loadCosmeticItems(){
        this.itemsDirectory = new File(plugin.getDataFolder(),"items");
        this.exampleItems = new File(plugin.getDataFolder(),CosminConstants.EXAMPLE_ITEM_FILE);
        if(!itemsDirectory.exists()){
            itemsDirectory.mkdirs();
            plugin.getLogger().info("Could not find items folder, generating one...");
            if(!exampleItems.exists()){
                plugin.getLogger().info("Could not find example-items.yml, generating one...");
                plugin.saveResource(CosminConstants.EXAMPLE_ITEM_FILE, false);
            }
        }

        loadItemsFromFolder(itemsDirectory);


        
    }

    private void loadItemsFromFolder(File itemDirectory){
        boolean isCosmeticSetEnabled = isCosmeticSetEnabled();
        int registeredItems = 0;
        for(File itemFile : itemsDirectory.listFiles(file -> file.isDirectory() || file.getName().endsWith(".yml"))){
            if(itemFile.isDirectory()){
                loadItemsFromFolder(itemFile);
                continue;
            }
            // if(CosminConstants.ISDEMO && registeredItems>5){
            //     plugin.getLogger().warning("This is a demo version of cosmin, can't register more then 5 items");
            //     plugin.getLogger().fine("Consider getting cosmin here: https://www.spigotmc.org/resources/92427/");
            //     break;
            // }
            try (FileReader reader = new FileReader(itemFile)) {
                YamlConfiguration yml = new YamlConfiguration();
                yml.load(reader);
                for(String key : yml.getKeys(false)){
                    try {
                        ConfigurationSection section = yml.getConfigurationSection(key);
                        if(section.contains("type") && isCosmeticSetEnabled){
                            if(section.getString("type","none").equalsIgnoreCase("SET")){
                                cosmeticSetSections.add(section);
                                continue;
                            }
                        }
                        CosminArmor armor = ItemBuilder.cosminArmorFromFile(section, null, key);
                        registeredItems++;
                        plugin.getArmorManager().addArmor(armor);
                    } catch (Exception e) {
                        Bukkit.getConsoleSender().sendMessage(ChatColor.RED+"Error occured while loading item named "+ChatColor.YELLOW+key);
                    }
                }
                reader.close();
            } catch (Exception ex) {
                plugin.getLogger().warning("Error while loading item from "+itemFile.getName());
                ex.printStackTrace();
                
            }
            
        }
    }



    

    public void reloadAllConfigs(){
        plugin.saveDefaultConfig();
        try {
            ConfigUpdater.update(plugin, "config.yml", new File(plugin.getDataFolder(),"config.yml"), new ArrayList<>());
        } catch (IOException e) {
            e.printStackTrace();
        }
        plugin.reloadConfig();
        plugin.getArmorManager().clearArmorMap();
        cosmeticSetSections.clear();
        loadCosmeticItems();
        loadCosmeticSets();
        blackListMaterials.clear();
        externalArmorMap.clear();
        hotKeysList.clear();
        
    }

    private void loadCosmeticSets(){
        ArmorManager armorManager = plugin.getArmorManager();
        CosminArmor nullArmor = new CosminArmor(null,"null","none");
        for(ConfigurationSection section: cosmeticSetSections){
            String perm = "none";
            if(section.contains("permission")){
                perm = section.getString("permission");
            }
            CosmeticSet set = new CosmeticSet(section.getName(),perm);
            String iconName = section.getString("icon");
            CosminArmor iconArmor = armorManager.getArmor(iconName);
            if(iconArmor == null) continue;
            iconArmor = iconArmor.clone();
            iconArmor.setItem(ItemBuilder.setCosmeticSetIconValue(iconArmor.getItem(), section.getName()));
            set.setIcon(iconArmor);
            set.setCost(section.getInt("cost",0));
            set.setPlayerPoints(section.getInt("points",0));
            set.setAllowCrossMatch(section.getBoolean("allow-cross-match",true));
            for(String slotKey:section.getConfigurationSection("items").getKeys(false)){
                CItemSlot slot = CItemSlot.valueOf(slotKey);
                if(slot == null) continue;
                CosminArmor armor = armorManager.getArmor(section.getString("items."+slotKey));
                if(armor == null){
                    armor = nullArmor;   
                }else{
                    if(!armor.getConfig().contains("hide")){
                        armor.setHide(true);
                    }
                    armor = armor.clone();
                }
                armor.setItem(ItemBuilder.setCosmeticSetArmorName(armor.getItem(), section.getName()));
                set.setArmorSlot(armor, slot);
            }
            armorManager.addCosmeticSet(set);
        }
    }


    public Map<Integer, Boolean> getExternalArmorMap() {
        if(externalArmorMap.isEmpty()){
            for(CItemSlot slot: CItemSlot.values()){
                externalArmorMap.put(slot.getFakeSlotId(), plugin.getConfig().getBoolean(CosminConstants.ALLOW_EXTERNAL_ARMOR+"."+slot.toString(),true));
            }
        }
        return externalArmorMap;
    }

    public boolean isMySQLEnabled(){
        return plugin.getConfig().getBoolean(CosminConstants.DB_ENABLED,false);
    }

    public Map<CItemSlot, List<String>> getBlackListMaterials() {
        if(blackListMaterials.isEmpty()){
            for(CItemSlot slot : CItemSlot.values()){
                blackListMaterials.put(slot, plugin.getConfig().getStringList("blacklist-materials."+slot.toString()));
                
            }
        }
        return blackListMaterials;
    }

    public boolean matchBlackListMaterial(Material mat,CItemSlot slot){
        String str = mat.toString();
        for(String s : getBlackListMaterials().get(slot)){
            if(s.startsWith("^") && str.startsWith(s.replace("^", ""))){
                return true;
            }
            if(s.endsWith("$") && str.endsWith(s.replace("$", ""))){
                return true;
            }
            if(str.equals(s)) return true;

        }
        return false;
    }

    public boolean validMaterial(){
        return false;
    }

    public List<ClickType> getHotKeysList() {
        if(hotKeysList.isEmpty()){
            hotKeysList.add(Enums.getIfPresent(ClickType.class, plugin.getConfig().getString(CosminConstants.HOTKEY_TOGGLE_DISPLAY))
            .or(ClickType.LEFT));
            hotKeysList.add(Enums.getIfPresent(ClickType.class, plugin.getConfig().getString(CosminConstants.HOTKEY_OPEN_WARDROBE)).or(ClickType.RIGHT));
            hotKeysList.add(Enums.getIfPresent(ClickType.class, plugin.getConfig().getString(CosminConstants.HOTKEY_CLEAR_WARDROBE)).or(ClickType.DROP));
            hotKeysList.add(Enums.getIfPresent(ClickType.class, plugin.getConfig().getString(CosminConstants.HOTKEY_OPEN_COSMETIC_SET_GUI)).or(ClickType.LEFT));
            hotKeysList.add(Enums.getIfPresent(ClickType.class, plugin.getConfig().getString(CosminConstants.HOTKEY_CLEAR_COSMETIC_SET)).or(ClickType.DROP));
        }
        return hotKeysList;
    }

    public List<String> getCommandAliases(){
        List<String> aliases = new ArrayList<>();
        aliases = plugin.getConfig().getStringList(CosminConstants.ALIASES);
        if(aliases.isEmpty() || aliases == null){
            return Arrays.asList("cosmetics");
        }
        else{
            return aliases;
        }
    }

    public void sendMessage(Player sender,String configMessage){
        String message = plugin.getConfig().getString(configMessage,"");
        if(plugin.isPAPIEnabled()){
            message = PlaceholderAPI.setPlaceholders(sender, message);
        }
        if(!message.equals("")){
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }
    }

    
    public boolean isCosmeticSetEnabled(){
        return plugin.getConfig().getBoolean(CosminConstants.ENABLE_COSMETIC_SET,true);
    }




}
