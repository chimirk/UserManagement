package configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseConfiguration {
    private static final String jdbcURL = "jdbc:mysql://localhost:3306/poll_app";
    private static final String jdbcUserName = "root";
    private static final String jdbcPassword = "";

    public static Connection getConnection() {
        Connection connection = null;
        try {
            //Class.forName("com.mysql.jdbc.Driver");
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(jdbcURL, jdbcUserName, jdbcPassword);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        return connection;
    }
}
