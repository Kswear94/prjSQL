import java.sql.*;
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
    public static void main(String[] args)
    {
        InvitationsSQL test = new InvitationsSQL();
        //test.createTable(TABLE_NAME);
        String[ ] infoArray = {"Kole", "AWFSAGE53251", "10", "$1.00", "$10.00", "Baby Shower!"};
        test.save(TABLE_NAME, infoArray);
    }
    // Database name and table name
    private final static String DBF_NAME = "javasql";
    private final static String TABLE_NAME = "invitations";

    /**
     * dbCon( ) - Manage the database connections
     */
    public class dbCon
    {
        // The mySQL username and password
        private final String USER_NAME = "root";
        private final String PASSWORD = "mysql";
        // name of computer running mySQL
        private final String SERVER_NAME = "localhost";
        // Default port of the MySQL server
        private final int PORT_NUMBER = 3306;
        Connection conn = null;

        /**
         * connect( ) - Connect to the database
         * @return - The new connection
         */
        public Connection connect( )
        {
            // set up variables for connection
            Properties connectionProps = new Properties();
            connectionProps.put("user", this.USER_NAME);
            connectionProps.put("password", this.PASSWORD);

            // Connect to MySQL
            try
            {
                this.conn = DriverManager.getConnection("jdbc:mysql://" + this.SERVER_NAME + ":" + this.PORT_NUMBER +
                    "/" + DBF_NAME, connectionProps);
            }
            catch(SQLException e)
            {
                System.out.println("ERROR: Could not connect to the database " + e.getMessage());
                e.printStackTrace();
            }
            return this.conn;
        } // end of connect( )

        /**
         * executeUpdate( ) - Used to run a sql command which does NOT return a resultSet:
         * CREATE/INSERT/UPDATE/DELETE/DROP
         *
         * @return boolean if command was successful or not
         * @throws SQLException if something goes wrong
         */
        public boolean executeUpdate(String command) throws SQLException
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

        /**
         * executeQuery( ) - Run a SQL command which returns a result set:
         * SELECT
         *
         * @throws SQLException if something goes wrong
         * @return ResultSet containing data from the table
         */
        public ResultSet executeQuery(Connection conn, String command) throws SQLException
        {
            ResultSet rs;
            java.sql.Statement stmt = null;
            try
            {
                stmt = conn.createStatement();
                rs = stmt.executeQuery(command);
                return rs;
            }
            finally
            {
                // This will run
                if(stmt != null) { stmt.close(); }
            }
        } // end of executeQuery( )

        /**
         * releaseResource( ) - Free up the system resources that were opened.
         *                      If not used, a null will be passed in for that param.
         * @param rs - Resultset
         *  - Statement
         * conn - Connection
         */
        public void releaseResource(ResultSet rs)
        {
            if(rs != null)
            {
                try { rs.close(); }
                catch(SQLException e) { /* Ignored */}
            }
            //if(ps != null)
            //{
            //   try { ps.close(); }
            //   catch (SQLException e) { /* Ignored */ }
            //}
            if(conn != null)
            {
                try {conn.close(); }
                catch(SQLException e) { /* Ignored */}
            }
        } // end of releaseResource( )
    } // end of dbCon( )

    /**
     * createTable( ) - Create a new database table
     * @param tableName - The name of the new table
     */
    public void createTable(String tableName)
    {
        boolean createTable = true;
        ResultSet rs = null;
        dbCon conn = new dbCon();
        conn.connect();

        // Create a new table
        try
        {
            // Check to see if there is an existing table
            DatabaseMetaData meta = conn.connect().getMetaData();
            rs = meta.getTables(null, null, "%", null);

            while(rs.next())
            {
                if(rs.getString(3).equals(tableName))
                {
                    createTable = false;
                    break;
                }
            }
            if(createTable)
            {
                String sql =
                    "CREATE TABLE " + tableName + " ( " +
                        "id smallint(5) NOT NULL AUTO_INCREMENT, " +
                        "name varchar(20) NOT NULL, " +
                        "partNumber varchar(20) NOT NULL, " +
                        "quantity varchar(5) NOT NULL, " +
                        "originalCostOfItem varchar(10) NOT NULL, " +
                        "sellingPrice varchar(15) NOT NULL, " +
                        "description varchar(50), " +
                        "PRIMARY KEY (id))";
                conn.executeUpdate(sql);
            }
            else
            {
                System.out.println("The table " + tableName + " already exists.");
            }

        }
        catch(SQLException e)
        {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
        // release the resources
        finally { conn.releaseResource(rs);}
    } // end of createTable( )


    /**
     * save( ) - Insert or Update a record in the database
     * @param tableName - The name of the table being using
     * @param infoArray - The information being inserted or updated
     */
    public void save(String tableName, String[ ] infoArray)
    {
        ResultSet rs = null;
        Statement stmt = null;
        String sql = "";
        dbCon conn = new dbCon();
        conn.connect();

        try
        {
            stmt = conn.connect().createStatement();
            rs = stmt.executeQuery("SELECT * FROM " + tableName + " WHERE name='" + infoArray[0] + "'");
            if(!rs.isBeforeFirst())
            {
                sql = "INSERT INTO " + tableName + " (name, partNumber, quantity, originalCostOfItem, sellingPrice, description)" +
                    " VALUES("
                    + "'" + infoArray[0] + "', "
                    + "'" + infoArray[1] + "', "
                    + "'" + infoArray[2] + "', "
                    + "'" + infoArray[3] + "', "
                    + "'" + infoArray[4] + "', "
                    + "'" + infoArray[5] + "') ";
                conn.executeUpdate(sql);
                System.out.println("Inserted: " + infoArray[0] + " " + infoArray[1] + " successfully.");
            }
            else
            {
                sql = "UPDATE " + tableName
                    + " SET name='"          + infoArray[0] + "', "
                    + "partNumber='"         + infoArray[1] + "', "
                    + "quantity='"           + infoArray[2] + "', "
                    + "originalCostOfItem='" + infoArray[3] + "', "
                    + "sellingPrice='"       + infoArray[4] + "', "
                    + "description='"        + infoArray[5] + "' "
                    + "WHERE name='" + infoArray[0] + "'";
                conn.executeUpdate(sql);
                System.out.println("Updated: " + infoArray[0] + " " + infoArray[1] + " successfully.");
            }
        }
        catch(SQLException e)
        {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
        finally{ conn.releaseResource(rs); }
    } // end of save( )
} // end of InvitationsSQL


