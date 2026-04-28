
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.print.PrinterJob;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class SiteController {

    // Текстові поля
    @FXML private TextField tf_name, tf_lat, tf_long, tf_region, tf_url;

    // Кнопки
    @FXML private Button btn_insert, btn_update, btn_clear, btn_print, btn_delete;

    // Таблиця
    @FXML private TableView<Site> tv_sites;

    // Колонки таблиці
    @FXML private TableColumn<Site, Integer> col_id;
    @FXML private TableColumn<Site, String> col_name;
    @FXML private TableColumn<Site, Double> col_lat;
    @FXML private TableColumn<Site, Double> col_long;
    @FXML private TableColumn<Site, String> col_region;
    @FXML private TableColumn<Site, String> col_url;

    // Поле для фото
    @FXML private ImageView iv_photo;

    // Пароль від бази даних (впишіть свій, якщо він є, зазвичай у XAMPP/WAMP він порожній, а у Workbench - ваш пароль)
    public static String dbPassword = "ваш_пароль_від_mysql"; 

    // Підключення до БД (Виправлено назву бази на ukraine_places)
    public Connection getConnection() {
        Connection conn;
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db", "root", dbPassword);
            return conn;
        } catch (Exception e) {
            showAlert("Помилка підключення", "Не вдалося підключитися до бази даних: " + e.getMessage(), Alert.AlertType.ERROR);
            return null;
        }
    }

    // Завантаження даних з БД (Виправлено назву таблиці на landmarks та колонки на photo_url)
    public ObservableList<Site> getSiteList() {
        ObservableList<Site> siteList = FXCollections.observableArrayList();
        Connection conn = getConnection();
        
        if (conn == null) return siteList;

        String query = "SELECT * FROM landmarks";
        
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                Site site = new Site(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("latitude"),
                        rs.getDouble("longitude"),
                        rs.getString("region"),
                        rs.getString("photo_url")
                );
                siteList.add(site);
            }
        } catch (Exception e) {
            System.out.println("Fetch Error: " + e.getMessage());
        }
        return siteList;
    }

    // Відображення даних у таблиці (Виправлено прив'язку photoUrl до Site.java)
    public void showSites() {
        ObservableList<Site> list = getSiteList();

        col_id.setCellValueFactory(new PropertyValueFactory<Site, Integer>("id"));
        col_name.setCellValueFactory(new PropertyValueFactory<Site, String>("name"));
        col_lat.setCellValueFactory(new PropertyValueFactory<Site, Double>("latitude"));
        col_long.setCellValueFactory(new PropertyValueFactory<Site, Double>("longitude"));
        col_region.setCellValueFactory(new PropertyValueFactory<Site, String>("region"));
        col_url.setCellValueFactory(new PropertyValueFactory<Site, String>("photoUrl"));

        tv_sites.setItems(list);
    }

    // Ініціалізація контролера
    @FXML
    public void initialize() {
        
        showSites();

        // Слухач вибору рядка в таблиці
        tv_sites.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                tf_name.setText(newValue.getName());
                tf_lat.setText(String.valueOf(newValue.getLatitude()));
                tf_long.setText(String.valueOf(newValue.getLongitude()));
                tf_region.setText(newValue.getRegion());
                tf_url.setText(newValue.getPhotoUrl());

                String webUrl = newValue.getPhotoUrl();
                if (webUrl != null && !webUrl.isEmpty()) {
                    try {
                        javafx.scene.image.Image img = new javafx.scene.image.Image(webUrl.trim(), true);
                        img.errorProperty().addListener((obs, oldVal, newVal) -> {
                            if (newVal) iv_photo.setImage(null);
                        });
                        iv_photo.setImage(img);
                    } catch (Exception e) {
                        iv_photo.setImage(null);
                    }
                } else {
                    iv_photo.setImage(null);
                }
            }
        });
    }

    // Перевірка формату посилання
    private boolean isValidImageLink(String url) {
        if (url == null || url.trim().isEmpty()) return true;
        String lowerUrl = url.toLowerCase();
        return lowerUrl.endsWith(".jpg") || lowerUrl.endsWith(".jpeg") || 
               lowerUrl.endsWith(".png") || lowerUrl.endsWith(".gif");
    }

    // Додавання запису (Виправлено таблицю та колонки)
    @FXML
    public void insertRecord() {
        if (!isValidImageLink(tf_url.getText())) {
            showAlert("Помилка безпеки", "Посилання має закінчуватися на .jpg, .jpeg, .png або .gif!", Alert.AlertType.ERROR);
            return;
        }

        String query = "INSERT INTO landmarks (name, latitude, longitude, region, photo_url) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, tf_name.getText());
            pstmt.setDouble(2, Double.parseDouble(tf_lat.getText()));
            pstmt.setDouble(3, Double.parseDouble(tf_long.getText()));
            pstmt.setString(4, tf_region.getText());
            pstmt.setString(5, tf_url.getText());

            pstmt.executeUpdate();
            showAlert("Успіх", "Запис успішно додано!", Alert.AlertType.INFORMATION);

            showSites();
            clearFields();
        } catch (Exception e) {
            showAlert("Помилка додавання", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // Оновлення запису (Виправлено таблицю та колонки)
    @FXML
    public void updateRecord() {

        if (!isValidImageLink(tf_url.getText())) {
            showAlert("Помилка безпеки", "Посилання має закінчуватися на .jpg, .jpeg, .png або .gif!", Alert.AlertType.ERROR);
            return;
        }

        String query = "UPDATE landmarks SET name=?, latitude=?, longitude=?, region=?, photo_url=? WHERE id=?";

        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, tf_name.getText());
            pstmt.setDouble(2, Double.parseDouble(tf_lat.getText()));
            pstmt.setDouble(3, Double.parseDouble(tf_long.getText()));
            pstmt.setString(4, tf_region.getText());
            pstmt.setString(5, tf_url.getText());

            pstmt.executeUpdate();
            showAlert("Успіх", "Запис успішно оновлено!", Alert.AlertType.INFORMATION);

            showSites();
            clearFields();
        } catch (Exception e) {
            showAlert("Помилка оновлення", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // Видалення запису (Виправлено таблицю)
    @FXML
    public void deleteRecord() {
        // 1. Отримуємо вибраний об'єкт безпосередньо з таблиці
        Site selectedSite = tv_sites.getSelectionModel().getSelectedItem();

        // 2. Перевіряємо, чи користувач дійсно щось вибрав
        if (selectedSite == null) {
            showAlert("Помилка", "Будь ласка, виберіть запис з таблиці для видалення.", Alert.AlertType.WARNING);
            return;
        }

        String query = "DELETE FROM landmarks WHERE id=?";

        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            // 3. Беремо ID з вибраного об'єкта (замість текстового поля)
            pstmt.setInt(1, selectedSite.getId());
            
            pstmt.executeUpdate();
            showAlert("Успіх", "Запис успішно видалено!", Alert.AlertType.INFORMATION);

            showSites();   // Оновлюємо таблицю після видалення
            clearFields(); // Очищаємо текстові поля
            
        } catch (Exception e) {
            showAlert("Помилка видалення", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // Очищення полів
    @FXML
    public void clearFields() {
        tf_name.clear();
        tf_lat.clear();
        tf_long.clear();
        tf_region.clear();
        tf_url.clear();
        iv_photo.setImage(null);
        tv_sites.getSelectionModel().clearSelection();
    }

    // Друк таблиці
    @FXML
    public void printTable() {
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null && job.showPrintDialog(tv_sites.getScene().getWindow())) {
            boolean success = job.printPage(tv_sites);
            if (success) {
                job.endJob();
                showAlert("Успіх", "Документ відправлено на друк!", Alert.AlertType.INFORMATION);
            }
        }
    }

    // Допоміжний метод для відображення повідомлень
    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}