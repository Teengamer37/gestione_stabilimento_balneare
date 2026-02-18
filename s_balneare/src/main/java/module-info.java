module com.example.s_balneare {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.s_balneare to javafx.fxml;
    exports com.example.s_balneare;
}