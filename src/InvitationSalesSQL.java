import java.sql.*;
import java.util.Properties;

/**
 * InvitationSalesSQL.java - Project SQL. Demonstrating my knowledge of SQL/CRUD
 *
 * @author Kole Swearingen
 *         Written: 11/11/2015
 *         Revised:
 *         Sources:
 */
public class InvitationSalesSQL
{
    public static void main(String[] args)
    {
        InvitationSalesSQL test = new InvitationSalesSQL();
        test.displayTable(TABLE_NAME);
        /*
        test.createTable(TABLE_NAME);
        String[ ] infoArray = {"Kole", "AWFSAGE53251", "10", "$1.00", "$10.00", "Baby Shower!"};
        test.save(TABLE_NAME, infoArray);
        String[ ] testArray = {"John Doe", "R3nD0m1D", "33", "$2.00", "$66.00", "Wedding!"};

        test.save(TABLE_NAME, testArray);
*/
    }
    // Database name and table name
    private final static String DBF_NAME = "javasql";
    private final static String TABLE_NAME = "invitations";

    /**
     * dbCon( ) - Manage the database connections
     */
    protected class dbCon
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
         *
         * @return - The new connection
         */
        private Connection connect( )
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
            catch(SQLException e) // if something went wrong
            {
                // display an error message to the user
                System.out.println("ERROR: Could not connect to the database " + e.getMessage());
                e.printStackTrace();
            }
            // return the connection
            return this.conn;
        } // end of connect( )

        /**
         * executeUpdate( ) - Used to run a sql command which does NOT return a resultSet:
         * CREATE/INSERT/UPDATE/DELETE/DROP
         *
         * @return boolean if command was successful or not
         * @throws SQLException if something goes wrong
         */
        private boolean executeUpdate(String command) throws SQLException
        {
            // declare/initialize variable
            Statement stmt = null;
            // attempt to 'update' the database
            try
            {
                // create a statement
                stmt = conn.createStatement();
                // execute the statement
                stmt.executeUpdate(command);
                // return true if it was successful
                return true;
            }
            finally
            {
                // clear/close the statement
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
        private ResultSet executeQuery(String command) throws SQLException
        {
            // declare/initialize variables
            ResultSet rs;
            Statement stmt = null;
            // select from database
            try
            {
                // create a statement
                stmt = conn.createStatement();
                // run the statement against the database
                // save it as a ResultSet
                rs = stmt.executeQuery(command);
                // return the selected results
                return rs;
            }
            finally
            {
                // clear/close the statement
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
        private void releaseResource(ResultSet rs, Statement stmt)
        {
            if(rs != null)
            {
                // close/clear the ResultSet
                try { rs.close(); }
                catch(SQLException e) { /* Ignored */}
            }
            if(stmt != null)
            {
               try { stmt.close(); }
               catch (SQLException e) { /* Ignored */ }
            }
            if(conn != null)
            {
                // close the database connection
                try {conn.close(); }
                catch(SQLException e) { /* Ignored */}
            }
        } // end of releaseResource( )
    } // end of dbCon( )


    /**
     * createTable( ) - Create a new database table
     *
     * @param tableName - The name of the new table
     */
    public void createTable(String tableName)
    {
        // declare/initialize variables
        boolean createTable = true;
        ResultSet rs = null;
        // establish database connection
        dbCon conn = new dbCon();
        conn.connect();

        // Create a new table
        try
        {
            // Check to see if there is an existing table
            DatabaseMetaData meta = conn.connect().getMetaData();
            rs = meta.getTables(null, null, "%", null);
            // loop through the table list
            while(rs.next())
            {
                // discover if table exists
                if(rs.getString(3).equals(tableName))
                {
                    // if tables does, set boolean to false
                    // so no attempt is made in recreating it
                    createTable = false;
                    break;
                }
            }
            if(createTable) // table does not exist
            {
                // sql statement to create a new table
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
                // execute the CREATE statement
                conn.executeUpdate(sql);
                System.out.println("Successfully created table: " + tableName);
            }
            else // if table already exists
            {
                System.out.println("The table " + tableName + " already exists.");
            }
        }
        catch(SQLException e) // if something went wrong
        {
            // display the error message to user
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
        // release the resources
        finally { conn.releaseResource(rs, null);}
    } // end of createTable( )

    /**
     * delete( ) - Delete a specified data entry
     *
     * @param tableName - Name of the table the data is deleted from
     * @param userID - The id(index) of the record in the table
     */
    public void delete(String tableName, int userID)
    {
        // connect to the database
        dbCon conn = new dbCon();
        conn.connect();

        // Delete user specified data entry
        try
        {
            // sql that runs against the database
            String sql = "DELETE FROM " + tableName + " WHERE id ='" + userID + "'";
            // execute sql
            conn.executeUpdate(sql);
            System.out.println("Deleted the record from the database.");
        }
        catch(SQLException e) // if something went wrong
        {
            // display an error message
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
        // disconnect resources
        finally{ conn.releaseResource(null, null); }
    } // end of delete( )


    public void displayTable(String tableName)
    {
        // declare/initialize variables
        Statement stmt = null;
        ResultSet rs = null;
        int id;
        String name;
        String partNumber;
        String quantity;
        String originalCostOfItem;
        String sellingPrice;
        String description;

        dbCon conn = new dbCon();
        conn.connect();

        // Grab the data from the database
        try
        {
            String sql = "SELECT * FROM " + tableName;
            // execute the statement and save it in rs
            stmt = conn.connect().createStatement();
            rs = stmt.executeQuery(sql);
            // display table 'header'
            System.out.println("\nID\tNAME\t\tPART#\t\tQUANTITY\tORIGINAL COST\tSELLING PRICE\tDESCRIPTION");
            System.out.println("------------------------------------------------------------------------------");
            // loop through all the data in the table
            while(rs.next())
            {
                id                  = rs.getInt("id");
                name                = rs.getString("name");
                partNumber          = rs.getString("partNumber");
                quantity            = rs.getString("quantity");
                originalCostOfItem  = rs.getString("originalCostOfItem");
                sellingPrice        = rs.getString("sellingPrice");
                description         = rs.getString("description");
                // display each row of data in the table
                System.out.printf("%d\t%s\t\t%s\t%s\t\t%s\t\t\t%s\t\t\t%s\n", id, name, partNumber, quantity, originalCostOfItem, sellingPrice, description);
            }
        }
        catch(SQLException e) // if something went wrong
        {
            // display an error message to the user.
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
        // disconnect the resources
        finally{ conn.releaseResource(rs, stmt);}
    }
    /**
     * save( ) - Insert or Update a record in the database
     *
     * @param tableName - The name of the table being using
     * @param infoArray - The information being inserted or updated
     */
    public void save(String tableName, String[ ] infoArray)
    {
        // declare variables
        ResultSet rs = null;
        Statement stmt = null;
        String sql;
        // establish database connection
        dbCon conn = new dbCon();
        conn.connect();

        try
        {
            // create a 'shell' for a statement
            stmt = conn.connect().createStatement();
            // run the sql and save it into rs
            rs = stmt.executeQuery("SELECT * FROM " + tableName + " WHERE name='" + infoArray[0] + "'");
            // if database returns no match
            //insert data
            if(!rs.isBeforeFirst())
            {
                // sql INSERT statement
                sql = "INSERT INTO " + tableName + " (name, partNumber, quantity, originalCostOfItem, sellingPrice, description)" +
                    " VALUES("
                    + "'" + infoArray[0] + "', "
                    + "'" + infoArray[1] + "', "
                    + "'" + infoArray[2] + "', "
                    + "'" + infoArray[3] + "', "
                    + "'" + infoArray[4] + "', "
                    + "'" + infoArray[5] + "') ";
                // execute the INSERT statement
                conn.executeUpdate(sql);
                // notify the user everything went as planned
                System.out.println("Inserted: " + infoArray[0] + " " + infoArray[1] + " successfully.");
            }
            else // if database returns a match
            {
                // sql UPDATE statement
                sql = "UPDATE " + tableName
                    + " SET name='"          + infoArray[0] + "', "
                    + "partNumber='"         + infoArray[1] + "', "
                    + "quantity='"           + infoArray[2] + "', "
                    + "originalCostOfItem='" + infoArray[3] + "', "
                    + "sellingPrice='"       + infoArray[4] + "', "
                    + "description='"        + infoArray[5] + "' "
                    + "WHERE name='" + infoArray[0] + "'";
                // execute the UPDATE statement
                conn.executeUpdate(sql);
                // notify user everything went as planned
                System.out.println("Updated: " + infoArray[0] + " " + infoArray[1] + " successfully.");
            }
        }
        catch(SQLException e) // if something goes wrong
        {
            // display an error message
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
        // disconnect resources
        finally{ conn.releaseResource(rs, stmt); }
    } // end of save( )
} // end of InvitationSalesSQL


