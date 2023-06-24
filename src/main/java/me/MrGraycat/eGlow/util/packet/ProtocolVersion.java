package me.MrGraycat.eGlow.util.packet;

import lombok.Getter;
import me.MrGraycat.eGlow.util.ServerUtil;
import me.MrGraycat.eGlow.util.dependency.Dependency;
import me.MrGraycat.eGlow.manager.glow.IEGlowPlayer;
import org.bukkit.entity.Player;
import us.myles.ViaVersion.api.Via;

import java.util.Arrays;

@Getter
public enum ProtocolVersion {

	UNKNOWN(999, "Unknown"),
	v1_20(763, "1.20"),
	v1_19_4(762, "1.19.4"),
	v1_19_3(761, "1.19.3"),
	v1_19_1(760, "1.19.1(.2)"),
	v1_19(759, "1.19"),
	v1_18_2(758, "1.18.2"),
	v1_18(757, "1.18(.1)"),
	v1_17_1(756, "1.17.1"),
	v1_17(755, "1.17"),
	v1_16_5(754, "1.16.5"),
	v1_16_4(754, "1.16.4"),
	v1_16_3(753, "1.16.3"),
	v1_16_2(751, "1.16.2"),
	v1_16_1(736, "1.16.1"),
	v1_16(735, "1.16"),
	v1_15_2(578, "1.15.2"),
	v1_15_1(575, "1.15.1"),
	v1_15(573, "1.15"),
	v1_14_4(498, "1.14.4"),
	v1_14_3(490, "1.14.3"),
	v1_14_2(485, "1.14.2"),
	v1_14_1(480, "1.14.1"),
	v1_14(477, "1.14"),
	v1_13_2(404, "1.13.2"),
	v1_13_1(401, "1.13.1"),
	v1_13(393, "1.13"),
	v1_12_2(340, "1.12.2"),
	v1_12_1(338, "1.12.1"),
	v1_12(335, "1.12"),
	v1_11_2(316, "1.11.2"),
	v1_11_1(316, "1.11.1"),
	v1_11(315, "1.11"),
	v1_10_2(210, "1.10.2"),
	v1_10_1(210, "1.10.1"),
	v1_10(210, "1.10"),
	v1_9_4(110, "1.9.4"),
	v1_9_3(110, "1.9.3"),
	v1_9_2(109, "1.9.2"),
	v1_9_1(108, "1.9.1"),
	v1_9(107, "1.9"),
	v1_8(47, "1.8.x"),
	v1_7_10(5, "1.7.10"),
	v1_7_9(5, "1.7.9"),
	v1_7_8(5, "1.7.8"),
	v1_7_7(5, "1.7.7"),
	v1_7_6(5, "1.7.6"),
	v1_7_5(4, "1.7.5"),
	v1_7_4(4, "1.7.4"),
	v1_7_2(4, "1.7.2"),
	v1_6_4(78, "1.6.4"),
	v1_6_2(74, "1.6.2"),
	v1_6_1(73, "1.6.1"),
	v1_5_2(61, "1.5.2"),
	v1_5_1(60, "1.5.1"),
	v1_5(60, "1.5"),
	v1_4_7(51, "1.4.7"),
	v1_4_6(51, "1.4.6");

	public static ProtocolVersion SERVER_VERSION;

	private final int networkId;
	private final String friendlyName;
	private int minorVersion;

	ProtocolVersion(int networkId, String friendlyName) {
		this.networkId = networkId;
		this.friendlyName = friendlyName;

		if (toString().equals("UNKNOWN")) {
			try {
				minorVersion = ServerUtil.getMinorVersion();
			} catch (Throwable t) {
				minorVersion = 999;
			}
		} else {
			minorVersion = Integer.parseInt(toString().split("_")[1]);
		}
	}

	public static ProtocolVersion fromServerString(String serverString) {
		if (serverString.startsWith("1.8")) return v1_8;
		try {
			return valueOf("v" + serverString.replace(".", "_"));
		} catch (Throwable e) {
			return UNKNOWN;
		}
	}

	public static ProtocolVersion fromNumber(int number) {
		return Arrays.stream(values())
				.filter(version -> version.getNetworkId() == number)
				.findFirst()
				.orElse(UNKNOWN);
	}

	public static ProtocolVersion getPlayerVersion(IEGlowPlayer glowPlayer) {
		if (Dependency.PROTOCOL_SUPPORT.isLoaded()) {
			int version = getProtocolVersionFromProtocolSupport(glowPlayer.getPlayer());

			if (version < ProtocolVersion.SERVER_VERSION.getNetworkId()) {
				return ProtocolVersion.fromNumber(version);
			}
		}

		if (Dependency.VIA_VERSION.isLoaded()) {
			return ProtocolVersion.fromNumber(getProtocolVersionFromViaVersion(glowPlayer.getPlayer()));
		}

		return ProtocolVersion.SERVER_VERSION;
	}

	private static int getProtocolVersionFromProtocolSupport(Player player) {
		try {
			Object protocolVersion = Class.forName("protocolsupport.api.ProtocolSupportAPI").
					getMethod("getProtocolVersion", Player.class)
					.invoke(null, player);
			return (int) protocolVersion.getClass().getMethod("getId").invoke(protocolVersion);
		} catch (Throwable e) {
			return 0;
		}
	}

	private static int getProtocolVersionFromViaVersion(Player player) {
		try {
			return Via.getAPI().getPlayerVersion(player.getUniqueId());
		} catch (Throwable e) {
			return 0;
		}
	}
}