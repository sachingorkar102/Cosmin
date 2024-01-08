package com.github.sachin.cosmin.gui;

import com.github.sachin.cosmin.Cosmin;
import com.github.sachin.cosmin.armor.CosmeticSet;
import com.github.sachin.cosmin.armor.CosminArmor;
import com.github.sachin.cosmin.player.CosminPlayer;
import com.github.sachin.cosmin.utils.CItemSlot;
import com.github.sachin.cosmin.utils.CosminConstants;
import com.github.sachin.cosmin.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

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

                CosminPlayer cosminPlayer = holder.getCosminPlayer();
                cosminPlayer.setCosminInvContents(Arrays.asList(holder.getInventory().getContents()));
                cosminPlayer.clearNonExsistantArmorItems();
                cosminPlayer.computeAndPutEquipmentPairList();
                plugin.getPlayerManager().addPlayer(cosminPlayer);
                new BukkitRunnable(){
                    public void run() {
                        if(player.isOnline() && !(player.getOpenInventory().getTopInventory().getHolder() instanceof PagedGui)){
                            cosminPlayer.sendPacketWithinRange(60);
                            cosminPlayer.setFakeSlotItems();
                        }
                    };
                }.runTaskLater(plugin, plugin.getConfig().getInt(CosminConstants.DELAY_AFTER_EQUIP,2));
            }
        }
        else if(e.getView().getTopInventory().getType() == InventoryType.CRAFTING && plugin.getPlayerManager().containsPlayer(player)){
            CosminPlayer cosminPlayer = plugin.getPlayerManager().getPlayer(player);
            cosminPlayer.clearNonExsistantArmorItems();
            cosminPlayer.computeAndPutEquipmentPairList();
            new BukkitRunnable(){
                @Override
                public void run() {
                    if(player.isOnline()){
                        cosminPlayer.sendPacketWithinRange(60, player);
                        cosminPlayer.setFakeSlotItems();
                    }
                }
            }.runTaskLater(plugin,plugin.getConfig().getInt(CosminConstants.DELAY_AFTER_EQUIP,2));
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
                        if(plugin.getConfigUtils().matchBlackListMaterial(e.getOldCursor().getType(), equipSlotMap.get(s)) || !plugin.getConfigUtils().getExternalArmorMap().get(s)){
                            e.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

    public List<Integer> getSlots(InventoryClickEvent e) {
        List<Integer> slots = new ArrayList<>();
        if(e.isShiftClick()) {
            ItemStack[] list = e.getInventory().getContents();
            int airSlot = -1;
            int stacked = e.getCurrentItem().getAmount();
            ItemStack t;

            for (int p = 0; p < list.length; p++) {
                t = list[p];
                if(t == null) {
                    if(airSlot == -1) {
                        airSlot = p;
                    }
                    continue;
                }
                if (t.getType() != e.getCurrentItem().getType()) {
                    continue;
                } else {
                    if(t.getAmount() + stacked > t.getMaxStackSize()) {
                        stacked -= t.getMaxStackSize() - t.getAmount();
                        slots.add(p);
                    } else {
                        stacked = 0;
                        slots.add(p);
                        break;
                    }
                }
            }
            if(airSlot != -1 && stacked > 0) {
                slots.add(airSlot);
            }
        }
        return slots;
    }



    @EventHandler(priority = EventPriority.HIGHEST,ignoreCancelled = true)
    public void cosminGuiClickEvent(InventoryClickEvent e){
        Player player = (Player) e.getWhoClicked();
        Inventory clickedInv = e.getClickedInventory();
        Inventory inv = e.getInventory();
        if(clickedInv == null) return;
        Map<Integer,Boolean> armorMap = plugin.getConfigUtils().getExternalArmorMap();

        // check for shift clicking in a disabled slot
        if(inv.getHolder() instanceof GuiHolder){
            GuiHolder guiHolder = (GuiHolder) inv.getHolder();
            if(guiHolder.context==GuiContext.COSMIN_INVENTORY){
                if(e.isShiftClick()){
                    List<Integer> slots = new ArrayList<>();
                    ItemStack[] list = guiHolder.getInventory().getContents();
                    int airSlot = -1;
                    ItemStack currentItem = e.getCurrentItem();
                    int stacked =0;
                    if(currentItem!=null){
                        stacked = currentItem.getAmount();
                    }
                    else{
                        currentItem = new ItemStack(Material.AIR);
                    }
                    ItemStack t;

                    for (int p = 0; p < list.length; p++) {
                        t = list[p];
                        if(t == null) {
                            if(airSlot == -1) {
                                airSlot = p;
                            }
                            continue;
                        }
                        if (t.getType() != currentItem.getType()) {
                            continue;
                        } else {
                            if(t.getAmount() + stacked > t.getMaxStackSize()) {
                                stacked -= t.getMaxStackSize() - t.getAmount();
                                slots.add(p);
                            } else {
                                stacked = 0;
                                slots.add(p);
                                break;
                            }
                        }
                    }
                    if(airSlot != -1 && stacked > 0) {
                        slots.add(airSlot);
                    }

                    for(int slot : slots){
                        for(int i : armorMap.keySet()){
                            if(i==slot && !armorMap.get(i)){
                                e.setCancelled(true);
                                break;
                            }
                        }

                    }

                }
            }
        }

        int clickedSlot = e.getSlot();
        if(clickedInv.getHolder() instanceof GuiHolder){
            if((e.getClick()==ClickType.SHIFT_LEFT||e.getClick()==ClickType.SHIFT_RIGHT) && !CosminConstants.COSMIN_ARMOR_SLOTS.contains(clickedSlot)) {
                e.setCancelled(true);
                return;
            }
            GuiHolder holder = (GuiHolder) clickedInv.getHolder();
            ItemStack clickedItem = e.getCurrentItem();
            if(holder.getContext()== GuiContext.COSMIN_INVENTORY){


                if(CosminConstants.COSMIN_ARMOR_SLOTS.contains(clickedSlot)){

                    if((e.getCursor() != null && plugin.getConfigUtils().matchBlackListMaterial(e.getCursor().getType(), equipSlotMap.get(e.getSlot()))) || ItemBuilder.isHatItem(clickedItem) || !armorMap.get(clickedSlot)) e.setCancelled(true);

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
        else if(clickedInv instanceof PlayerInventory && e.isShiftClick() && (inv.getHolder() instanceof GuiHolder) && e.getCurrentItem() != null){
            GuiHolder holder = (GuiHolder) inv.getHolder();
            if(holder.getContext()==GuiContext.COSMIN_INVENTORY){
                for(int s : getSlots(e)){
                    if(equipSlotMap.containsKey(s) && plugin.getConfigUtils().matchBlackListMaterial(e.getCurrentItem().getType(),equipSlotMap.get(s))){
                        e.setCancelled(true);
                    }
                }
            }
        }
        else if(e.getView().getTopInventory().getType() == InventoryType.CRAFTING){
            if(plugin.getPlayerManager().containsPlayer(player)){
                CosminPlayer cosminPlayer = plugin.getPlayerManager().getPlayer(player);
                cosminPlayer.setInventoryOpen(true);
            }
        }
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
}