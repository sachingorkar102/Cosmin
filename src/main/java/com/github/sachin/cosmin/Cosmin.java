package com.github.sachin.cosmin;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.Map.Entry;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.github.sachin.cosmin.armor.ArmorManager;
import com.github.sachin.cosmin.commands.CommandManager;
import com.github.sachin.cosmin.commands.CosmeticCommand;
import com.github.sachin.cosmin.commands.TabComplete;
import com.github.sachin.cosmin.compat.MMOItemsAPI;
import com.github.sachin.cosmin.compat.PacketEvents;
import com.github.sachin.cosmin.database.MySQL;
import com.github.sachin.cosmin.database.PlayerData;
import com.github.sachin.cosmin.economy.PlayerPointsHook;
import com.github.sachin.cosmin.economy.VaultHook;
import com.github.sachin.cosmin.gui.GuiListener;
import com.github.sachin.cosmin.gui.GuiManager;
import com.github.sachin.cosmin.compat.CosminPAPIExpansion;
import com.github.sachin.cosmin.listener.PlayerListener;
import com.github.sachin.cosmin.player.CosminPlayer;
import com.github.sachin.cosmin.player.PlayerManager;
import com.github.sachin.cosmin.protocol.*;
import com.github.sachin.cosmin.utils.*;
import com.github.sachin.prilib.McVersion;
import com.github.sachin.prilib.Prilib;
import com.github.sachin.prilib.nms.NBTItem;
import com.github.sachin.prilib.utils.FastItemStack;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandMap;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;


public final class Cosmin extends JavaPlugin implements Listener{

    private static final Gson gson = new Gson();

    public Prilib prilib;
    private static Cosmin instance;
    private boolean pluginDisabled;

    public boolean postNetherUpdate;
    private boolean papiEnabled;

    private boolean isGrimACEnabled = false;
    private boolean isMMOItemsEnabled = false;
    private CosminPAPIExpansion papiExpansion;
    public boolean isEconomyEnabled;
    public GuiManager guiManager;
    public MiscItems miscItems;

    private CommandManager commandManager;
    private ProtocolManager protocolManager;
    private VaultHook vaultEco;
    private PlayerPointsHook playerPointsEco;
    private ArmorManager armorManager = new ArmorManager();
    private PlayerManager playerManager = new PlayerManager();
    private Message messageManager;


    private ConfigUtils configUtils;
    private File playerData;

    private File playerDataYML;

    private MySQL mySQL;
    private Map<Integer,Player> entityIdMap = new HashMap<>();

    private List<UUID> commandCoolDown = new ArrayList<>();

    @Override
    public void onEnable() {
        this.pluginDisabled = false;
        instance = this;

        if(CosminConstants.ISDEMO){
            getServer().getConsoleSender().sendMessage(ChatColor.YELLOW+""+ChatColor.BOLD+"Running a demo version of cosmin...");
            getServer().getConsoleSender().sendMessage(ChatColor.YELLOW+""+ChatColor.BOLD+"Only op players can use /cosmetics,/cos command");
            getServer().getConsoleSender().sendMessage(ChatColor.YELLOW+""+ChatColor.BOLD+"Consider getting cosmin here: https://www.spigotmc.org/resources/92427/");
        }
        if(!getServer().getPluginManager().isPluginEnabled("ProtocolLib")){
            getLogger().warning("Could not find ProtocolLib which is a required dependency, stopping cosmin");
            this.pluginDisabled = true;
        }
        this.prilib = new Prilib(this);
        prilib.initialize();
        if(!prilib.isNMSEnabled() || !setupVersion()){
            getLogger().warning("Running incompatiable minecraft version, stopping cosmin");
            this.pluginDisabled = true;
            return;
        }

//        NBTAPI nbt = new NBTAPI();
//        if(!nbt.loadVersions(this,mcVersion)){
//            getLogger().warning("Running incompatiable minecraft version, stopping cosmin");
//            this.pluginDisabled = true;
//            return;
//        }
        if(this.pluginDisabled){
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        this.saveDefaultConfig();
        if(!new File(getDataFolder(),CosminConstants.MISC_ITEMS_FILE).exists()){
            this.saveResource(CosminConstants.MISC_ITEMS_FILE, false);
        }
        playerData = new File(getDataFolder(),CosminConstants.PLAYER_DATA_FILE);
        playerDataYML = new File(getDataFolder(),"player-data.yml");
        saveDefaultPlayerDataYML();
        saveDefaultPlayerData();
        // register events
        PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvents(new GuiListener(this), this);
        pm.registerEvents(new PlayerListener(this), this);
        this.papiEnabled = false;
        if(pm.isPluginEnabled("PlaceHolderAPI")){
            getLogger().info("Found PlaceHolderAPI registering the expansion...");
            this.papiEnabled = true;
            this.papiExpansion = new CosminPAPIExpansion(this);
            papiExpansion.register();
            
        }

        
        reloadAllConfigs();
        registerCommands();

        // register packet listeners
        this.protocolManager = ProtocolLibrary.getProtocolManager();
        this.isGrimACEnabled = getServer().getPluginManager().isPluginEnabled("GrimAC");
        this.isMMOItemsEnabled = getServer().getPluginManager().isPluginEnabled("MMOItems");
        if(isGrimACEnabled){
            getLogger().info("grimac enabled, Registering packet listener for packetevents library");
            PacketEvents.enablePacketEvents();
        }
        else{
            this.protocolManager.addPacketListener(new SetSlotPacketListener(this));
        }
        this.protocolManager.addPacketListener(new EntityEquipmentPacketListener(this));
        this.protocolManager.addPacketListener(new SpawnPlayerPacketListener(this));
        this.protocolManager.addPacketListener(new PlayerUseItemPacketListener(this));
        Bukkit.getOnlinePlayers().forEach(p -> {entityIdMap.put(p.getEntityId(), p);});


        loadPlayerData();


    }

    @Override
    public void onDisable() {
        if(pluginDisabled) return;
        savePlayerData();
        if(isGrimACEnabled){
            PacketEvents.disablePacketEvents();
        }
    }

    public void enabledEconomy(){
        if(getServer().getPluginManager().isPluginEnabled("Vault")){
            getLogger().info("Found Vault, trying to initialize economy support..");
            this.vaultEco = new VaultHook(this); 
        }
        else if(getServer().getPluginManager().isPluginEnabled("PlayerPoints")){
            getLogger().info("Found PlayerPoints, trying to initialize economy support..");
            this.playerPointsEco = new PlayerPointsHook(this);
        }
        else{
            this.isEconomyEnabled = false;
            getLogger().info("No economy plugins found, disabling economy features");
        }

        
        if(vaultEco != null || playerPointsEco != null){
            this.isEconomyEnabled = true;
        }
    }

    public String getArmorName(FastItemStack fastItem){
        String armorName = null;
        if(fastItem.hasKey(CosminConstants.COSMIN_ARMOR_KEY,PersistentDataType.STRING)){
            armorName =  fastItem.get(CosminConstants.COSMIN_ARMOR_KEY,PersistentDataType.STRING);
        }
        else if(ItemBuilder.isHatItem(fastItem.get())){
            armorName = ItemBuilder.getArmorName(fastItem.get());
        }
        if(armorName != null && getArmorManager().containsArmor(armorName)){
            return armorName;
        }
        return null;
    }


    public void loadPlayerData(){
        
        saveDefaultPlayerData();
        if(configUtils.isMySQLEnabled() && mySQL.isConnected()){
            getLogger().info("Already connected to database not loading data from "+CosminConstants.PLAYER_DATA_FILE);
            if(mySQL.hasDataUpdated()){
                for(Player player : Bukkit.getOnlinePlayers()){
                    PlayerData playerData = new PlayerData(player.getUniqueId());
                    List<ItemStack> items = getItemsFromYAML(InventoryUtils.decompressYAMLString(playerData.getPlayerData()),null);
                    CosminPlayer cosminPlayer = new CosminPlayer(player.getUniqueId(),items);
                    cosminPlayer.computeAndPutEquipmentPairList();
                    cosminPlayer.setPurchasedItems(playerData.getPurchasedItems("PurchasedItems"));
                    cosminPlayer.setPurchasedSets(playerData.getPurchasedItems("PurchasedSets"));
                    getPlayerManager().addPlayer(cosminPlayer);
                }

            }else{
                for(String id : mySQL.getPlayers()){
                    UUID uuid = UUID.fromString(id);
                    PlayerData playerData = new PlayerData(uuid);
                    boolean isYamlString = InventoryUtils.isYamlString(playerData.getPlayerData());
                    if(playerData.playerExists()){
                        List<ItemStack> items;
                        if(!isYamlString){
                            items = Arrays.asList(InventoryUtils.base64ToItemStackArray(playerData.getPlayerData()));
                        }else{
                            items = getItemsFromYAML(InventoryUtils.decompressYAMLString(playerData.getPlayerData()),null);
                        }
                        CosminPlayer cosminPlayer = new CosminPlayer(uuid,items);
                        cosminPlayer.computeAndPutEquipmentPairList();
                        cosminPlayer.setPurchasedItems(playerData.getPurchasedItems("PurchasedItems"));
                        cosminPlayer.setPurchasedSets(playerData.getPurchasedItems("PurchasedSets"));
                        Player player = Bukkit.getPlayer(uuid);
                        if(player != null && player.isOnline()){
                            getPlayerManager().addPlayer(cosminPlayer);
                        }
                        if(!isYamlString){
                            YamlConfiguration yamlConfig = new YamlConfiguration();
                            ConfigurationSection itemsConfig = convertToYaml(yamlConfig.createSection("items"),cosminPlayer.getCosminInvContents(),true);
                            playerData.updatePlayerData(InventoryUtils.compressYAMLString(yamlConfig.saveToString()),cosminPlayer.getPurchasedItems(),cosminPlayer.getPurchasedSets());
                        }
                    }
                }
                mySQL.changeDataUpdateStatus();
            }
            return;

        }
        if(playerData.exists()){
            getLogger().info("loading player data from player-data.json for last time...");
            try (FileReader reader = new FileReader(playerData)) {
                JsonObject object = gson.fromJson(reader, JsonObject.class);
                if(object != null){
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

                }

                reader.close();
            } catch (Exception e) {
                getLogger().warning("could not load player data");
                e.printStackTrace();
            }
            getLogger().info("renaming player-data.json to player-data-disabled.json, since now switching to player-data.yml as storage option");
            File disabledData = new File(getDataFolder(),"player-data-disabled.json");
            if(disabledData.exists()){
                disabledData.delete();
            }

            playerData.renameTo(disabledData);

        }
        else{
            YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(playerDataYML);
            for(String key : yamlConfiguration.getKeys(false)){
                ConfigurationSection playerConfig = yamlConfiguration.getConfigurationSection(key);
                UUID uuid = UUID.fromString(key);
                List<ItemStack> items = new ArrayList<>();
                if(playerConfig.isConfigurationSection("items")){
                    ConfigurationSection itemConfig = playerConfig.getConfigurationSection("items");
                    items = getItemsFromYAML(null,itemConfig);
                }
                else if(playerConfig.isString("items")){
                    items = getItemsFromYAML(InventoryUtils.decompressYAMLString(playerConfig.getString("items")),null);
                }
                CosminPlayer cosminPlayer = new CosminPlayer(uuid,items);
                cosminPlayer.setPurchasedItems(new HashSet<>(playerConfig.getStringList("purchased-items")));
                cosminPlayer.setPurchasedSets(new HashSet<>(playerConfig.getStringList("purchased-sets")));
                playerManager.addPlayer(cosminPlayer);
            }
        }


    }

    public void savePlayerData(){
        saveDefaultPlayerData();
        if(configUtils.isMySQLEnabled()){
            if(this.mySQL != null && this.mySQL.isConnected()){
                getLogger().info("Already connected to database not saving data to "+CosminConstants.PLAYER_DATA_FILE);
                for(CosminPlayer player : getPlayerManager().getCosminPlayers()){
                    PlayerData playerData = new PlayerData(player.getUuid());
                    YamlConfiguration yamlConfig = new YamlConfiguration();
                    ConfigurationSection itemsConfig = convertToYaml(yamlConfig.createSection("items"),player.getCosminInvContents(),false);
//                        String data = InventoryUtils.itemStackListToBase64(player.getCosminInvContents());
                    playerData.updatePlayerData(InventoryUtils.compressYAMLString(yamlConfig.saveToString()),player.getPurchasedItems(),player.getPurchasedSets());
                }
                return;
            }
        }
        if(playerManager.getCosminPlayers().isEmpty()) return;
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(playerDataYML);
        for(CosminPlayer cosminPlayer : playerManager.getCosminPlayers()){
            ConfigurationSection playerConfig = yamlConfiguration.createSection(cosminPlayer.getUuid().toString());
            playerConfig.set("purchased-items", new ArrayList<>(cosminPlayer.getPurchasedItems()));

            playerConfig.set("purchased-sets",new ArrayList<>(cosminPlayer.getPurchasedSets()));
            playerConfig.set("name",cosminPlayer.getBukkitPlayerOffline().getName());
            YamlConfiguration yamlConfig = new YamlConfiguration();
            ConfigurationSection itemsConfig = convertToYaml(yamlConfig.createSection("items"),cosminPlayer.getCosminInvContents(),false);
            playerConfig.set("items",InventoryUtils.compressYAMLString(yamlConfig.saveToString()));
        }
        try {
            yamlConfiguration.save(playerDataYML);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if(playerData.exists()){
            getLogger().info("renaming player-data.json to player-data-disabled.json, since now switching to player-data.yml as storage option");
            playerData.renameTo(new File(getDataFolder(),"player-data-disabled.json"));
        }
//        try (FileWriter writer = new FileWriter(playerData)) {
//            JsonObject object = new JsonObject();
//            getLogger().info("Saving player data to player-data.json...");
//            for(CosminPlayer cosminPlayer: playerManager.getCosminPlayers()){
//                JsonObject playerInfo = new JsonObject();
//                JsonArray purchasedItems = new JsonArray();
//                JsonArray purchasedSets = new JsonArray();
//                for(String i:cosminPlayer.getPurchasedItems()){
//                    purchasedItems.add(i);
//                }
//                for(String i:cosminPlayer.getPurchasedSets()){
//                    purchasedSets.add(i);
//                }
//                playerInfo.addProperty("name", cosminPlayer.getBukkitPlayerOffline().getName());
//                playerInfo.add("purchased-items", purchasedItems);
//                playerInfo.add("purchased-sets", purchasedSets);
//                playerInfo.addProperty("contents", InventoryUtils.itemStackListToBase64(cosminPlayer.getCosminInvContents()));
//                object.add(cosminPlayer.getUuid().toString(), playerInfo);
//            }
//            gson.toJson(object,writer);
//            // writer.close();
//        } catch (Exception e) {
//            getLogger().warning("Could not store player data");
//            e.printStackTrace();
//        }
    }

    public ConfigurationSection convertToYaml(ConfigurationSection itemsConfig,List<ItemStack> items,boolean convertMMOItem){
        for(int i=0;i<items.size();i++){
            ItemStack item = items.get(i);
            String key = Integer.toString(i);
            FastItemStack fastItem = new FastItemStack(item);
            if(item==null){
                itemsConfig.set(key,new ItemStack(Material.AIR));
                continue;
            }
            else if(item.isSimilar(miscItems.getDisableItem())){
                fastItem.set(CosminConstants.TOGGLE_ITEM_KEY, PersistentDataType.INTEGER,0);
                itemsConfig.set(key,fastItem.get());
            }
            else if(item.isSimilar(miscItems.getEnableItem())){
                fastItem.set(CosminConstants.TOGGLE_ITEM_KEY,PersistentDataType.INTEGER,1);
                itemsConfig.set(key,fastItem.get());
            }
            else if(ItemBuilder.isHatItem(item) && !fastItem.hasKey(CosminConstants.COSMIN_ARMOR_KEY,PersistentDataType.STRING)){
                itemsConfig.set(key,fastItem.set(CosminConstants.COSMIN_ARMOR_KEY,PersistentDataType.STRING,ItemBuilder.getArmorName(item)).get());
            }
            if(getConfigUtils().isCosmeticSetEnabled() && item.isSimilar(miscItems.getCosmeticSetButton())){continue;}
            if(item.isSimilar(miscItems.getFillerGlass())){continue;}
            if(isMMOItemsEnabled && MMOItemsAPI.isMMOItem(item) && convertMMOItem){
                itemsConfig.set(key,MMOItemsAPI.setMMOItemInfo(item));
                continue;
            }
            itemsConfig.set(key,item);
        }
        return itemsConfig;
    }


    public List<ItemStack> getItemsFromYAML(String yamlString,ConfigurationSection itemConfig){
        List<ItemStack> items = Arrays.asList(new ItemStack[18]);
        if(itemConfig == null && yamlString != null){
            try {
                YamlConfiguration yamlConfig = new YamlConfiguration();
                yamlConfig.loadFromString(yamlString);
                itemConfig = yamlConfig.getConfigurationSection("items");
            } catch (InvalidConfigurationException e) {
                return items;
            }
        }

        for(int i=0;i<18;i++){
            if(CosminConstants.FILLAR_SLOTS.contains(i)) items.set(i,miscItems.getFillerGlass());
            else if(itemConfig.contains(Integer.toString(i))){
                ItemStack item = itemConfig.getItemStack(Integer.toString(i));
                if(item==null || item.getType().isAir()) {
                    items.set(i, item);
                    continue;
                }
                FastItemStack fastItem = new FastItemStack(item);
                String armorName = getArmorName(fastItem);
                if(fastItem.hasKey(CosminConstants.TOGGLE_ITEM_KEY,PersistentDataType.INTEGER)){
                    items.set(i,fastItem.get(CosminConstants.TOGGLE_ITEM_KEY,PersistentDataType.INTEGER)==0 ? miscItems.getDisableItem() : miscItems.getEnableItem());
                }
                else if(armorName != null){
                    items.set(i,getArmorManager().getArmor(armorName).getItem());
                }
                else if(isMMOItemsEnabled && fastItem.hasKey(MMOItemsAPI.ID,PersistentDataType.STRING)){
                    items.set(i,MMOItemsAPI.getMMOItem(item));
                }
                else{
                    items.set(i,item);
                }
            }

        }
        if(getConfigUtils().isCosmeticSetEnabled()){
            items.set(0,miscItems.getCosmeticSetButton());
        }
        return items;
    }

    private boolean setupVersion(){
        if(CosminConstants.COMPATIBLE_VERSIONS_PRE_NETHER_UPDATE.contains(prilib.getBukkitVersion())){
            postNetherUpdate = false;
            getLogger().info("Running pre nether update");
            return true;
        }
        else if(prilib.getMcVersion().isAtLeast(new McVersion(1,16))){
            getLogger().info("Running post nether update");
            postNetherUpdate = true;
            return true;
        }
        return false;
    }

    public int getPacketInt(){
        return this.is1_17_1() ? 2 : 1;
    }

    public boolean is1_17_1(){
        return prilib.getMcVersion().isAtLeast(new McVersion(1,17,1));
//        return minecraftVersion.equals("v1_17_R1") || minecraftVersion.equals("v1_18_R1") || minecraftVersion.equals("v1_18_R2")|| minecraftVersion.equals("v1_19_R1");
    }

    public boolean isPost1_19(){
        return prilib.getMcVersion().isAtLeast(new McVersion(1,19));
    }

    public void registerCommands(){
        this.commandManager = new CommandManager(this);
        this.commandManager.registerSubCommands();
        getLogger().info("Registering commands...");
        this.getCommand("cosmin").setExecutor(this.commandManager);
        this.getCommand("cosmin").setTabCompleter(new TabComplete(this));
        registerCommand("cosmetic", new CosmeticCommand(this));
        
    }

    public static NamespacedKey getKey(String key){
        return new NamespacedKey(Cosmin.getInstance(),key);
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
        this.messageManager = new Message(this);
        // setup vault
        enabledEconomy();
        configUtils.reloadAllConfigs();
        if(configUtils.isMySQLEnabled()){
            this.mySQL = new MySQL(this);
        }
        File textureFolder = new File(getDataFolder(),"textures");
        
        if(!textureFolder.exists()) textureFolder.mkdir();
        File resourceFolder = new File(getDataFolder(),"resource-packs");
        if(!resourceFolder.exists()) resourceFolder.mkdir();
        registerCommand("cosmetic", new CosmeticCommand(this));

        // registerCommands();
        // Bukkit.getOnlinePlayers().forEach(p -> p.updateCommands());
        getServer().getConsoleSender().sendMessage(messageManager.getMessage(CosminConstants.M_RELOADED));
        getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', messageManager.getPrefix()+"&6Loaded &e"+getArmorManager().getAllArmor().size() +" &6items"));
        getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', messageManager.getPrefix()+"&6Loaded &e"+getArmorManager().getCosmeticSets().values().size()+" &6cosmetic sets"));
    }

    public void saveDefaultPlayerData(){
//        if(!playerData.exists()){
//            getLogger().info("Could not find player-data.json, generating one...");
//            this.saveResource(CosminConstants.PLAYER_DATA_FILE, false);
//        }
    }

    public void saveDefaultPlayerDataYML(){
        if(!playerDataYML.exists()){
            getLogger().info("Could not find player-data.yml, generating one...");
            this.saveResource("player-data.yml", false);
        }
    }

    public NBTItem getNBTItem(ItemStack item){
        return prilib.getNmsHandler().newItem(item);
    }

    public Message getMessageManager() {
        return messageManager;
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


    public ArmorManager getArmorManager() {
        return armorManager;
    }

    public ConfigUtils getConfigUtils() {
        return configUtils;
    }


    public MySQL MySQL() {
        return mySQL;
    }

    public boolean isPAPIEnabled(){
        return papiEnabled;
    }

    public boolean isGrimACEnabled() {
        return isGrimACEnabled;
    }

    public boolean isEconomyEnabled(){
        return isEconomyEnabled;
    }

    public boolean isMMOItemsEnabled() {
        return isMMOItemsEnabled;
    }

    public VaultHook getVaultEco() {
        return vaultEco;
    }

    public PlayerPointsHook getPlayerPointsEco() {
        return playerPointsEco;
    }

    public List<UUID> getCommandCoolDown() {
        return commandCoolDown;
    }
}
