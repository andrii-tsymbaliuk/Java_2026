package com.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class App extends JFrame {

    // Налаштування підключення винесено в константи
    private static final String DB_CONNECTION_STR = "jdbc:mysql://localhost:3306/test_db";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "1111";

    private JTable studentsTable;
    private DefaultTableModel model;

    public App() {
        configureWindow();
        initUI();
        refreshData();
    }

    private void configureWindow() {
        setTitle("Система перегляду студентів");
        setSize(650, 450);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void initUI() {
        // Створюємо панель з відступами
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        
        model = new DefaultTableModel();
        studentsTable = new JTable(model);
        
        // Додаємо таблицю в скрол-панель
        JScrollPane scrollPane = new JScrollPane(studentsTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);


        setContentPane(mainPanel);
    }

    private void refreshData() {
        String sqlQuery = "SELECT * FROM students";

        try (Connection conn = DriverManager.getConnection(DB_CONNECTION_STR, DB_USER, DB_PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sqlQuery)) {

            ResultSetMetaData meta = rs.getMetaData();
            int cols = meta.getColumnCount();

            // Очищення моделі перед заповненням
            model.setColumnIdentifiers(new String[0]); 
            model.setRowCount(0);

            // Встановлюємо заголовки
            for (int i = 1; i <= cols; i++) {
                model.addColumn(meta.getColumnName(i));
            }

            // Заповнюємо рядки через масив Object (альтернатива Vector)
            while (rs.next()) {
                Object[] rowData = new Object[cols];
                for (int i = 1; i <= cols; i++) {
                    rowData[i - 1] = rs.getObject(i);
                }
                model.addRow(rowData);
            }

        } catch (SQLException e) {
            showErrorMessage("Критична помилка БД: " + e.getMessage());
        }
    }

    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Помилка", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        // Використання стандартної теми системи для іншого вигляду
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        EventQueue.invokeLater(() -> new App().setVisible(true));
    }
}