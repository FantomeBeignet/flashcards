package com.amplet.views;

import com.amplet.app.Model;
import com.amplet.app.ViewController;
import javafx.animation.RotateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;


public class EditionCarte extends ViewController {
    public EditionCarte(Model model) {
        super(model);
        model.addObserver(this);
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @FXML
    private VBox carte;

    private VBox carteFront;
    private VBox carteBack;
    private boolean isFront = true;
    private boolean isFlipping = false;

    @FXML
    public void initialize() {
        carteFront = new VBox();
        carteFront.getChildren().addAll(carte.getChildren());
        carte.getChildren().clear();
        carte.getChildren().addAll(carteFront);
    }

    @FXML
    private void flipCard() {
        if (isFlipping) {
            return;
        }
        isFlipping = true;
        carteBack = new VBox();
        // Vertically center a label
        Label backLabel = new Label("Back");
        backLabel.setTranslateY(-backLabel.getHeight() / 2);
        carteBack.getChildren().addAll(backLabel);
        if (isFront) {
            RotateTransition rotateTransition = new RotateTransition(Duration.seconds(0.5), carte);
            rotateTransition.setByAngle(90);
            rotateTransition.setAxis(Rotate.X_AXIS);
            rotateTransition.play();
            rotateTransition.setOnFinished(event -> {
                // Put upside down
                RotateTransition flip = new RotateTransition(Duration.seconds(0.5), carte);
                flip.setByAngle(-90);
                flip.setAxis(Rotate.X_AXIS);
                flip.play();
                carte.getChildren().clear();
                carte.getChildren().addAll(carteBack);
                flip.setOnFinished(event1 -> {
                    isFlipping = false;
                });
            });
        } else {
            RotateTransition rotateTransition = new RotateTransition(Duration.seconds(0.5), carte);
            rotateTransition.setByAngle(90);
            rotateTransition.setAxis(Rotate.X_AXIS);
            rotateTransition.play();
            rotateTransition.setOnFinished(event -> {
                // Put upside down
                RotateTransition flip = new RotateTransition(Duration.seconds(0.5), carte);
                flip.setByAngle(-90);
                flip.setAxis(Rotate.X_AXIS);
                flip.play();
                carte.getChildren().clear();
                carte.getChildren().addAll(carteFront);
                flip.setOnFinished(event1 -> {
                    isFlipping = false;
                });
            });
        }
        isFront = !isFront;
    }

    @FXML
    private Label labelQuestion;

    @FXML
    private Label labelTitre;

    @FXML
    private Label labelReponse;

    @FXML
    public void validerTitre() {

    }

    @FXML
    public void validerQuestion() {

    }

    @FXML
    public void validerReponse() {

    }

    @FXML
    public void retour() {

    }

    @FXML
    public void dupliquerCarte() {

    }

    @FXML
    public void supprimerCarte() {

    }

    public void update() {

    }


}
