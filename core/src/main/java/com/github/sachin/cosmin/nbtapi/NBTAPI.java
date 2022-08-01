package com.github.sachin.cosmin.nbtapi;

import com.github.sachin.cosmin.nbtapi.nms.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;

public class NBTAPI {

    private String version;
    public static NMSHelper NMSHelper;

    public boolean loadVersions(@NotNull JavaPlugin plugin,String mcVersion){
        this.version = plugin.getServer().getClass().getPackage().getName().split("\\.")[3];
        if(mcVersion.equals("1.19")){
            version = "v1_19_R1";
        }
        else if(mcVersion.equals("1.19.1")){
            version = "v1_19_R11";
        }
        try {
            //abstractNmsHandler = (AbstractNMSHandler) Class.forName(packageName + ".internal.nms." + internalsName + ".NMSHandler").newInstance();
            NMSHelper = (NMSHelper) Class.forName("com.github.sachin.cosmin.nms."+version+".NMSHandler").getDeclaredConstructor().newInstance();
            return true;
        } catch (final ClassNotFoundException | InstantiationException | IllegalAccessException | ClassCastException | NoSuchMethodException | InvocationTargetException exception) {
//            plugin.getLogger().severe("The included JeffLib version (" + version + ")does not fully support the Minecraft version you are currently running:");
            exception.printStackTrace();
            return false;
        }
//        if(version.equals("v1_16_R3")){
//            NMSHelper = new NBTItem_1_16_R3(null);
//            return true;
//        }
//        else if(version.equals("v1_17_R1")){
//            NMSHelper = new NBTItem_1_17_R1(null);
//            return true;
//        }
//        else if(version.equals("v1_18_R1")){
//            NMSHelper = new NBTItem_1_18_R1(null);
//            return true;
//        }
        
    }
    
}
