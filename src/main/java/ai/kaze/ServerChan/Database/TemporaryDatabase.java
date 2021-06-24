package ai.kaze.ServerChan.Database;

import org.mapdb.DB;
import org.mapdb.DBMaker;

import java.util.*;
import java.util.concurrent.ConcurrentMap;

public class TemporaryDatabase implements Database {
    DB db;
    ConcurrentMap map;

    @Override
    public void open() {
        if (db != null && !db.isClosed()) {
            close();
        }
        db = DBMaker.memoryDB().make();
        map = db.hashMap("map").createOrOpen();
    }

    @Override
    public void close() {
        db.close();
        db = null;
    }

    @Override
    public void set(Object key, Object value) {
        map.put(key, value);
    }

    @Override
    public void remove(Object key) {
        map.remove(key);
    }

    @Override
    public Object get(Object key, Object def) {
        return map.getOrDefault(key, def);
    }

    public void checkExpired() {
        List<UUID> uuids = new ArrayList<>();
        map.forEach((o, o2) -> {
            Map<String, String> value = (Map<String, String>) o2;
            String ddl = value.getOrDefault("deadline", "0");
            long deadline = Long.parseLong(ddl);
            if ((deadline / 1000) < (System.currentTimeMillis() / 1000)) {
                uuids.add((UUID) o);
            }
        });
        uuids.forEach(uuid -> map.remove(uuid));
    }

    public Map<UUID, String> getLuckyPair(String qq) {
        Map<UUID, String> result = new HashMap<>();
        map.forEach((o, o2) -> {
            if (o instanceof UUID uuid && o2 instanceof Map map) {
                String q = (String) map.get("qq");
                if (q.equals(qq)) {
                    result.put(uuid, (String) map.get("lucky"));
                }
            }
        });
        return result;
    }
}
