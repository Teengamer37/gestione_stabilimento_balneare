package com.example.s_balneare.ui.controller;

import com.example.s_balneare.ui.navigation.Navigator;
import com.example.s_balneare.ui.navigation.ViewId;
import com.example.s_balneare.ui.viewmodel.UiSession;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import com.example.s_balneare.domain.user.Role;
import javafx.scene.layout.BorderPane;

public final class LoginController {
    @FXML private BorderPane contentHost;
    @FXML private ComboBox<Role> roleCombo;

    @FXML
    private void initialize() {
        roleCombo.setItems(FXCollections.observableArrayList(Role.values()));
        roleCombo.getSelectionModel().select(Role.CUSTOMER);
    }

    @FXML
    private void onEnter() {
        //TBD: deve raggiungere RootController
        System.out.println("Login role: " + roleCombo.getValue());

        Role role = roleCombo.getValue();
        UiSession.setRole(role);

        BorderPane contentHost = (BorderPane) roleCombo.getScene().lookup("#contentHost");
        if (contentHost == null) {
            System.out.println("Errore: contentHost non trovato");
            return;
        }

        Navigator nav = new Navigator(contentHost);
        switch (role) {
            case CUSTOMER -> nav.goTo(ViewId.CUSTOMER_HOME);
            case OWNER -> nav.goTo(ViewId.OWNER_DASHBOARD);
            case ADMIN -> nav.goTo(ViewId.ADMIN_DASHBOARD);
        }
    }

}
