package com.github.sachin.cosmin.utils;

import java.util.Arrays;
import java.util.List;

public class CosminConstants {

    // file & folder names
    public static final String MISC_ITEMS_FILE = "misc-items.yml";
    public static final String PLAYER_DATA_FILE = "player-data.json";
    public static final String EXAMPLE_ITEM_FILE = "items/example-items.yml";

    // misc item names
    public static final String FILLAR_GLASS = "filler-glass";
    public static final String ENABLE_ITEM = "enable-item";
    public static final String DISABLE_ITEM = "disable-item";
    public static final String PREVIOUS_BUTTON = "previous-button";
    public static final String NEXT_BUTTON = "next-button";
    public static final String BACK_BUTTON = "back-button";
    public static final String COSMETIC_SET_BUTTON = "cosmetic-set-button";
    public static final String SHOP_BUTTON = "shop-button";
    public static final String CONFIRM_BUTTON = "confirm-button";
    public static final String CANCEL_BUTTON = "cancel-button";
    // public static final String APLLY_BUTTON = "apply-button";

    // titles from config
    public static final String MAIN_GUI = "inventory-titles.main-gui";
    public static final String HELMET_GUI = "inventory-titles.helmet-page";
    public static final String CHESTPLATE_GUI = "inventory-titles.chestplate-page";
    public static final String LEGGINGS_GUI = "inventory-titles.leggings-page";
    public static final String BOOTS_GUI = "inventory-titles.boots-page";
    public static final String OFFHAND_GUI = "inventory-titles.offhand-page";
    public static final String COSMETIC_SETS_PAGE = "inventory-titles.cosmetic-sets-page";
    public static final String SHOP_PAGE = "inventory-titles.store-page";
    public static final String CONFIRM_PAGE = "inventory-titles.confirm-page";

    // config keys for database
    public static final String DB_ENABLED = "database.enabled";
    public static final String DB_HOST = "database.host";
    public static final String DB_PORT = "database.port";
    public static final String DB_USERNAME = "database.username";
    public static final String DB_PASSWORD = "database.password";
    public static final String DB_NAME = "database.name";
    public static final String DB_TABLE_NAME = "database.table-name";
    public static final String DB_TYPE = "database.type";

    public static final String DB_MAX_LIFE_TIME = "database.max-life-time";

    public static final String DB_AUTO_RECONNECT = "database.auto-reconnect";

    // config key for prevent external slots
    public static final String ALLOW_EXTERNAL_ARMOR = "allow-external-armor";
    public static final String ALIASES = "aliases";
    public static final String ENABLE_COSMETIC_SET = "enable-cosmetic-set";
    public static final String ENABLE_STORE = "enable-store";
    public static final String SHOP_ITEM_LORE = "shop-item-lore";
    public static final String TOGGLE_ITEM_TOOLTIP = "toggle-item-tooltip.";
    public static final String BLACKLIST_MATERIALS = "blacklist-materials";
    public static final String OPEN_COSMETIC_GUI_ON_WB_CLOSE = "open-cosmetic-gui-on-wardrobe-close";

    // messages
    public static final String M_PREFIX = "prefix";
    public static final String M_NO_PERM = "no-permission";
    public static final String M_NOT_ENOUGH_BALANCE = "not-enough-balance";
    public static final String M_CANT_DEQUIP = "cant-deequip";
    public static final String M_OFFLINE_PLAYER = "offline-player";
    public static final String M_INVALID_SLOT = "invalid-slot";
    public static final String M_INVALID_ITEM = "invalid-item";
    public static final String M_RELOADED = "reloaded";
    public static final String M_GAVE_ITEM = "gave-item";
    public static final String M_DATA_CLEARED = "data-cleared";

    // hotkey config
    public static final String HOTKEY_TOGGLE_DISPLAY = "hot-keys.toggle-display";
    public static final String HOTKEY_OPEN_WARDROBE = "hot-keys.open-wardrobe";
    public static final String HOTKEY_CLEAR_WARDROBE = "hot-keys.clear-wardrobe-item";
    public static final String HOTKEY_OPEN_COSMETIC_SET_GUI = "hot-keys.open-cosmetic-set-gui";
    public static final String HOTKEY_CLEAR_COSMETIC_SET = "hot-keys.clear-cosmetic-set";

    public static final String DELAY_AFTER_EQUIP = "delay-after-equip";


    // list of some unclickable slots
    public static final List<Integer> TOGGLABLE_SLOTS = Arrays.asList(2,3,4,5,6);
    public static final List<Integer> COSMIN_ARMOR_SLOTS = Arrays.asList(11,12,13,14,15);
    public static final List<Integer> FILLAR_SLOTS = Arrays.asList(0,1,7,8,9,10,16,17);
    public static final List<Integer> BORDER_SLOTS = Arrays.asList(0,1,2,3,4,5,6,7,8,9,18,27,36,45,46,47,48,51,52,53,17,26,35,44);
    public static final List<Integer> HAT_SLOTS = Arrays.asList(10,11,12,13,14,15,16,
                                                                19,20,21,22,23,24,25,
                                                                28,29,30,31,32,33,34,
                                                                37,38,39,40,41,42,43);

    public static final List<Integer> EQUIPMENT_SLOTS = Arrays.asList(5,6,7,8,45);
    public static final List<String> COMPATIBLE_VERSIONS_PRE_NETHER_UPDATE = Arrays.asList("v1_12_R1","v1_13_R1","v1_13_R2","v1_14_R1","v1_15_R1");
    public static final List<String> COMPATIBLE_VERSIONS_POST_NETHER_UPDATE = Arrays.asList("v1_16_R1","v1_16_R2","v1_16_R3","v1_17_R1","v1_18_R1","v1_18_R2","v1_19_R1","v1_19_R2","v1_19_R3","v1_20_R1","v1_20_R2");

    public static final boolean ISDEMO  = false;



    // permissions
    public static final String PERM_COMMAND_RELOAD = "cosmin.command.reload";
    public static final String PERM_COMMAND_VIEW = "cosmin.command.view";
    public static final String PERM_COMMAND_GIVE = "cosmin.command.give";
    public static final String PERM_COMMAND_CLEAR = "cosmin.command.clear";
    public static final String PERM_COMMAND_COSMETICS = "cosmin.command.cosmetics";
    public static final String PERM_COMMAND_EQUIP = "cosmin.command.forceequip";
    public static final String PERM_COMMAND_DEQUP = "cosmin.command.forcedequip";
    public static final String PERM_COMMAND_HELP = "cosmin.command.help";
    public static final String PERM_COMMAND_TAKE = "cosmin.command.take";
    public static final String PERM_COMMAND_BUY = "cosmin.command.buy";
    public static final String PERM_COMMAND_ALL = "cosmin.command.*";
    public static final String PERM_COSMETICSET = "cosmin.cosmeticset";
    public static final String PERM_FORCEEQUIP_REMOVE = "cosmin.forceequip.remove";
    public static final String PERM_REALARMOR = "cosmin.realarmor";

    
}


