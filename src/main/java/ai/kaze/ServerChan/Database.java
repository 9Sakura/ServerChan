package ai.kaze.ServerChan;

public class Database {
    static private final Database database = new Database();

    static public Database getInstance() {
        return database;
    }

    private Database() {

    }

    public String getPlayerFromQQ(String qq) {
        return "";
    }

    public String getQQFromPlayer(String player) {
        return "";
    }
}
