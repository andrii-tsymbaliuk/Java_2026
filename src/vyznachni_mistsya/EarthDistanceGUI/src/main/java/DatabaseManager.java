import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String URL = "jdbc:mysql://localhost:3306/uacities";
    private static final String USER = "root";
    private final String password;

    public DatabaseManager(String password) {
        this.password = password;
    }

    public List<String> getAllCityNames() throws SQLException {
        List<String> names = new ArrayList<>();
        String sql = "SELECT city_name FROM cities ORDER BY city_name ASC";
        try (Connection conn = DriverManager.getConnection(URL, USER, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                names.add(rs.getString("city_name"));
            }
        }
        return names;
    }

    public double[] getCoordinates(String cityName) throws SQLException {
        String sql = "SELECT lat, lng FROM cities WHERE city_name = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cityName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new double[]{rs.getDouble("lat"), rs.getDouble("lng")};
                }
            }
        }
        return new double[]{0, 0};
    }
}
