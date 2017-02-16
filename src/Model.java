import models.CustomerResult;
import models.DBCredentials;

import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.logging.*;

/** das model
 * Created by asi on 27.12.2016.
 */
class Model {

  /** logger */
  private static Logger LOG = Logger.getLogger(Model.class.getName());
  private static Level loglevel = Level.ALL;

  private View view;

    private String date = new SimpleDateFormat("dd-MM-yyyy").format(new java.util.Date());
    private String date2 = new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date());

  Model(View view) {
      try {
          initLogger(date + " modelLogfile.txt");
      } catch (IOException e) {
          e.printStackTrace();
      }
      this.view = view;
  }

    private List<CustomerResult> customerResults = new ArrayList<>();

    /** hinzugefügt von Armin, ArrayList für Speicherung der Namen aus DB für Dropdown Field */
    private ArrayList<String> names;

    /**hinzugefügt von Armin, ArrayList für Speicherung der Typen aus DB für Dropdownd Field */
    private ArrayList<String> types;

    /** hinzugefügt von Armin, ArrayList für Speicherung der Server aus DB für Dropdown Field */
    private ArrayList<String> server;

    /** Anzeige dass keine Server für den Kunden vorhanden sind */
    private ArrayList<String> noServer;

    /** lädt alle kunden mit servern für die suche */
    public List<CustomerResult> getCustomerResults() throws SQLException {
        loadCustomers();
        return customerResults;
    }

    /** hinzugefügt von Armin, get Methode für Names */
    public ArrayList<String> getNames() throws SQLException {
        loadNames();
        return names;
    }

    /** hinzugefügt von Armin, get Methode für Server */
    public ArrayList<String> getServer() throws SQLException {
        loadServer(view.getValueCustomerField());
        return server;
    }

    /** hinzugefügt von Armin, get Methode für Type */
    public ArrayList<String> getType() {
        loadTypes();
        return types;
    }


    /**
     * methode für suchfeld im view
     * @param customerList customer liste
     * @param key suchkriterium
     * @return customer liste
     */
    public List<CustomerResult> findCustomers(List<CustomerResult> customerList, String key) {
        List<CustomerResult> resultCustomer = new ArrayList<>();
        for(CustomerResult customerResult : customerList) {
            if (customerResult.getName().toLowerCase().contains(key.toLowerCase())) {
                resultCustomer.add(customerResult);
            }
        }
        return resultCustomer;
    }

    /**
     * Methode loadNames liest alle Namen aus vom table Customer
     * Die Ergebnisse werden in eine ArrayList gespeichert. Diese wird benötigt, um das DropDown Field view.getCustomerNames zu bestücken.
     * Da hier direkt eine SQL Abfrage stattfindet, wird eine mögliche Exception weitergegeben. Diese wird beim Aufrufen der addServerBox mit try/catch abgefangen.
     */
    private void loadNames() throws SQLException {

        Connection conngetNames = DriverManager.getConnection(DBCredentials.getUrl(), DBCredentials.getUser(), DBCredentials.getPass()); //Connection to the local Database

        Statement stategetNames = conngetNames.createStatement();
        ResultSet resultSetgetNames = stategetNames.executeQuery("SELECT name FROM Customer");
        ResultSetMetaData NamesMD = resultSetgetNames.getMetaData();
        int columns = NamesMD.getColumnCount();

        ArrayList<String> names = new ArrayList<>(columns);

        while(resultSetgetNames.next()) {
            int i = 1;
            while(i <= columns) {
                names.add(resultSetgetNames.getString(i++));
            }
            this.names = names;
        }
        resultSetgetNames.close();
        stategetNames.close();
        conngetNames.close();
        LOG.info(date2 + " " + System.getProperty("user.name") + " hat Namen aus DB geladen");
    }

    /**
     * Methode loadTypes definiert die zwei Server Typen.
     * Die Ergebnisse werden in eine ArrayList gespeichert. Diese wird benötigt, um das Dropdown Field addServerTypeField zu definieren.
     */
    private void loadTypes() {
        ArrayList<String> types = new ArrayList<>();
        types.add(0, "VSP");
        types.add(1, "Sentry");
        this.types = types;
    }

    /**
     * Methode loadServer liest die verschiedenen Server für einen spezifischen Kunden aus.
     * Die Ergebnisse werden in eine ArrayList gespeichert. Diese wird benötigt, um das Dropdown Field view.getServerField zu definieren.
     * Da in diesem Dropdown Field jeweils nur die Server des ausgewählten Kunden angezeigt werden sollen, ist im Prepared Statement ein JOIN zwischen server und customer table.
     * Eine mögliche SQL Exception wird hier ebenfalls weitergeben. Diese wird beim Aufrufen der removeServerBox mit try/catch abgefangen.
     */
    private void loadServer(String name) throws SQLException {

        Connection conngetServer = DriverManager.getConnection(DBCredentials.getUrl(), DBCredentials.getUser(), DBCredentials.getPass()); //Connection to the local Database

        PreparedStatement prepStatermServer =
                conngetServer.prepareStatement("SELECT s.hostname FROM server s JOIN customer c ON (s.id_customer = c.id) WHERE c.name = ?"); //prepared Statement to insert new Values into customer table
        prepStatermServer.setString(1, name);

        ResultSet resultSetgetServer = prepStatermServer.executeQuery();
        ResultSetMetaData ServerMD = resultSetgetServer.getMetaData();
        int columns = ServerMD.getColumnCount();

        if (!resultSetgetServer.isBeforeFirst()) {
            ArrayList<String> server = new ArrayList<>();
            this.server = server;
        } else {

        ArrayList<String> server = new ArrayList<>(columns);

            while (resultSetgetServer.next()) {
                int i = 1;
                while (i <= columns) {
                    server.add(resultSetgetServer.getString(i++));
                }
                this.server = server;
            }
            resultSetgetServer.close();
            prepStatermServer.close();
            conngetServer.close();
            LOG.info(date2 + " " + System.getProperty("user.name") + " hat Server aus DB geladen");
        }
    }

    private void loadCustomers() throws SQLException {

        Connection conngetTypes = DriverManager.getConnection(DBCredentials.getUrl(), DBCredentials.getUser(), DBCredentials.getPass());

        Statement stategetTypes = conngetTypes.createStatement();
        ResultSet resultSetCustomer = stategetTypes.executeQuery("SELECT\n" +
                "  cust.name    AS name,\n" +
                "  srv.hostname AS host,\n" +
                "  srv.ip       AS ip,\n" +
                "  srv.type     AS type\n" +
                "FROM customer cust\n" +
                "  JOIN server srv ON srv.id_customer = cust.id");
        List<CustomerResult> customerResults = new ArrayList<>();
        while(resultSetCustomer.next()) {
            CustomerResult customerResult = new CustomerResult();
            customerResult.setName(resultSetCustomer.getString("name"));
            customerResult.setHostname(resultSetCustomer.getString("host"));
            customerResult.setIp(resultSetCustomer.getString("ip"));
            customerResult.setType(resultSetCustomer.getString("type"));
            customerResults.add(customerResult);
        }
        this.customerResults = customerResults;
        resultSetCustomer.close();
        stategetTypes.close();
        conngetTypes.close();
        LOG.info( date2 + " " + System.getProperty("user.name") + " hat Kunden aus DB geladen");
    }

        private static void initLogger(String filename) throws IOException {
        boolean append = true;
        Handler handler = new FileHandler(filename, append);
        handler.setFormatter(new SimpleFormatter());
        LOG.setLevel(loglevel);
        LOG.addHandler(handler);
    }

}
