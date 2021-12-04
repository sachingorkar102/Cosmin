package com.github.sachin.cosmin.armor;

import com.github.sachin.cosmin.utils.CItemSlot;

import java.util.HashMap;
import java.util.Map;


public class CosmeticSet {

    private Map<CItemSlot,CosminArmor> armorSet = new HashMap<>();
    private CosminArmor icon;
    private String internalName;
    private String permission;
    private int cost;
    private int points;
    private boolean allowCrossMatch=true;

    public CosmeticSet(Map<CItemSlot,CosminArmor> armorSet,CosminArmor icon,String internalName){
        this.icon = icon;
        this.internalName = internalName;
        this.armorSet = armorSet;
    }


    public CosmeticSet(String internalName,String permission){
        this.internalName = internalName;
        this.permission = permission;
    }

    public CosminArmor getIcon() {
        return icon;
    }

    public void setIcon(CosminArmor icon) {
        this.icon = icon;
    }

    public Map<CItemSlot, CosminArmor> getArmorSet() {
        return armorSet;
    }

    public void setArmorSet(Map<CItemSlot, CosminArmor> armorSet) {
        this.armorSet = armorSet;
    }

    public int getPlayerPoints() {
        return points;
    }

    public void setPlayerPoints(int points) {
        this.points = points;
    }

    public void setArmorSlot(CosminArmor cosminArmor,CItemSlot slot){
        this.armorSet.put(slot, cosminArmor);
    }

    public String getInternalName() {
        return internalName;
    }

    public CosminArmor getArmor(CItemSlot slot){
        return this.armorSet.get(slot);
    }

    public String getPermission() {
        return permission;
    }
    public void setPermission(String permission) {
        this.permission = permission;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }


    public boolean allowCrossMatch(){
        return allowCrossMatch;
    }

    public void setAllowCrossMatch(boolean value){
        this.allowCrossMatch = value;
    }

    
}
