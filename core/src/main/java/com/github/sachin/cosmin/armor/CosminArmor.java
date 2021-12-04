package com.github.sachin.cosmin.armor;

import com.github.sachin.cosmin.gui.GuiContext;
import com.github.sachin.cosmin.utils.CItemSlot;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class CosminArmor{

    private ConfigurationSection config;
    private ItemStack item;
    private GuiContext context;
    private String internalName;
    private CItemSlot slot;
    private String permission;
    private boolean hide;
    private int cost = 0;
    private String optifineFile;
    private int points;

    public CosminArmor(ItemStack item,String internalName){
        this.item = item;
        this.internalName = internalName;
        this.context = GuiContext.HELMET_PAGE;
        this.hide = false;
        
    }

    public CosminArmor(ItemStack item,String internalName,CItemSlot slot){
        this.item = item;
        this.internalName = internalName;
        this.slot = slot;
        this.context = slot.getContext();
    }

    public CosminArmor(ItemStack item,String internalName,String permission){
        this.item = item;
        this.permission = permission;
        this.internalName = internalName;
        this.context = GuiContext.HELMET_PAGE;
        this.hide = false;
    }

    public ConfigurationSection getConfig() {
        return config;
    }

    public void setConfig(ConfigurationSection config) {
        this.config = config;
    }


    public ItemStack getItem() {
        return item;
    }

    public GuiContext getContext() {
        return context;
    }

    public String getInternalName() {
        return internalName;
    }

    public CItemSlot getSlot() {
        return slot;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public int getPlayerPoints() {
        return points;
    }

    public void setPlayerPoints(int points) {
        this.points = points;
    }

    public String getOptifineFile() {
        return optifineFile;
    }

    public void setOptifineFile(String optifineFile) {
        this.optifineFile = optifineFile;
    }
    
    public void setContext(GuiContext context) {
        this.context = context;
    }

    public void setSlot(CItemSlot slot) {
        this.slot = slot;
    }

    public String getPermission() {
        return permission;
    }
    public void setPermission(String permission) {
        this.permission = permission;
    }

    public void setHide(boolean value){
        this.hide = value;
    }

    public boolean hide(){
        return this.hide;
    }

    public int getCost() {
        return cost;
    }
    public void setCost(int cost) {
        this.cost = cost;
    }

    public CosminArmor clone(){
        CosminArmor armor = new CosminArmor(this.item,this.internalName,this.permission);
        armor.setCost(this.cost);
        armor.setHide(this.hide);
        armor.setContext(this.context);
        armor.setSlot(this.slot);
        armor.setConfig(this.config);
        return armor;
    }
    
}
