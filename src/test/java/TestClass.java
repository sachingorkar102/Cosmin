import java.io.FileReader;
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

import org.bukkit.Color;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;


public class TestClass {
    public static void main(String[] args) {
        // List<Integer> realList = Arrays.asList(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17);
        // List<Integer> dummyList = Arrays.asList(new Integer[18]);
        // System.out.println("before modifying "+realList);
        // realList = realList.stream().filter(i -> !CosminConstants.FILLAR_SLOTS.contains(i)).collect(Collectors.toList());
        // System.out.println("after modifying "+realList);
        // for(int i=0;i<18;i++){
        //     if(CosminConstants.FILLAR_SLOTS.contains(i)){
        //         dummyList.set(i, i);
        //     }
        //     else{
        //         dummyList.set(i, realList.get(0));
        //         realList.remove(0);
        //     }
        // }
        // System.out.println(dummyList);
        System.out.println(Color.RED.asRGB());
        System.out.println(Color.RED.asBGR());
        Set<String> list = new HashSet<>();
        list.add("aa");
        String s = list.toString();
        Set<String> set = new HashSet<>(Arrays.asList(s.replace("[", "").replace("]", "").replace(" ", "").split(",")));
        System.out.println(set);
        // String my = list.toString().replace("[", "").replace("]", "");
        
        // System.out.println(Arrays.asList(my.split(",")));
    }
}

