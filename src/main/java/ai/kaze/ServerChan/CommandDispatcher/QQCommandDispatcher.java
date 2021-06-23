package ai.kaze.ServerChan.CommandDispatcher;

import ai.kaze.ServerChan.Database;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Function;

public class QQCommandDispatcher extends CommandDispatcher {
    Object server = null;

    public QQCommandDispatcher() {
        try {
            Method getServer = Bukkit.getServer().getClass().getMethod("getServer");
            server = getServer.invoke(Bukkit.getServer());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @CommandHandler(command = "say")
    public void onSay(String sender, String content) {
        String qq = Database.getInstance().getQQFromPlayer(sender);
        Bukkit.getServer().broadcastMessage("");
        // TODO: 发送
    }

    @CommandHandler(command = "tps")
    public void OnTPS(String sender) {
        if (server == null) {
            // TODO: 发送
            return;
        }
        try {
            Field recentTps = server.getClass().getField("recentTps");
            double[] tps = (double[]) recentTps.get(server);
            // TODO: 发送
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @CommandHandler(command = "list")
    public void onList(String sender) {
        String[] onlinePlayers = (String[]) Bukkit
                .getOnlinePlayers()
                .stream()
                .map((Function<Player, String>) HumanEntity::getName)
                .toArray();
        String finalList = String.join("，", onlinePlayers);
        // TODO: 发送回去
    }

    @CommandHandler(command = "bind")
    public void onBind(String sender, String secret) {

    }

    @CommandHandler(command = "unbind")
    public void onUnbind(String sender) {

    }

    @CommandHandler(command = "opcmd", op = true)
    public void onOpCommand(String sender, String command) {

    }

    @Override
    boolean isOp(String user) {
        return false;
    }
}
