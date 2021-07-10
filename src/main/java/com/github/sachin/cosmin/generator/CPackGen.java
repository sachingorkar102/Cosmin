package com.github.sachin.cosmin.generator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import com.github.sachin.cosmin.Cosmin;
import com.github.sachin.cosmin.armor.CosminArmor;
import com.github.sachin.cosmin.utils.CItemSlot;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import org.bukkit.craftbukkit.libs.org.apache.commons.io.FileUtils;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FilenameUtils;
import org.bukkit.inventory.ItemStack;

public class CPackGen {
    

    public static void createPack(String packname) throws IOException{
        Cosmin plugin = Cosmin.getInstance();
        Gson gson = new Gson();
        File resource = new File(plugin.getDataFolder(),"resource-packs/"+packname+"/assets/minecraft");
        File textures = new File(plugin.getDataFolder(),"textures");
        File modelFolder = new File(resource,"models/item");
        File textureFolder = new File(resource,"textures/item");
        textureFolder.mkdirs();
        modelFolder.mkdirs();
        if(textures.mkdir()){
            plugin.getLogger().info("textures folder was either empty or not found..");
            return;
        }
        if(resource.mkdirs()){
            plugin.getLogger().info("Created base directory for "+packname);
            
            new File(resource,"textures").mkdir();
        }

        List<File> textureFiles = Arrays.asList(textures.listFiles());
        for (CosminArmor armor : plugin.getArmorManager().getAllArmor()) {
            if(armor.getSlot() == CItemSlot.HEAD){
                ItemStack item = armor.getItem();
                if(item.getItemMeta().hasCustomModelData()){
                    // json = models/item/head
                    // png = textures/item/head
                    File pngFile = null;
                    File jsonFile = null;
                    for(File file: textureFiles){
                        if(FilenameUtils.getExtension(file.getName()).equals("json") && file.getName().replace(".json", "").equals(armor.getInternalName())){
                            jsonFile = file;
                        }
                        if(FilenameUtils.getExtension(file.getName()).equals("png") && file.getName().replace(".png", "").equals(armor.getInternalName())){
                            pngFile = file;
                        }
                    }
                    if(pngFile != null && jsonFile != null){
                        File newItemModelFile = new File(modelFolder,jsonFile.getName());
                        FileUtils.copyFile(jsonFile, newItemModelFile);


                        createBaseItemFile(modelFolder,gson,armor);
                        redirectTexturesPath(modelFolder, armor, gson);


                        File newItemPngFile = new File(textureFolder,pngFile.getName());
                        FileUtils.copyFile(pngFile, newItemPngFile);
                    }
                }
            }
                
        }
        plugin.getLogger().info(packname+" generated");    
    }

    private static void redirectTexturesPath(File folder,CosminArmor armor,Gson gson) throws IOException{
        File file = new File(folder,armor.getInternalName()+".json");
        if(!file.exists()){
            return;
        }
        FileReader reader = new FileReader(file);
        JsonObject obj = gson.fromJson(reader, JsonObject.class);
        JsonObject textureObj = new JsonObject();
        textureObj.addProperty("0", "item/"+armor.getInternalName());
        obj.add("textures", textureObj);
        FileWriter writer = new FileWriter(file);
        gson.toJson(obj, writer);
        writer.close();
        reader.close();
    }

    private static void createBaseItemFile(File folder,Gson gson,CosminArmor armor) throws IOException{
        String itemName = armor.getItem().getType().toString().toLowerCase();
        File baseItemJson = new File(folder,itemName+".json");
        JsonObject obj = new JsonObject();
        JsonArray oldOverides = null;
        if(!baseItemJson.exists()){
            baseItemJson.createNewFile();
        }
        else{
            FileReader reader = new FileReader(baseItemJson);
            JsonObject oldObject = gson.fromJson(reader, JsonObject.class);
            if(oldObject.has("overrides")){
                oldOverides = oldObject.get("overrides").getAsJsonArray();
            }
            reader.close();
        }
        FileWriter writer = new FileWriter(baseItemJson);
        
        obj.addProperty("parent", "item/handheld_rod");
        JsonObject texturesobj = new JsonObject();
        texturesobj.addProperty("layer0", "minecraft:item/"+itemName);
        obj.add("textures",texturesobj);
        JsonObject model = new JsonObject();
        model.addProperty("custom_model_data", armor.getItem().getItemMeta().getCustomModelData());
        JsonObject predicate = new JsonObject();
        predicate.add("predicate", model);
        predicate.addProperty("model", "item/"+armor.getInternalName());
        JsonArray overides = new JsonArray();
        overides.add(predicate);
        if(oldOverides != null){
            for(JsonElement el : oldOverides){
                overides.add(el);
            }
        }
        obj.add("overrides", overides);
        gson.toJson(obj, writer);

        writer.close();
    }
        
}
