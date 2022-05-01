package me.MrGraycat.eGlow.Util.Packets;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

import org.bukkit.Bukkit;
import io.netty.channel.Channel;
import me.MrGraycat.eGlow.Util.Text.ChatUtil;

@SuppressWarnings({"rawtypes"})
public class NMSStorage {
	private final String serverPackage;
	public int minorVersion;
	
	public Class<?> Packet;
	public Class<?> EntityPlayer;
	public Class<?> CraftPlayer;
	public Class<?> PlayerConnection;
	public Class<?> NetworkManager;
	public Field PLAYER_CONNECTION;
	public Field NETWORK_MANAGER;
	public Field CHANNEL;
	public Method getHandle;
	public Method sendPacket;
	public Method setFlag;
	public Method getDataWatcher;

	public Class<Enum> EnumChatFormat;
	public Class<?> IChatBaseComponent;
	public Class<?> ChatSerializer;
	public Method ChatSerializer_DESERIALIZE;

	//Spigot
	public Class<?> SpigotConfig;
	public Class<?> skullMeta;
	public Method setOwningPlayer;
	public Field bungee;


	//PacketPlayOutChat
	public Class<?> ChatMessageType;
	public Constructor<?> newPacketPlayOutChat;
	public Enum[] ChatMessageType_values;

	//PacketPlayOutEntityMetadata
	public Class<?> PacketPlayOutEntityMetadata;//
	public Constructor<?> newPacketPlayOutEntityMetadata;//
	public Field PacketPlayOutEntityMetadata_LIST;
	
	//Scoreboard
	private Class<?> Scoreboard;
	private Class<?> ScoreboardTeam;
	public Constructor<?> newScoreboard;
	public Constructor<?> newScoreboardTeam;
	public Method ScoreboardTeam_setPrefix;
	public Method ScoreboardTeam_setSuffix;
	public Method ScoreboardTeam_setNameTagVisibility;
	public Method ScoreboardTeam_setCollisionRule;
	public Method ScoreboardTeam_setColor;
	public Method ScoreboardTeam_getPlayerNameSet;
	public Class<?> PacketPlayOutScoreboardTeam_a;
	public Method PacketPlayOutScoreboardTeam_of;
	public Method PacketPlayOutScoreboardTeam_ofBoolean;
	public Method PacketPlayOutScoreboardTeam_ofString;
	
	//PacketPlayOutScoreboardTeam
	public Class<?> PacketPlayOutScoreboardTeam;//
	public Class<?> EnumNameTagVisibility;
	public Class<?> EnumTeamPush;
	public Constructor<?> newPacketPlayOutScoreboardTeam;
	public Field PacketPlayOutScoreboardTeam_NAME;
	public Field PacketPlayOutScoreboardTeam_PLAYERS;
	
	//DataWatcher
	public Class<?> DataWatcher;
	public Constructor<?> newDataWatcher;
	public Method DataWatcher_REGISTER;

	private Class<?> DataWatcherItem;
	public Field DataWatcherItem_TYPE;
	public Field DataWatcherItem_VALUE;

	public Class<?> DataWatcherObject;
	public Constructor<?> newDataWatcherObject;
	public Field DataWatcherObject_SLOT;
	public Field DataWatcherObject_SERIALIZER;
	public Class<?> DataWatcherRegistry;
	private Class<?> DataWatcherSerializer;

	public NMSStorage() {
		serverPackage = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
		minorVersion = Integer.parseInt(serverPackage.split("_")[1]);
		initialiseValues();
	}
	
	@SuppressWarnings("unchecked")
	public void initialiseValues() {
		try {
			this.Packet = getNMSClass("net.minecraft.network.protocol.Packet", "Packet");
			this.EntityPlayer = getNMSClass("net.minecraft.server.level.EntityPlayer", "EntityPlayer");
			this.CraftPlayer = Class.forName("org.bukkit.craftbukkit." + serverPackage + ".entity.CraftPlayer");
			this.PlayerConnection = getNMSClass("net.minecraft.server.network.PlayerConnection", "PlayerConnection");
			this.NetworkManager = getNMSClass("net.minecraft.network.NetworkManager", "NetworkManager" );
			this.PLAYER_CONNECTION = getFields(this.EntityPlayer, this.PlayerConnection).get(0);
			this.NETWORK_MANAGER = getFields(this.PlayerConnection, this.NetworkManager).get(0);
			this.CHANNEL = getFields(this.NetworkManager, Channel.class).get(0);
			this.getHandle = getMethod(this.CraftPlayer, new String[] { "getHandle" });
			this.sendPacket = getMethod(this.PlayerConnection, new String[] { "sendPacket", "a", "func_147359_a" }, this.Packet);
			this.setFlag = getMethod(this.EntityPlayer, new String[] { "setFlag", "b", "setEntityFlag" }, int.class, boolean.class);
			this.getDataWatcher = getMethod(this.EntityPlayer, new String[] {"getDataWatcher", "ai"});
		
			this.EnumChatFormat = (Class)getNMSClass(new String[] { "net.minecraft.EnumChatFormat", "EnumChatFormat" });
			this.IChatBaseComponent = getNMSClass("net.minecraft.network.chat.IChatBaseComponent", "IChatBaseComponent");
			this.ChatSerializer = getNMSClass("net.minecraft.network.chat.IChatBaseComponent$ChatSerializer", "IChatBaseComponent$ChatSerializer", "ChatSerializer");
			this.ChatSerializer_DESERIALIZE = getMethod(this.ChatSerializer, new String[] { "a", "func_150699_a" }, String.class);

			this.SpigotConfig = getNormalClass("org.spigotmc.SpigotConfig");

			if (this.minorVersion <= 12) {
				this.skullMeta = getNormalClass("org.bukkit.inventory.meta.SkullMeta");
				this.setOwningPlayer = getMethod(this.skullMeta, new String[] {"setOwner"}, String.class);
			}

			this.bungee = getField(this.SpigotConfig, "bungee");

			this.DataWatcher = getNMSClass("net.minecraft.network.syncher.DataWatcher", "DataWatcher");
		    this.DataWatcherItem = getNMSClass("net.minecraft.network.syncher.DataWatcher$Item", "DataWatcher$Item", "DataWatcher$WatchableObject", "WatchableObject");
		    this.DataWatcherObject = getNMSClass("net.minecraft.network.syncher.DataWatcherObject", "DataWatcherObject" );
		    this.DataWatcherRegistry = getNMSClass("net.minecraft.network.syncher.DataWatcherRegistry", "DataWatcherRegistry");
		    this.DataWatcherSerializer = getNMSClass("net.minecraft.network.syncher.DataWatcherSerializer", "DataWatcherSerializer");
		    this.newDataWatcher = this.DataWatcher.getConstructors()[0];
		    this.newDataWatcherObject = this.DataWatcherObject.getConstructors()[0];
		    this.DataWatcherItem_TYPE = getFields(this.DataWatcherItem, this.DataWatcherObject).get(0);
		    this.DataWatcherItem_VALUE = getFields(this.DataWatcherItem, Object.class).get(0);
		    this.DataWatcherObject_SLOT = getFields(this.DataWatcherObject, int.class).get(0);
		    this.DataWatcherObject_SERIALIZER = getFields(this.DataWatcherObject, this.DataWatcherSerializer).get(0);
			//this.DataWatcher_REGISTER = this.DataWatcher.getMethod("register", new Class[] { this.DataWatcherObject, Object.class });
			this.DataWatcher_REGISTER = getMethod(this.DataWatcher, new String[] {"register", "a"}, this.DataWatcherObject, Object.class);
			
			this.PacketPlayOutEntityMetadata = getNMSClass("net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata", "PacketPlayOutEntityMetadata", "Packet40EntityMetadata");
			this.newPacketPlayOutEntityMetadata = this.PacketPlayOutEntityMetadata.getConstructor(int.class, this.DataWatcher, boolean.class);
			this.PacketPlayOutEntityMetadata_LIST = getFields(this.PacketPlayOutEntityMetadata, List.class).get(0);
			
			this.Scoreboard = getNMSClass("net.minecraft.world.scores.Scoreboard", "Scoreboard");
			this.ScoreboardTeam = getNMSClass("net.minecraft.world.scores.ScoreboardTeam", "ScoreboardTeam");
			this.newScoreboard = this.Scoreboard.getConstructor(new Class[0]);
			this.newScoreboardTeam = this.ScoreboardTeam.getConstructor(this.Scoreboard, String.class);
			
		    if (this.minorVersion >= 13) {
		      this.ScoreboardTeam_setPrefix = getMethod(this.ScoreboardTeam, new String[] {"setPrefix", "b"}, this.IChatBaseComponent);
		      this.ScoreboardTeam_setSuffix = getMethod(this.ScoreboardTeam, new String[] {"setSuffix", "c"}, this.IChatBaseComponent);
		      this.ScoreboardTeam_setColor = getMethod(this.ScoreboardTeam, new String[] {"setColor", "a"}, this.EnumChatFormat);
		    } else {
		      this.ScoreboardTeam_setPrefix = getMethod(this.ScoreboardTeam, new String[] { "setPrefix", "func_96666_b" }, String.class);
		      this.ScoreboardTeam_setSuffix = getMethod(this.ScoreboardTeam, new String[] { "setSuffix", "func_96662_c" }, String.class);
		    }
		    
			this.EnumNameTagVisibility = getNMSClass("net.minecraft.world.scores.ScoreboardTeamBase$EnumNameTagVisibility", "ScoreboardTeamBase$EnumNameTagVisibility", "EnumNameTagVisibility");
			this.EnumTeamPush = getNMSClass("net.minecraft.world.scores.ScoreboardTeamBase$EnumTeamPush", "ScoreboardTeamBase$EnumTeamPush");
		    this.ScoreboardTeam_setNameTagVisibility = getMethod(this.ScoreboardTeam, new String[] { "setNameTagVisibility", "a" }, this.EnumNameTagVisibility);
		    this.ScoreboardTeam_setCollisionRule = getMethod(this.ScoreboardTeam, new String[] { "setCollisionRule", "a"}, this.EnumTeamPush);
		    this.ScoreboardTeam_getPlayerNameSet = getMethod(this.ScoreboardTeam, new String[] { "getPlayerNameSet", "g", "func_96670_d" }, new Class[0]);
		    
		    this.PacketPlayOutScoreboardTeam = getNMSClass("net.minecraft.network.protocol.game.PacketPlayOutScoreboardTeam", "PacketPlayOutScoreboardTeam", "Packet209SetScoreboardTeam");
			this.PacketPlayOutScoreboardTeam_NAME = getFields(this.PacketPlayOutScoreboardTeam, String.class).get(0);
			this.PacketPlayOutScoreboardTeam_PLAYERS = getFields(this.PacketPlayOutScoreboardTeam, Collection.class).get(0);
		    
		   if (this.minorVersion >= 17) {
			   this.PacketPlayOutScoreboardTeam_a = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutScoreboardTeam$a");
			   this.PacketPlayOutScoreboardTeam_of = this.PacketPlayOutScoreboardTeam.getMethod("a", this.ScoreboardTeam);
			   this.PacketPlayOutScoreboardTeam_ofBoolean = this.PacketPlayOutScoreboardTeam.getMethod("a", this.ScoreboardTeam, boolean.class);
			   this.PacketPlayOutScoreboardTeam_ofString = this.PacketPlayOutScoreboardTeam.getMethod("a", this.ScoreboardTeam, String.class, this.PacketPlayOutScoreboardTeam_a);
		   } else {
			   this.newPacketPlayOutScoreboardTeam = this.PacketPlayOutScoreboardTeam.getConstructor(this.ScoreboardTeam, int.class);
		   }

			Class<?> PacketPlayOutChat = getNMSClass("net.minecraft.network.protocol.game.PacketPlayOutChat", "PacketPlayOutChat", "Packet3Chat");
			if (minorVersion >= 12) {
				ChatMessageType = getNMSClass("net.minecraft.network.chat.ChatMessageType", "ChatMessageType");
				ChatMessageType_values = getEnumValues(ChatMessageType);
			}
			if (minorVersion >= 16) {
				newPacketPlayOutChat = PacketPlayOutChat.getConstructor(IChatBaseComponent, ChatMessageType, UUID.class);
			} else if (minorVersion >= 12) {
				newPacketPlayOutChat = PacketPlayOutChat.getConstructor(IChatBaseComponent, ChatMessageType);
			} else if (minorVersion >= 8) {
				newPacketPlayOutChat = PacketPlayOutChat.getConstructor(IChatBaseComponent, byte.class);
			}
		} catch (Exception e) {
			ChatUtil.reportError(e);
		}
	}
	  
	private Class<?> getNMSClass(String... names) throws ClassNotFoundException {
		for (String name : names) {
			try {
				return getNMSClass(name);
			} catch (ClassNotFoundException classNotFoundException) {
				continue;
			}
		}
		throw new ClassNotFoundException("No class found with possible names " + Arrays.toString(names));
	}

	private Class<?> getNormalClass(String name) throws  ClassNotFoundException {
		try {
			return Class.forName(name);
		} catch (NullPointerException e) {
			throw new ClassNotFoundException(name);
		}
	}

	private Class<?> getNMSClass(String name) throws ClassNotFoundException {
	    if (this.minorVersion >= 17)
	      return Class.forName(name); 
	    try {
	      return Class.forName("net.minecraft.server." + this.serverPackage + "." + name);
	    }  catch (NullPointerException e) {
	      throw new ClassNotFoundException(name);
	    } 
	  }

	public Method getMethod(Class<?> clazz, String[] names, Class<?>... parameterTypes) throws NoSuchMethodException {
		for (String name : names) {
			try {
				return clazz.getMethod(name, parameterTypes);
			} catch (Exception exception) {
				continue;
			}
		}
		throw new NoSuchMethodException("No method found with possible names " + Arrays.toString(names) + " in class " + clazz.getName());
	}

	private Field getField(Class<?> clazz, String name) {
		if (clazz == null) return null;
		for (Field field : clazz.getDeclaredFields()) {
			field.setAccessible(true);
			if (field.getName().equalsIgnoreCase(name))
				return field;
		}
		return null;
	}

	private List<Field> getFields(Class<?> clazz, Class<?> type){
		List<Field> list = new ArrayList<>();
		if (clazz == null) return list;
		for (Field field : clazz.getDeclaredFields()) {
			field.setAccessible(true);
			if (field.getType() == type) list.add(field);
		}
		return list;
	}

	private Enum[] getEnumValues(Class<?> enumClass) {
		if (enumClass == null) throw new IllegalArgumentException("Class cannot be null");
		if (!enumClass.isEnum()) throw new IllegalArgumentException(enumClass.getName() + " is not an enum class");
		try {
			return (Enum[]) enumClass.getMethod("values").invoke(null);
		} catch (ReflectiveOperationException e) {
			//this should never happen
			e.printStackTrace();
			return new Enum[0];
		}
	}
}