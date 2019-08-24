package network.marble.inventoryapi;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import network.marble.inventoryapi.api.InventoryAPI;
import network.marble.inventoryapi.inventories.ArmourSet;
import network.marble.inventoryapi.inventories.Inventory;
import network.marble.inventoryapi.inventories.Menu;
import network.marble.inventoryapi.itemstacks.InventoryItem;

public class InventoryAPIPlugin extends JavaPlugin {
    private static InventoryAPIPlugin plugin;
    private static InventoryConfig config;
    public static String serverID;
    public static boolean menuModeRegistered = false;
    
    public static Map<Integer,InventoryItem> globalItems = new HashMap<>(); //The global player inventory mapping for InventoryItems that will appear in any sub-inventory
    public static Map<UUID,Integer> playerInventories = new HashMap<>(); //Mapping of which sub-inventories players have been assigned
    public static Map<Integer,Inventory> inventories = new HashMap<>(); //Mapping of what integers correspond to which inventories
    public static Map<UUID,Menu> playerCurrentMenus = new HashMap<>(); //Mapping of the current Menus players are viewing
    
    public static Map<UUID,ArmourSet> playerArmour = new HashMap<>(); //Mapping of what ArmourSets each player is current wearing
    public static ArmourSet globalArmour = new ArmourSet(); //Mapping of what ArmourSets everyone defaults to wearing
    
    public static Map<String, File> worldPlayersFolders = new HashMap<>();
    
    public static InventoryItem globalOffHandItem;
    public static Map<Integer, InventoryItem> inventoryOffHandItems = new HashMap<>(); //Mapping of what player inventories have in their off-hand slots 
    
    @Override
    public void onEnable() {
        plugin = this;
        inventories.put(0, new Inventory(0));//Generate a default inventory
        
        initConfig(false);
        if(config.enableMenuMode){
        	InventoryAPI.enableMenus();
        }
        
    	Bukkit.getWorlds().stream().forEach(w -> worldPlayersFolders.put(w.getName(), new File(Bukkit.getServer().getWorld(w.getName()).getWorldFolder(), "playerdata"))); //Pregen files for player.dat removal
    	
        if(this.isEnabled()){
        	getLogger().info("InventoryAPI successfully loaded.");
        }else{
        	getLogger().severe("Error loading InventoryAPI! This may cause the server to perform poorly.");
        }
    }

	private void initConfig(boolean repeat) {
    	try(Reader reader = new FileReader(this.getDataFolder() + "/config.json")){//Load configuration if it already exists
			config = new Gson().fromJson(reader, InventoryConfig.class);
    		return;
		} catch (Exception e){
			if(repeat){
				e.printStackTrace();
				return;
			}
			getLogger().info("Generating config.");
		}
    	InventoryConfig builder = new InventoryConfig();//Failing a load, create a new one
    	File directory = this.getDataFolder();
		
    	if (!directory.exists()){//Directory creation
			try{
				directory.mkdir();
			}catch(Exception e){
				e.printStackTrace();
				this.setEnabled(false);
				return;
			}
		}
		
		try(Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.getDataFolder() + "/config.json")))){//Default configuration write
			Gson file = new GsonBuilder().create();
			file.toJson(builder, writer);
			getLogger().info("Config file has been created");
		} catch (Exception e){
			e.printStackTrace();
			this.setEnabled(false);
		}
		if(!repeat){
			initConfig(true);
		}
	}
    
    public static InventoryAPIPlugin getPlugin() {
        return plugin;
    }
    
    public static InventoryConfig getConfigModel() {
        return config;
    }
}
