package com.github.sachin.cosmin.gui;

import com.github.sachin.cosmin.Cosmin;
import com.github.sachin.cosmin.armor.CosminArmor;
import com.github.sachin.cosmin.utils.CosminConstants;
import com.github.sachin.cosmin.utils.ItemBuilder;
import com.github.sachin.cosmin.utils.MiscItems;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;



public class PagedGui extends GuiHolder {

    protected int page = 0;
    protected int maxItemsPerPage = 28;
    protected int index = 0;
    protected MiscItems miscItems;
    protected List<ItemStack>  items = new ArrayList<>();


    public PagedGui(Player player, GuiContext context) {
        super(player, context);
        this.miscItems = Cosmin.getInstance().miscItems;
        // setItems();
        
    }

    public PagedGui(final Player player,@Nullable final Player targetPlayer,GuiContext context){
        super(player, targetPlayer, context);
        this.miscItems = Cosmin.getInstance().miscItems;
        // setItems();
    }

    public int getMaxItemsPerPage() {
        return maxItemsPerPage;
    }

    public void setItems(){

        for(CosminArmor a : plugin.getArmorManager().getAllArmor()){
            if(a.hide()) continue;
            if(a.getContext() == context){
                String perm = a.getPermission();
                if(!perm.equals("none")){
                    if(!player.hasPermission(perm)){
                        continue;
                    }
                }
                if(a.getCost() != 0 || a.getPlayerPoints() != 0){
                    if(!getCosminPlayer().getPurchasedItems().contains(a.getInternalName())) continue;
                }
                this.items.add(a.getItem());
            }
        }
    }

    public void handlePageClicks(InventoryClickEvent e){
        e.setCancelled(true);
        if(e.getCurrentItem() == null) return;
        handleMiscClicks(e);
        
        if (ItemBuilder.isHatItem(e.getCurrentItem()) && (e.getClickedInventory().getHolder() instanceof PagedGui)){
            updateHat(e.getCurrentItem());
        }
        
        
    }

    public void handleMiscClicks(InventoryClickEvent e){
        if(e.getCurrentItem().isSimilar(miscItems.getShopButton())){
            plugin.guiManager.showShopGui(player, targetPlayer, context);
            
        }
        else if(e.getCurrentItem().isSimilar(miscItems.getPreviousButton())){
            if (page != 0){
                page = page - 1;
                openPage();
            }
        }
        else if(e.getCurrentItem().isSimilar(miscItems.getBackButton())){
            revertToFakeGui();
        }
        else if (e.getCurrentItem().isSimilar(miscItems.getNextButton())){
            if (!((index + 1) >= items.size())){
                page = page + 1;
                openPage();
            }
        }
    }

    private void updateHat(ItemStack hatItem){
        List<ItemStack> contents = targetPlayer == null ? plugin.getPlayerManager().getPlayer(player).getCosminInvContents() : plugin.getPlayerManager().getPlayer(targetPlayer).getCosminInvContents();
        
        ItemStack replaceItem = contents.get(context.getSlotid());
        if(replaceItem == null || ItemBuilder.isHatItem(replaceItem)){
            contents.set(context.getSlotid(), hatItem);
            revertToFakeGui();
        }
        else{
            revertToFakeGui();
        }
    }
    public void openPage(){
        // if(items.isEmpty() || items == null) return;
        this.inventory = Bukkit.createInventory(this, 54,getTitle());
        setBorderItems();
        if(plugin.getConfig().getBoolean(CosminConstants.ENABLE_STORE,true) && plugin.isEconomyEnabled()){
            inventory.setItem(50, miscItems.getShopButton());
        }
        player.openInventory(inventory); 
    }

    public void revertToFakeGui(){
        if(plugin.getConfig().getBoolean(CosminConstants.OPEN_COSMETIC_GUI_ON_WB_CLOSE)){
            plugin.guiManager.showFakeGui(player, targetPlayer);
        }else{
            getPlayer().closeInventory();
        }
    }


    public String getTitle(){
        String pageString = String.valueOf(page+1);
        
        String title = plugin.getConfig().getString(context.getTitle(),"GUI").replace("%page%", pageString);
        if(plugin.isPAPIEnabled()){
            title = PlaceholderAPI.setPlaceholders(getPlayer(), title);
        }
        return ChatColor.translateAlternateColorCodes('&', title);
        
    }

    public void setBorderItems(){
        inventory.setItem(45, miscItems.getFillerGlass());
        inventory.setItem(53,miscItems.getFillerGlass());
        inventory.setItem(50,miscItems.getFillerGlass());
        
        inventory.setItem(49, miscItems.getBackButton());
        for (int slot : CosminConstants.BORDER_SLOTS) {
            inventory.setItem(slot, miscItems.getFillerGlass());
        }
        for(int i = 0; i < getMaxItemsPerPage(); i++) {
            index = getMaxItemsPerPage() * page + i;
            if(index >= items.size()) break;
            if (items.get(index) != null){
                inventory.setItem(inventory.firstEmpty(), items.get(index));
            }
        }
        if(page != 0){
            inventory.setItem(45, miscItems.getPreviousButton());
        }
        if(items.size() > maxItemsPerPage){
            inventory.setItem(53, miscItems.getNextButton());
        }
    }

    
    
}
