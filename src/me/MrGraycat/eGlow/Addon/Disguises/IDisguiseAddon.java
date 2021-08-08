package me.MrGraycat.eGlow.Addon.Disguises;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import de.robingrether.idisguise.api.DisguiseAPI;
import de.robingrether.idisguise.api.DisguiseEvent;
import de.robingrether.idisguise.api.UndisguiseEvent;
import me.MrGraycat.eGlow.EGlow;
import me.MrGraycat.eGlow.Config.EGlowMessageConfig.Message;
import me.MrGraycat.eGlow.Manager.Interface.IEGlowPlayer;
import me.MrGraycat.eGlow.Util.EnumUtil.GlowDisableReason;
import me.MrGraycat.eGlow.Util.Text.ChatUtil;

/*
 * Making sure to disable the player glow when disguised the prevent errors.
 * Plugin: iDisguise
 * Versions: 1.9-1.13 (Currently discontinued)
 */
public class IDisguiseAddon implements Listener {
	private EGlow instance;
	private DisguiseAPI api;

	/**
	 * Register IDisguise disguise events & api
	 */
	public IDisguiseAddon(EGlow instance) {
		setInstance(instance);
		getInstance().getServer().getPluginManager().registerEvents(this, getInstance());
		setAPI(getInstance().getServer().getServicesManager().getRegistration(DisguiseAPI.class).getProvider());
	}
	
	/**
	 * Check to see if player is diguised
	 * @param player Player to check
	 * @return true is disguised, false if not
	 */
	public boolean isDisguised(Player player) {
		return getAPI().isDisguised(player);
	}
	
	@EventHandler
	public void onDisguise(DisguiseEvent event) {
		IEGlowPlayer player = getInstance().getDataManager().getEGlowPlayer(event.getPlayer());
		
		if (player != null && player.getGlowStatus() || player != null && player.getFakeGlowStatus()) {
			player.setGlowDisableReason(GlowDisableReason.DISGUISE);
			player.toggleGlow();
			ChatUtil.sendMsgWithPrefix(player.getPlayer(), Message.DISGUISE_BLOCKED.get());
		}
	}
	
	@EventHandler
	public void onUnDisguise(UndisguiseEvent event) {
		IEGlowPlayer player = getInstance().getDataManager().getEGlowPlayer(event.getPlayer());
		
		if (player != null && player.getGlowDisableReason().equals(GlowDisableReason.DISGUISE)) {
			player.toggleGlow();
			player.setGlowDisableReason(GlowDisableReason.NONE);
			ChatUtil.sendMsgWithPrefix(player.getPlayer(), Message.DISGUISE_ALLOWED.get());
		}
	}
	
	//Setters
	private void setInstance(EGlow instance) {
		this.instance = instance;
	}
	
	private void setAPI(DisguiseAPI api) {
		this.api = api;
	}
	
	//Getters
	private DisguiseAPI getAPI() {
		return this.api;
	}
	
	private EGlow getInstance() {
		return this.instance;
	}
}
