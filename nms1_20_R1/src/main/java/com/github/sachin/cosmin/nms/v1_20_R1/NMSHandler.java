package com.github.sachin.cosmin.nms.v1_20_R1;

import com.github.sachin.cosmin.nbtapi.nms.NMSHelper;
import net.minecraft.nbt.CompoundTag;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class NMSHandler extends NMSHelper {


    private net.minecraft.world.item.ItemStack nmsItem;
    private CompoundTag compound;

    public NMSHandler(){

    }

    public NMSHandler(ItemStack item){
        if(item == null) return;
        ItemStack bukkitItem = item.clone();
        this.nmsItem = CraftItemStack.asNMSCopy(bukkitItem);
        this.compound = (nmsItem.hasTag()) ? nmsItem.getTag() : new CompoundTag();
    }


    @Override
    public NMSHelper newItem(ItemStack item) {
        return new NMSHandler(item);
    }

    @Override
    public void setString(String key, String value) {
        compound.putString(key,value);
    }

    @Override
    public void setBoolean(String key, boolean value) {
        compound.putBoolean(key,value);
    }

    @Override
    public void setInt(String key, int value) {
        compound.putInt(key,value);
    }

    @Override
    public void setLong(String key, long value) {
        compound.putLong(key,value);
    }

    @Override
    public void setDouble(String key, double value) {
        compound.putDouble(key,value);
    }

    @Override
    public String getString(String key) {
        return compound.getString(key);
    }

    @Override
    public boolean getBoolean(String key) {
        return compound.getBoolean(key);
    }

    @Override
    public int getInt(String key) {
        return compound.getInt(key);
    }

    @Override
    public long getLong(String key) {
        return compound.getLong(key);
    }

    @Override
    public double getDouble(String key) {
        return compound.getDouble(key);
    }

    @Override
    public boolean hasKey(String key) {
        return compound.contains(key);
    }

    @Override
    public org.bukkit.inventory.ItemStack getItem() {
        nmsItem.save(compound);
        return CraftItemStack.asBukkitCopy(nmsItem);
    }

    @Override
    public void removeKey(String key) {
        compound.remove(key);
    }
}

