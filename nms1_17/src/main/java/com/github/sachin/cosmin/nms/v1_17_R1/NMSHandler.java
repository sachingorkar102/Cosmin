package com.github.sachin.cosmin.nms.v1_17_R1;

import com.github.sachin.cosmin.nbtapi.nms.NMSHelper;

import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import net.minecraft.nbt.NBTTagCompound;

public class NMSHandler extends NMSHelper{

    private net.minecraft.world.item.ItemStack nmsItem;
    private NBTTagCompound compound;


    public NMSHandler(){

    }

    public NMSHandler(ItemStack item){
        if(item == null) return;
        ItemStack bukkitItem = item.clone();
        this.nmsItem = CraftItemStack.asNMSCopy(bukkitItem);
        this.compound = (nmsItem.hasTag()) ? nmsItem.getTag() : new NBTTagCompound();

    }



    @Override
    public NMSHelper newItem(ItemStack item) {
        return new NMSHandler(item);
    }

    @Override
    public void setString(String key,String value){
        compound.setString(key, value);
    }

    @Override
    public ItemStack getItem() {
        nmsItem.setTag(compound);
        return CraftItemStack.asBukkitCopy(nmsItem);
    }

    @Override
    public boolean hasKey(String key) {
        return compound.hasKey(key);
    }

    @Override
    public String getString(String key) {
        return compound.getString(key);
    }

    @Override
    public void removeKey(String key) {
        compound.remove(key);
    }

    @Override
    public void setBoolean(String key, boolean value) {
        compound.setBoolean(key, value);
    }

    @Override
    public boolean getBoolean(String key) {
        return compound.getBoolean(key);
    }

    @Override
    public void setInt(String key, int value) {
        compound.setInt(key, value);
    }

    @Override
    public void setLong(String key, long value) {
        compound.setLong(key, value);
    }

    @Override
    public void setDouble(String key, double value) {
        compound.setDouble(key, value);

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

}
