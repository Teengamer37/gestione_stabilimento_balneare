package com.example.s_balneare.ui.controller;

import com.example.s_balneare.domain.user.Role;
import com.example.s_balneare.ui.navigation.Navigator;
import com.example.s_balneare.ui.navigation.ViewId;
import com.example.s_balneare.ui.viewmodel.UiSession;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

public final class RootController {
    @FXML private BorderPane contentHost;
    @FXML private Label userLabel;
    private Navigator navigator;

    //TBD: da implementare con role recuperato da DBMS
    private Role role = null;

    @FXML
    private void initialize() {
        navigator = new Navigator(contentHost);
        refreshTopBar();
        navigator.goTo(ViewId.PUBLIC_HOME);
    }

    @FXML
    private void onHome() {
        navigator.goTo(ViewId.PUBLIC_HOME);
        refreshTopBar();
    }

    @FXML
    private void onLogin() {
        navigator.goTo(ViewId.LOGIN);
        refreshTopBar();
    }

    @FXML
    private void onReserved() {
        Role role = UiSession.getRole();
        if (role == null) {
            navigator.goTo(ViewId.LOGIN);
            refreshTopBar();
            return;
        }

        switch (role) {
            case CUSTOMER -> navigator.goTo(ViewId.CUSTOMER_HOME);
            case OWNER -> navigator.goTo(ViewId.OWNER_DASHBOARD);
            case ADMIN -> navigator.goTo(ViewId.ADMIN_DASHBOARD);
        }
        refreshTopBar();
    }

    private void refreshTopBar() {
        Role role = UiSession.getRole();
        userLabel.setText(role == null ? "Benvenuto, Ospite" : "Benvenuto, " + role);
    }
}
