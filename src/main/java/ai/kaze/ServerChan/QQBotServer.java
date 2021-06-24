package ai.kaze.ServerChan;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.Arrays;

public class QQBotServer extends WebSocketServer {
    ServerChanPlugin plugin;

    QQBotServer(int port) {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        plugin.getLogger().info("WebSocket 客户端已连接");
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        plugin.getLogger().info("WebSocket 客户端已关闭");
    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {
        JsonElement json = new Gson().fromJson(s, JsonElement.class);
        try {
            JsonObject jsonObject = json.getAsJsonObject();
            int groupId = jsonObject.get("group_id").getAsInt();
            if (plugin.getConfig().getInt("group", -1) != groupId) {
                return;
            }
            String user = ((Integer) jsonObject.get("user_id").getAsInt()).toString();
            String message = jsonObject.get("message").getAsString();
            String[] splits = Arrays.stream(message.split(" ")).toArray(String[]::new);
            if (splits.length == 0) {
                return;
            }
            String command = splits[0];
            splits = Arrays.stream(splits).skip(1).toArray(String[]::new);
            if (command.startsWith("/")) {
                command = command.substring(1);
                plugin.qqCommandDispatcher.dispatchCommand(user, command, splits);
            }
        } catch (NullPointerException | IllegalStateException | IndexOutOfBoundsException | UnsupportedOperationException ignored) {

        }
    }

    public void sendMessage(String message) {
        int groupId = plugin.getConfig().getInt("group", -1);
        JsonObject params = new JsonObject();
        params.addProperty("group_id", groupId);
        params.addProperty("message", message);
        JsonObject root = new JsonObject();
        root.addProperty("action", "send_group_msg");
        root.add("params", params);
        this.broadcast(new Gson().toJson(root));
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        plugin.getLogger().severe("WebSocket 客户端遇到错误：" + e.getMessage());
    }

    @Override
    public void onStart() {
        plugin.getLogger().info("WebSocket 服务器已启动");
    }
}
