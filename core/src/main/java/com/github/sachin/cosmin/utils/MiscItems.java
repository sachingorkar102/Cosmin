package com.github.sachin.cosmin.utils;

import com.github.sachin.cosmin.Cosmin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.FileReader;

public class MiscItems {

    private Cosmin plugin;

    private File miscItemsFile;

    private YamlConfiguration config;

    private ItemStack fillerGlass;
    private ItemStack enableItem;
    private ItemStack disableItem;
    private ItemStack previousButton;
    private ItemStack nextButton;
    private ItemStack backButton;
    private ItemStack cosmeticSetButton;
    private ItemStack shopButton;
    private ItemStack confirmButton;
    private ItemStack cancelButton;
    // private ItemStack applyButton;

    public MiscItems(Cosmin plugin){
        this.plugin = plugin;
        this.miscItemsFile = new File(plugin.getDataFolder(),CosminConstants.MISC_ITEMS_FILE);
        if(!miscItemsFile.exists()){
            plugin.saveResource(CosminConstants.MISC_ITEMS_FILE, false);
        }
        
    }

    public void loadMiscItems(){
        try (FileReader reader = new FileReader(miscItemsFile)) {
            config = new YamlConfiguration();
            config.load(reader);
            backButton = ItemBuilder.itemFromFile(config.getConfigurationSection(CosminConstants.BACK_BUTTON), CosminConstants.BACK_BUTTON);
            fillerGlass = ItemBuilder.itemFromFile(config.getConfigurationSection(CosminConstants.FILLAR_GLASS),CosminConstants.FILLAR_GLASS);
            enableItem = ItemBuilder.itemFromFile(config.getConfigurationSection(CosminConstants.ENABLE_ITEM),CosminConstants.ENABLE_ITEM);
            disableItem = ItemBuilder.itemFromFile(config.getConfigurationSection(CosminConstants.DISABLE_ITEM),CosminConstants.DISABLE_ITEM);
            previousButton = ItemBuilder.itemFromFile(config.getConfigurationSection(CosminConstants.PREVIOUS_BUTTON), CosminConstants.PREVIOUS_BUTTON);
            nextButton = ItemBuilder.itemFromFile(config.getConfigurationSection(CosminConstants.NEXT_BUTTON), CosminConstants.NEXT_BUTTON);
            cosmeticSetButton = ItemBuilder.itemFromFile(config.getConfigurationSection(CosminConstants.COSMETIC_SET_BUTTON), CosminConstants.COSMETIC_SET_BUTTON);
            shopButton = ItemBuilder.itemFromFile(config.getConfigurationSection(CosminConstants.SHOP_BUTTON), CosminConstants.SHOP_BUTTON);
            confirmButton = ItemBuilder.itemFromFile(config.getConfigurationSection(CosminConstants.CONFIRM_BUTTON), CosminConstants.CONFIRM_BUTTON);
            cancelButton = ItemBuilder.itemFromFile(config.getConfigurationSection(CosminConstants.CANCEL_BUTTON), CosminConstants.CANCEL_BUTTON);
            // applyButton = ItemBuilder.itemFromFile(yml.getConfigurationSection(CosminConstants.APLLY_BUTTON), CosminConstants.APLLY_BUTTON);
            reader.close();
        } catch (Exception e) {
            plugin.getLogger().warning("Error while loading misc-items.yml");
            e.printStackTrace();
        } 
    }

    public ItemStack getFillerGlass() {
        return fillerGlass;
    }

    public ItemStack getEnableItem() {
        return enableItem;
    }

    public ItemStack getDisableItem() {
        return disableItem;
    }

    public ItemStack getNextButton() {
        return nextButton;
    }
    public ItemStack getPreviousButton() {
        return previousButton;
    }
    public ItemStack getBackButton() {
        return backButton;
    }
    public ItemStack getCosmeticSetButton() {
        return cosmeticSetButton;
    }
    public ItemStack getShopButton() {
        return shopButton;
    }

    public ItemStack getConfirmButton() {
        return confirmButton;
    }
    public ItemStack getCancelButton() {
        return cancelButton;
    }

    public YamlConfiguration getConfig() {
        return config;
    }
}
