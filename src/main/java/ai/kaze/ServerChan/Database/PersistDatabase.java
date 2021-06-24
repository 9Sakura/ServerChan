package ai.kaze.ServerChan.Database;

import ai.kaze.ServerChan.ServerChanPlugin;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class PersistDatabase implements Database {
    ServerChanPlugin plugin;
    DB db;
    ConcurrentMap<UUID, String> map;

    @Override
    public void open() {
        db = DBMaker.fileDB(new File(plugin.getDataFolder(), "map.db")).fileMmapEnableIfSupported().make();
        map = db.hashMap("map", Serializer.UUID, Serializer.STRING).createOrOpen();
    }

    @Override
    public void close() {
        db.close();
    }

    @Override
    public void set(Object key, Object value) {
        map.put((UUID) key, (String) value);
    }

    @Override
    public void remove(Object key) {
        map.remove((UUID) key);
    }

    @Override
    public Object get(Object key, Object def) {
        return map.getOrDefault((UUID) key,(String) def);
    }

    public OfflinePlayer getPlayerFromQQ(String qq) {
        OfflinePlayer[] players = getPlayersFromQQ(qq);
        return players.length > 0 ? players[0] : null;
    }

    public OfflinePlayer[] getPlayersFromQQ(String qq) {
        List<OfflinePlayer> players = new ArrayList<>();
        OfflinePlayer[] offlinePlayers = Bukkit.getOfflinePlayers();
        map.forEach((uuid, s) -> {
            if (s.equals(qq)) {
                players.addAll(Arrays.stream(offlinePlayers).filter(player -> player.getUniqueId().equals(uuid)).collect(Collectors.toList()));
            }
        });
        return (OfflinePlayer[]) players.toArray();
    }
}