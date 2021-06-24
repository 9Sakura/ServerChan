package ai.kaze.ServerChan.CommandDispatcher;

import ai.kaze.ServerChan.ServerChanPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Logger;

public class MinecraftCommandDispatcher {
    public ServerChanPlugin plugin;

    private final Logger logger = Logger.getLogger("Minecraft");

    @CommandHandler(command = "qqsay")
    public void onQQSay(CommandSender sender, String[] payloads) {
        if (sender instanceof Player player) {
            UUID uuid = player.getUniqueId();
            String qq = (String) plugin.persistDatabase.get(uuid, "0");
            plugin.qqBotServer.sendMessage(String.format("[CQ:at,qq=%s] 在游戏中说：%s", qq, String.join(" ", payloads)));
        } else if (sender instanceof ConsoleCommandSender) {
            plugin.qqBotServer.sendMessage("服务器娘说：" + String.join(" ", payloads));
        }
    }

    // TODO: 彩色字
    @CommandHandler(command = "bind")
    public void onBindQQ(CommandSender sender, String[] payloads) {
        plugin.temporaryDatabase.checkExpired();
        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage("控制台不需要绑定 QQ 号");
        } else if (sender instanceof Player player) {
            UUID uuid = player.getUniqueId();
            if (payloads.length == 0) {
                player.sendMessage("[服务器娘] 搞不懂你要做什么呢……");
                return;
            }
            if (payloads[0].equals("cancel")) {
                plugin.temporaryDatabase.remove(uuid);
                player.sendMessage("[服务器娘] 已经取消绑定操作啦！");
                return;
            }
            String qq = payloads[0];
            boolean isValid = qq.length() >= 6 &&
                    qq.length() <= 10 &&
                    qq.charAt(0) != '0' &&
                    qq.chars().mapToObj(c -> c >= '0' && c <= '9').reduce(true, (a, b) -> a && b);
            if (!isValid) {
                player.sendMessage("[服务器娘] QQ 号码似乎不对呢……");
                return;
            }
            {
                Map<String, String> value = (Map<String, String>) plugin.temporaryDatabase.get(uuid, null);
                if (value != null) {
                    String deadline = value.get("deadline");
                    // FIXME: 比较 ddl 剩余时间，比较 QQ 号
                }
            }
            String luckyString = getRandomString();
            long ddl = System.currentTimeMillis() + 1000 * 60 * 5;
            Map<String, String> value = new HashMap<>();
            value.put("qq", payloads[1]);
            value.put("lucky", luckyString);
            value.put("deadline", ((Long) ddl).toString());
            plugin.temporaryDatabase.set(uuid, value);
            player.sendMessage(String.format("[服务器娘] 您正在请求绑定 QQ 号，请在群中发送 /bind %s 信息来完成绑定，该信息五分钟内有效。", luckyString));
        }
    }

    @CommandHandler(command = "unbind")
    public void onUnbind(CommandSender sender, String[] payloads) {
        plugin.temporaryDatabase.checkExpired();
        // FIXME: /unbind 和 /unbind confirm
    }

    @CommandHandler(command = "forceunbind", op = true)
    public void onForceUnbind(CommandSender sender, String[] payloads) {
        plugin.temporaryDatabase.checkExpired();
        //FIXME: /forceunbind 和 /forceunbind confirm
    }

    public boolean dispatchCommand(CommandSender sender, String command, String[] parameters) {
        Method[] methods = this.getClass().getMethods();
        boolean isOp = sender.isOp();
        return Arrays.stream(methods).filter(method -> {
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
        }).map(method -> {
            try {
                method.invoke(this, sender, parameters);
                return true;
            } catch (IllegalAccessException | InvocationTargetException e) {
                logger.severe(e.getMessage());
            }
            return false;
        }).reduce((a, b) -> a || b).orElse(false);
    }

    private String getRandomString() {
        String chars = "abcdefghijkmnpqrstwxyz1234567890";
        StringBuilder randomString = new StringBuilder();
        Random rnd = new Random();
        while (randomString.length() < 7) {
            int index = (int) (rnd.nextFloat() * chars.length());
            randomString.append(chars.charAt(index));
        }
        return randomString.toString();
    }
}
