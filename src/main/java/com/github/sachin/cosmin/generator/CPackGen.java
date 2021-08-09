package com.github.sachin.cosmin.generator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.github.sachin.cosmin.Cosmin;
import com.github.sachin.cosmin.armor.CosminArmor;
import com.github.sachin.cosmin.utils.CItemSlot;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FileUtils;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FilenameUtils;
import org.bukkit.inventory.ItemStack;

public class CPackGen {
    

    public static void createPack(String packname) throws IOException{
        Cosmin plugin = Cosmin.getInstance();
        if(plugin.getMinecraftVersion().equals("v1_12_R1")){
            plugin.getLogger().info("Running older version then 1.14, using legacy pack generation");
            CPackGen1_12.generatePack(packname, plugin);
            return;
        }
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
            ItemStack item = armor.getItem();
            if(armor.getSlot() == CItemSlot.HEAD && armor.getOptifineFile() == null){
                if(item.getItemMeta().hasCustomModelData()){
                    generateModels(modelFolder, textureFolder, textureFiles, armor, plugin, gson);
                }
            }
            else if(armor.getSlot() == CItemSlot.CHEST && item.getType()== Material.ELYTRA){
                if(item.getItemMeta().hasCustomModelData()){
                    File optifineFolder = new File(resource,"optifine/cit");
                    optifineFolder.mkdirs();
                    File pngDisplayFile = null;
                    File pngIconFile = null;
                    for(File file : textureFiles){
                        if(FilenameUtils.getExtension(file.getName()).equals("png")){
                            String fileName = file.getName().replace(".png", "");
                            if(fileName.equals(armor.getInternalName())){
                                pngDisplayFile = file;
                                
                            }
                            if(fileName.endsWith("-icon") && fileName.replace("-icon", "").equals(armor.getInternalName())){
                                pngIconFile = file;
                            }
                        }
                    }
                    if(pngIconFile != null && pngDisplayFile != null){
                        plugin.getLogger().info("Generating optifine files for "+armor.getInternalName());
                        File newPropFile = new File(optifineFolder,"armor");
                        File newItemFile = new File(optifineFolder,"items");
                        newItemFile.mkdirs();
                        newPropFile.mkdirs();
                        FileUtils.copyFile(pngIconFile, new File(newItemFile,pngIconFile.getName()));
                        FileUtils.copyFile(pngDisplayFile, new File(newPropFile,pngDisplayFile.getName()));
                        createIconPropFile(newItemFile, armor, pngIconFile.getName());
                        createElytraPropFile(newPropFile, armor, pngDisplayFile.getName());

                    }
                    if(pngIconFile == null){
                        plugin.getLogger().warning("Could not find "+armor.getInternalName()+"-icon.png file in textures folder(icon file)");
                    }
                    if(pngDisplayFile == null){
                        plugin.getLogger().warning("Could not fine "+armor.getOptifineFile()+".png file in textures folder");
                    }

                }
            }
            else if(armor.getSlot() == CItemSlot.CHEST || armor.getSlot() == CItemSlot.LEGS || armor.getSlot() == CItemSlot.FEET || armor.getSlot() == CItemSlot.HEAD){
                String layer = null;
                if(armor.getSlot() == CItemSlot.LEGS){
                    layer = "2";
                }
                else{
                    layer = "1";
                }
                if(item.getItemMeta().hasCustomModelData() && armor.getOptifineFile() != null){
                    File optifineFolder = new File(resource,"optifine/cit");
                    optifineFolder.mkdirs();
                    File pngLayerFile = null;
                    File pngIconFile = null;
                    for(File file : textureFiles){
                        if(FilenameUtils.getExtension(file.getName()).equals("png")){
                            String fileName = file.getName().replace(".png", "");
                            if(fileName.equals(armor.getOptifineFile())){
                                pngLayerFile = file;
                            }
                            if(fileName.equals(armor.getInternalName()) ){
                                pngIconFile = file;
                            }
                        }
                    }
                    if(pngIconFile != null && pngLayerFile != null){
                        plugin.getLogger().info("Generating optifine files for "+armor.getInternalName());
                        File newPropFile = new File(optifineFolder,"armor");
                        File newItemFile = new File(optifineFolder,"items");
                        newItemFile.mkdirs();
                        newPropFile.mkdirs();
                        FileUtils.copyFile(pngIconFile, new File(newItemFile,pngIconFile.getName()));
                        FileUtils.copyFile(pngLayerFile, new File(newPropFile,pngLayerFile.getName()));

                        createLayerPropFile(newPropFile, armor, pngLayerFile.getName(), layer);
                        createIconPropFile(newItemFile, armor, pngIconFile.getName());
                    }
                    if(pngIconFile == null){
                        plugin.getLogger().warning("Could not find "+armor.getInternalName()+".png file in textures folder(icon file)");
                    }
                    if(pngLayerFile == null){
                        plugin.getLogger().warning("Could not fine "+armor.getOptifineFile()+".png file in textures folder(layer file)");
                    }
                }
            }
            else if(armor.getSlot() == CItemSlot.OFFHAND){
                if(item.getItemMeta().hasCustomModelData()){
                    if(item.getType() == Material.SHIELD){
                        File pngFile = null;
                        for(File file : textureFiles){
                            if(FilenameUtils.getExtension(file.getName()).equals("png")){
                                if(file.getName().replace(".png", "").equals(armor.getInternalName())){
                                    pngFile = file;
                                    break;
                                }
                            }
                        }
                        if(pngFile != null){
                            plugin.getLogger().info("Generating optifine files for "+armor.getInternalName());
                            File optifineShieldFolder = new File(resource,"optifine/cit/shields/"+armor.getInternalName());
                            optifineShieldFolder.mkdirs();
                            JsonObject shieldObj = gson.fromJson(new InputStreamReader(plugin.getResource("shields/shield.json")), JsonObject.class);
                            JsonObject shieldBlockObj = gson.fromJson(new InputStreamReader(plugin.getResource("shields/shield_block.json")), JsonObject.class);
                            FileUtils.copyFile(pngFile, new File(optifineShieldFolder,"shield.png"));
                            File newShieldFile = new File(optifineShieldFolder,"shield.json");
                            File newShieldBlockFile = new File(optifineShieldFolder,"shield_block.json");
                            newShieldFile.createNewFile();
                            newShieldBlockFile.createNewFile();
                            FileWriter writer1 = new FileWriter(newShieldFile);
                            gson.toJson(shieldObj, writer1);
                            writer1.close();
                            FileWriter writer2 = new FileWriter(newShieldBlockFile);
                            gson.toJson(shieldBlockObj, writer2);
                            writer2.close();
    
                            // creating properties file
                            File newPropFile = new File(optifineShieldFolder,"shield.properties");
                            newPropFile.createNewFile();
                            FileWriter writer = new FileWriter(newPropFile);
                            writer.write("type=item\nitems=shield\n");
                            writer.write("model=shield\nmodel.shield_blocking=shield_block\n");
                            writer.write("nbt.CustomModelData="+item.getItemMeta().getCustomModelData());
                            writer.close();
                        }
                    }
                    else{
                        generateModels(modelFolder, textureFolder, textureFiles, armor, plugin, gson);
                    }

                }
            }
                
        }
        plugin.getLogger().info("Generating pack.mcmeta");
        generateMcMeta(new File(plugin.getDataFolder(),"resource-packs/"+packname), plugin.getMinecraftVersion());
        plugin.getLogger().info(packname+" generated");    
    }

    private static void generateMcMeta(File resource,String mcVersion) throws IOException{
        
        File file = new File(resource,"pack.mcmeta");
        file.createNewFile();
        FileWriter writer = new FileWriter(file);
        String packFormat = "7";
        if(mcVersion.equals("v1_17_R1")){
            packFormat = "7";
        }
        else if(Arrays.asList("v1_16_R1","v1_16_R2","v1_16_R3").contains(mcVersion)){
            packFormat = "6";
        }
        else if(mcVersion.equals("v1_14_R1")){
            packFormat = "4";
        }
        else if(mcVersion.equals("v1_15_R1")){
            packFormat = "5";
        }
        else if(Arrays.asList("v1_13_R1","v1_13_R2").contains(mcVersion)){
            packFormat = "3";
        }
        writer.write("{\"pack\":{\"pack_format\":"+packFormat+",\"description\":\"Resource Pack For Cosmin\"}}");
        writer.close();
    }

    private static void generateModels(File modelFolder,File textureFolder,List<File> textureFiles,CosminArmor armor,Cosmin plugin,Gson gson) throws IOException{
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
            plugin.getLogger().info("Generating files for "+armor.getInternalName());
            File newItemModelFile = new File(modelFolder,jsonFile.getName());
            FileUtils.copyFile(jsonFile, newItemModelFile);


            createBaseItemFile(modelFolder,gson,armor);
            redirectTexturesPath(modelFolder, armor, gson);


            File newItemPngFile = new File(textureFolder,pngFile.getName());
            FileUtils.copyFile(pngFile, newItemPngFile);
        }
        if(pngFile == null){
            plugin.getLogger().warning("Could not find the "+armor.getInternalName()+".png file in textures folder");
        }
        if(jsonFile == null){
            plugin.getLogger().warning("Could not find the "+armor.getInternalName()+".json file in textures folder");
        }
    }
    

    private static void createLayerPropFile(File folder,CosminArmor armor,String pngFile,String layer) throws IOException{
        File file = new File(folder,armor.getInternalName()+".properties");
        file.createNewFile();
        String itemType = armor.getItem().getType().toString().toLowerCase();
        FileWriter writer = new FileWriter(file);
        writer.write("type=armor\n");
        writer.write("items="+itemType+"\n");
        writer.write("texture."+getArmorMaterial(itemType)+"_layer_"+layer+"="+pngFile+"\n");
        writer.write("nbt.CustomModelData="+armor.getItem().getItemMeta().getCustomModelData());
        writer.close();
    }

    private static void createIconPropFile(File folder,CosminArmor armor,String pngFile) throws IOException{
        File file = new File(folder,armor.getInternalName()+".properties");
        file.createNewFile();
        String itemType = armor.getItem().getType().toString().toLowerCase();
        FileWriter writer = new FileWriter(file);
        writer.write("type=item\n");
        writer.write("matchItems="+itemType+"\n");
        writer.write("texture="+pngFile+"\n");
        writer.write("nbt.CustomModelData="+armor.getItem().getItemMeta().getCustomModelData());
        writer.close();
    }

    private static void createElytraPropFile(File folder,CosminArmor armor,String pngFile) throws IOException{
        File file = new File(folder,armor.getInternalName()+".properties");
        file.createNewFile();
        FileWriter writer = new FileWriter(file);
        writer.write("type=elytra\n");
        writer.write("elytra\n");
        writer.write("texture="+pngFile+"\n");
        writer.write("nbt.CustomModelData="+armor.getItem().getItemMeta().getCustomModelData());
        writer.close();
    }

    private static String getArmorMaterial(String itemType){
        if(itemType.endsWith("_chestplate")){
            return itemType.replace("_chestplate","");
        }
        else if(itemType.endsWith("_helmet")){
            return itemType.replace("_helmet", "");
        }
        else if(itemType.endsWith("_leggings")){
            return itemType.replace("_leggings", "");
        }
        else if(itemType.endsWith("_boots")){
            return itemType.replace("_boots", "");
        }
        return null;
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
        if(armor.getConfig().contains("option.parent")){
            obj.addProperty("parent", "item/"+armor.getConfig().getString("options.parent","handheld_rod"));;
        }
        else{
            obj.addProperty("parent", "item/handheld_rod");
        }
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
        JsonArray sortedOverides = new JsonArray();
        Map<Integer,JsonElement> map = new HashMap<>();
        for(JsonElement o : overides){
            map.put(o.getAsJsonObject().get("predicate").getAsJsonObject().get("custom_model_data").getAsInt(), o);
        }
        TreeMap<Integer,JsonElement> tree = new TreeMap<>(map);
        for(JsonElement o : tree.values()){
            sortedOverides.add(o);
        }
        obj.add("overrides", sortedOverides);
        gson.toJson(obj, writer);

        writer.close();
    }
        
}
