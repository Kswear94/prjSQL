import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * InvitationsSQL.java - Project SQL. Demonstrating my knowledge of SQL/CRUD
 *
 * @author Kole Swearingen
 *         Written: 11/11/2015
 *         Revised:
 *         Sources:
 */
public class InvitationsSQL
{
    // Set up two constants to be used in this test environment.
    private final static String DBF_NAME = "javasql";
    private final static String TABLE_NAME = "invitations";

    // The mySQL username and password (may be empty)
    private final String USER_NAME = "root";
    private final String PASSWORD = "mysql";
    // name of computer running mySQL
    private final String SERVER_NAME = "localhost";
    // Port of the MySQL server (default is 3306 or 8889 on MAMP)
    private final int PORT_NUMBER = 3306;

    /**
     * dbCon( ) - Connect to the database
     * @return  The new connection
     * @throws SQLException
     */
    protected Connection dbCon( ) throws SQLException
    {
        // set up variables for connection
        Connection conn = null;
        Properties connectionProps = new Properties( );
        connectionProps.put("user", this.USER_NAME);
        connectionProps.put("password", this.PASSWORD);

        // Connect to MySQL
        try
        {
            conn = DriverManager.getConnection("jdbc:mysql://" + this.SERVER_NAME + ":" + this.PORT_NUMBER +
                    "/" + DBF_NAME, connectionProps);
        }
        catch (SQLException e)
        {
            System.out.println("ERROR: Could not connect to the database");
            e.printStackTrace();
        }
        return conn;
    } // end of dbCon( )


    /**
     * executeUpdate( ) - Used to run a sql command which does NOT return a resultSet:
     * CREATE/INSERT/UPDATE/DELETE/DROP
     *
     * @return boolean if command was successful or not
     * @throws SQLException if something goes wrong
     */
    public boolean executeUpdate(Connection conn, String command) throws SQLException
    {
        java.sql.Statement stmt = null;
        try
        {
            stmt = conn.createStatement();
            stmt.executeUpdate(command); // this will throw a SQLException if it fails
            return true;
        }
        finally
        {
            // this will run whether we throw an exception or not
            if(stmt != null) { stmt.close( ); }
        }
    } // end of executeUpdate( )

} // end of InvitationsSQL
