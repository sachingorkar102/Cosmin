package com.github.sachin.cosmin.gui;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction;
import com.github.sachin.cosmin.Cosmin;
import com.github.sachin.cosmin.armor.CosmeticSet;
import com.github.sachin.cosmin.armor.CosminArmor;
import com.github.sachin.cosmin.player.CosminPlayer;
import com.github.sachin.cosmin.utils.CItemSlot;
import com.github.sachin.cosmin.utils.CosminConstants;
import com.github.sachin.cosmin.utils.ItemBuilder;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class GuiListener implements Listener{

    private Cosmin plugin;
    private Map<Integer,CItemSlot> equipSlotMap = new HashMap<>();

    public GuiListener(Cosmin cosmin) {
        this.plugin = cosmin;
        equipSlotMap.clear();
        for(CItemSlot cSlot:CItemSlot.values()){
            equipSlotMap.put(cSlot.getFakeSlotId(), cSlot);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST,ignoreCancelled = true)
    public void cosminGuiCloseEvent(InventoryCloseEvent e){
        Player player = (Player) e.getPlayer();
        if(e.getInventory().getHolder() instanceof GuiHolder){
            
            GuiHolder holder = (GuiHolder) e.getInventory().getHolder();
            // CosminPlayer cosminPlayer = new CosminPlayer(player,Arrays.asList(holder.getInventory().getContents()));
            if(holder.getContext() == GuiContext.COSMIN_INVENTORY){

                new BukkitRunnable(){
                    @Override
                    public void run() {
                        CosminPlayer cosminPlayer = holder.getCosminPlayer();
                        cosminPlayer.setCosminInvContents(Arrays.asList(holder.getInventory().getContents()));
                        cosminPlayer.clearNonExsistantArmorItems();
                        cosminPlayer.computeAndPutEquipmentPairList();
                        plugin.getPlayerManager().addPlayer(cosminPlayer);
                         
                        if(!(player.getOpenInventory().getTopInventory().getHolder() instanceof PagedGui)){
                            cosminPlayer.sendPacketWithinRange(60);
                            cosminPlayer.setFakeSlotItems();
                        }
                    }
                }.runTaskLater(plugin, 2);
            }
        }
        else if(e.getView().getTopInventory().getType() == InventoryType.CRAFTING && plugin.getPlayerManager().containsPlayer(player)){
            CosminPlayer cosminPlayer = plugin.getPlayerManager().getPlayer(player);
            cosminPlayer.clearNonExsistantArmorItems();
            cosminPlayer.computeAndPutEquipmentPairList();
            cosminPlayer.sendPacketWithinRange(60, player);
            cosminPlayer.setFakeSlotItems();
            cosminPlayer.setInventoryOpen(false);
        }
    }

    @EventHandler
    public void cosminGuiDragEvent(InventoryDragEvent e){
        
        if(e.getInventory().getHolder() instanceof PagedGui){
            e.setCancelled(true);
        }
        if(e.getInventory().getHolder() instanceof GuiHolder){
            GuiHolder holder = (GuiHolder) e.getInventory().getHolder();
            if(holder.getContext() == GuiContext.COSMIN_INVENTORY){
                for(int s : e.getInventorySlots()){
                    if(CosminConstants.COSMIN_ARMOR_SLOTS.contains(s)){
                        if(plugin.getConfigUtils().matchBlackListMaterial(e.getOldCursor().getType(), equipSlotMap.get(s))){
                            e.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

   

    @EventHandler
    public void cosminGuiClickEvent(InventoryClickEvent e){
        Player player = (Player) e.getWhoClicked();
        Inventory clickedInv = e.getClickedInventory();
        if(clickedInv == null) return;
        
        if(clickedInv.getHolder() instanceof GuiHolder){
            if(e.getClick()==ClickType.SHIFT_LEFT||e.getClick()==ClickType.SHIFT_RIGHT) {
                e.setCancelled(true);
                return;
            }
            GuiHolder holder = (GuiHolder) clickedInv.getHolder();
            ItemStack clickedItem = e.getCurrentItem();
            int clickedSlot = e.getSlot();
            if(holder.getContext()== GuiContext.COSMIN_INVENTORY){

                if(CosminConstants.COSMIN_ARMOR_SLOTS.contains(clickedSlot)){
                    if(e.getCursor() != null && plugin.getConfigUtils().matchBlackListMaterial(e.getCursor().getType(), equipSlotMap.get(e.getSlot()))) e.setCancelled(true);
                    else if(!plugin.getConfigUtils().getExternalArmorMap().get(e.getSlot()) || ItemBuilder.isHatItem(clickedItem)){
                        e.setCancelled(true);
                    }
                }
                else if(clickedItem != null && clickedItem.isSimilar(plugin.miscItems.getCosmeticSetButton())){
                    onCosmeticSetClickEvent(e, player,holder);
                }
                else if(CosminConstants.TOGGLABLE_SLOTS.contains(clickedSlot)){
                    onToggleSlotsClickEvent(e, player, holder);
                }
                else if(CosminConstants.FILLAR_SLOTS.contains(clickedSlot)){
                    e.setCancelled(true);
                }
            }
            // check for paged gui
            else if(clickedInv.getHolder() instanceof PagedGui){
                PagedGui hGui = (PagedGui) e.getInventory().getHolder();
                hGui.handlePageClicks(e);
                return;
            }
            // check for cosmetic set gui
            else if(clickedInv.getHolder() instanceof CosmeticSetGui){
                CosmeticSetGui cGui = (CosmeticSetGui) e.getInventory().getHolder();
                cGui.handlePageClicks(e);
                return;
            }
            else if(clickedInv.getHolder() instanceof ConfirmGui){
                onConfirmGuiClickEvent(e);
                return;
            }
        }
        else if(e.getView().getTopInventory().getType() == InventoryType.CRAFTING){
            if(plugin.getPlayerManager().containsPlayer(player)){
                CosminPlayer cosminPlayer = plugin.getPlayerManager().getPlayer(player);
                cosminPlayer.setInventoryOpen(true);
            }
        }
        
        /*
        
        
        ItemStack clickedItem = e.getCurrentItem();

        
        
        if(e.getClickedInventory().getHolder() instanceof GuiHolder){
            // check for cosmetic set gui
            else if(e.getInventory().getHolder() instanceof CosmeticSetGui){
                CosmeticSetGui cGui = (CosmeticSetGui) e.getInventory().getHolder();
                cGui.handlePageClicks(e);
                return;
            }
            else if(e.getInventory().getHolder() instanceof ConfirmGui){
                onConfirmGuiClickEvent(e);
                return;
            }
            GuiHolder holder = (GuiHolder) e.getClickedInventory().getHolder();
            if(ItemBuilder.isHatItem(clickedItem)){
                e.setCancelled(true);
            }

            // check for main inventory
            if(holder.getContext() == GuiContext.COSMIN_INVENTORY){
                if(CosminConstants.COSMIN_ARMOR_SLOTS.contains(e.getSlot())){
                    if(!plugin.getConfigUtils().getExternalArmorMap().get(e.getSlot())){
                        e.setCancelled(true);
                    }
                }
                else if(e.getCurrentItem().isSimilar(plugin.miscItems.getCosmeticSetButton()) && plugin.getConfigUtils().isCosmeticSetEnabled()){
                    onCosmeticSetClickEvent(e, player,holder);

                }
                else if(CosminConstants.TOGGLABLE_SLOTS.contains(e.getSlot())){
                    onToggleSlotsClickEvent(e, player, holder);
                }
                else if(CosminConstants.FILLAR_SLOTS.contains(e.getSlot())){
                    e.setCancelled(true);
                } 
            }
        }
        */
    }

    private void onConfirmGuiClickEvent(InventoryClickEvent e){
        e.setCancelled(true);
        ConfirmGui cHolder = (ConfirmGui) e.getInventory().getHolder();
        String name = cHolder.getItemName();
        Player player = (Player) e.getWhoClicked();
        ItemStack clickedItem = e.getCurrentItem();
        if(clickedItem == null) return;
        if(clickedItem.isSimilar(plugin.miscItems.getCancelButton())){
            player.closeInventory();
        }
        else if(clickedItem.isSimilar(plugin.miscItems.getConfirmButton())){
            CosminPlayer cPlayer = cHolder.getCosminPlayer();
            if(plugin.getArmorManager().containsArmor(name)){
                CosminArmor armor = plugin.getArmorManager().getArmor(name);
                if(plugin.getVaultEco() != null && armor.getCost() != 0){
                    plugin.getVaultEco().withDraw(player, armor.getCost());
                }
                if(plugin.getPlayerPointsEco() != null && armor.getPlayerPoints() != 0){
                    plugin.getPlayerPointsEco().withDraw(player, armor.getPlayerPoints());
                }
                cPlayer.addPurchasedItem(name);
                plugin.guiManager.showPagedGui(player, cHolder.getTargetPlayer(),cHolder.context);
            }
            else if(plugin.getArmorManager().containsSet(name)){
                CosmeticSet set = plugin.getArmorManager().getSet(name);
                if(plugin.getVaultEco() != null && set.getCost() != 0){
                    plugin.getVaultEco().withDraw(player, set.getCost());
                }
                if(plugin.getPlayerPointsEco() != null && set.getPlayerPoints() != 0){
                    plugin.getPlayerPointsEco().withDraw(player, set.getPlayerPoints());
                }
                cPlayer.addPurchasedSet(name);
                plugin.guiManager.showCosmeticSetGui(player,cHolder.getTargetPlayer());
            }
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
        // on left click(on toggle visibility)
        if(ItemBuilder.isForcedItem(e.getClickedInventory().getItem(clickedSlot+9)) && !player.hasPermission(CosminConstants.PERM_FORCEEQUIP_REMOVE)){
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
            if(ItemBuilder.isHatItem(e.getClickedInventory().getItem(clickedSlot+9))){
                e.getClickedInventory().setItem(clickedSlot+9, null);
            }
        }   
        e.setCancelled(true);

    }
}