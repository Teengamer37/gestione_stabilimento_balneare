package com.example.s_balneare.ui.controller;

import com.example.s_balneare.ui.navigation.Navigator;
import com.example.s_balneare.ui.navigation.ViewId;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

public final class CustomerHomeController {
    @FXML private TextField searchField;
    @FXML private ListView<String> beachList;
    @FXML private Button bookButton;
    @FXML private Label hintLabel;

    @FXML
    private void initialize() {
        beachList.getItems().setAll(
                "Lido Aurora - Rimini",
                "Bagno 42 - Riccione"
        );

        beachList.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            bookButton.setDisable(newV == null);
        });
    }

    @FXML
    private void onBook() {
        String selected = beachList.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        //TBD: metodo "grezzo", da modificare in seguito
        BorderPane contentHost = (BorderPane) beachList.getScene().lookup("#contentHost");
        if (contentHost == null) {
            hintLabel.setText("Errore: contentHost non trovato");
            return;
        }

        Navigator nav = new Navigator(contentHost);
        nav.goTo(ViewId.BOOKING);

        //TBD: da passare poi con l'implementazione della session, lo stab. selezionato
    }
}
