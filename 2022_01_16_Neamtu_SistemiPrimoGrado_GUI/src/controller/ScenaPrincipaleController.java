package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.EqPrimoGrado;
import model.IndetImpException;
import model.SistemaPrimoGrado;

import java.awt.event.MouseEvent;

public class ScenaPrincipaleController {
    @FXML private TextField a;
    @FXML private TextField b;
    @FXML private TextField c;
    @FXML private TextField d;
    @FXML private TextField e;
    @FXML private TextField f;

    @FXML private Label risoluzione;
    @FXML private Label errore;

    @FXML private void initialize() {
    }

    @FXML private void handleRisolvi() {
        EqPrimoGrado eq1=null, eq2=null;
        SistemaPrimoGrado sistema=null;

        double a=0, b=0, c=0, d=0, e=0, f=0;
        boolean err=true;
        double da, dx, dy;
        String app="";

        errore.setText("");
        risoluzione.setText("Qui visualizzerai la soluzione del sistema tramite il metodo di Cramer appena premerai su \"Risolvi!\"");

        try {
            a=Double.parseDouble(this.a.getText());
            b=Double.parseDouble(this.b.getText());
            c=Double.parseDouble(this.c.getText());
            d=Double.parseDouble(this.d.getText());
            e=Double.parseDouble(this.e.getText());
            f=Double.parseDouble(this.f.getText());
            err=false;
        } catch (Exception exc) {
            errore.setText("ERRORE: inserisci correttamente i dati");
        }

        if (!err) {
            eq1 = new EqPrimoGrado(a,b,c);
            eq2 = new EqPrimoGrado(d,e,f);
            sistema = new SistemaPrimoGrado(eq1,eq2);

            try {
                da = (a*e)-(d*b);
                app="D = ("+a+"x("+e+")) - ("+d+"x("+b+")) = "+da+"\n";
                sistema.risolvi();
                dx = (c*e)-(f*b);
                app+="Dx = ("+c+"x("+e+")) - ("+f+"x("+b+")) = "+dx+"\n";
                dy = (a*f)-(d*c);
                app+="Dy = ("+a+"x("+f+")) - ("+d+"x("+c+")) = "+dy+"\n\n";
                app+="X = "+dx+"/("+da+") = "+(dx/da)+"\n";
                app+="Y = "+dy+"/("+da+") = "+(dy/da);
                risoluzione.setText(app);
            } catch (IndetImpException exception) {
                errore.setText("ERRORE: " + exception.getMessage());
                risoluzione.setText(app);
            }
        }
    }
}