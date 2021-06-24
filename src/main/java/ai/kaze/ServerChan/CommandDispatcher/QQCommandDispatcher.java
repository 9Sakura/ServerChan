package ai.kaze.ServerChan.CommandDispatcher;

import ai.kaze.ServerChan.ServerChanPlugin;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.permissions.ServerOperator;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class QQCommandDispatcher {
    public ServerChanPlugin plugin;
    Object server;
    private final Logger logger = Logger.getLogger("Minecraft");

    public QQCommandDispatcher() {
        try {
            Method getServer = Bukkit.getServer().getClass().getMethod("getServer");
            server = getServer.invoke(Bukkit.getServer());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @CommandHandler(command = "say")
    public void onSay(String sender, String[] payloads) {
        OfflinePlayer[] players = plugin.persistDatabase.getPlayersFromQQ(sender);
        Bukkit.getServer().broadcastMessage(String.format("%s 说：%s", Arrays.stream(players).map(OfflinePlayer::getName).collect(Collectors.joining(", ")), String.join(" ", payloads)));
    }

    @CommandHandler(command = "tps")
    public void OnTPS(String sender, String[] payloads) {
        if (server == null) {
            plugin.qqBotServer.sendMessage("无法获取当前服务器的 TPS 呢……");
            return;
        }
        try {
            Field recentTps = server.getClass().getField("recentTps");
            double[] tps = (double[]) recentTps.get(server);
            if (tps.length < 3) {
                plugin.qqBotServer.sendMessage("无法获取当前服务器的 TPS 呢……");
                return;
            }
            plugin.qqBotServer.sendMessage(String.format("服务器1分钟、5分钟和15分钟的平均TPS分别为：%.2f %.2f %.2f", tps[0], tps[1], tps[2]));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @CommandHandler(command = "list")
    public void onList(String sender, String[] payloads) {
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        String result = players.stream()
                .map((Function<Player, String>) player -> {
                    String qq = (String) plugin.persistDatabase.get(player.getUniqueId(), "");
                    if (qq.isEmpty()) {
                        return player.getName();
                    }
                    return String.format("%s（[CQ:at,qq=%s]）", player.getName(), qq);
                })
                .collect(Collectors.joining("，"));
        plugin.qqBotServer.sendMessage(String.format("服务器当前在线人数有 %d 人：\n%s", players.size(), result));
    }

    @CommandHandler(command = "bind")
    public void onBind(String sender, String[] payloads) {
        // FIXME: /bind 随机码
    }

    @CommandHandler(command = "unbind")
    public void onUnbind(String sender, String[] payloads) {
        // FIXME: /unbind 和 /unbind confirm
    }

    @CommandHandler(command = "opcmd", op = true)
    public void onOpCommand(String sender, String[] payloads) {
        // FIXME: /opcmd
    }

    public void dispatchCommand(String sender, String command, String[] parameters) {
        Method[] methods = this.getClass().getMethods();
        boolean isOp = isOp(sender);
        Arrays.stream(methods).filter(method -> {
            if (method.isAnnotationPresent(CommandHandler.class)) {
                CommandHandler cmd = method.getAnnotation(CommandHandler.class);
                if (cmd.command().equals(command)) {
                    if (cmd.op()) {
                        return isOp;
                    } else {
                        return true;
                    }
                }
            }
            return false;
        }).forEach(method -> {
            try {
                method.invoke(this, sender, parameters);
            } catch (IllegalAccessException | InvocationTargetException e) {
                logger.severe(e.getMessage());
            }
        });
    }

    boolean isOp(String user) {
        OfflinePlayer[] players = plugin.persistDatabase.getPlayersFromQQ(user);
        return Arrays.stream(players).map(ServerOperator::isOp).reduce((a, b) -> a || b).orElse(false);
    }
}
