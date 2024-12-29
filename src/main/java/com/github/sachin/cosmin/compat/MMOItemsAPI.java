package com.github.sachin.cosmin.compat;

import com.github.sachin.cosmin.Cosmin;
import com.github.sachin.prilib.utils.FastItemStack;
import io.lumine.mythic.lib.api.item.NBTItem;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.Indyuce.mmoitems.manager.TierManager;
import net.Indyuce.mmoitems.manager.TypeManager;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class MMOItemsAPI {

    public static final NamespacedKey TYPE = Cosmin.getKey("mmoitem-type");
    public static final NamespacedKey ID = Cosmin.getKey("mmoitem-id");
    public static final NamespacedKey LEVEL = Cosmin.getKey("mmoitem-level");
    public static final NamespacedKey TIER = Cosmin.getKey("mmoitem-tier");


    public static boolean isMMOItem(ItemStack item){
        return NBTItem.get(item).hasType();
    }
// type, id, level, tier
    public static ItemStack setMMOItemInfo(ItemStack item){
        FastItemStack fItem = new FastItemStack(item);
        NBTItem nbtItem = NBTItem.get(item);
        fItem.set(TYPE, PersistentDataType.STRING,nbtItem.getType());
        fItem.set(ID,PersistentDataType.STRING,nbtItem.getString("MMOITEMS_ITEM_ID"));
        fItem.set(LEVEL,PersistentDataType.INTEGER,nbtItem.getInteger("MMOITEMS_REQUIRED_LEVEL"));
        fItem.set(TIER,PersistentDataType.STRING,nbtItem.getString("MMOITEMS_TIER"));
        return fItem.get();
    }

    public static ItemStack getMMOItem(ItemStack item){
        FastItemStack fItem = new FastItemStack(item);
        String type,id,tier = null;
        int level = -1;
        type = fItem.get(TYPE,PersistentDataType.STRING);
        id = fItem.get(ID,PersistentDataType.STRING);
        tier = fItem.get(TIER,PersistentDataType.STRING);
        level = fItem.hasKey(LEVEL,PersistentDataType.INTEGER) ? fItem.get(LEVEL,PersistentDataType.INTEGER) : 0;
        if(level==-1) level = 0;

        if(type != null && id != null && tier != null){
            TierManager tiers = MMOItems.plugin.getTiers();
            boolean tierExists = tiers.has(tier);
            TypeManager types = MMOItems.plugin.getTypes();
            boolean typeExists = types.has(type);
            if(tierExists && typeExists){

                ItemStack MMOItem = MMOItems.plugin.getItem(types.get(type),id,level,tiers.get(tier));
                MMOItem.setAmount(item.getAmount());
                return MMOItem;
            }
        }
        Cosmin.getInstance().getLogger().info("Could not find MMOItem with ID: "+id+" and TYPE: "+type);
        return item;
    }
}
