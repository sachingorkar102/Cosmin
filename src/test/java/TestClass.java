import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import com.github.sachin.cosmin.utils.ColorUtils;
import com.github.sachin.cosmin.utils.CosminConstants;
import com.github.sachin.cosmin.utils.HexColor;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.apache.commons.io.FileUtils;
import org.bukkit.Color;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.ChatColor;


public class TestClass {
    public static void main(String[] args) {
        String str = "{#ff9a9e}Config files reloaded successfully{#/fecfef}";
        String str1 = "Hthis is a test..... {2F329F}Hello this {2F329F}is {2F329F}text";
        // String str2 = "Hthis is a test..... {2F329F}Hello this {2F329F}is text";
        System.out.println(ColorUtils.applyColor(str));
        System.out.println(ColorUtils.applyColor(str1));
        
    }

    public static void generatePack() throws IOException{
        File resource = new File("resource-pack/assets");
        File textures = new File("Textures");
        if(!textures.exists()){
            System.out.println("could not find Textures folder to take models from..");
            textures.mkdir();
            return;
        }
        if(resource.mkdirs()){
            System.out.println("created parent directory");
        }
        FileUtils.copyFile(new File(textures,"cherry-helmet.json"), new File(resource,"cherry-helmet.json"));


    }
}

