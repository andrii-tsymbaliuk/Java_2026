import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PZ1 extends JFrame {

    private JTextField lat1Field, lon1Field, lat2Field, lon2Field, radiusField;
    private JTextField resultField;

    public PZ1() {
        setTitle("Distance between two points (Haversine)");
        setSize(420, 320);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(8, 2, 5, 5));

        panel.add(new JLabel("Latitude 1 (degrees):"));
        lat1Field = new JTextField();
        panel.add(lat1Field);

        panel.add(new JLabel("Longitude 1 (degrees):"));
        lon1Field = new JTextField();
        panel.add(lon1Field);

        panel.add(new JLabel("Latitude 2 (degrees):"));
        lat2Field = new JTextField();
        panel.add(lat2Field);

        panel.add(new JLabel("Longitude 2 (degrees):"));
        lon2Field = new JTextField();
        panel.add(lon2Field);

        panel.add(new JLabel("Earth radius (meters):"));
        radiusField = new JTextField("6371000");
        panel.add(radiusField);

        panel.add(new JLabel("Distance (meters):"));
        resultField = new JTextField();
        resultField.setEditable(false);
        panel.add(resultField);

        JButton solveButton = new JButton("Solve");
        JButton clearButton = new JButton("Clear");

        panel.add(solveButton);
        panel.add(clearButton);

        add(panel);

        solveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculateDistance();
            }
        });

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearFields();
            }
        });
    }

    private void calculateDistance() {
        try {
            double lat1 = Double.parseDouble(lat1Field.getText());
            double lon1 = Double.parseDouble(lon1Field.getText());
            double lat2 = Double.parseDouble(lat2Field.getText());
            double lon2 = Double.parseDouble(lon2Field.getText());
            double R = Double.parseDouble(radiusField.getText());

            double phi1 = Math.toRadians(lat1);
            double phi2 = Math.toRadians(lat2);
            double deltaPhi = Math.toRadians(lat2 - lat1);
            double deltaLambda = Math.toRadians(lon2 - lon1);

            double a = Math.sin(deltaPhi / 2) * Math.sin(deltaPhi / 2)
                    + Math.cos(phi1) * Math.cos(phi2)
                    * Math.sin(deltaLambda / 2) * Math.sin(deltaLambda / 2);

            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

            double D = R * c;

            resultField.setText(String.format("%.2f", D));

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid input!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        lat1Field.setText("");
        lon1Field.setText("");
        lat2Field.setText("");
        lon2Field.setText("");
        resultField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new PZ1().setVisible(true);
            }
        });
    }
}