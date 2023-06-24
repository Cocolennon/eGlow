package me.MrGraycat.eGlow.command.subcommand.impl.admin;

import me.MrGraycat.eGlow.command.subcommand.SubCommand;
import me.MrGraycat.eGlow.config.EGlowMainConfig.MainConfig;
import me.MrGraycat.eGlow.config.EGlowMessageConfig.Message;
import me.MrGraycat.eGlow.manager.glow.IEGlowPlayer;
import me.MrGraycat.eGlow.util.chat.ChatUtil;
import org.bukkit.command.CommandSender;

import java.util.List;

public class UnsetCommand extends SubCommand {
	@Override
	public String getName() {
		return "unset";
	}

	@Override
	public String getDescription() {
		return "Stop the glowing of a player/NPC";
	}

	@Override
	public String getPermission() {
		return "eglow.command.unset";
	}

	@Override
	public String[] getSyntax() {
		return new String[] {"/eGlow unset <player/npc>"};
	}

	@Override
	public boolean isPlayerCmd() {
		return false;
	}

	@Override
	public void perform(CommandSender sender, IEGlowPlayer ePlayer, String[] args) {
		List<IEGlowPlayer> eTargets = getTarget(sender, args);

		if (eTargets == null) {
			sendSyntax(sender);
			return;
		}

		for (IEGlowPlayer eTarget : eTargets) {
			if (eTarget == null)
				continue;
			
			if (eTarget.isGlowing()) {
				eTarget.disableGlow(false);
				
				if (eTarget.getEntityType().equals("PLAYER") && MainConfig.SETTINGS_NOTIFICATIONS_TARGET_COMMAND.getBoolean())
					ChatUtil.sendMessage(eTarget.getPlayer(), Message.TARGET_NOTIFICATION_PREFIX.get() + Message.DISABLE_GLOW.get(), true);
			}

			ChatUtil.sendMessage(sender, Message.OTHER_CONFIRM_OFF.get(eTarget), true);
		}	
	}
}