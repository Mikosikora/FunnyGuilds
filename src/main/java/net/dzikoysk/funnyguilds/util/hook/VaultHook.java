package net.dzikoysk.funnyguilds.util.hook;

import net.dzikoysk.funnyguilds.util.FunnyLogger;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultHook {

    private static Economy economyHook;

    public static void initEconomyHook() {
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);

        if (rsp == null) {
            FunnyLogger.warning("Economy provider could not be found, some features may not be available.");
            return;
        }

        economyHook = rsp.getProvider();
    }

    public static boolean isHooked() {
        return economyHook != null;
    }

    public static boolean canAfford(Player player, double money) {
        Validate.notNull(player, "player can't be null!");
        return economyHook.has(player, money);
    }

    public static EconomyResponse withdrawFromPlayerBank(Player player, double money) {
        Validate.notNull(player, "player can't be null!");
        return economyHook.withdrawPlayer(player, money);
    }
}
