package com.github.sachin.cosmin.utils;

import com.github.sachin.cosmin.Cosmin;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class InventoryUtils {

    // private List<ItemStack> dummyList = new ArrayList<>();

    public static String itemStackListToBase64(List<ItemStack> items){
        Cosmin plugin = Cosmin.getInstance();
        items = items.stream().filter(i -> {
            if(i != null){
                if(i.isSimilar(plugin.miscItems.getFillerGlass())){
                    return false;
                }
                if(plugin.getConfigUtils().isCosmeticSetEnabled() && i.isSimilar(plugin.miscItems.getCosmeticSetButton())){
                    return false;
                }
                
            }
            return true;
        }).collect(Collectors.toList());
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            
            dataOutput.writeInt(items.size());
            
            // for (int i = 0; i < items.length; i++) {
            //     dataOutput.writeObject(items[i]);
            // }
            for (ItemStack itemStack : items) {
                dataOutput.writeObject(itemStack);
            }
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ItemStack[] base64ToItemStackArray(String string){
        Cosmin plugin = Cosmin.getInstance();
        List<ItemStack> dummyList = Arrays.asList(new ItemStack[18]);
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
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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

    
    
}
