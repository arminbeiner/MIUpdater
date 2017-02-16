package models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Logger;

/** a customer object equals to a db customer
 * Created by asi on 03.01.2017.
 */
public class Customer {

    /** Definition der benötigten Variablen. Erstellung der getter / setter
     */
    private String name;
    private String bch4; // 4 Buchstabencode als eindeutiger Identifier

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBch4() {
        return bch4;
    }

    public void setBch4(String bch4) {
        this.bch4 = bch4;
    }

    /**
     * neuen Kunden in DB laden
     * @param customer kunde
     * @throws SQLException
     * Vorbereitung des Prepared Statement zum hinzufügen eines Customers in die Datenbank.
     * Name und 4BCH werden über die getName / getBCH4 Methoden übergeben.
     */
    public void addCustomerToDB(Customer customer) throws SQLException{
        Connection connAddCustomer = DriverManager.getConnection(DBCredentials.getUrl(), DBCredentials.getUser(), DBCredentials.getPass());

        PreparedStatement prepStateAddCustomer =
                connAddCustomer.prepareStatement("INSERT INTO customer(name, bch4) VALUES (?, ?)");
        prepStateAddCustomer.setString(1, customer.getName());
        prepStateAddCustomer.setString(2, customer.getBch4());

        prepStateAddCustomer.executeUpdate();

        prepStateAddCustomer.close();
        connAddCustomer.close();
    }

}
