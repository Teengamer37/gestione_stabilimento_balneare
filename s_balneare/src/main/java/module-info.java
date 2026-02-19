module com.example.s_balneare {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.example.s_balneare to javafx.fxml;
    opens com.example.s_balneare.ui.controller to javafx.fxml;

    exports com.example.s_balneare;
}