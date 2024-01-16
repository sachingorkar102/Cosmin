package com.github.sachin.cosmin.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.sachin.cosmin.Cosmin;
import com.github.sachin.cosmin.armor.CosminArmor;
import com.github.sachin.cosmin.compat.OraxenAPI;
import com.github.sachin.cosmin.gui.GuiContext;
import com.github.sachin.cosmin.compat.ItemsAddersAPI;
import com.github.sachin.cosmin.xseries.XEnchantment;
import com.github.sachin.cosmin.xseries.XMaterial;
import com.github.sachin.prilib.nms.NBTItem;
import com.google.common.base.Enums;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.block.BlockState;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;


public class ItemBuilder {

    private static final Cosmin plugin = Cosmin.getInstance();

    public ItemBuilder(){ }




    @SuppressWarnings("deprecation") // for damage methods
    public static ItemStack itemFromFile(ConfigurationSection section,String miscItemType){
        Preconditions.checkNotNull(section, "item cant be null");
        Preconditions.checkArgument(section.contains("id"), "item should atleast contain id");
        ItemStack item = XMaterial.matchXMaterial(section.getString("id")).get().parseItem();
        
        // check if section has amount
        if(section.contains("amount")){
            item.setAmount(section.getInt("amount",1));
        }

        // this should only happen if material type is air
        ItemMeta meta = item.getItemMeta();
        if(meta == null){
            return item;
        }

        // check for display name
        if(section.contains("display")){
            meta.setDisplayName(ColorUtils.applyColor(section.getString("display")));
        }

        // check for lore
        if(section.contains("lore")){
            List<String> lore = new ArrayList<>();
            section.getStringList("lore").forEach(s -> {
                lore.add(ColorUtils.applyColor(s));
            });
            meta.setLore(lore);
        }

        // check for damage, can be usefull versions below 1.14 for resource pack
        if(section.contains("damage")){
            if (XMaterial.isNewVersion()) {
                if (meta instanceof Damageable) {
                    int damage = section.getInt("damage");
                    if (damage > 0) ((Damageable) meta).setDamage(damage);
                }
            } else {
                int damage = section.getInt("damage");
                if (damage > 0) item.setDurability((short) damage);
            }
        }

        if (section.contains("enchants")) {
            ConfigurationSection enchants = section.getConfigurationSection("enchants");
            for (String string : enchants.getKeys(false)) {
                Optional<XEnchantment> enchant = XEnchantment.matchXEnchantment(string);
                enchant.ifPresent(xEnchantment -> meta.addEnchant(xEnchantment.parseEnchantment(), enchants.getInt(string), true));
            }
        }

        if(section.contains("flags")){
            List<String> itemFlags = section.getStringList("flags").stream().map(m -> m.toUpperCase()).collect(Collectors.toList());
            for(String str : itemFlags){
                if(str.equals("ALL")){
                    meta.addItemFlags(ItemFlag.values());
                    break;
                }
                ItemFlag itemFlag = Enums.getIfPresent(ItemFlag.class, str).orNull();
                if(itemFlag != null){
                    meta.addItemFlags(itemFlag);
                }
            }
        }
        if((meta instanceof FireworkEffectMeta) && section.contains("firework")) {
            FireworkEffectMeta firework = (FireworkEffectMeta) meta;
            ConfigurationSection fireworkConfig = section.getConfigurationSection("firework");
            if(fireworkConfig != null){

                FireworkEffect.Builder builder = FireworkEffect.builder();
                builder.with(Type.STAR);
                List<String> strColors = fireworkConfig.getStringList("colors");
                List<Color> colors = new ArrayList<>(strColors.size());
                for (String str: strColors) {
                    colors.add(parseColor(str));
                }
                builder.withColor(colors);
                firework.setEffect(builder.build());
            }
        }
          

        // check for extra options on item
        if(section.contains("options")){
            ConfigurationSection options = section.getConfigurationSection("options");
            if(options.getBoolean("enchanted",false)){
                meta.addEnchant(Enchantment.MENDING, 1, false);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            if(options.contains("patterns")){
                if(item.getType() == Material.SHIELD){
                    BlockStateMeta blockStateMeta = (BlockStateMeta) meta;
                    BlockState state = blockStateMeta.getBlockState();
                    Banner bannerState = (Banner) state;
                    bannerState.setPatterns(getBannerPatterns(options.getConfigurationSection("patterns")));
                    bannerState.update();
                    blockStateMeta.setBlockState(bannerState);
                }
                else if(meta instanceof BannerMeta){
                    BannerMeta banner = (BannerMeta) meta;
                    banner.setPatterns(getBannerPatterns(options.getConfigurationSection("patterns")));
                }
            }
            else if(meta instanceof LeatherArmorMeta){
                LeatherArmorMeta leather = (LeatherArmorMeta) meta;
                String colorStr = options.getString("color");
                if (colorStr != null) {
                    leather.setColor(parseColor(colorStr));
                }
            }
            else if ((meta instanceof PotionMeta) && options.contains("color")){
                PotionMeta potion = (PotionMeta) meta;
                potion.setColor(Color.fromRGB(options.getInt("color",0)));
            }
            else if((meta instanceof SkullMeta) && options.contains("texture")){
                SkullMeta skullMeta = (SkullMeta) meta;
//                mutateItemMeta(skullMeta, options.getString("texture"));
            }
            if(options.contains("model")){
                try {
                    meta.setCustomModelData(options.getInt("model",0));
                } catch (NoSuchMethodError e) {
                    
                }
            }
        }
           
        if(miscItemType != null){
            item.setItemMeta(meta);
            NBTItem nbti = plugin.getNBTItem(item);
            nbti.setString("cosmin:misc-item", miscItemType);
            if(miscItemType.equals(CosminConstants.ENABLE_ITEM) || miscItemType.equals(CosminConstants.DISABLE_ITEM)){
                nbti.setBoolean("show-tool-tip", section.getBoolean("show-tool-tip",false));
                nbti.setString("display-name", meta.getDisplayName());
            }
            item = nbti.getItem();
        }
        else{
            item.setItemMeta(meta);
        }

        return item;
    }


    public static CosminArmor cosminArmorFromFile(ConfigurationSection section,String miscItemType,String armorName){
        ItemStack armorItem = null;
        if(section.contains("item")){
            armorItem = section.getItemStack("item");
        }
        else if(section.contains("ItemsAdder") && ItemsAddersAPI.isEnabled && ItemsAddersAPI.isInRegistry(section.getString("ItemsAdder"))){
            armorItem = ItemsAddersAPI.getItem(section.getString("ItemsAdder"));
        }
        else if(section.contains("Oraxen") && OraxenAPI.isEnabled && OraxenAPI.isItem(section.getString("Oraxen"))){
            armorItem = OraxenAPI.getItem(section.getString("Oraxen"));
        }
        else{
            armorItem = itemFromFile(section, null);
        }
        armorItem = setHatItem(armorItem,armorName);
        String perm = "none";
        if(section.contains("permission")){
            perm = section.getString("permission");
        }
        
        CosminArmor armor = new CosminArmor(armorItem,armorName,perm);
        CItemSlot slot = Enums.getIfPresent(CItemSlot.class, section.getString("type","HEAD")).or(CItemSlot.HEAD);
        armor.setSlot(slot);
        if(!section.contains("type") && armorItem != null){
            if(armorItem.getType().toString().endsWith("CHESTPLATE")){
                armor.setSlot(CItemSlot.CHEST);
            }
            else if(armorItem.getType().toString().endsWith("LEGGINGS")){
                armor.setSlot(CItemSlot.LEGS);
            }
            else if(armorItem.getType().toString().endsWith("BOOTS")){
                armor.setSlot(CItemSlot.FEET);
            }
        }
        GuiContext context = slot.getContext();
        armor.setCost(section.getInt("cost",0));
        armor.setPlayerPoints(section.getInt("points",0));
        armor.setHide(section.getBoolean("hide",false));
        armor.setContext(context);
        armor.setConfig(section);
        if(section.contains("options.optifine")){
            armor.setOptifineFile(section.getString("options.optifine"));
        }
        return armor;
    }


    public static boolean isEnableItem(ItemStack item){
        if(item == null || item.getType() == Material.AIR) return false;
        NBTItem nbti = plugin.getNBTItem(item);
        return nbti.getString("cosmin:misc-item").equals(CosminConstants.ENABLE_ITEM);
    }

    public static boolean isForcedItem(ItemStack item){
        if(item == null || item.getType() == Material.AIR) return false;
        NBTItem nbti = plugin.getNBTItem(item);
        return nbti.getString("cosmin:forced-item").equals("forcedItem");
    }

    public static boolean isCrossMatchAllowed(ItemStack item){
        if(item == null) return true;
        NBTItem nbti = plugin.getNBTItem(item);
        return nbti.hasKey("allow-cross-match") ? nbti.getBoolean("allow-cross-match") : true;
    }

    public static ItemStack setCrossMatchAllowed(ItemStack item,boolean value){
        if(item == null) return item;
        NBTItem nbti = plugin.getNBTItem(item);
        nbti.setBoolean("allow-cross-match", value);
        return nbti.getItem();
    }

    public static ItemStack setForcedItem(ItemStack item,boolean remove){
        if(item == null || item.getType() == Material.AIR) return item;
        NBTItem nbti = plugin.getNBTItem(item);
        if(remove){
            nbti.removeKey("cosmin:forced-item");
        }
        else{
            nbti.setString("cosmin:forced-item", "forcedItem");
        }
        return nbti.getItem();
    }

    public static boolean isCosmeticSetIcon(ItemStack item){
        if(item == null || item.getType() == Material.AIR) return false;
        NBTItem nbti = plugin.getNBTItem(item);
        return nbti.hasKey("cosmin:cosmetic-set");
    }

    public static boolean isCosmeticSetArmor(ItemStack item){
        if(item == null || item.getType() == Material.AIR) return false;
        NBTItem nbti = plugin.getNBTItem(item);
        return nbti.hasKey("cosmin:cosmetic-set-armor");
    }

    public static String getCosmeticSetArmorName(ItemStack item){
        if(item == null || item.getType() == Material.AIR) return null;
        NBTItem nbtItem = plugin.getNBTItem(item);
        return nbtItem.getString("cosmin:cosmetic-set-armor");
    }

    public static ItemStack setCosmeticSetArmorName(ItemStack item,String name){
        if(item == null || item.getType() == Material.AIR) return item;
        NBTItem nbti = plugin.getNBTItem(item);
        nbti.setString("cosmin:cosmetic-set-armor", name);
        return nbti.getItem();
    }

    public static boolean showToolTip(ItemStack item){
        if(item == null || item.getType() == Material.AIR) return false;
        NBTItem nbtItem = plugin.getNBTItem(item);
        return nbtItem.getBoolean("show-tool-tip");
    }

    public static String getDisplayName(ItemStack item){
        if(item == null || item.getType() == Material.AIR) return null;
        NBTItem nbtItem = plugin.getNBTItem(item);
        return nbtItem.getString("display-name");
    }

    public static ItemStack setCosmeticSetIconValue(ItemStack item,String value){
        if(item == null || item.getType() == Material.AIR) return item;
        NBTItem nbti = plugin.getNBTItem(item);
        nbti.setString("cosmin:cosmetic-set", value);
        return nbti.getItem();
    }

    public static String getCosmeticSetIconValue(ItemStack item) {
        if(item == null || item.getType() == Material.AIR) return " ";
        NBTItem nbti = plugin.getNBTItem(item);
        return nbti.getString("cosmin:cosmetic-set");
    }

    public static boolean isHatItem(ItemStack item){
        if(item == null || item.getType() == Material.AIR) return false;
        NBTItem nbti = plugin.getNBTItem(item);
        return nbti.hasKey("cosmin-armor");
    }

    public static ItemStack setHatItem(ItemStack item,String name){
        if(item == null || item.getType() == Material.AIR) return null;
        NBTItem nbti = plugin.getNBTItem(item);
        nbti.setString("cosmin-armor",name);
        return nbti.getItem();
    }

    public static String getArmorName(ItemStack item){
        if(item == null || item.getType() == Material.AIR) return null;
        NBTItem nbti = plugin.getNBTItem(item);
        return nbti.getString("cosmin-armor");
    }

    public static ItemStack removeHatNBT(ItemStack item){
        if(item == null || item.getType() == Material.AIR) return null;
        NBTItem nbti = plugin.getNBTItem(item);
        if(nbti.hasKey("isHat")){
            nbti.removeKey("isHat");
            return nbti.getItem();
        }
        else{
            return item;
        }
        
    }

    public static ItemStack updateItemLore(ItemStack i, int cost,int points){
        // ItemStack i = item;
        Cosmin plugin = Cosmin.getInstance();
        ItemMeta meta = i.getItemMeta();
        List<String> lore = new ArrayList<>();
        for(String l : plugin.getConfig().getStringList(CosminConstants.SHOP_ITEM_LORE)){
            if(l.equals("%lore%")){
                if(meta.hasLore()){
                    lore.addAll(meta.getLore());
                }
                continue;
            }
            else{
                String s = l.replace("%cost%", String.valueOf(cost)).replace("%points%", String.valueOf(points));
                
                lore.add(ColorUtils.applyColor(s));
            }
        }
        meta.setLore(lore);
        i.setItemMeta(meta);
        return i;
    }

    private static Color parseColor(String str) {
        if (Strings.isNullOrEmpty(str)) return Color.BLACK;
        String[] rgb = StringUtils.split(StringUtils.deleteWhitespace(str), ',');
        if (rgb.length < 3){
            return getColorFromString(str);
        }
        return Color.fromRGB(NumberUtils.toInt(rgb[0], 0), NumberUtils.toInt(rgb[1], 0), NumberUtils.toInt(rgb[2], 0));
    }

    private static Color getColorFromString(String str){
        switch (str) {
            case "WHITE":
                return Color.WHITE;
            case "RED":
                return Color.RED;
            case "BLUE":
                return Color.BLUE;
            case "GREEN":
                return Color.GREEN;
            case "AQUA":
                return Color.AQUA;
            case "BLACK":
                return Color.BLACK;
            case "SILVER":
                return Color.SILVER;
            case "MAROON":
                return Color.MAROON;
            case "YELLOW":
                return Color.YELLOW;
            case "OLIVE":
                return Color.OLIVE;
            case "ORANGE":
                return Color.ORANGE;
            case "PURPLE":
                return Color.PURPLE;
            case "TEAL":
                return Color.TEAL;                                          
            default:
                return Color.WHITE;
        }
    }

    private static List<Pattern> getBannerPatterns(ConfigurationSection patterns){
        List<Pattern> list = new ArrayList<>();
        if (patterns != null) {
            for (String pattern : patterns.getKeys(false)) {
                PatternType type = PatternType.getByIdentifier(pattern);
                if (type == null) type = Enums.getIfPresent(PatternType.class, pattern.toUpperCase(Locale.ENGLISH)).or(PatternType.BASE);
                DyeColor color = Enums.getIfPresent(DyeColor.class, patterns.getString(pattern).toUpperCase(Locale.ENGLISH)).or(DyeColor.WHITE);
                list.add(new Pattern(color, type));
                
            }
        }
        return list;
    }

    
    
}
