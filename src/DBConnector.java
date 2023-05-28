import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public  class DBConnector {
    private static final String _dbURL = "jdbc:sqlserver://localhost;database=lab9;user=guest;password=777;encrypt=true;trustServerCertificate=true";

    public static Connection connectToDb() throws SQLException {
        return DriverManager.getConnection(_dbURL);
    }
}