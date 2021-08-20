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
import java.util.stream.Collectors;

import com.github.sachin.cosmin.utils.CosminConstants;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.apache.commons.io.FileUtils;
import org.bukkit.Color;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;


public class TestClass {
    public static void main(String[] args) {
        System.out.println(3*100/4);
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

