package fr.stylobow.gravity;

import fr.stylobow.gravity.Commands.GravityCommand;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class GravityListener implements Listener {
    String prefix = "§7[§d§lGravity§r§7]§f ";

    static boolean gameStarted = false;

    private final Main plugin;
    public GravityListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerFall(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player && gameStarted == true) {
            Player player = (Player) event.getEntity();

            if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                event.setCancelled(true);

                player.sendMessage(prefix + "§cVous avez échoué !");
                GravityCommand.TeleportBack(player, plugin);
            }
        }
    }

    public static void UpdateGameStatue(boolean gameStatue) {
        gameStarted = gameStatue;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location loc = player.getLocation();

        if (loc.getBlock().getType() == Material.WATER && loc.getBlockY() < -50 && gameStarted == true) {
            GravityCommand.TeleportNextLevel(player, plugin);
        }
    }
}
