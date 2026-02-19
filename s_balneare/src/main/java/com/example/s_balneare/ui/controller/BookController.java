package com.example.s_balneare.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public final class BookController {
    //TBD: inerfaccia incompleta, codice incompleto

    @FXML private Label beachLabel;
    @FXML private DatePicker datePicker;

    @FXML private Spinner<Integer> personeSpinner;
    @FXML private Spinner<Integer> ombrelloniSpinner;
    @FXML private Spinner<Integer> tendeSpinner;
    @FXML private Spinner<Integer> lettiniSpinner;
    @FXML private Spinner<Integer> sdraioSpinner;

    @FXML private Label capacityLabel;
    @FXML private Button confirmButton;
    @FXML private Label statusLabel;

    // MOCK: per ora valori fissi, poi arrivano dal DB/stabilimento selezionato
    private static final int MAX_PEOPLE_PER_UMBRELLA = 4;
    private static final int MAX_PEOPLE_PER_TENT = 6;

    @FXML
    private void initialize() {
        lettiniSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,10, 0));
        sdraioSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,10, 0));
        personeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 2));
        ombrelloniSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 20, 1));
        tendeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 20, 0));

        //TBD: metodo "grezzo", da modificare in seguito
        beachLabel.setText("Stabilimento: (selezionato dall'utente)");

        // Aggiorna validazione ogni volta che cambia un valore
        personeSpinner.valueProperty().addListener((obs, o, n) -> updateCapacityAndValidation());
        ombrelloniSpinner.valueProperty().addListener((obs, o, n) -> updateCapacityAndValidation());
        tendeSpinner.valueProperty().addListener((obs, o, n) -> updateCapacityAndValidation());

        updateCapacityAndValidation();
    }

    private void updateCapacityAndValidation() {
        int persone = personeSpinner.getValue();
        int ombrelloni = ombrelloniSpinner.getValue();
        int tende = tendeSpinner.getValue();

        int capacity = ombrelloni * MAX_PEOPLE_PER_UMBRELLA + tende * MAX_PEOPLE_PER_TENT;

        capacityLabel.setText("Capienza: " + capacity + " ("
                + ombrelloni + " ombrelloni x " + MAX_PEOPLE_PER_UMBRELLA
                + " persone + " + tende + " tende x " + MAX_PEOPLE_PER_TENT + " persone)");

        boolean ok = persone <= capacity && capacity > 0;

        confirmButton.setDisable(!ok);

        if (capacity == 0) {
            statusLabel.setText("Seleziona almeno 1 ombrellone o 1 tenda.");
        } else if (!ok) {
            statusLabel.setText("Troppe persone per i posti selezionati. Aumenta ombrelloni/tende.");
        } else {
            statusLabel.setText("");
        }
    }

    @FXML
    private void onConfirm() {
        // Validazione minima: oltre ai numeri, serve anche la data
        if (datePicker.getValue() == null) {
            statusLabel.setText("Seleziona una data.");
            return;
        }

        statusLabel.setText("Prenotazione effettuata con successo!");
    }
}
