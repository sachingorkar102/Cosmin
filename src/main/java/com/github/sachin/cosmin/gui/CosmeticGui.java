package com.github.sachin.cosmin.gui;

import com.github.sachin.cosmin.player.CosminPlayer;
import com.github.sachin.cosmin.utils.CItemSlot;
import com.github.sachin.cosmin.utils.CosminConstants;
import com.github.sachin.cosmin.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CosmeticGui extends GuiHolder{


    private final Map<CItemSlot,Integer> itemSlotMap = new HashMap<>();
    private final Map<CItemSlot,Integer> tItemSlotMap = new HashMap<>();

    private Map<Integer,ItemStack> fillerSlots = new HashMap<>();

    private int setButtonSlot = -1;

    private int invSize;


    public CosmeticGui(Player player, @Nullable Player targetPlayer) {
        super(player, targetPlayer,GuiContext.COSMIN_INVENTORY);
        for(CItemSlot slot : CItemSlot.values()){
            itemSlotMap.put(slot,-1);
            tItemSlotMap.put(slot,-1);
        }
        List<String> invLayoutList = plugin.getConfig().getStringList(CosminConstants.CONFIG_MAIN_GUI_LAYOUT);
        if(invLayoutList.isEmpty()){
            invLayoutList = CosminConstants.DEFAULT_GUI_LAYOUT;
            invSize = CosminConstants.DEFAULT_INV_SIZE;
        }
        createLayout(invLayoutList);
        if(targetPlayer != null){
            this.inventory = Bukkit.createInventory(this, invSize,plugin.guiManager.getTitle(player,CosminConstants.MAIN_GUI)+" -> "+targetPlayer.getName());
        }
        else{
            this.inventory = Bukkit.createInventory(this, invSize,plugin.guiManager.getTitle(player,CosminConstants.MAIN_GUI));
        }
    }

    public void open(){
        List<ItemStack> contents;
        if(!plugin.getPlayerManager().containsPlayer(player)){
            plugin.getPlayerManager().createCosminPlayer(player);
        }
        CosminPlayer cosminPlayer = plugin.getPlayerManager().getPlayer(player);
        contents = cosminPlayer.getCosminInvContents();
        for(int slot:fillerSlots.keySet()){
            inventory.setItem(slot,fillerSlots.get(slot));
        }
        for(CItemSlot slot : itemSlotMap.keySet()){
            if(itemSlotMap.get(slot)==-1){
                continue;
            }
            inventory.setItem(itemSlotMap.get(slot),contents.get(slot.getFakeSlotId()));

        }
        for(CItemSlot slot : tItemSlotMap.keySet()){
            if(tItemSlotMap.get(slot)==-1){
                continue;
            }
            ItemStack item = contents.get(slot.getToggleSlotId());
            if(ItemBuilder.isEnableItem(item)){
                item = generateToolTips(plugin.miscItems.getEnableItem(), slot);
            }
            else{
                item = generateToolTips(plugin.miscItems.getDisableItem(), slot);
            }
            inventory.setItem(tItemSlotMap.get(slot), item);

        }
//        if(setButtonSlot ==-1){
//            inventory.setItem(setButtonSlot,plugin.miscItems.getFillerGlass());
//        }
        if(setButtonSlot != -1 && plugin.getConfigUtils().isCosmeticSetEnabled() && player.hasPermission(CosminConstants.PERM_COSMETICSET)){
            inventory.setItem(setButtonSlot, plugin.miscItems.getCosmeticSetButton());
        }

        player.openInventory(inventory);
    }

    public void close(){
        List<ItemStack> tempItemList = new ArrayList<>();
        for(int i=0;i<18;i++){
            tempItemList.add(null);
        }
        for(int i : CosminConstants.FILLAR_SLOTS){ tempItemList.set(i,plugin.miscItems.getFillerGlass());}
        for(CItemSlot slot : tItemSlotMap.keySet()){
            if(tItemSlotMap.get(slot)==-1){
                tempItemList.set(slot.getToggleSlotId(),plugin.miscItems.getDisableItem());
                continue;
            }
            ItemStack item = inventory.getItem(tItemSlotMap.get(slot));
            if(ItemBuilder.isEnableItem(item)){
                tempItemList.set(slot.getToggleSlotId(),plugin.miscItems.getEnableItem());
            }
            else{
                tempItemList.set(slot.getToggleSlotId(),plugin.miscItems.getDisableItem());
            }
        }

        for(CItemSlot slot : itemSlotMap.keySet()){
            if(itemSlotMap.get(slot)==-1){
                tempItemList.set(slot.getFakeSlotId(),null);
                continue;
            }
            tempItemList.set(slot.getFakeSlotId(),inventory.getItem(itemSlotMap.get(slot)));
        }
        CosminPlayer cosminPlayer = getCosminPlayer();
        cosminPlayer.setCosminInvContents(tempItemList);
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




    public void handleInventoryCLicks(InventoryClickEvent e){
        int clickedSlot = e.getSlot();
        ItemStack clickedItem = e.getCurrentItem();
        if(itemSlotMap.values().contains(clickedSlot)){
            CItemSlot slot = null;
            for(CItemSlot s : itemSlotMap.keySet()){
                if(itemSlotMap.get(s) != clickedSlot) continue;
                slot = s;
            }
            if(slot==null) return;
            if(e.getAction()== InventoryAction.HOTBAR_SWAP || e.getAction()==InventoryAction.HOTBAR_MOVE_AND_READD) e.setCancelled(true);
            if((e.getCursor() != null && plugin.getConfigUtils().matchAllowedListMaterial(e.getCursor().getType(), slot)) || ItemBuilder.isHatItem(clickedItem) || !plugin.getConfigUtils().getExternalArmorMap().get(slot.getFakeSlotId())) e.setCancelled(true);
        }
        else if(clickedItem != null && clickedItem.isSimilar(plugin.miscItems.getCosmeticSetButton())){
            onCosmeticSetClickEvent(e, player,this);
        }
        else if(tItemSlotMap.values().contains(clickedSlot)){
            onToggleSlotsClickEvent(e);
        }
        else if(fillerSlots.keySet().contains(clickedSlot)){
            e.setCancelled(true);
        }
    }

    public void handleDragClicks(InventoryDragEvent e){
        for(int i : e.getInventorySlots()){
            if(itemSlotMap.values().contains(i)){
                CItemSlot slot = null;
                for(CItemSlot s : itemSlotMap.keySet()){
                    if(itemSlotMap.get(s) != i) continue;
                    slot = s;
                }
                if(plugin.getConfigUtils().matchAllowedListMaterial(e.getOldCursor().getType(), slot) || !plugin.getConfigUtils().getExternalArmorMap().get(slot.getFakeSlotId())){
                    e.setCancelled(true);
                }
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
            for(int i : itemSlotMap.values()){
                if(i == -1) continue;
                ItemStack item = e.getClickedInventory().getItem(i);
                if(ItemBuilder.isForcedItem(item) && !player.hasPermission(CosminConstants.PERM_FORCEEQUIP_REMOVE)) continue;
                if(ItemBuilder.isHatItem(item)){
                    e.getClickedInventory().setItem(i, null);

                }
            }
        }
        e.setCancelled(true);

    }

    private void onToggleSlotsClickEvent(InventoryClickEvent e){
        ItemStack clickedItem = e.getCurrentItem();
        int clickedSlot = e.getSlot();
        CItemSlot slot=null;
        for(CItemSlot s : tItemSlotMap.keySet()){
            if(tItemSlotMap.get(s)==clickedSlot){
                slot = s;
                break;
            }
        }
        if(slot==null) return;
        ItemStack armor = e.getClickedInventory().getItem(clickedSlot+9);
        // on left click(on toggle visibility)
        if(ItemBuilder.isForcedItem(armor) && !player.hasPermission(CosminConstants.PERM_FORCEEQUIP_REMOVE)){
            plugin.getMessageManager().sendMessage(CosminConstants.M_CANT_DEQUIP, player);
        }
        else if(e.getClick() == plugin.getConfigUtils().getHotKeysList().get(0)){

            if(ItemBuilder.isEnableItem(clickedItem)){
                e.setCurrentItem(generateToolTips(plugin.miscItems.getDisableItem(), slot));
            }
            else{
                e.setCurrentItem(generateToolTips(plugin.miscItems.getEnableItem(), slot));
            }
        }
        // on right click
        else if(e.getClick() == plugin.getConfigUtils().getHotKeysList().get(1)){
            if(plugin.getConfig().getBoolean("wardrobe-enabled."+slot.toString(),true)){
                plugin.guiManager.showPagedGui(player, targetPlayer, slot.getContext());
            }
        }
        // on drop
        else if(e.getClick() == plugin.getConfigUtils().getHotKeysList().get(2)){
            if(ItemBuilder.isHatItem(armor) && ItemBuilder.isCrossMatchAllowed(armor)){
                e.getClickedInventory().setItem(itemSlotMap.get(slot), null);
            }
        }
        e.setCancelled(true);

    }
    public void createLayout(List<String> invlayoutList){
        Map<Integer, ItemStack> invLayout = new HashMap<>();
        if(invlayoutList.size() > 6){
            plugin.getLogger().severe("Error in creating gui layout (inventory size can not exceed 6 rows), using the default layout...");
            invSize = CosminConstants.DEFAULT_INV_SIZE;
            createLayout(CosminConstants.DEFAULT_GUI_LAYOUT);
            return;
        }
        int slot = 0;
        for(int i =0;i<invlayoutList.size();i++){
            List<String> row = Arrays.asList(invlayoutList.get(i).split("\\|"));
            if(row.size()!=9){
                plugin.getLogger().severe("Error in creating gui layout (every inventory row can only have 9 slots), using the default layout...");
                createLayout(CosminConstants.DEFAULT_GUI_LAYOUT);
                break;
            }
            for(int k=0;k<row.size();k++){
                String itemTag = row.get(k);
                boolean filled = false;
                for(CItemSlot cSlot : CItemSlot.values()){
                    if(cSlot.getGuiKey().equalsIgnoreCase(itemTag)){
                        itemSlotMap.put(cSlot,slot);
                        filled = true;
                    }
                    else if(cSlot.getTGuiKey().equalsIgnoreCase(itemTag)){
                        tItemSlotMap.put(cSlot,slot);
                        filled = true;
                    }
                }
                if(itemTag.equalsIgnoreCase("set-button") && setButtonSlot==-1){
                    setButtonSlot = slot;
                    filled = true;
                }
                if(itemTag.equalsIgnoreCase("filler-glass")){
                    fillerSlots.put(slot,plugin.miscItems.getFillerGlass());
                    filled = true;
                }
                if(!filled){
                    ItemStack fillerItem;
                    if(Material.getMaterial(itemTag) != null){
                        fillerItem = new ItemStack(Material.getMaterial(itemTag));
                    }
                    else if(plugin.getArmorManager().containsArmor(itemTag)){
                        fillerItem = plugin.getArmorManager().getArmor(itemTag).getItem();
                    }
                    else{
                        fillerItem = plugin.miscItems.getFillerGlass();
                        plugin.getLogger().severe("unknown item "+itemTag+", using filler-glass as default");
                    }
                    fillerSlots.put(slot,fillerItem);
                }
                slot++;
            }
        }
        invSize = invlayoutList.size() * 9;

    }

    public ItemStack generateToolTips(ItemStack item,CItemSlot slot){
        if(!ItemBuilder.showToolTip(item)) return item;
        ItemMeta meta = item.getItemMeta();
        String toolTip = plugin.getConfig().getString(CosminConstants.TOGGLE_ITEM_TOOLTIP+slot.toString()," ");
        meta.setDisplayName(ItemBuilder.getDisplayName(item)+ " "+ ChatColor.translateAlternateColorCodes('&', toolTip));
        item.setItemMeta(meta);
        return item;
    }

    public Map<CItemSlot, Integer> getItemSlotMap() {
        return itemSlotMap;
    }

    public Map<CItemSlot, Integer> getTItemSlotMap() {
        return tItemSlotMap;
    }

}
