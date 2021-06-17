package com.github.sachin.cosmin;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.github.sachin.cosmin.armor.ArmorManager;
import com.github.sachin.cosmin.commands.CommandManager;
import com.github.sachin.cosmin.commands.CosmeticCommand;
import com.github.sachin.cosmin.commands.TabComplete;
import com.github.sachin.cosmin.database.MySQL;
import com.github.sachin.cosmin.database.PlayerData;
import com.github.sachin.cosmin.economy.EconomyManager;
import com.github.sachin.cosmin.gui.GuiListener;
import com.github.sachin.cosmin.gui.GuiManager;
import com.github.sachin.cosmin.listener.PlayerListener;
import com.github.sachin.cosmin.nbtapi.NBTAPI;
import com.github.sachin.cosmin.player.CosminPlayer;
import com.github.sachin.cosmin.player.PlayerManager;
import com.github.sachin.cosmin.protocol.EntityEquipmentPacketListener;
import com.github.sachin.cosmin.protocol.SetSlotPacketListener;
import com.github.sachin.cosmin.protocol.SpawnPlayerPacketListener;
import com.github.sachin.cosmin.utils.ConfigUtils;
import com.github.sachin.cosmin.utils.CosminConstants;
import com.github.sachin.cosmin.utils.InventoryUtils;
import com.github.sachin.cosmin.utils.MiscItems;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.CommandMap;
import org.bukkit.command.defaults.BukkitCommand;


import java.lang.reflect.Field;


public final class Cosmin extends JavaPlugin implements Listener{

    private static final Gson gson = new Gson();
    private static Cosmin instance;
    private String minecraftVersion;

    public boolean postNetherUpdate;
    private boolean papiEnabled;
    public GuiManager guiManager;
    public MiscItems miscItems;

    private CommandManager commandManager;
    private ProtocolManager protocolManager;
    private EconomyManager economyManager;
    private ArmorManager armorManager = new ArmorManager();
    private PlayerManager playerManager = new PlayerManager();


    private ConfigUtils configUtils;
    private File playerData;

    private MySQL mySQL;
    private Map<Integer,Player> entityIdMap = new HashMap<>();


    @Override
    public void onEnable() {

        instance = this;
        if(!setupVersion()){
            getLogger().warning("Running incompatiable minecraft version, stopping cosmin");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        NBTAPI nbt = new NBTAPI();
        if(!nbt.loadVersions(this)){
            getLogger().warning("Running incompatiable minecraft version, stopping cosmin");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        this.saveDefaultConfig();
        if(!new File(getDataFolder(),CosminConstants.MISC_ITEMS_FILE).exists()){
            this.saveResource(CosminConstants.MISC_ITEMS_FILE, false);
        }
        playerData = new File(getDataFolder(),CosminConstants.PLAYER_DATA_FILE);
        saveDefaultPlayerData();
        // register events
        PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvents(new GuiListener(this), this);
        pm.registerEvents(new PlayerListener(this), this);
        this.papiEnabled = pm.isPluginEnabled("PlaceHolderAPI");

        
        reloadAllConfigs();
        registerCommands();

        // register packet listeners
        this.protocolManager = ProtocolLibrary.getProtocolManager();
        this.protocolManager.addPacketListener(new EntityEquipmentPacketListener(this));
        this.protocolManager.addPacketListener(new SetSlotPacketListener(this));
        this.protocolManager.addPacketListener(new SpawnPlayerPacketListener(this));

        Bukkit.getOnlinePlayers().forEach(p -> {entityIdMap.put(p.getEntityId(), p);});

        loadPlayerData();


    }

    @Override
    public void onDisable() {
        if(!isEnabled()) return;
        savePlayerData();
    }


    public void loadPlayerData(){
        
        saveDefaultPlayerData();
        if(configUtils.isMySQLEnabled()){
            if(this.mySQL.isConnected()){
                getLogger().info("Already connected to database not loading data from "+CosminConstants.PLAYER_DATA_FILE);
                Bukkit.getServer().getOnlinePlayers().forEach(p -> {
                    PlayerData playerData = new PlayerData(p.getUniqueId());
                    if(playerData.playerExists()){
                        List<ItemStack> items = Arrays.asList(InventoryUtils.base64ToItemStackArray(playerData.getPlayerData()));
                        CosminPlayer cosminPlayer = new CosminPlayer(p.getUniqueId(),items);
                        cosminPlayer.computeAndPutEquipmentPairList();
                        cosminPlayer.setPurchasedItems(playerData.getPurchasedItems("PurchasedItems"));
                        cosminPlayer.setPurchasedSets(playerData.getPurchasedItems("PurchasedSets"));
                        getPlayerManager().addPlayer(cosminPlayer);
                    }
                });
                return;
            }
        }
        try (FileReader reader = new FileReader(playerData)) {
            JsonObject object = gson.fromJson(reader, JsonObject.class);
            getLogger().info("Loading player data from player-data.json...");
            playerManager.clear();
            for (Entry<String,JsonElement> element : object.entrySet()) {
                UUID uuid = UUID.fromString(element.getKey());
                JsonObject playerInfo = element.getValue().getAsJsonObject();
                
                List<ItemStack> contents = Arrays.asList(InventoryUtils.base64ToItemStackArray(playerInfo.get("contents").getAsString()));
                if(contents == null || contents.isEmpty()) continue;
                CosminPlayer cosminPlayer = new CosminPlayer(uuid,contents);
                JsonArray purchasedItems = playerInfo.get("purchased-items").getAsJsonArray();
                JsonArray purchasedSets = playerInfo.get("purchased-sets").getAsJsonArray();
                for(JsonElement e:purchasedItems){
                    cosminPlayer.addPurchasedItem(e.getAsString());
                }
                for(JsonElement e:purchasedSets){
                    cosminPlayer.addPurchasedSet(e.getAsString());
                }
                playerManager.addPlayer(cosminPlayer);
                
            }
            // reader.close();
        } catch (Exception e) {
            getLogger().warning("could not load player data");
            e.printStackTrace();
        }
        
    }

    public void savePlayerData(){
        saveDefaultPlayerData();
        if(configUtils.isMySQLEnabled()){
            if(this.mySQL != null){
                if(this.mySQL.isConnected()){
                    getLogger().info("Already connected to database not saving data to "+CosminConstants.PLAYER_DATA_FILE);
                    for(CosminPlayer player : getPlayerManager().getCosminPlayers()){
                        PlayerData playerData = new PlayerData(player.getUuid());
                        String data = InventoryUtils.itemStackListToBase64(player.getCosminInvContents());
                        playerData.updatePlayerData(data,player.getPurchasedItems(),player.getPurchasedSets());
                    }
                    return;
                }
            }
        }
        if(playerManager.getCosminPlayers().isEmpty()) return;
        try (FileWriter writer = new FileWriter(playerData)) {
            JsonObject object = new JsonObject();
            getLogger().info("Saving player data to player-data.json...");
            for(CosminPlayer cosminPlayer: playerManager.getCosminPlayers()){
                JsonObject playerInfo = new JsonObject();
                JsonArray purchasedItems = new JsonArray();
                JsonArray purchasedSets = new JsonArray();
                for(String i:cosminPlayer.getPurchasedItems()){
                    purchasedItems.add(i);
                }
                for(String i:cosminPlayer.getPurchasedSets()){
                    purchasedSets.add(i);
                }
                playerInfo.addProperty("name", cosminPlayer.getBukkitPlayerOffline().getName());
                playerInfo.add("purchased-items", purchasedItems);
                playerInfo.add("purchased-sets", purchasedSets);
                playerInfo.addProperty("contents", InventoryUtils.itemStackListToBase64(cosminPlayer.getCosminInvContents()));
                object.add(cosminPlayer.getUuid().toString(), playerInfo);
            }
            gson.toJson(object,writer);
            // writer.close();
        } catch (Exception e) {
            getLogger().warning("Could not store player data");
            e.printStackTrace();
        }
    }


    private boolean setupVersion(){
        try {
            this.minecraftVersion = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
        getLogger().info("Running "+minecraftVersion+" minecraft version");
        if(CosminConstants.COMPATIBLE_VERSIONS_PRE_NETHER_UPDATE.contains(minecraftVersion)){
            postNetherUpdate = false;
            getLogger().info("Running pre nether update");
            return true;
        }
        else if(CosminConstants.COMPATIBLE_VERSIONS_POST_NETHER_UPDATE.contains(minecraftVersion)){
            getLogger().info("Running post nether update");
            postNetherUpdate = true;
            return true;
        }
        return false;
    }

    public void registerCommands(){
        this.commandManager = new CommandManager(this);
        this.commandManager.registerSubCommands();
        getLogger().info("Registering commands...");
        this.getCommand("cosmin").setExecutor(this.commandManager);
        this.getCommand("cosmin").setTabCompleter(new TabComplete(this));
        registerCommand("cosmetic", new CosmeticCommand(this));
        
    }

    private void registerCommand(String fallback, BukkitCommand command) {
        try {
            Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            bukkitCommandMap.setAccessible(true);
            CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());
            commandMap.register(fallback, command);
            
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }
    } 


    public void reloadAllConfigs(){
        this.guiManager = new GuiManager();
        this.configUtils = new ConfigUtils(this);
        this.miscItems = new MiscItems(this);
        this.miscItems.loadMiscItems();
        // setup vault
        this.economyManager = new EconomyManager(this);
        configUtils.reloadAllConfigs();
        if(configUtils.isMySQLEnabled()){
            this.mySQL = new MySQL(this);
        }
        registerCommand("cosmetic", new CosmeticCommand(this));
        // registerCommands();
        Bukkit.getOnlinePlayers().forEach(p -> p.updateCommands());
        getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', CosminConstants.MESSAGE_PREFIX+"&6Config files successfully loaded"));
        getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', CosminConstants.MESSAGE_PREFIX+"&6Loaded &e"+getArmorManager().getAllArmor().size() +" &6items"));
        getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', CosminConstants.MESSAGE_PREFIX+"&6Loaded &e"+getArmorManager().getCosmeticSets().values().size()+" &6cosmetic sets"));
    }

    public void saveDefaultPlayerData(){
        if(!playerData.exists()){
            getLogger().info("Could not find player-data.json, generating one...");
            this.saveResource(CosminConstants.PLAYER_DATA_FILE, false);
        }
    }

    public static Cosmin getInstance(){
        return instance;
    }

    public Map<Integer, Player> getEntityIdMap() {
        return entityIdMap;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public ProtocolManager getProtocolManager() {
        return protocolManager;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public String getMinecraftVersion() {
        return minecraftVersion;
    }

    public ArmorManager getArmorManager() {
        return armorManager;
    }

    public ConfigUtils getConfigUtils() {
        return configUtils;
    }

    public EconomyManager getEconomyManager() {
        return economyManager;
    }

    public MySQL MySQL() {
        return mySQL;
    }

    public boolean isPAPIEnabled(){
        return papiEnabled;
    }



}
