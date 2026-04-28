<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Обчислення відстані між точками (Haversine)</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        .container { max-width: 400px; padding: 20px; border: 1px solid #ccc; border-radius: 8px; }
        .field { margin-bottom: 10px; }
        label { display: block; margin-bottom: 5px; }
        input[type="text"] { width: 100%; padding: 8px; box-sizing: border-box; }
        .buttons { margin-top: 15px; }
        .result { margin-top: 20px; font-weight: bold; color: green; }
        .error { color: red; }
    </style>
</head>
<body>

<div class="container">
    <h2>Обчислення відстані (D)</h2>
    <form method="post">
        <div class="field">
            <label>Широта 1 (градуси) [lat1]:</label>
            <input type="text" name="lat1" value="${param.lat1}" required>
        </div>
        <div class="field">
            <label>Довгота 1 (градуси) [lon1]:</label>
            <input type="text" name="lon1" value="${param.lon1}" required>
        </div>
        <div class="field">
            <label>Широта 2 (градуси) [lat2]:</label>
            <input type="text" name="lat2" value="${param.lat2}" required>
        </div>
        <div class="field">
            <label>Довгота 2 (градуси) [lon2]:</label>
            <input type="text" name="lon2" value="${param.lon2}" required>
        </div>
        <div class="field">
            <label>Радіус Землі (м) [R]:</label>
            <input type="text" name="R" value="${param.R != null ? param.R : '6371000'}" required>
        </div>
        
        <div class="buttons">
            <button type="submit" name="action" value="solve">Solve</button>
            <button type="button" onclick="window.location.href=window.location.pathname">Clear</button>
        </div>
    </form>

    <%
        // Логіка обчислення (аналог методу calculateDistance в PZ1.java)
        String action = request.getParameter("action");
        if ("solve".equals(action)) {
            try {
                // Отримання параметрів із форми [cite: 6, 11, 15]
                double lat1 = Double.parseDouble(request.getParameter("lat1"));
                double lon1 = Double.parseDouble(request.getParameter("lon1"));
                double lat2 = Double.parseDouble(request.getParameter("lat2"));
                double lon2 = Double.parseDouble(request.getParameter("lon2"));
                double R = Double.parseDouble(request.getParameter("R"));

                // Конвертація в радіани [cite: 16, 17, 18, 19]
                double phi1 = Math.toRadians(lat1);
                double phi2 = Math.toRadians(lat2);
                double deltaPhi = Math.toRadians(lat2 - lat1);
                double deltaLambda = Math.toRadians(lon2 - lon1);

                // Формула гаверсинуса 
                double a = Math.sin(deltaPhi / 2) * Math.sin(deltaPhi / 2)
                         + Math.cos(phi1) * Math.cos(phi2)
                         * Math.sin(deltaLambda / 2) * Math.sin(deltaLambda / 2);

                double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
                double D = R * c; // Відстань у метрах
    %>
                <div class="result">
                    Результат: <%= String.format("%.2f", D) %> м.
                </div>
    <%
            } catch (Exception e) {
    %>
                <div class="error">Помилка: Перевірте правильність введених даних!</div>
    <%
            }
        }
    %>
</div>

</body>
</html>