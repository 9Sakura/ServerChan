package ai.kaze.ServerChan.CommandDispatcher;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class MinecraftCommandDispatcher extends CommandDispatcher {
    @CommandHandler(command = "qqsay")
    public void onQQSay(String sender, String content) {
        Player player = Bukkit.getPlayer(sender);
    }

    @CommandHandler(command = "bind")
    public void onBindQQ(String sender, String qq) {

    }

    @CommandHandler(command = "unbind")
    public void onUnbind(String sender, String confirm) {

    }

    @CommandHandler(command = "forceunbind", op = true)
    public void onForceUnbind(String sender, String player, String confirm) {

    }

    @Override
    boolean isOp(String user) {
        return false;
    }
}
