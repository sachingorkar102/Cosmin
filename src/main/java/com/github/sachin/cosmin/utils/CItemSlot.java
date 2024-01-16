package com.github.sachin.cosmin.utils;

import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;
import com.github.sachin.cosmin.gui.GuiContext;

public enum CItemSlot {
    
    OFFHAND(ItemSlot.OFFHAND,45,40,15,6,GuiContext.OFFHAND_PAGE,"offhand-slot"),
    FEET(ItemSlot.FEET,8,36,14,5,GuiContext.BOOTS_PAGE,"feet-slot"),
    LEGS(ItemSlot.LEGS,7,37,13,4,GuiContext.LEGGINGS_PAGE,"legs-slot"),
    CHEST(ItemSlot.CHEST,6,38,12,3,GuiContext.CHESTPLATE_PAGE,"chest-slot"),
    HEAD(ItemSlot.HEAD,5,39,11,2,GuiContext.HELMET_PAGE,"head-slot");

    private CItemSlot(ItemSlot protocolSlot,int equipmentSlotId,int altSlotId,int fakeSlotId,int toggleSlotId,GuiContext context,String guiKey){
        this.protocolSlot = protocolSlot;
        this.altSlotId = altSlotId;
        this.equipmentSlotId = equipmentSlotId;
        this.fakeSlotId = fakeSlotId;
        this.toggleSlotId = toggleSlotId;
        this.context = context;
        this.guiKey = guiKey;
        this.tGuiKey = "toggle-"+guiKey;
    }

    private ItemSlot protocolSlot;

    private GuiContext context;

    private int equipmentSlotId;
    private int fakeSlotId;
    private int toggleSlotId;
    private int altSlotId;

    private String guiKey;
    private String tGuiKey;

    public int getAltSlotId() {
        return altSlotId;
    }

    public ItemSlot getProtocolSlot() {
        return protocolSlot;
    }

    public int getEquipmentSlotId() {
        return equipmentSlotId;
    }
    public int getFakeSlotId() {
        return fakeSlotId;
    }
    public int getToggleSlotId() {
        return toggleSlotId;
    }
    public GuiContext getContext() {
        return context;
    }

    public String getGuiKey() {
        return guiKey;
    }

    public String getTGuiKey() {
        return tGuiKey;
    }
}

