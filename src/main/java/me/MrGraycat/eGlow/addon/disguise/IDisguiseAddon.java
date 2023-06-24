package me.MrGraycat.eGlow.addon.disguise;

import de.robingrether.idisguise.api.DisguiseAPI;
import de.robingrether.idisguise.api.DisguiseEvent;
import de.robingrether.idisguise.api.UndisguiseEvent;
import lombok.Getter;
import me.MrGraycat.eGlow.addon.GlowAddon;
import me.MrGraycat.eGlow.config.EGlowMessageConfig.Message;
import me.MrGraycat.eGlow.EGlow;
import me.MrGraycat.eGlow.manager.DataManager;
import me.MrGraycat.eGlow.manager.glow.IEGlowPlayer;
import me.MrGraycat.eGlow.util.Common.GlowDisableReason;
import me.MrGraycat.eGlow.util.chat.ChatUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Objects;

/*
 * Making sure to disable the player glow when disguised to prevent errors.
 * Plugin: iDisguise
 * Versions: 1.9-1.13 (Currently discontinued)
 */

@Getter
public class IDisguiseAddon extends GlowAddon implements Listener {

	@Getter
	private final DisguiseAPI disguiseApi;

	/**
	 * Register IDisguise disguise events & api
	 */
	public IDisguiseAddon(EGlow instance) {
		super(instance);

		this.disguiseApi = Objects.requireNonNull(instance.getServer().getServicesManager()
						.getRegistration(DisguiseAPI.class), "Unable to hook into IDisguise").getProvider();
	}

	/**
	 * Check to see if player is disguised
	 *
	 * @param player Player to check
	 * @return whether player is disguised
	 */
	public boolean isDisguised(Player player) {
		return disguiseApi.isDisguised(player);
	}

	@EventHandler
	public void onDisguise(DisguiseEvent event) {
		IEGlowPlayer eGlowPlayer = DataManager.getEGlowPlayer(event.getPlayer());

		if (eGlowPlayer != null && eGlowPlayer.isGlowing()) {
			eGlowPlayer.setGlowDisableReason(GlowDisableReason.DISGUISE, false);
			eGlowPlayer.disableGlow(false);
			ChatUtil.sendMessage(eGlowPlayer.getPlayer(), Message.DISGUISE_BLOCKED.get(), true);
		}
	}

	@EventHandler
	public void onUnDisguise(UndisguiseEvent event) {
		IEGlowPlayer eGlowPlayer = DataManager.getEGlowPlayer(event.getPlayer());

		if (eGlowPlayer != null && eGlowPlayer.getGlowDisableReason().equals(GlowDisableReason.DISGUISE)) {
			if (eGlowPlayer.setGlowDisableReason(GlowDisableReason.NONE, false)) {
				eGlowPlayer.activateGlow();
				ChatUtil.sendMessage(eGlowPlayer.getPlayer(), Message.DISGUISE_ALLOWED.get(), true);
			}
		}
	}
}