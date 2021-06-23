package ai.kaze.ServerChan;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.logging.Logger;

public class ServerChanPlugin extends JavaPlugin {
    final Logger logger = Logger.getLogger("Minecraft");
    private QQBotServer server;

    @Override
    public void onLoad() {
        super.onLoad();
        saveDefaultConfig();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (server != null) {
            try {
                server.stop();
            } catch (IOException | InterruptedException e) {
                logger.severe(e.getMessage());
                return;
            }
        }
        server = new QQBotServer(getConfig().getInt("port", 10000));
        server.plugin = this;
        server.start();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (server != null) {
            try {
                server.stop();
                server = null;
            } catch (IOException | InterruptedException e) {
                logger.severe(e.getMessage());
            }
        }
    }

    public void dispatchCommand(String command) {

    }
}
