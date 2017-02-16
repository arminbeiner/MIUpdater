package models;

/** datenbank zugangs daten
 * Created by asi on 29.01.2017.
 */
public class DBCredentials {

    private static String url = "jdbc:postgresql://localhost/MIUpdater";
    private static String user = "postgres";
    private static String pass = "postgres";

    public static String getUrl() {
        return url;
    }

    public static String getUser() {
        return user;
    }

    public static String getPass() {
        return pass;
    }
}
