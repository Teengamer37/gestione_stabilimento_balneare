package com.example.s_balneare.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public final class PublicHomeController {

    //TBD: da implementare tasto ricerca e filtraggio ricerche

    @FXML private TextField searchField;
    @FXML private ListView<String> beachList;
    @FXML private Label hintLabel;

    @FXML
    private void initialize() {
        beachList.getItems().setAll(
                "Lido Aurora - Rimini",
                "Bagno 42 - Riccione"
        );
    }
}
