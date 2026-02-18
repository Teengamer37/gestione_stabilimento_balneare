module com.example.stabilimento_balneare {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.stabilimento_balneare to javafx.fxml;
    exports com.example.stabilimento_balneare;
}