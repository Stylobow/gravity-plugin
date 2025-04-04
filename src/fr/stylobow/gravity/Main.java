package fr.stylobow.gravity;

import fr.stylobow.gravity.Commands.GravityCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        System.out.println("Démarrage du plugin Gravity.");
        getServer().getPluginManager().registerEvents(new GravityListener(this), this);
        getCommand("gravity").setExecutor(new GravityCommand(this));
    }


    @Override
    public void onDisable() {
        System.out.println("Arrêt du plugin Gravity.");
    }

}
