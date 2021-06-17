package com.github.sachin.cosmin.gui;

import com.github.sachin.cosmin.utils.CosminConstants;

public enum GuiContext {
    CONFIRM_PAGE(0,CosminConstants.CONFIRM_PAGE),
    SHOP_PAGE(0,CosminConstants.SHOP_PAGE),
    COSMIN_INVENTORY(0,CosminConstants.MAIN_GUI),
    COSMETIC_SET_GUI(0,CosminConstants.COSMETIC_SETS_PAGE),
    HELMET_PAGE(11,CosminConstants.HELMET_GUI),
    CHESTPLATE_PAGE(12,CosminConstants.CHESTPLATE_GUI),
    LEGGINGS_PAGE(13,CosminConstants.LEGGINGS_GUI),
    BOOTS_PAGE(14,CosminConstants.BOOTS_GUI),
    OFFHAND_PAGE(15,CosminConstants.OFFHAND_GUI);

    /*
        0  1  2  3  4  5  6  7  8
        9  10 11 12 13 14 15 16 17
    */
    private GuiContext(int slotid,String title){
        this.slotid = slotid;
        this.title = title;
    }

    private int slotid;
    private String title;

    public String getTitle() {
        return title;
    }

    public int getSlotid() {
        return slotid;
    }


    
}
