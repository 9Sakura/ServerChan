package ai.kaze.ServerChan.Database;

public interface Database {
    void open();
    void close();
    void set(Object key, Object value);
    void remove(Object key);
    Object get(Object key, Object def);
}
