import java.sql.*;
import java.util.Properties;

/**
 * InvitationSalesSQL.java - Project SQL. Demonstrating my knowledge of SQL/CRUD
 *
 * @author Kole Swearingen
 *         Written: 11/11/2015
 *         Revised: 11/12/2015 - Added PreparedStatements to avoid SQL INJECTION!
 *         Sources: docs.oracale, stackoverflow, Peter K. Johhnson(SQL & GIT Tutorial)
 */
public class InvitationSalesSQL
{
    public static void main(String[] args)
    {
        InvitationSalesSQL test = new InvitationSalesSQL();
        //test.displayTable(TABLE_NAME);
        String testStr = "Test";
        test.createTable(testStr);

        String[ ] infoArray = {"Kole", "AWFSAGE53251", "20", "$1.00", "$10.00", "Baby Shower!"};
        test.save(TABLE_NAME, infoArray);
        String[ ] testArray = {"John Doe", "R3nD0m1D", "33", "$2.00", "$66.00", "Wedding!"};
        test.save(TABLE_NAME, testArray);
        test.displayTable(TABLE_NAME);
        //test.save(TABLE_NAME, testArray);
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
            connectionProps.setProperty("user", this.USER_NAME);
            connectionProps.setProperty("password", this.PASSWORD);

            // Connect to MySQL
            try
            {
                this.conn = DriverManager.getConnection("jdbc:mysql://" + this.SERVER_NAME
                    + ":" + this.PORT_NUMBER + "/" + DBF_NAME, connectionProps);
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
        private boolean executeUpdate(String command, String[ ] data) throws SQLException
        {
            // declare/initialize variables
            PreparedStatement stmt = null; /* Released later to avoid SQLException errors */
            boolean isValid = false;
            int i = 1;
            // select from database
            try
            {
                // create a prepared statement
                // to prevent SQL injection
                stmt = conn.prepareStatement(command);
                // check if createTable is the calling method
                // by seeing if the data array is empty
                if(!data[0].equals(""))
                {
                    // Loop through the data
                    for(String counter : data)
                    {
                        // set the strings
                        stmt.setString(i, counter);
                        // increment the param location
                        i += 1;
                    }
                }
                // execute the command
                stmt.executeUpdate();
                // set isValid to true if it was successful
                isValid = true;
            }
            catch(SQLException e) // if unsuccessful
            {
                // inform the user
                System.out.println("ERROR: " + e.getMessage());
                e.printStackTrace();
                // release the resources
                releaseResource(null, stmt);
            }
            // return isValid
            return isValid;
        } // end of executeUpdate( )

        /**
         * executeQuery( ) - Run a SQL command which returns a result set:
         * SELECT
         *
         * @throws SQLException if something goes wrong
         * @return ResultSet containing data from the table
         */
        private ResultSet executeQuery(String[ ] command) throws SQLException
        {
            // declare/initialize variable
            ResultSet rs = null;            /* Released later to avoid SQLException errors */
            PreparedStatement stmt = null;  /* Released later to avoid SQLException errors */
            // attempt to 'update' the database
            try
            {
                // Create/use prepared statements
                // to avoid SQL injection
                stmt = conn.prepareStatement(command[0]);
                // if calling method uses prepared statements
                if(command.length > 1)
                {
                    // set the Prepared Statement String
                    stmt.setString(1, command[1]);
                }
                // execute the statement saving it as an RS
                rs = stmt.executeQuery( );
            }
            catch(SQLException e) // if unsuccessful
            {
                // notify the user of the error
                System.out.println("ERROR: " + e.getMessage());
                e.printStackTrace();
                releaseResource(null, stmt);
            }
            return rs;
        } // end of executeQuery( )

        /**
         * releaseResource( ) - Free up the system resources that were opened.
         *                      If not used, a null will be passed in for that param.
         * @param rs   - Resultset
         * @param stmt - PreparedStatement
         * conn - Connection
         */
        protected void releaseResource(ResultSet rs, PreparedStatement stmt)
        {
            if(rs != null)
            {
                // close/clear the ResultSet
                try { rs.close(); }
                catch(SQLException e) { /* Ignored */}
            }
            if(stmt != null)
            {
                // close/clear the PreparedStatement resources
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
                    // if table does, set boolean to false
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
                String[ ] empty = {""};
                // execute the CREATE statement
                conn.executeUpdate(sql, empty);
                // notify the user of succession
                System.out.println("Successfully created table: " + tableName);
            }
            else // if table already exists
            {
                // notify the user
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
        // convert the integer into a string
        // that is put inside of an array
        // for executing the update
        String[ ] data = {Integer.toString(userID)};
        // Delete user specified data entry
        try
        {
            // sql that runs against the database
            // using prepared statements
            String sql = "DELETE FROM " + tableName + " WHERE id = ?";
            // execute sql
            conn.executeUpdate(sql, data);
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

    /**
     * displayTable( ) - Displays the database table to the user
     *
     * @param tableName - the name of the table being displayed
     */
    public void displayTable(String tableName)
    {
        // declare/initialize variables
        ResultSet rs = null;
        int id;
        String name;
        String partNumber;
        String quantity;
        String originalCostOfItem;
        String sellingPrice;
        String description;
        // establish a database connection
        dbCon con = new dbCon();
        con.connect();

        // Grab the data from the database
        try
        {
            // Save the sql statement as a String Array
            // for query purposes
            String[ ] sql = {"SELECT * FROM " + tableName};
            // execute the statement and save it in rs
            rs = con.executeQuery(sql);
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
        finally{ con.releaseResource(rs, null);}
    } // end of displayTable( )

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
        // establish database connection
        dbCon con = new dbCon();
        con.connect();

        try
        {
            // Create the SQL statement as an array
            // to avoid sql injection once executed
            String[ ] sql = {"SELECT * FROM " + tableName + " WHERE name = ?", infoArray[0]};
            // execute the query, saving it in an rs
            rs = con.executeQuery(sql);
            //insert data
            if(rs.next()) // if match was found
            {
                // sql UPDATE statement
                // Copy the new information into the dataArray
                // So the WHERE location can be specified
                String[ ] dataArray = new String[7];
                dataArray[6] = infoArray[0];
                System.arraycopy(infoArray, 0, dataArray, 0, dataArray.length-1);
                // execute the update by sending both the SQL
                // and the updated information to the method
                // so the prepared statement can
                // NEGATE SQL INJECTION!
                con.executeUpdate("UPDATE " + tableName + " SET name = ?, partNumber = ?, quantity = ?, originalCostOfItem = ?, sellingPrice = ?, description = ? WHERE name = ?", dataArray);
                /**
                 *     ***REFERENCE FOR PREPARED STATEMENT***
                 *        stmt.setString(1, dataArray[0]);
                 *        stmt.setString(2, dataArray[1]);
                 *        stmt.setString(3, dataArray[2]);
                 *        stmt.setString(4, dataArray[3]);
                 *        stmt.setString(5, dataArray[4]);
                 *        stmt.setString(6, dataArray[5]);
                 *        stmt.setString(7, dataArray[6]);
                 */
                // notify user everything went as planned
                System.out.println("Updated: " + infoArray[0] + " " + infoArray[1] + " successfully.");
            }
            else // if match was not found
            {
                // sql INSERT statement
                // execute the update by sending the SQL
                // and the new information to be inserted
                // to the method so SQL INJECTION is prevented
                con.executeUpdate("INSERT INTO " + tableName + " (name, partNumber, quantity, originalCostOfItem, sellingPrice, description)" +
                    " VALUES( ?, ?, ?, ?, ?, ?) ", infoArray);
                /**
                 *     ***REFERENCE FOR PREPARED STATEMENT***
                 *        stmt.setString(1, dataArray[0]);
                 *        stmt.setString(2, dataArray[1]);
                 *        stmt.setString(3, dataArray[2]);
                 *        stmt.setString(4, dataArray[3]);
                 *        stmt.setString(5, dataArray[4]);
                 *        stmt.setString(6, dataArray[5]);
                 */
                // notify the user everything went as planned
                System.out.println("Inserted: " + infoArray[0] + " " + infoArray[1] + " successfully.");
            }
        }
        catch(SQLException e) // if something goes wrong
        {
            // display an error message
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
        // disconnect resources
        finally{ con.releaseResource(rs, null); }
    } // end of save( )
} // end of InvitationSalesSQL