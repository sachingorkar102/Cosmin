package com.github.sachin.cosmin.gui;

import com.github.sachin.cosmin.Cosmin;
import com.github.sachin.cosmin.compat.CosmeticCoreAPI;
import com.github.sachin.cosmin.player.CosminPlayer;
import com.github.sachin.cosmin.utils.CosminConstants;
import com.github.sachin.cosmin.utils.ItemBuilder;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;


public class GuiManager {

    private Cosmin plugin;

    public GuiManager(){
        this.plugin = Cosmin.getInstance();
    }

    // public void showFakeGui(Player player){
    //     final GuiHolder holder = new GuiHolder(player,GuiContext.COSMIN_INVENTORY);
    //     final Inventory inventory = Bukkit.createInventory(holder, 18,getTitle(player));
    //     holder.setInventory(getCosminInventory(player, inventory));
    //     player.openInventory(inventory);

    // }

    public void showFakeGui(@NotNull Player player,@Nullable Player targetPlayer){
//        final GuiHolder holder = new GuiHolder(player,targetPlayer,GuiContext.COSMIN_INVENTORY);
//        Player p = targetPlayer == null ? player : targetPlayer;
        CosmeticGui gui = new CosmeticGui(player,targetPlayer);
        gui.open();
//        Inventory inventory;
//        if(targetPlayer != null){
//            inventory = Bukkit.createInventory(holder, 18,getTitle(player,CosminConstants.MAIN_GUI)+" -> "+targetPlayer.getName());
//        }
//        else{
//            inventory = Bukkit.createInventory(holder, 18,getTitle(player,CosminConstants.MAIN_GUI));
//        }
//        holder.setInventory(getCosminInventory(p, inventory));
//        player.openInventory(inventory);

    }

    public String getTitle(Player player,String config){
        String message = plugin.getConfig().getString(config,"GUI");
        if(plugin.isPAPIEnabled()){
            message = PlaceholderAPI.setPlaceholders(player, message);
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }



    public Inventory getCosminInventory(Player player,Inventory inventory){
        List<ItemStack> contents;
        if(!plugin.getPlayerManager().containsPlayer(player)){
            plugin.getPlayerManager().createCosminPlayer(player);
        }
        CosminPlayer cosminPlayer = plugin.getPlayerManager().getPlayer(player);
        contents = cosminPlayer.getCosminInvContents();
        // clearNonExsistantArmorItems(contents);
        for(int slot: CosminConstants.TOGGLABLE_SLOTS){
            ItemStack item;
            if(ItemBuilder.isEnableItem(contents.get(slot))){
                item = generateToolTips(plugin.miscItems.getEnableItem(), slot);
            }
            else{
                item = generateToolTips(plugin.miscItems.getDisableItem(), slot);
            }
            inventory.setItem(slot, item);
        }
        for(int slot:CosminConstants.FILLAR_SLOTS){

            inventory.setItem(slot, plugin.miscItems.getFillerGlass());
        }
        for(int slot:CosminConstants.COSMIN_ARMOR_SLOTS){
            inventory.setItem(slot, contents.get(slot));
        }
        if(plugin.getConfigUtils().isCosmeticSetEnabled() && player.hasPermission(CosminConstants.PERM_COSMETICSET)){
            inventory.setItem(0, plugin.miscItems.getCosmeticSetButton());
        }
        return inventory;
    }

    public List<ItemStack> getMainGuiTemplate(){
        List<ItemStack> list = Arrays.asList(new ItemStack[18]);
        for(int i=0;i<18;i++){
            if(CosminConstants.FILLAR_SLOTS.contains(i)){
                list.set(i, plugin.miscItems.getFillerGlass());
            }
            else if(CosminConstants.TOGGLABLE_SLOTS.contains(i)){
                list.set(i, plugin.miscItems.getDisableItem());
            }
        }
        return list;
    }





    public void showPagedGui(@NotNull Player player,@Nullable Player targePlayer,@NotNull GuiContext context){

        final PagedGui pholder = new PagedGui(player,targePlayer, context);
        pholder.setItems();
        pholder.openPage();
    }
    public void showCosmeticSetGui(@NotNull Player player,@Nullable Player targePlayer){

        final CosmeticSetGui sholder = new CosmeticSetGui(player,targePlayer);
        sholder.setItems();
        sholder.openPage();
    }


    public void showShopGui(@NotNull Player player,@Nullable Player targetPlayer,@NotNull GuiContext context){
        final ShopGui sHolder = new ShopGui(player,targetPlayer,context);
        sHolder.setItems();
        sHolder.openPage();
    }


    public void showConfirmGui(@NotNull Player player,@Nullable Player targetPlayer,@NotNull GuiContext context,@NotNull ItemStack item){
        String name;
        if(ItemBuilder.isCosmeticSetIcon(item) && context == GuiContext.COSMETIC_SET_GUI){
            name = ItemBuilder.getCosmeticSetIconValue(item);
        }
        else{
            name = ItemBuilder.getArmorName(item);
        }
        final ConfirmGui cHolder = new ConfirmGui(player,targetPlayer, context,name);
        Inventory inventory = Bukkit.createInventory(cHolder,27, getTitle(player, CosminConstants.CONFIRM_PAGE));
        cHolder.setInventory(inventory);
        inventory.setItem(13, item);
        inventory.setItem(15, plugin.miscItems.getConfirmButton());
        inventory.setItem(11, plugin.miscItems.getCancelButton());
        for(int i : Arrays.asList(0,1,2,3,4,5,6,7,8,9,10,12,14,16,17,18,19,20,21,22,23,24,25,26)){
            inventory.setItem(i, plugin.miscItems.getFillerGlass());
        }
        player.openInventory(inventory);

    }



    public ItemStack generateToolTips(ItemStack item,int slot){
        if(!ItemBuilder.showToolTip(item)) return item;
        ItemMeta meta = item.getItemMeta();
        String toolTip = "";
        switch(slot){
            case 2:
                toolTip = plugin.getConfig().getString(CosminConstants.TOGGLE_ITEM_TOOLTIP+"HEAD"," ");
                break;
            case 3:
                toolTip = plugin.getConfig().getString(CosminConstants.TOGGLE_ITEM_TOOLTIP+"CHEST"," ");
                break;
            case 4:
                toolTip = plugin.getConfig().getString(CosminConstants.TOGGLE_ITEM_TOOLTIP+"LEGS"," ");
                break;
            case 5:
                toolTip = plugin.getConfig().getString(CosminConstants.TOGGLE_ITEM_TOOLTIP+"FEET"," ");
                break;
            case 6:
                toolTip = plugin.getConfig().getString(CosminConstants.TOGGLE_ITEM_TOOLTIP+"OFFHAND"," ");
                break;
        }
        meta.setDisplayName(ItemBuilder.getDisplayName(item)+ " "+ChatColor.translateAlternateColorCodes('&', toolTip));
        item.setItemMeta(meta);
        return item;
    }
}