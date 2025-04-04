package fr.stylobow.gravity.Commands;

import fr.stylobow.gravity.GravityListener;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GravityCommand implements CommandExecutor {

    private final JavaPlugin plugin;
    private static List<Player> playersInGame = new ArrayList<>();
    private static List<String> LevelList = new ArrayList<>();
    private static final HashMap<Player, String> playerLevels = new HashMap<>();
    private static boolean gameStarted = false;
    private static Location spawn;

    public GravityCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
        FileConfiguration config = plugin.getConfig();
        String prefix = config.getString("prefix", "§7[§d§lGravity§r§7]§f ");

        if (!(sender instanceof Player)) {
            sender.sendMessage(prefix + "Seul les joueurs peuvent exécuter cette commande.");
            return true;
        }
        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage(prefix + "§cVeuillez utilisez §n/gravity help§r§c pour obtenir de l'aide");
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("setspawn")) {
            if (gameStarted == true) {
                player.sendMessage(prefix + "§4§l[ERROR]: §r§cImpossible de modifier le point de spawn du lobby pendant qu'une partie est en cours.");
                player.playSound(player.getLocation(), Sound.ENTITY_WOLF_GROWL, 0.5f, 1.25f);
            } else {
                Location loc = player.getLocation();
                //FileConfiguration config = plugin.getConfig();

                config.set("lobby.spawn.world", loc.getWorld().getName());
                config.set("lobby.spawn.x", loc.getX());
                config.set("lobby.spawn.y", loc.getY());
                config.set("lobby.spawn.z", loc.getZ());
                config.set("lobby.spawn.yaw", loc.getYaw());
                config.set("lobby.spawn.pitch", loc.getPitch());

                plugin.saveConfig();
                spawn = loc;
                player.sendMessage(prefix + "§aLe point de spawn du lobby a été défini en :" +
                        "           §fX: §b" + loc.getBlockX() + " §fY: §b" + loc.getBlockY() + " §fZ: §b" + loc.getBlockZ() +
                        " §fYaw: §b" + loc.getYaw() + " §fPitch: §b" + loc.getPitch());
                player.playSound(player.getLocation(), Sound.ENTITY_CAT_AMBIENT, 0.5f, 1.0f);
            }
            return true;
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("setlevelspawn")) {
            if (gameStarted == true) {
                player.sendMessage(prefix + "§4§l[ERROR]: §r§cImpossible d'ajouter un niveau pendant qu'une partie est en cours.");
                player.playSound(player.getLocation(), Sound.ENTITY_WOLF_GROWL, 0.5f, 1.25f);
            } else {
                String levelName = args[1];
                Location loc = player.getLocation();

                config.set("levels." + levelName + ".spawn.world", loc.getWorld().getName());
                config.set("levels." + levelName + ".spawn.x", loc.getX());
                config.set("levels." + levelName + ".spawn.y", loc.getY());
                config.set("levels." + levelName + ".spawn.z", loc.getZ());
                config.set("levels." + levelName + ".spawn.yaw", loc.getYaw());
                config.set("levels." + levelName + ".spawn.pitch", loc.getPitch());

                plugin.saveConfig();

                player.sendMessage(prefix + "§aLe point de spawn du niveau §c§l" + levelName + "§r§a a été défini aux coordonnées : " +
                        "§fX: §b" + loc.getBlockX() + " §fY: §b" + loc.getBlockY() + " §fZ: §b" + loc.getBlockZ() +
                        " §fYaw: §b" + loc.getYaw() + " §fPitch: §b" + loc.getPitch());
                player.playSound(player.getLocation(), Sound.ENTITY_CAT_AMBIENT, 0.5f, 1.0f);
            }
            return true;
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("removelevel")) {
            String levelName = args[1];

            if (!config.contains("levels." + levelName)) {
                player.sendMessage(prefix + "§cLe niveau §l" + levelName + "§r§c n'existe pas !");
                return true;
            }

            if (gameStarted == true) {
                player.sendMessage(prefix + "§4§l[ERROR]: §r§cImpossible de supprimer un niveau pendant qu'une partie est en cours.");
            } else {
                config.set("levels." + levelName, null);
                plugin.saveConfig();
                player.sendMessage(prefix + "§aLe niveau §c§l" + levelName + "§r§a a été supprimé.");
            }
            player.playSound(player.getLocation(), Sound.ENTITY_WOLF_GROWL, 0.5f, 1.25f);
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
            if (!config.contains("levels") || config.getConfigurationSection("levels").getKeys(false).isEmpty()) {
                player.sendMessage(prefix + "§c Il n'y a actuellement aucun niveau. Utilise la commande §n/gravity setlevelspawn <nom>§r§c pour en créer un.");
                return true;
            }

            player.sendMessage(prefix + "§aListe des niveaux disponibles :");

            for (String levelName : config.getConfigurationSection("levels").getKeys(false)) {
                String world = config.getString("levels." + levelName + ".spawn.world", "Inconnu");

                if(world.contains("nether")) {
                    world = "Nether";
                } else if (world.contains("the_end")) {
                    world = "The End";
                } else {
                    world = "Overworld";
                }
                if (config.contains("levels." + levelName + ".spawn")) {
                    double x = config.getDouble("levels." + levelName + ".spawn.x");
                    double y = config.getDouble("levels." + levelName + ".spawn.y");
                    double z = config.getDouble("levels." + levelName + ".spawn.z");

                    player.sendMessage(" §7- §e" + levelName + " §7(§f" + world + "§7) : §bX=" + x + " Y=" + y + " Z=" + z);
                } else {
                    player.sendMessage(" §7- §e" + levelName + " §7(§f" + world + "§7) : §cAucune position définie !");
                }
            }
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("start")) {
            if(gameStarted == true) {
                player.sendMessage(prefix + "§cUne partie est déjà en cours.");
            } else {
                if (!config.contains("levels") | config.getConfigurationSection("levels").getKeys(false).isEmpty()) {
                    player.sendMessage(prefix + "§4§l[ERROR]: §r§cAucun niveau n'est défini");
                } else {
                    this.playersInGame = new ArrayList<>();
                    playersInGame.clear();
                    playersInGame.addAll(Bukkit.getOnlinePlayers());
                    gameStarted = true;
                    LevelList.clear();
                    LevelList.addAll(config.getConfigurationSection("levels").getKeys(false));

                    System.out.println("[GRAVITY] Début de la partie avec les niveaux suivants : " + LevelList);
                    GravityListener.UpdateGameStatue(gameStarted);

                    String firstLevel = LevelList.get(0);

                    if (!config.contains("levels." + firstLevel + ".spawn")) {
                        player.sendMessage(prefix + "§4Error: §cLe niveau " + firstLevel + " n'a pas de spawn définie !");
                        return true;
                    }

                    String worldName = config.getString("levels." + firstLevel + ".spawn.world");
                    World world = Bukkit.getWorld(worldName);

                    if (world == null) {
                        player.sendMessage(prefix + "§4Error: §cLe monde du niveau " + firstLevel + " n'existe pas ou est introuvable !");
                        return true;
                    }

                    double x = config.getDouble("levels." + firstLevel + ".spawn.x");
                    double y = config.getDouble("levels." + firstLevel + ".spawn.y");
                    double z = config.getDouble("levels." + firstLevel + ".spawn.z");
                    float yaw = (float) config.getDouble("levels." + firstLevel + ".spawn.yaw");
                    float pitch = (float) config.getDouble("levels." + firstLevel + ".spawn.pitch");

                    Location startLocation = new Location(world, x, y, z, yaw, pitch);
                    for (Player p : playersInGame) {
                        playerLevels.put(p, firstLevel);
                        p.teleport(startLocation);
                        p.setGameMode(GameMode.ADVENTURE);
                        p.sendMessage(prefix + "§aDébut de la partie.");
                    }

                }
            }
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
            player.sendMessage(prefix + "§aListe des commandes :");
            player.sendMessage(" §7- §e/gravity setspawn §f: Définir le point de spawn du lobby");
            player.sendMessage(" §7- §e/gravity setlevelspawn <nom> §f: Définir le point de spawn d'un niveau");
            player.sendMessage(" §7- §e/gravity removelevel <nom> §f: Supprimer un niveau");
            player.sendMessage(" §7- §e/gravity start §f: Démarre une partie");
            player.sendMessage(" §7- §e/gravity list §f: Affiche la liste des niveaux");
            return true;
        }

        player.sendMessage(prefix + "§cCommande inconnue. Pour obtenir de l'aide veuillez utiliser: /gravity help");
        return false;
    }

    public static void TeleportBack(Player player, JavaPlugin plugin) {
        FileConfiguration config = plugin.getConfig();
        if (!playerLevels.containsKey(player)) {
            playerLevels.put(player, LevelList.get(0));
        }
        if (!config.contains("levels." + playerLevels.get(player) + ".spawn")) {
            player.sendMessage("§4Error: §cLe niveau n'est pas défini.");
            return;
        }
        World world = Bukkit.getWorld(config.getString("levels." + playerLevels.get(player) + ".spawn.world"));
        double x = config.getDouble("levels." + playerLevels.get(player) + ".spawn.x");
        double y = config.getDouble("levels." + playerLevels.get(player) + ".spawn.y");
        double z = config.getDouble("levels." + playerLevels.get(player) + ".spawn.z");
        float yaw = (float) config.getDouble("levels." + playerLevels.get(player) + ".spawn.yaw");
        float pitch = (float) config.getDouble("levels." + playerLevels.get(player) + ".spawn.pitch");
        if (world == null) {
            player.sendMessage("§4Error: §cLe monde du niveau n'existe pas ou est introuvable");
            return;
        }

        Location spawnLocation = new Location(world, x, y ,z ,yaw, pitch);
        player.teleport(spawnLocation);

    }

    public static void TeleportNextLevel(Player player, JavaPlugin plugin) {
        FileConfiguration config = plugin.getConfig();
        if (!playerLevels.containsKey(player)) {
            playerLevels.put(player, LevelList.get(0));
        }

        String currentLevel = playerLevels.get(player);
        int currentIndex = LevelList.indexOf(currentLevel);

        if (currentIndex == -1) {
            player.sendMessage("§4Error: §cCe niveau ne peut pas être trouvé !");
            return;
        }

        int nextIndex = currentIndex + 1;

        if (nextIndex < LevelList.size()) {
            String nextLevel = LevelList.get(nextIndex);
            playerLevels.put( player, nextLevel);

            if (!config.contains("levels." + nextLevel + ".spawn")) {
                player.sendMessage("§4Error: §cLe niveau " + nextLevel + " n'a pas de position définie !");
                return;
            }

            String worldName = config.getString("levels." + nextLevel + ".spawn.world");
            World world = Bukkit.getWorld(worldName);

            if (world == null) {
                player.sendMessage("§4Error: §cLe monde du niveau " + nextLevel + " n'existe pas ou est introuvable.");
                return;
            }

            double x = config.getDouble("levels." + nextLevel + ".spawn.x");
            double y = config.getDouble("levels." + nextLevel + ".spawn.y");
            double z = config.getDouble("levels." + nextLevel + ".spawn.z");
            float yaw = (float) config.getDouble("levels." + nextLevel + ".spawn.yaw");
            float pitch = (float) config.getDouble("levels." + nextLevel + ".spawn.pitch");

            Location nextSpawn = new Location(world, x, y, z, yaw, pitch);
            player.sendMessage("§7[§d§lGravity§r§7]§f §aVous avez réussie le niveau ! Téléportation vers le prochain niveau...");
            player.teleport(nextSpawn);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1f);
        } else {
            playerLevels.remove(player);
            playersInGame.remove(player);

            if (!config.contains("lobby.spawn")) {
                player.sendMessage("§4Error: §cLe point de spawn du lobby n'est pas défini !");
                return;
            }

            String worldName = config.getString("lobby.spawn.world");
            World world = Bukkit.getWorld(worldName);

            if (world == null) {
                player.sendMessage("§4Error: §cLe monde du lobby n'existe pas ou est introuvable.");
                return;
            }

            double x = config.getDouble("lobby.spawn.x");
            double y = config.getDouble("lobby.spawn.y");
            double z = config.getDouble("lobby.spawn.z");
            float yaw = (float) config.getDouble("lobby.spawn.yaw");
            float pitch = (float) config.getDouble("lobby.spawn.pitch");

            Location lobbySpawn = new Location(world, x, y ,z ,yaw, pitch);
            player.teleport(lobbySpawn);
            player.sendMessage("§7[§d§lGravity§r§7]§f §6Félicitations ! Vous avez terminé la partie !");
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1f);

            if (playersInGame.isEmpty()) {
                gameStarted = false;
                GravityListener.UpdateGameStatue(gameStarted);
                Bukkit.broadcastMessage("§7[§d§lGravity§r§7]§f §aLa partie est terminée !");
            }
        }
    }
}
