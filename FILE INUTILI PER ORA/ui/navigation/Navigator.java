package com.example.s_balneare.ui.navigation;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

import java.util.Map;
import java.util.Objects;

public final class Navigator {
    private final BorderPane host;
    private final Map<ViewId, String> routes;

    public Navigator(BorderPane host) {
        this.host = host;
        this.routes = Map.of(
                ViewId.PUBLIC_HOME, "/com/example/s_balneare/fxml/public-home.fxml",
                ViewId.LOGIN, "/com/example/s_balneare/fxml/login.fxml",
                ViewId.CUSTOMER_HOME, "/com/example/s_balneare/fxml/customer-home.fxml",
                ViewId.OWNER_DASHBOARD, "/com/example/s_balneare/fxml/owner-dashboard.fxml",
                ViewId.ADMIN_DASHBOARD, "/com/example/s_balneare/fxml/admin-dashboard.fxml",
                ViewId.BOOKING, "/com/example/s_balneare/fxml/book.fxml"
        );
    }

    public void goTo(ViewId id) {
        String fxml = routes.get(id);
        if (fxml == null) throw new IllegalArgumentException("No route for " + id);

        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource(fxml)));
            Parent view = loader.load();
            host.setCenter(view);
        } catch (Exception e) {
            throw new RuntimeException("Cannot load view " + fxml + ": ", e);
        }
    }
}
