package ai.kaze.ServerChan;

import ai.kaze.ServerChan.CommandDispatcher.MinecraftCommandDispatcher;
import ai.kaze.ServerChan.CommandDispatcher.QQCommandDispatcher;
import ai.kaze.ServerChan.Database.PersistDatabase;
import ai.kaze.ServerChan.Database.TemporaryDatabase;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.logging.Logger;

public class ServerChanPlugin extends JavaPlugin {
    final Logger logger = Logger.getLogger("Minecraft");
    final QQCommandDispatcher qqCommandDispatcher = new QQCommandDispatcher();
    final MinecraftCommandDispatcher minecraftCommandDispatcher = new MinecraftCommandDispatcher();
    final public PersistDatabase persistDatabase = new PersistDatabase();
    final public TemporaryDatabase temporaryDatabase = new TemporaryDatabase();

    public QQBotServer qqBotServer;

    @Override
    public void onLoad() {
        super.onLoad();
        saveDefaultConfig();
        qqCommandDispatcher.plugin = this;
        minecraftCommandDispatcher.plugin = this;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (qqBotServer != null) {
            try {
                qqBotServer.stop();
            } catch (IOException | InterruptedException e) {
                logger.severe(e.getMessage());
                return;
            }
        }
        qqBotServer = new QQBotServer(getConfig().getInt("port", 443));
        qqBotServer.plugin = this;
        qqBotServer.start();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (qqBotServer != null) {
            try {
                qqBotServer.stop();
                qqBotServer = null;
            } catch (IOException | InterruptedException e) {
                logger.severe(e.getMessage());
            }
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return minecraftCommandDispatcher.dispatchCommand(sender, label, args);
    }
}
