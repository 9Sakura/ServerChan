package ai.kaze.ServerChan.CommandDispatcher;

import ai.kaze.ServerChan.ServerChanPlugin;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.permissions.ServerOperator;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class QQCommandDispatcher {
    public ServerChanPlugin plugin;
    Object server;
    private final Logger logger = Logger.getLogger("Minecraft");
    private final Random random = new Random();

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
        if (players.length == 0) {
            plugin.qqBotServer.sendMessage(String.format("[CQ:at,qq=%s] 还没有绑定哦，无法发送信息哦！", sender));
            return;
        }
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
        plugin.temporaryDatabase.checkExpired();
        if (payloads.length == 0) {
            plugin.qqBotServer.sendMessage(String.format("[CQ:at,qq=%s] 请使用 /bind [验证码] 来完成绑定哦！\n验证码需要在游戏内获得。", sender));
            return;
        }
        Map<UUID, String> lucky = plugin.temporaryDatabase.getLuckyPair(sender);
        if (lucky.size() == 0) {
            plugin.qqBotServer.sendMessage(String.format("[CQ:at,qq=%s] 还没有在游戏中申请绑定哦！", sender));
            return;
        }

        List<String> names = new ArrayList<>();

        lucky.forEach((uuid, s) -> {
            if (s.equals(payloads[0])) {
                plugin.persistDatabase.set(uuid, sender);
                Player player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    player.sendMessage(String.format("[服务器娘] 您与 %s 绑定成功了哦～", sender));
                }
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
                names.add(offlinePlayer.getName());
            }
        });

        plugin.qqBotServer.sendMessage(String.format("[CQ:at,qq=%s] 您与 %s 绑定成功了哦～", sender, String.join("，", names)));
    }

    @CommandHandler(command = "unbind")
    public void onUnbind(String sender, String[] payloads) {
        // FIXME: /unbind 和 /unbind confirm
    }

    @CommandHandler(command = "opcmd", op = true)
    public void onOpCommand(String sender, String[] payloads) {
        // FIXME: /opcmd
    }

    @CommandHandler(command = "d4")
    public void onD4(String sender, String[] payloads) {
        plugin.qqBotServer.sendMessage(String.format("[CQ:at,qq=%s] 您掷出 %d 点!", sender, random.nextInt(4) + 1));
    }

    @CommandHandler(command = "d6")
    public void onD6(String sender, String[] payloads) {
        plugin.qqBotServer.sendMessage(String.format("[CQ:at,qq=%s] 您掷出 %d 点!", sender, random.nextInt(6) + 1));
    }

    @CommandHandler(command = "d8")
    public void onD8(String sender, String[] payloads) {
        plugin.qqBotServer.sendMessage(String.format("[CQ:at,qq=%s] 您掷出 %d 点!", sender, random.nextInt(8) + 1));
    }

    @CommandHandler(command = "d10")
    public void onD10(String sender, String[] payloads) {
        plugin.qqBotServer.sendMessage(String.format("[CQ:at,qq=%s] 您掷出 %d 点!", sender, random.nextInt(10) + 1));
    }

    @CommandHandler(command = "d12")
    public void onD12(String sender, String[] payloads) {
        plugin.qqBotServer.sendMessage(String.format("[CQ:at,qq=%s] 您掷出 %d 点!", sender, random.nextInt(12) + 1));
    }

    @CommandHandler(command = "d20")
    public void onD20(String sender, String[] payloads) {
        plugin.qqBotServer.sendMessage(String.format("[CQ:at,qq=%s] 您掷出 %d 点!", sender, random.nextInt(20) + 1));
    }

    @CommandHandler(command = "roll")
    public void onRoll(String sender, String[] payloads) {
        try {
            if (payloads.length == 0) {
                throw new Exception();
            }
            String upper = payloads[0];
            long upperLong = Long.parseLong(upper, 10);
            if (upperLong < 1 || upperLong > 114514) {
                throw new Exception();
            }
            plugin.qqBotServer.sendMessage(String.format("[CQ:at,qq=%s] 您掷出 %d 点!", sender, random.nextInt(Math.toIntExact(upperLong)) + 1));
        } catch (Exception e) {
            plugin.qqBotServer.sendMessage(String.format("[CQ:at,qq=%s] roll 的用法是\"/roll 上限数\"，上限不能超过 114514 并且不能小于 1 哦！", sender));
        }
    }

    @CommandHandler(command = "help")
    public void onHelp(String sender, String[] payloads) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("是服务器娘哦！\n");
        stringBuilder.append("服务器娘当前可以做这些事：\n");
        stringBuilder.append("    /d4            投掷四面骰\n");
        stringBuilder.append("    /d6            投掷六面骰\n");
        stringBuilder.append("    /d8            投掷八面骰\n");
        stringBuilder.append("    /d10           投掷十面骰\n");
        stringBuilder.append("    /d12           投掷十二面骰\n");
        stringBuilder.append("    /d20           投掷二十面骰\n");
        stringBuilder.append("    /roll n        投掷n面骰（n≥1且n≤114514）\n");
        stringBuilder.append("    /tps           获取服务器TPS\n");
        stringBuilder.append("    /list          查看服务器在线人员列表\n");
        stringBuilder.append("    /bind x        使用验证码x与游戏内角色绑定\n");
        stringBuilder.append("    /unbind        撤销绑定\n");
        stringBuilder.append("    /say           发送信息到游戏内");
        plugin.qqBotServer.sendMessage(stringBuilder.toString());
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
