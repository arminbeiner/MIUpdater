package models;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.*;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/** testing add server / remove server
 * Created by asi on 26.01.2017.
 */
public class ServerTest {
    private Server server = new Server();

    @Before
    public void initServer() {
        server.setHostname("TestServer");
        server.setIP("0.0.0.0");
        server.setType("Sentr");
        server.setName_customer("TestCustomer");

    }


    @After
    public void checkServerDeleted() {
        Connection connAddServer = null;
        PreparedStatement prepStatement = null;
        ResultSet rs = null;
        try {
            connAddServer = DriverManager.getConnection(DBCredentials.getUrl(), DBCredentials.getUser(), DBCredentials.getPass());

            prepStatement = connAddServer.prepareCall("SELECT * FROM SERVER WHERE HOSTNAME = ? AND IP = ? AND TYPE = ?");
            prepStatement.setString(1, server.getHostname());
            prepStatement.setString(2, server.getIP());
            prepStatement.setString(3, server.getType());
            rs = prepStatement.executeQuery();
            boolean found = false;
            while (rs.next()) {
                found = true;
            }

            if(found) {
                prepStatement =
                        connAddServer.prepareStatement("DELETE FROM SERVER WHERE HOSTNAME = ? AND " +
                                "IP = ? AND " +
                                "TYPE = ?");
                prepStatement.setString(1, server.getHostname());
                prepStatement.setString(2, server.getIP());
                prepStatement.setString(3, server.getType());

                prepStatement.execute();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            try {
                prepStatement.close();
                connAddServer.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    @Test
    public void removeServerSuccess() {
        boolean success = false;
        try {
            server.removeServerfromDB(server);
            success = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Assert.assertTrue(success);
    }
}
