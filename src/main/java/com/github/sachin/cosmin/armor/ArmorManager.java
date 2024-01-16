package com.github.sachin.cosmin.armor;

import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Manages the instance of CosminArmor from its internal names
 */
public class ArmorManager {

    private final Map<String,CosminArmor> cosminArmors = new HashMap<>();
    private final Map<String,CosmeticSet> cosmeticSets = new HashMap<>();
    


    public void addArmor(@Nonnull CosminArmor armor){
        cosminArmors.put(armor.getInternalName(), armor);
    }

    public void removeArmor(@Nonnull CosminArmor armor){
        cosminArmors.remove(armor.getInternalName());
    }

    public CosminArmor getArmor(String internalName){
       return cosminArmors.get(internalName);
    }

    public boolean containsArmor(String name){
        return cosminArmors.keySet().contains(name);
    }

    public void clearArmorMap(){
        cosminArmors.clear();
        cosmeticSets.clear();
    }

    public Collection<CosminArmor> getAllArmor(){
        return Collections.unmodifiableCollection(cosminArmors.values());
    }
    public Collection<String> getInternalNames(){
        return Collections.unmodifiableCollection(cosminArmors.keySet());
    }

    public Collection<ItemStack> getArmorItems(){
        return Collections.unmodifiableCollection(cosminArmors.values().stream().map(a -> a.getItem()).collect(Collectors.toList()));
    }

    public Map<String, CosmeticSet> getCosmeticSets() {
        return cosmeticSets;
    }
    public void addCosmeticSet(CosmeticSet set){
        this.cosmeticSets.put(set.getInternalName(), set);
    }

    public CosmeticSet getSet(String name){
        return this.cosmeticSets.get(name);
    }

    public boolean containsSet(String name){
        return cosmeticSets.keySet().contains(name);
    }
}
