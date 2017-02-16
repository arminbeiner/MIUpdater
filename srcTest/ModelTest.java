import models.Customer;
import models.CustomerResult;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/** testing the modelNull
 * Created by asi on 27.01.2017.
 */
public class ModelTest {

    private Model modelNull = null;
    private java.util.List<CustomerResult> customerList = new ArrayList<>();

    @Before
    public void initModel() {
        modelNull = new Model(null);
        customerList = loadTestCustomers();
     }

    @Test
    public void testLoadNamesNotNull() {
        try {
            assertNotNull(modelNull.getNames());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testLoadTypesNotNull() {
        assertNotNull(modelNull.getType());
    }

    @Test
    public void testfindCustomerWith1ResultSizeIs1() {
        java.util.List<CustomerResult> result = modelNull.findCustomers(customerList, "1");
        assertEquals(result.size(), 1);
    }

    @Test
    public void testfindCustomerWithEResultSizeIs3() {
        java.util.List<CustomerResult> result = modelNull.findCustomers(customerList, "e");
        assertEquals(result.size(), 3);
    }

    @Test
    public void testfindCustomerWithYResultSizeIs0() {
        java.util.List<CustomerResult> result = modelNull.findCustomers(customerList, "y");
        assertEquals(result.size(), 0);
    }


    /**
     * lädt test kunden
     * @return
     */
    private java.util.List<CustomerResult> loadTestCustomers() {
        java.util.List<CustomerResult> result = new ArrayList<>();
        CustomerResult result1 = new CustomerResult();
        result1.setName("Cusotmer1");

        CustomerResult result2 = new CustomerResult();
        result2.setName("Kunde2");

        CustomerResult result3 = new CustomerResult();
        result3.setName("Heinz");

        CustomerResult result4 = new CustomerResult();
        result4.setName("Egon");

        result.add(result1);
        result.add(result2);
        result.add(result3);
        result.add(result4);
        return result;
    }
}
