import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class EarthDistanceGUI extends JFrame {
    private final DatabaseManager dbManager;
    private JTextField lat1Field, lon1Field, lat2Field, lon2Field;
    private JComboBox<String> city1Combo, city2Combo;
    private final JLabel resultLabel;

    public EarthDistanceGUI(@NotNull String password) {
        this.dbManager = new DatabaseManager(password);

        setTitle("Earth Distance Calculator");
        setSize(500, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Manual Input", createManualPanel());
        tabbedPane.addTab("Database Select", createDatabasePanel());
        add(tabbedPane, BorderLayout.CENTER);

        resultLabel = new JLabel("Distance: 0.0 km", SwingConstants.CENTER);
        resultLabel.setFont(new Font("Arial", Font.BOLD, 16));
        resultLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(resultLabel, BorderLayout.SOUTH);

        loadCities();
    }

    private JPanel createManualPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        lat1Field = new JTextField(); lon1Field = new JTextField();
        lat2Field = new JTextField(); lon2Field = new JTextField();
        JButton calcBtn = new JButton("Calculate");

        panel.add(new JLabel("Lat 1:")); panel.add(lat1Field);
        panel.add(new JLabel("Lon 1:")); panel.add(lon1Field);
        panel.add(new JLabel("Lat 2:")); panel.add(lat2Field);
        panel.add(new JLabel("Lon 2:")); panel.add(lon2Field);
        panel.add(new JLabel("")); panel.add(calcBtn);

        calcBtn.addActionListener(_ -> {
            try {
                double lat1 = Double.parseDouble(lat1Field.getText());
                double lon1 = Double.parseDouble(lon1Field.getText());
                double lat2 = Double.parseDouble(lat2Field.getText());
                double lon2 = Double.parseDouble(lon2Field.getText());
                double distance = DistanceCalculator.calculate(lat1, lon1, lat2, lon2);
                resultLabel.setText(String.format("Distance: %.2f km", distance));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid coordinates.");
            }
        });
        return panel;
    }

    private JPanel createDatabasePanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        city1Combo = new JComboBox<>();
        city2Combo = new JComboBox<>();
        JButton calcDbBtn = new JButton("Calculate from DB");

        panel.add(new JLabel("City 1:")); panel.add(city1Combo);
        panel.add(new JLabel("City 2:")); panel.add(city2Combo);
        panel.add(new JLabel("")); panel.add(calcDbBtn);

        calcDbBtn.addActionListener(_ -> calculateFromDB());
        return panel;
    }

    private void loadCities() {
        try {
            List<String> cities = dbManager.getAllCityNames();
            for (String cityName : cities) {
                city1Combo.addItem(cityName);
                city2Combo.addItem(cityName);
            }
        } catch (SQLException e) {
            showError("Database connection error: " + e.getMessage());
        }
    }

    private void calculateFromDB() {
        try {
            String name1 = (String) city1Combo.getSelectedItem();
            String name2 = (String) city2Combo.getSelectedItem();

            double[] coord1 = dbManager.getCoordinates(name1);
            double[] coord2 = dbManager.getCoordinates(name2);

            double distance = DistanceCalculator.calculate(coord1[0], coord1[1], coord2[0], coord2[1]);
            resultLabel.setText(String.format("Distance: %.2f km", distance));
        } catch (SQLException e) {
            showError("Error fetching coordinates: " + e.getMessage());
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private static String promptForPassword() {
        JPanel panel = new JPanel();
        JLabel label = new JLabel("Enter MySQL Password:");
        JPasswordField passwordField = new JPasswordField(15);
        panel.add(label);
        panel.add(passwordField);

        int option = JOptionPane.showConfirmDialog(null, panel, "Database Login",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        return (option == JOptionPane.OK_OPTION) ? new String(passwordField.getPassword()) : null;
    }

    static void main(String[] args) {
        String password = promptForPassword();
        if (password != null) {
            SwingUtilities.invokeLater(() -> new EarthDistanceGUI(password).setVisible(true));
        } else {
            System.exit(0);
        }
    }
}