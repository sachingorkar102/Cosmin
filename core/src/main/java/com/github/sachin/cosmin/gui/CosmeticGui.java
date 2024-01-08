package com.github.sachin.cosmin.gui;

import com.github.sachin.cosmin.player.CosminPlayer;
import com.github.sachin.cosmin.utils.CItemSlot;
import com.github.sachin.cosmin.utils.CosminConstants;
import com.github.sachin.cosmin.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CosmeticGui extends GuiHolder{


    private final Map<CItemSlot,Integer> itemSlotMap = new HashMap<>();
    private final Map<CItemSlot,Integer> tItemSlotMap = new HashMap<>();

    private List<Integer> fillerSlots;
    private final List<String> DEFAULT_LAYOUT = Arrays.asList("shop-button|filler-glass|head-toggle-slot|chest-toggle-slot|legs-toggle-slot|feet-toggle-slot|offhand-toggle-slot|filler-glass|filler-glass",
                                                              "filler-glass|filler-glass|head-slot|chest-slot|legs-slot|feet-slot|offhand-slot|filler-glass|filler-glass");

    private final int DEFAULT_INV_SIZE = 18;
    private int setButtonSlot;

    private int invSize;


    public CosmeticGui(Player player, @Nullable Player targetPlayer) {
        super(player, targetPlayer,GuiContext.COSMIN_INVENTORY);
        if(targetPlayer == null){
            this.targetPlayer = this.player;
        }
        for(CItemSlot slot : CItemSlot.values()){
            itemSlotMap.put(slot,-1);
            tItemSlotMap.put(slot,-1);
        }
        List<String> invLayoutList = plugin.miscItems.getConfig().getStringList("main-gui-layout");
        if(invLayoutList.isEmpty()){
            invLayoutList = DEFAULT_LAYOUT;
            invSize = DEFAULT_INV_SIZE;
        }
        createLayout(invLayoutList);
    }

    public void open(){
        Inventory inventory;
        if(targetPlayer != null){
            inventory = Bukkit.createInventory(this, invSize,plugin.guiManager.getTitle(player,CosminConstants.MAIN_GUI)+" -> "+targetPlayer.getName());
        }
        else{
            inventory = Bukkit.createInventory(this, invSize,plugin.guiManager.getTitle(player,CosminConstants.MAIN_GUI));
        }
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

        targetPlayer.openInventory(inventory);
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
        meta.setDisplayName(ItemBuilder.getDisplayName(item)+ " "+ ChatColor.translateAlternateColorCodes('&', toolTip));
        item.setItemMeta(meta);
        return item;
    }



    public void handClicks(InventoryClickEvent e){
        int clickedSlot = e.getSlot();
        ItemStack clickedItem = e.getCurrentItem();
        if(itemSlotMap.values().contains(clickedSlot)){


//            if((e.getCursor() != null && plugin.getConfigUtils().matchBlackListMaterial(e.getCursor().getType(), equipSlotMap.get(e.getSlot()))) || ItemBuilder.isHatItem(clickedItem) || !armorMap.get(clickedSlot)) e.setCancelled(true);

        }
        else if(clickedItem != null && clickedItem.isSimilar(plugin.miscItems.getCosmeticSetButton())){
            onCosmeticSetClickEvent(e, player,this);
        }
        else if(tItemSlotMap.values().contains(clickedSlot)){
            onToggleSlotsClickEvent(e, player, this);
        }
        else if(itemSlotMap.values().contains(clickedSlot)){
            e.setCancelled(true);
        }
    }

    private void onCosmeticSetClickEvent(InventoryClickEvent e,Player player,GuiHolder holder){

        // on right click
        if(e.getClick() == plugin.getConfigUtils().getHotKeysList().get(3)){
            plugin.guiManager.showCosmeticSetGui(player, holder.getTargetPlayer());

        }
        // on drop
        else if(e.getClick() == plugin.getConfigUtils().getHotKeysList().get(4)){
            for(int i=11;i<16;i++){
                ItemStack item = e.getClickedInventory().getItem(i);
                if(ItemBuilder.isForcedItem(item) && !player.hasPermission(CosminConstants.PERM_FORCEEQUIP_REMOVE)) continue;
                if(ItemBuilder.isHatItem(item)){
                    e.getClickedInventory().setItem(i, null);

                }
            }
        }
        e.setCancelled(true);

    }

    private void onToggleSlotsClickEvent(InventoryClickEvent e,Player player,GuiHolder holder){
        ItemStack clickedItem = e.getCurrentItem();
        int clickedSlot = e.getSlot();
        ItemStack armor = e.getClickedInventory().getItem(clickedSlot+9);
        // on left click(on toggle visibility)
        if(ItemBuilder.isForcedItem(armor) && !player.hasPermission(CosminConstants.PERM_FORCEEQUIP_REMOVE)){
            plugin.getMessageManager().sendMessage(CosminConstants.M_CANT_DEQUIP, player);
        }
        else if(e.getClick() == plugin.getConfigUtils().getHotKeysList().get(0)){

            if(ItemBuilder.isEnableItem(clickedItem)){
                e.setCurrentItem(plugin.guiManager.generateToolTips(plugin.miscItems.getDisableItem(), clickedSlot));
            }
            else{
                e.setCurrentItem(plugin.guiManager.generateToolTips(plugin.miscItems.getEnableItem(), clickedSlot));
            }
        }
        // on right click
        else if(e.getClick() == plugin.getConfigUtils().getHotKeysList().get(1)){
            for(CItemSlot slot:CItemSlot.values()){
                if(slot.getContext().getSlotid() == (clickedSlot+9)){
                    if(plugin.getConfig().getBoolean("wardrobe-enabled."+slot.toString(),true)){
                        plugin.guiManager.showPagedGui(player, holder.getTargetPlayer(), slot.getContext());
                        break;
                    }
                }
            }
        }
        // on drop
        else if(e.getClick() == plugin.getConfigUtils().getHotKeysList().get(2)){
            if(ItemBuilder.isHatItem(armor) && ItemBuilder.isCrossMatchAllowed(armor)){
                e.getClickedInventory().setItem(clickedSlot+9, null);
            }
        }
        e.setCancelled(true);

    }
    public void createLayout(List<String> invlayoutList){
        Map<Integer, ItemStack> invLayout = new HashMap<>();
        if(invlayoutList.size() > 5){
            invSize = DEFAULT_INV_SIZE;
            createLayout(DEFAULT_LAYOUT);
            return;
        }
        for(int i =0;i<invlayoutList.size();i++){
            List<String> row = Arrays.asList(invlayoutList.get(i).split("\\|"));
            if(row.size()!=9){
                plugin.getLogger().info("wrong layout");
                createLayout(DEFAULT_LAYOUT);
                break;
            }
            for(int k=0;k<row.size();k++){
                int slot = k * (i+1);
                String itemTag = row.get(k);
                switch (itemTag){
                    case "filler-glass":
                        fillerSlots.add(slot);

                    case "toggle-head-slot":
                        tItemSlotMap.put(CItemSlot.HEAD,slot);
                    case "toggle-chest-slot":
                        tItemSlotMap.put(CItemSlot.CHEST,slot);
                    case "toggle-legs-slot":
                        tItemSlotMap.put(CItemSlot.LEGS,slot);
                    case "toggle-feet-slot":
                        tItemSlotMap.put(CItemSlot.FEET,slot);
                    case "toggle-offhand-slot":
                        tItemSlotMap.put(CItemSlot.OFFHAND,slot);

                    case "head-slot":
                        itemSlotMap.put(CItemSlot.HEAD,slot);
                    case "chest-slot":
                        itemSlotMap.put(CItemSlot.CHEST,slot);
                    case "legs-slot":
                        itemSlotMap.put(CItemSlot.LEGS,slot);
                    case "feet-slot":
                        itemSlotMap.put(CItemSlot.FEET,slot);
                    case "offhand-slot":
                        itemSlotMap.put(CItemSlot.OFFHAND,slot);

                    case "shop-button":
                        setButtonSlot = slot;

                    default:
                        fillerSlots.add(slot);
                }
            }
        }
        invSize = invlayoutList.size() * 9;

    }
}
