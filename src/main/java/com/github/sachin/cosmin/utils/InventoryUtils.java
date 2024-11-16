package com.github.sachin.cosmin.utils;

import com.github.sachin.cosmin.Cosmin;
import com.github.sachin.cosmin.compat.MMOItemsAPI;
import com.github.sachin.prilib.utils.FastItemStack;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class InventoryUtils {

    // private List<ItemStack> dummyList = new ArrayList<>();

    public static String itemStackListToBase64(List<ItemStack> items){
        Cosmin plugin = Cosmin.getInstance();

        YamlConfiguration yaml = new YamlConfiguration();
        for (int i = 0; i < items.size(); i++) {
            ItemStack item = items.get(i);
            String key = Integer.toString(i);
            if(item==null){
                yaml.set(key,new ItemStack(Material.AIR));
                continue;
            };
            if(item.isSimilar(plugin.miscItems.getFillerGlass()) || item.isSimilar(plugin.miscItems.getCosmeticSetButton())) continue;
            FastItemStack fastItem = new FastItemStack(item);

            if(item.isSimilar(plugin.miscItems.getDisableItem())){
                fastItem.set(CosminConstants.TOGGLE_ITEM_KEY, PersistentDataType.INTEGER,0);
            }
            else if(item.isSimilar(plugin.miscItems.getEnableItem())){
                fastItem.set(CosminConstants.TOGGLE_ITEM_KEY,PersistentDataType.INTEGER,1);
            }
            yaml.set(key, fastItem.get());
        }
        return yaml.saveToString();

//        try {
//            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
//
//            dataOutput.writeInt(items.size());
//
//            // for (int i = 0; i < items.length; i++) {
//            //     dataOutput.writeObject(items[i]);
//            // }
//            for (ItemStack itemStack : items) {
//                dataOutput.writeObject(itemStack);
//            }
//            dataOutput.close();
//            return Base64Coder.encodeLines(outputStream.toByteArray());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
    }

    public static ItemStack[] base64ToItemStackArray(String string){
        Cosmin plugin = Cosmin.getInstance();
        YamlConfiguration yaml = new YamlConfiguration();
        List<ItemStack> dummyList = Arrays.asList(new ItemStack[18]);
        try {
            yaml.loadFromString(string);
            for(int i =0;i<18;i++){
                if(CosminConstants.FILLAR_SLOTS.contains(i)) dummyList.set(i,plugin.miscItems.getFillerGlass());
                else if(yaml.contains(Integer.toString(i))){
                    ItemStack item = yaml.getItemStack(Integer.toString(i));
                    FastItemStack fastItem = new FastItemStack(item);
                    String armorName = plugin.getArmorName(fastItem);
                    if(fastItem.hasKey(CosminConstants.TOGGLE_ITEM_KEY,PersistentDataType.INTEGER)){
                        dummyList.set(i,fastItem.get(CosminConstants.TOGGLE_ITEM_KEY,PersistentDataType.INTEGER)==0 ? plugin.miscItems.getDisableItem() : plugin.miscItems.getEnableItem());
                    }
                    else if(armorName != null){
                        dummyList.set(i,plugin.getArmorManager().getArmor(armorName).getItem());
                    }
                    else if(plugin.isMMOItemsEnabled() && fastItem.hasKey(MMOItemsAPI.ID,PersistentDataType.STRING)){
                        dummyList.set(i,MMOItemsAPI.getMMOItem(item));
                    }
                    else{
                        dummyList.set(i,item);
                    }
                }
            }
            if(plugin.getConfigUtils().isCosmeticSetEnabled()){
                dummyList.set(0,plugin.miscItems.getCosmeticSetButton());
            }
            return dummyList.toArray(new ItemStack[0]);
        } catch (Exception ex) {
            try {
                ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(string));
                BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
                ItemStack[] items = new ItemStack[dataInput.readInt()];
                List<ItemStack> initialList = new ArrayList<>();
                for (int i = 0; i < items.length; i++) {
                    items[i] = (ItemStack)dataInput.readObject();
                    initialList.add(items[i]);
                }
                for (int i=0;i<18;i++){

                    if(CosminConstants.FILLAR_SLOTS.contains(i)){
                        dummyList.set(i, plugin.miscItems.getFillerGlass());
                    }
                    else{
                        dummyList.set(i, initialList.get(0));
                        initialList.remove(0);
                    }

                }
                if(plugin.getConfigUtils().isCosmeticSetEnabled()){
                    dummyList.set(0, plugin.miscItems.getCosmeticSetButton());
                }
                dataInput.close();
                return dummyList.toArray(new ItemStack[0]);
            } catch (Exception e) {
                return plugin.getItemsFromYAML(decompressYAMLString(string),null).toArray(new ItemStack[0]);
            }


        }
    }

    public static String serializeItem(ItemStack item){
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            // dataOutput.writeInt(1);
            dataOutput.writeObject(item);
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            System.out.println("Error occured while serializing item");
            e.printStackTrace();
        }
        return null;
    }

    public static ItemStack deserializeItem(String string){
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(string));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack item = (ItemStack)dataInput.readObject();
            if(item != null){
                return item;
            }
        } catch (Exception e) {
            System.out.println("Error occured while deserializing item");
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isYamlString(String string){
        try {
            YamlConfiguration yaml = new YamlConfiguration();
            yaml.loadFromString(string);
            return true;
        }catch (Exception ignored){
            return false;
        }
    }

    public static String compressYAMLString(String yamlString){
        if (yamlString == null || yamlString.length() == 0) {
            return yamlString;
        }
        ByteArrayOutputStream obj = new ByteArrayOutputStream();
        try (GZIPOutputStream gzip = new GZIPOutputStream(obj)) {
            gzip.write(yamlString.getBytes("UTF-8"));
        } catch (IOException e) {
            return yamlString;
        }
        return Base64.getEncoder().encodeToString(obj.toByteArray());
    }

    public static String decompressYAMLString(String string){
        if (string == null || string.isEmpty()) {
            return string;
        }
        try {
            byte[] bytes = Base64.getDecoder().decode(string);
            ByteArrayInputStream byteStream = new ByteArrayInputStream(bytes);
            try (GZIPInputStream gzip = new GZIPInputStream(byteStream)) {
                byte[] buffer = new byte[1024];
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                int len;
                while ((len = gzip.read(buffer)) > 0) {
                    out.write(buffer, 0, len);
                }
                return out.toString("UTF-8");
            }
        } catch (IllegalArgumentException | IOException e) {
            return string;
        }
    }

    
    
}
