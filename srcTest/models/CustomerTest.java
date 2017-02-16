package models;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/** testing add customer
 * Created by asi on 26.01.2017.
 */
public class CustomerTest {

    private Customer customerValid = new Customer();
    private Customer customerNotValid = new Customer();

    @Before
    public void initCustomer() {
        customerValid.setName("TestCustomer");
        customerValid.setBch4("bch4");

        customerNotValid.setName("TestCustomer");
        customerNotValid.setBch4("bch4sdfd");
    }

    @After
    public void deleteValidCustomer() {
        Connection connAddCustomer = null;
        PreparedStatement prepStateAddCustomer = null;
        try {
            connAddCustomer = DriverManager.getConnection(DBCredentials.getUrl(), DBCredentials.getUser(), DBCredentials.getPass());

        prepStateAddCustomer =
                connAddCustomer.prepareStatement("DELETE FROM CUSTOMER WHERE NAME = ? AND BCH4 = ?");
        prepStateAddCustomer.setString(1, customerValid.getName());
        prepStateAddCustomer.setString(2, customerValid.getBch4());

        prepStateAddCustomer.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            try {
                prepStateAddCustomer.close();
                connAddCustomer.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void addCustomerSuccess() {
        boolean success = false;
        try {
            customerValid.addCustomerToDB(customerValid);
            success = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Assert.assertTrue(success);
    }

    @Test
    public void addCustomerNotValidBCH4Value() {
        boolean success = false;
        try {
            customerNotValid.addCustomerToDB(customerNotValid);
            success = true;
        } catch (SQLException e) {
            e.getMessage();
        }
        Assert.assertFalse(success);
    }
}
