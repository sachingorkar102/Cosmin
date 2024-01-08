package com.github.sachin.cosmin.player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;
import com.comphenix.protocol.wrappers.MinecraftKey;
import com.comphenix.protocol.wrappers.Pair;
import com.github.sachin.cosmin.Cosmin;
import com.github.sachin.cosmin.armor.CosmeticSet;
import com.github.sachin.cosmin.armor.CosminArmor;
import com.github.sachin.cosmin.compat.CosmeticCoreAPI;
import com.github.sachin.cosmin.utils.CItemSlot;
import com.github.sachin.cosmin.utils.CosminConstants;
import com.github.sachin.cosmin.utils.ItemBuilder;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class CosminPlayer {

    private UUID uuid;

    private List<ItemStack> cosminInvContents = new ArrayList<>();
    private Map<CItemSlot,ItemStack> equipmentMap = new HashMap<>();

    public int swiftSneakLevel = 0;

    private Map<CItemSlot,Boolean> orignalArmorMap = new HashMap<>();
    private Set<String> purchasedItems = new HashSet<>();
    private Set<String> purchasedSets = new HashSet<>();
    private Cosmin plugin;

    private boolean hasInventoryOpen=false;
    private boolean preventPacketSending=false;
    private boolean preventEntityEquipPacket=false;


    // public CosminPlayer(Player player,List<ItemStack> cosminInvContents){
    //     this.cosminInvContents = cosminInvContents;
    //     this.player = player;
    //     this.uuid = player.getUniqueId();
    //     this.hasInventoryOpen = false;
    //     // computeAndPutEquipmentPairList();
    // }

    public CosminPlayer(UUID uuid,List<ItemStack> cosminInvContents){
        this.cosminInvContents = cosminInvContents;
        this.uuid = uuid;
        this.plugin = Cosmin.getInstance();
        for(CItemSlot slot : CItemSlot.values()){
            orignalArmorMap.put(slot, false);
        }
        // computeAndPutEquipmentPairList();
    }



    public List<ItemStack> getCosminInvContents() {
        return cosminInvContents;
    }
    

    public Map<CItemSlot,ItemStack> getEquipmentMap() {
        return equipmentMap;
    }

    public Set<String> getPurchasedItems() {
        return purchasedItems;
    }
    public Set<String> getPurchasedSets() {
        return purchasedSets;
    }
    public void addPurchasedItem(String name){
        this.purchasedItems.add(name);
    }
    public void addPurchasedSet(String name){
        this.purchasedSets.add(name);
    }

    public void setPurchasedItems(Set<String> purchasedItems) {
        this.purchasedItems = purchasedItems;
    }

    public void setPurchasedSets(Set<String> purchasedSets) {
        this.purchasedSets = purchasedSets;
    }


    public void setInventoryOpen(boolean hasInventoryOpen) {
        this.hasInventoryOpen = hasInventoryOpen;
    }

    public boolean getInventoryOpen(){
        return this.hasInventoryOpen;
    }

    public void preventSetSlotPacket(boolean value) {
        this.preventPacketSending = value;
    }

    public boolean preventSetSlotPacket(){
        return this.preventPacketSending;
    }

    public void preventEntityEquipPacket(boolean value) {
        this.preventEntityEquipPacket = value;
    }

    public boolean preventEntityEquipPacket(){
        return this.preventEntityEquipPacket;
    }

    public void removeItem(int index){
        this.cosminInvContents.set(index, null);
    }

    public UUID getUuid() {
        return uuid;
    }
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }



    public Optional<Player> getBukkitPlayer() {
        return Optional.ofNullable(Bukkit.getPlayer(uuid));
    }

    public OfflinePlayer getBukkitPlayerOffline() {
        return Bukkit.getOfflinePlayer(uuid);
    }


    public void setCosminInvContents(List<ItemStack> cosminInvContents) {
        this.cosminInvContents = cosminInvContents;
    }

    public void clearCosminInv(){
        Cosmin plugin = Cosmin.getInstance();
        for(int i=11;i<16;i++){
            cosminInvContents.set(i, new ItemStack(Material.AIR));
            cosminInvContents.set(i-9, plugin.miscItems.getDisableItem());
        }
    }

    public boolean isEnabled(CItemSlot slot){
        return ItemBuilder.isEnableItem(cosminInvContents.get(slot.getToggleSlotId()));
    }

    public ItemStack getSlotItem(CItemSlot slot){
        ItemStack item = equipmentMap.get(slot);
        if(item == null){
            return new ItemStack(Material.AIR);
        }
       return item;
    }

    public ItemStack getOrignalItem(CItemSlot slot){
        ItemStack item = getBukkitPlayer().get().getInventory().getItem(slot.getAltSlotId());
        if(item == null){
            return new ItemStack(Material.AIR);
        }
        return item;
    }

    public void setSlotItem(CItemSlot slot,ItemStack item){
        equipmentMap.put(slot, item);
    }

    public Map<CItemSlot, Boolean> getOrignalArmorMap() {
        return orignalArmorMap;
    }

    

    public void computeAndPutEquipmentPairList(){
        if(!getBukkitPlayer().isPresent()) return;
        Player player = getBukkitPlayer().get();
        
        if(!player.isOnline()) return;
        // clearNonExsistantArmorItems();
        Map<CItemSlot,ItemStack> pairs = new HashMap<>();
        PlayerInventory inv = player.getInventory();
        swiftSneakLevel = 0;
        for(int i =2;i<7;i++){
            ItemStack orignalArmor = null;
            // 2  3  4  5  6
            // 11 12 13 14 15
            // 39 38 37 36 45
            int slotId = i+9;
            ItemStack toggleItem = cosminInvContents.get(i);
            ItemStack armor = cosminInvContents.get(slotId);
            
            CItemSlot slot = null;
            boolean isValidArmor = true;
            String armorName = armor == null ? "AIR" : armor.getType().toString();
            
            switch(slotId){
                case 11:
                    slot = CItemSlot.HEAD;
                    if(inv.getHelmet() != null){
                        orignalArmor = inv.getHelmet().clone();
                    }
                    break;
                case 12:
                    slot = CItemSlot.CHEST;
                    if(inv.getChestplate() != null){
                        orignalArmor = inv.getChestplate().clone();
                    }
                    if(!armorName.endsWith("CHESTPLATE") && !armorName.equals("AIR") && !armorName.endsWith("ELYTRA")){
                        isValidArmor = false;
                    }
                    break;
                case 13:
                    slot = CItemSlot.LEGS;
                    if(inv.getLeggings() != null){
                        orignalArmor = inv.getLeggings().clone();
                        if(plugin.isPost1_19() && orignalArmor.getEnchantmentLevel(Enchantment.SWIFT_SNEAK) > 0){
                            swiftSneakLevel = orignalArmor.getEnchantmentLevel(Enchantment.SWIFT_SNEAK);
                        }
                    }
                    if(!armorName.endsWith("LEGGINGS") && !armorName.equals("AIR")){
                        isValidArmor = false;
                    }
                    break;
                case 14:
                    slot = CItemSlot.FEET;
                    if(inv.getBoots() != null){
                        orignalArmor = inv.getBoots().clone();

                    }
                    if(!armorName.endsWith("BOOTS") && !armorName.equals("AIR")){
                        isValidArmor = false;
                    }
                    break;
                case 15:
                    slot = CItemSlot.OFFHAND;
                    if(inv.getItemInOffHand() != null){
                        orignalArmor = inv.getItemInOffHand().clone();
                    }
                    break;             
            }
            if(armor == null){
                armor = new ItemStack(Material.AIR);
            }
            if(armor.getType() == Material.AIR && (!hasAirEquipPerms(player,slot) || !plugin.getConfig().getBoolean("allow-empty-slots."+slot.toString(),true))){
                pairs.put(slot, orignalArmor);
                orignalArmorMap.put(slot, true);
            }
            else if(!player.hasPermission("cosmin.equip.nonmodelitems") && !hasCustomModel(armor) && armor.getType() != Material.AIR){
                pairs.put(slot, orignalArmor);
                orignalArmorMap.put(slot, true);
            }
            else if(ItemBuilder.isEnableItem(toggleItem) && isValidArmor){
                pairs.put(slot, armor);
                orignalArmorMap.put(slot, false);
            }
            else{
                pairs.put(slot, orignalArmor);
                orignalArmorMap.put(slot, true);
            }
        }
        this.equipmentMap = pairs;
        
    }

    private boolean hasCustomModel(ItemStack item){
        if(!item.hasItemMeta()) return false;
        return item.getItemMeta().hasCustomModelData();
    }

    private boolean hasAirEquipPerms(Player player,CItemSlot slot){
        return player.hasPermission("cosmin.hidearmor"+slot.toString().toLowerCase());
    }

    // this is used when player closes cosmin inventory and CRAFTING inventory
    // for other players on server
    public void fakeEquip(Player targetPlayer){
        if(!getBukkitPlayer().isPresent()) return;
        Player player = getBukkitPlayer().get();
        if(!player.isOnline()) return;
        if(equipmentMap.isEmpty()) computeAndPutEquipmentPairList();
        
        

        if(plugin.postNetherUpdate){
            List<Pair<ItemSlot,ItemStack>> pairs = new ArrayList<>();
            PacketContainer packet = plugin.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);
            packet.getIntegers().write(0, player.getEntityId());
            for(CItemSlot slot:equipmentMap.keySet()){
                if(CosmeticCoreAPI.isEnabled && slot==CItemSlot.HEAD){
                    ItemStack ccHat = CosmeticCoreAPI.getHatItem(player);
                    if(ccHat != null){
                        pairs.add(new Pair<>(slot.getProtocolSlot(),ccHat));
                        continue;
                    }
                }
                if(getOrignalArmorMap().get(slot)){
                    pairs.add(new Pair<>(slot.getProtocolSlot(),player.getInventory().getItem(slot.getAltSlotId())));
                }
                else{
                    pairs.add(new Pair<>(slot.getProtocolSlot(),getEquipmentMap().get(slot)));
                }
            }
            pairs.add(new Pair<ItemSlot,ItemStack>(ItemSlot.MAINHAND,player.getInventory().getItemInMainHand()));
            packet.getSlotStackPairLists().write(0, pairs);
            
            sendPacket(packet, targetPlayer);
            return;
        }
        for (CItemSlot slot : equipmentMap.keySet()) {
            ItemStack item = equipmentMap.get(slot);
            PacketContainer packet = plugin.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);
            packet.getIntegers().write(0, player.getEntityId());
            packet.getItemSlots().write(0, slot.getProtocolSlot());
            packet.getItemModifier().write(0, item);
            if(!getOrignalArmorMap().get(slot)){
                sendPacket(packet, targetPlayer);
            }
        }

    }
    private void sendPacket(PacketContainer packet,Player targetPlayer){
        preventEntityEquipPacket(true);
        if(targetPlayer.hasPermission(CosminConstants.PERM_REALARMOR)){
            if(!getBukkitPlayer().isPresent()) return;
            Player player = getBukkitPlayer().get();
            if(plugin.postNetherUpdate){
                List<Pair<ItemSlot,ItemStack>> pairs = new ArrayList<>();
                for(CItemSlot slot:equipmentMap.keySet()){
                    pairs.add(new Pair<ItemSlot,ItemStack>(slot.getProtocolSlot(),player.getInventory().getItem(slot.getAltSlotId())));
                }
                pairs.add(new Pair<ItemSlot,ItemStack>(ItemSlot.MAINHAND,player.getInventory().getItemInMainHand()));
                packet.getSlotStackPairLists().write(0, pairs);
            }
            else{
                CItemSlot slot = CItemSlot.valueOf(packet.getItemSlots().read(0).toString());
                packet.getItemModifier().write(0, player.getInventory().getItem(slot.getAltSlotId()));
            }
        }
        try {
            plugin.getProtocolManager().sendServerPacket(targetPlayer, packet);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public void sendPacketWithinRange(int radius,Player originPlayer){
        for (Player player : Bukkit.getOnlinePlayers()) {
            if(!player.getWorld().getName().equalsIgnoreCase(originPlayer.getWorld().getName()) || player.getUniqueId() == originPlayer.getUniqueId()) continue;
            if(originPlayer.getLocation().distanceSquared(player.getLocation()) <= radius*radius){
                fakeEquip(player);
            }
        }
    }

    public void sendPacketWithinRange(int radius){
        if(!getBukkitPlayer().isPresent()) return;
        Player player = getBukkitPlayer().get();
        sendPacketWithinRange(radius, player);
    }

    // for target player itself
    public void setFakeSlotItems(){
        if(!getBukkitPlayer().isPresent()) return;
        Player player = getBukkitPlayer().get();
        if(!player.isOnline()) return;
        if(player.getGameMode() == GameMode.CREATIVE) return;
        if(equipmentMap.isEmpty()) computeAndPutEquipmentPairList();
        for (CItemSlot slot : equipmentMap.keySet()) {
            ItemStack item = getSlotItem(slot);
            PacketContainer packet = new PacketContainer(PacketType.Play.Server.SET_SLOT);
            preventSetSlotPacket(true);
            packet.getIntegers().write(0, 0);
            packet.getIntegers().write(getPacketInt(), slot.getEquipmentSlotId());
            packet.getItemModifier().write(0, item);

            try {
                plugin.getProtocolManager().sendServerPacket(player, packet);
                plugin.getProtocolManager().sendServerPacket(player,stopSound(item));
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    public PacketContainer stopSound(ItemStack armor){
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.STOP_SOUND);
        packet.getSoundCategories().writeSafely(0,EnumWrappers.SoundCategory.PLAYERS);
        String armorType = armor.getType().toString();

        String key;
        if(armorType.contains("DIAMOND")) key = "item.armor.equip_diamond";
        else if(armorType.contains("GOLDEN")) key = "item.armor.equip_gold";
        else if(armorType.contains("IRON")) key = "item.armor.equip_iron";
        else if(armorType.contains("LEATHER")) key = "item.armor.equip_leather";
        else if(armorType.contains("NETHERITE")) key = "item.armor.equip_netherite";
        else if(armorType.contains("CHAINMAIL")) key = "item.armor.equip_chain";
        else key = "item.armor.equip_generic";

        packet.getMinecraftKeys().writeSafely(0,new MinecraftKey(key));
        return packet;
    }

    public void setFakeItem(CItemSlot slot,ItemStack item){
        if(!getBukkitPlayer().isPresent()) return;
        Player player = getBukkitPlayer().get();
        if(!player.isOnline()) return;
        if(player.getGameMode() == GameMode.CREATIVE) return;
        if(equipmentMap.isEmpty()) computeAndPutEquipmentPairList();
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.SET_SLOT);
        preventSetSlotPacket(true);
        packet.getIntegers().write(0, 0);
        packet.getIntegers().write(getPacketInt(), slot.getEquipmentSlotId());
        packet.getItemModifier().write(0, item);
        try {
            plugin.getProtocolManager().sendServerPacket(player, packet);
            plugin.getProtocolManager().sendServerPacket(player,stopSound(item));
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public int getPacketInt(){
        return plugin.is1_17_1() ? 2 : 1;
    }

    public void equipOrignalArmor(){
        if(!getBukkitPlayer().isPresent()) return;
        Player player = getBukkitPlayer().get();
        if(!player.isOnline()) return;
        for (CItemSlot slot : CItemSlot.values()) {
            ItemStack item = getOrignalItem(slot);
            PacketContainer packet = new PacketContainer(PacketType.Play.Server.SET_SLOT);
            preventSetSlotPacket(true);
            packet.getIntegers().write(0, 0);
            packet.getIntegers().write(getPacketInt(), slot.getEquipmentSlotId());
            packet.getItemModifier().write(0, item);
            try {
                plugin.getProtocolManager().sendServerPacket(player, packet);
                plugin.getProtocolManager().sendServerPacket(player,stopSound(item));
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    public void clearNonExsistantArmorItems(){
        for(int i = 11; i<16; i++){
            ItemStack item = cosminInvContents.get(i);
            if(!ItemBuilder.isHatItem(item) || ItemBuilder.isForcedItem(item)) continue;
            CosminArmor armor = plugin.getArmorManager().getArmor(ItemBuilder.getArmorName(item));
            if(armor == null) {
                cosminInvContents.set(i, null);
            }
            else if(plugin.getConfig().getBoolean(CosminConstants.ENABLE_COSMETIC_SET) &&ItemBuilder.isCosmeticSetArmor(item)){
                CosmeticSet set = plugin.getArmorManager().getSet(ItemBuilder.getCosmeticSetArmorName(item));
                if( set != null && set.getCost() != 0 && !getPurchasedSets().contains(set.getInternalName())){
                    cosminInvContents.set(i, null);

                }
            }
            else if(armor.getCost() != 0 && !getPurchasedItems().contains(armor.getInternalName()) && !armor.hide()){
                cosminInvContents.set(i, null);
            }
        }
    }
  

    public void updateArmorSets(String setName,boolean forceEquip){
        if(!plugin.getArmorManager().getCosmeticSets().containsKey(setName)) return;
        CosmeticSet set = plugin.getArmorManager().getCosmeticSets().get(setName);
        for (CItemSlot slot : set.getArmorSet().keySet()) {
            ItemStack replaceItem = cosminInvContents.get(slot.getFakeSlotId());
            if(ItemBuilder.isForcedItem(replaceItem) && !forceEquip) continue;

            ItemStack item = set.getArmor(slot).getItem();
            item = ItemBuilder.setCrossMatchAllowed(item,set.allowCrossMatch());
            if(forceEquip){
                cosminInvContents.set(slot.getToggleSlotId(), plugin.miscItems.getEnableItem());
                item = ItemBuilder.setForcedItem(item, false);
            }
            if(ItemBuilder.isHatItem(replaceItem) || replaceItem == null){
            
               cosminInvContents.set(slot.getFakeSlotId(), item); 
            }
        }
    }

    
}
