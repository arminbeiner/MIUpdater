import models.Customer;
import models.Server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

/** der database controller
 * Created by asi on 27.12.2016.
 */
public class Controller implements ActionListener {

    /** controller logger */
    private static Logger LOG = Logger.getLogger(Controller.class.getName());
    private static Level loglevel = Level.ALL;

    private View view;
    private Model model;

    private String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
    private String date2 = new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date());

    /**
     * der controller für die daten interaction
     * @param view zeigt die daten
     * @param model arbeiten mit den daten
     */
    Controller(View view, Model model) {
        this.view = view;
        this.model = model;

        try {
            initLogger(date + "controllerLogfile.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void actionPerformed(ActionEvent e) {
        /**
         * Beim klicken auf Button "Add Customer" wird die addCustomerBox aufgerufen
         */
        if(e.getActionCommand().equals("Add Customer")) {
          view.addCustomerBox();
        }
        /**
         * Beim klicken auf Button "Add Server" wird die addServerBox aufgerufen.
         * addServerBox kann eine SQL Exception generieren, da das Dropdown Field zur Auswahl des Kunden mit einer SQL Abfrage verknüpft ist. Deshalb try/catch Block für SQLException
         */
        else if(e.getActionCommand().equals("Add Server")) {
            try {
                view.addServerBox();
            }catch(SQLException ex) {
                view.showErrorBox(ex.getMessage());
                LOG.severe(date2 + " " + System.getProperty("user.name") + " generierte Exception: " + ex.getMessage());
            }
        }
        /**
         * Beim klicken auf Button "Remove Server" wird die removeServerBox aufgerufen.
         * removeServerBox kann ebenfalls eine SQL Exception generieren, da das Dropdown Field zur Auswahl des Kunden / des Servers mit einer SQL Abfrage verknüpft ist. Deshalb try/catch Block für SQLException
         */
        else if(e.getActionCommand().equals("Remove Server")) {
            try {
                view.removeServerBox();
            }catch(SQLException ex){
                view.showErrorBox(ex.getMessage());
            }
        }
        /**
         * Beim klicken auf Button "Run" wird die runningBox aufgerufen
         */
        else if(e.getActionCommand().equals("Run")) {
            LOG.info(date2 + " " + System.getProperty("user.name") + " hat Run gestartet");
            view.runningBox();
        }
        else if(e.getActionCommand().equals("Cancel")){
            LOG.info("Ausführung abgebrochen durch " + System.getProperty("user.name"));
            view.runningFrame.dispose();
        }
        else if(e.getActionCommand().equals("Exit")) {
            exit();
        }
        /**
         * Beim klicken auf den "Add" Button innerhalb der addCustomerBox werden die benötigten Parameter aus den definierten Feldern ausgelesen.
         * Sollte ein Feld leer sein, wird eine ErrorBox generiert.
         * Mit den ausgelesenen Werten für das customer Objekt wird die Methode "addCustomertoDB" aufgerufen.
         * In der addCustomertoDB Methode wird das prepared Statement ausgeführt, deshalb try/catch Block für SQL und ClassNotFound Exception
         */
        else if(e.getActionCommand().equals("Add")) {
            Customer customer = new Customer();
            customer.setName(view.getAddCustomerNameTextField().getText());
            customer.setBch4(view.getAddCustomerBch4TextField().getText());
            if(view.getAddCustomerNameTextField().getText().isEmpty()){
                view.showErrorBox("Name Field empty!");
            }
            else if (view.getAddCustomerBch4TextField().getText().isEmpty()) {
                view.showErrorBox("4BCH Field empty!");
            }
            else {
            try {
                Class.forName("org.postgresql.Driver");
                customer.addCustomerToDB(customer);
                view.showSuccessBox(customer);
                LOG.info(date2 +" " + System.getProperty("user.name") + " hat Kunde zur DB hinzugefuegt");
            } catch (ClassNotFoundException | SQLException ex) {
                view.showErrorBox(ex.getMessage());
                LOG.severe(date2 + " " +System.getProperty("user.name") + " generierte Exception beim hinzufügen eines Kunden: " + ex.getMessage());
            }
            }
        }
            /**
             * Beim klicken auf den "Add New Server" Button innerhalb der addServerBox werden die benötigten Parameter aus den definierten Feldern ausgelesen.
             * Sollte ein Feld leer sein, wird eine ErrorBox generiert.
             * Mit den ausgelesenen Werten für das server Objekt wird die Methode addServertoDB aufgerufen.
             * In der addServertoDB Methode wird das prepared Statement ausgeführt, deshalb try/catch Block für SQL und ClassNotFound Exception
             */
             else if (e.getActionCommand().equals("Add New Server")) {

                Server server = new Server();
                server.setHostname(view.getAddServerHostnameTextField().getText());
                server.setIP(view.getAddServerIPTextField().getText());
                server.setType(view.getServerTypeField());
                server.setName_customer(view.getValueCustomerField().toString());
                if(view.getValueCustomerField() == "nocustomerselected") {          //Überprüfung ob "fikitver Wert "nocustomerselected" übergeben wird
                    view.showErrorBox("Please select a customer!");
                }
                else if(view.getAddServerHostnameTextField().getText().isEmpty()) {
                    view.showErrorBox("Hostname Field is empty!");
                }
                else if(view.getAddServerIPTextField().getText().isEmpty()){
                    view.showErrorBox("IP Field is empty!");
                }
                else {
                    try {
                        Class.forName("org.postgresql.Driver");
                        server.addServerToDB(server);
                        view.showSuccessBox(server);
                        LOG.info(date2 + " " + System.getProperty("user.name") + " hat Server zur DB hinzugefuegt");
                    } catch (ClassNotFoundException | SQLException ex) {
                        view.showErrorBox(ex.getMessage());
                        LOG.severe(date2 + " " + System.getProperty("user.name") + " generierte Exception beim hinzufügen eines Server: " + ex.getMessage());
                    }
                }
            }
            /**
             * Beim Klicken auf den "Remove" Button innerhalb der removeServerBox werden die benötigten Parameter aus den definierten Feldern ausgelesen.
             * Mit den ausgelesenen Werten für das remove Objekt wird die Methode removeServerfromDB aufgerufen.
             * In der removeServerfromDB Methode wird das prepared Statement ausgeführt, deshalb try/catch Block für SQL und ClassNotFound Exception
             */
            else if (e.getActionCommand().equals("Remove")) {
            Server removeserver = new Server();
            removeserver.setHostname(view.getValueRemoveServer());
            if (view.getValueRemoveServer() == "notnull") {
            }
            else {
                try {
                    Class.forName("org.postgresql.Driver");
                    removeserver.removeServerfromDB(removeserver);
                    view.rmSuccessBox(removeserver);
                    LOG.info(date2 + " " + System.getProperty("user.name") + " hat Server aus DB geloescht");
                } catch (ClassNotFoundException | SQLException ex) {
                    view.showErrorBox(ex.getMessage());
                    LOG.info(date2 + " " + System.getProperty("user.name") + " generierte Exception beim löschen eines Server: " + ex.getMessage());
                }
            }
        }
    }

    private void exit() {
        LOG.info(date2 + " " + System.getProperty("user.name") + " hat das Programm beendet");
        System.exit(0);
    }

    private static void initLogger(String filename) throws IOException {
        boolean append = true;
        Handler handler = new FileHandler(filename, append);
        handler.setFormatter(new SimpleFormatter());
        LOG.setLevel(loglevel);
        LOG.addHandler(handler);
    }
}
