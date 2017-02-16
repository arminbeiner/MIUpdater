package models;

import java.sql.*;

/**
 * Created by Armin on 16.01.2017.
 */
public class Server {

    /**
     * Erstellung der benötigten Variablen sowie der getter / setter
     */
    private String hostname;
    private String IP;
    private String Type;
    private String name_customer;
    private int id;

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getName_customer() {
        return name_customer;
    }

    public void setName_customer(String name_customer) {
        this.name_customer = name_customer;
    }


    /**
     * server in datebank laden
     * @param server server
     * @throws SQLException
     * Vorbereitung eines Prepared Statements zum Hinzufügen eines neuen Servers zur Datenbank.
     * Hostname, IP und Type werden über getHostname, getIP und getType Methoden ausgelesen.
     * Um die ID des Customers auszulesen und den Server dem korrekten Kunden zuzuweisen, wurde ein eigenes SQL Statement definiert (stategetNames)
     * */
    public void addServerToDB(Server server) throws SQLException {

        Connection connAddServer = DriverManager.getConnection(DBCredentials.getUrl(), DBCredentials.getUser(), DBCredentials.getPass());        //Verbindung zur lokalen Datenbank

        Statement stategetNames = connAddServer.createStatement();
        ResultSet resultSetgetID = stategetNames.executeQuery("SELECT id FROM customer WHERE name = '" + getName_customer() + "'");     // Auslesen der ID des im Dropdown Feld ausgewählten Kunden

        /**
         * Aufbereitung des Resultset mit der ID des ausgewählten Kunden
         */
        while(resultSetgetID.next()) {
            int id = resultSetgetID.getInt(1);
            this.id = id;
        }

        /**
         * Vorbereitung Prepared Statement um Server hinzuzufügen.
         */
        PreparedStatement prepStateAddServer=
                connAddServer.prepareStatement("INSERT INTO server(hostname, ip, type, id_customer) VALUES (?, ?, ?, ?)");      //prepared Statement um neue Werte zum server table hinzuzufügen
        prepStateAddServer.setString(1, server.getHostname());      //get Hostname Value
        prepStateAddServer.setString(2, server.getIP());            //get Server IP Value
        prepStateAddServer.setString(3, server.getType());          //get Server Type Value
        prepStateAddServer.setInt(4, id);                           //get ID from selected Customer

        prepStateAddServer.executeUpdate();   //Ausführung des prepared Statement

        stategetNames.close();          //close Statement getNames
        prepStateAddServer.close();     //close prepared Statement AddServer
        connAddServer.close();          //close Connection to the Database
    }

    /**
     * server aus datenbank loeschen
     * @param removeServer server zum loeschen
     * @throws SQLException
     * Vorbereitung eines Prepared Statement um einen Server aus der DB zu löschen
     * Die Entfernung des Servers wird anhand des Hostnames definiert
     * Auslesen des ausgewählten Hostname über getHostname
     */
    public void removeServerfromDB(Server removeServer) throws SQLException {

        Connection connrmServer = DriverManager.getConnection(DBCredentials.getUrl(), DBCredentials.getUser(), DBCredentials.getPass());     //Verbindung zur lokalen Datenbank
        PreparedStatement prepStatermServer =
                connrmServer.prepareStatement("DELETE FROM server WHERE hostname = ?");     //prepared Statement um einen Server aus der Datenbank zu löschen
        prepStatermServer.setString(1, removeServer.getHostname());   //get Hostname of chosen Server

        prepStatermServer.executeUpdate();      //Ausführung des prepared Statement

        prepStatermServer.close();      //close prepared Statement rmServer
        connrmServer.close();       //close Connection to the Database
    }

}
