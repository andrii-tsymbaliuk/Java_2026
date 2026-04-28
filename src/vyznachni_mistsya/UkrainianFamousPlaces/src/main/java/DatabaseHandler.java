
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseHandler {
    // Змінено назву бази з 'ukraine_places' на 'db'
    private static final String URL = "jdbc:mysql://localhost:3306/db";
    private static final String USER = "root"; 
    private static final String PASS = "ВАШ_ПАРОЛЬ_ВІД_MYSQL"; // вкажіть ваш справжній пароль

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}