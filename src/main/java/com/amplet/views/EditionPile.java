package com.amplet.views;

import com.amplet.app.App;
import com.amplet.app.Carte;
import com.amplet.app.Model;
import com.amplet.app.Pile;
import com.amplet.app.ViewController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;

public class EditionPile extends ViewController {
    @FXML
    private TextField nomPile;
    @FXML
    private TextField descPile;
    @FXML
    private TextField tagField;
    @FXML
    private HBox tags;

    public EditionPile(Model model) {
        super(model);
        model.addObserver(this);
    }

    public EditionPile(Model model, Object... args) {
        super(model, args);
        ctxEdit.setPileCourante((Pile) args[0]);
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @FXML
    private TilePane availableCards;
    @FXML
    private TilePane chosenCards;
    @FXML
    private TextField searchAvailable;
    @FXML
    private TextField searchChosen;

    @FXML
    private Label labelTagsCount;

    @Override
    public void update() {
        // Clear the list
        availableCards.getChildren().clear();
        chosenCards.getChildren().clear();

        // Add the plus button
        Button button = createAddCard();
        availableCards.getChildren().add(button);

        // Add in chosenCards the cards that are in the pile
        for (Carte carte : ctxEdit.getPileCourante().getCartes()) {
            Button card = createCard(carte);
            if (carte.getTitre().toLowerCase().contains(searchChosen.getText().toLowerCase())) {
                chosenCards.getChildren().add(card);
            }
        }

        // Add the cards that are not in chosenCards
        for (Carte carte : model.getAllCartes()) {
            Boolean found = false;
            for (Node node : chosenCards.getChildren()) {
                if (node instanceof Button) {
                    Button b = (Button) node;
                    if (b.getId().equals(carte.getId().toString())) {
                        found = true;
                        break;
                    }
                }
            }
            if (!found) {
                Button card = createCard(carte);
                if (carte.getTitre().toLowerCase()
                        .contains(searchAvailable.getText().toLowerCase())) {
                    availableCards.getChildren().add(card);
                }
            }
        }

        // Add the tags
        tags.getChildren().clear();
        for (String tag : ctxEdit.getPileCourante().getTags()) {
            Button tagButton = new Button();
            tagButton.setText(tag);
            tagButton.setOnAction(event -> {
                try {
                    model.delete(ctxEdit.getPileCourante(), tag);
                } catch (Exception e) {
                    // System.out.println(e.getMessage());
                }
                update();
            });
            tagButton.getStylesheets().add(App.class.getResource("boutonTag.css").toExternalForm());
            tagButton.getStyleClass().add("tag");
            tags.getChildren().add(tagButton);
        }

        labelTagsCount
                .setText(Integer.toString(ctxEdit.getPileCourante().getTags().size()) + "/10");
    }

    @FXML
    public void initialize() {
        availableCards.setOnDragOver(event -> {
            if (event.getGestureSource() != availableCards && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        availableCards.setOnDragDropped(event -> {
            Dragboard dragboard = event.getDragboard();
            boolean success = false;
            if (dragboard.hasString()) {
                String id = dragboard.getString();
                Boolean found = false;
                for (Node node : availableCards.getChildren()) {
                    if (node instanceof Button) {
                        Button button = (Button) node;
                        if (button.getId().equals(id)) {
                            availableCards.getChildren().remove(button);
                            found = true;
                            break;
                        }
                    }
                }
                for (Node node : chosenCards.getChildren()) {
                    if (node instanceof Button) {
                        Button button = (Button) node;
                        if (button.getId().equals(id)) {
                            chosenCards.getChildren().remove(button);
                            break;
                        }
                    }
                }
                Carte c = ctxEdit.getCarte(id);
                Button card = createCard(c);
                availableCards.getChildren().add(card);
                success = true;
                if (!found) {
                    try {
                        model.delete(ctxEdit.getPileCourante(), c);
                    } catch (Exception e) {
                        // System.out.println(e.getMessage());
                    }
                }
            }
            event.setDropCompleted(success);
            event.consume();
            update();
        });

        chosenCards.setOnDragOver(event -> {
            if (event.getGestureSource() != chosenCards && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        chosenCards.setOnDragDropped(event -> {
            Dragboard dragboard = event.getDragboard();
            boolean success = false;
            if (dragboard.hasString()) {
                String id = dragboard.getString();
                for (Node node : availableCards.getChildren()) {
                    if (node instanceof Button) {
                        Button button = (Button) node;
                        if (button.getId().equals(id)) {
                            availableCards.getChildren().remove(button);
                            break;
                        }
                    }
                }
                Boolean found = false;
                for (Node node : chosenCards.getChildren()) {
                    if (node instanceof Button) {
                        Button button = (Button) node;
                        if (button.getId().equals(id)) {
                            chosenCards.getChildren().remove(button);
                            found = true;
                            break;
                        }
                    }
                }
                Carte c = ctxEdit.getCarte(id);
                Button card = createCard(c);
                chosenCards.getChildren().add(card);
                success = true;
                if (!found) {
                    try {
                        model.create(ctxEdit.getPileCourante(), c);
                    } catch (Exception e) {
                        // System.out.println(e.getMessage());
                    }
                }
            }
            event.setDropCompleted(success);
            event.consume();
            update();
        });

        ctxEdit.resetCartes();

        nomPile.setText(ctxEdit.getPileCourante().getNom());
        descPile.setText(ctxEdit.getPileCourante().getDescription());
        nomPile.textProperty().addListener((observable, oldValue, newValue) -> {
            ctxEdit.getPileCourante().setNom(newValue);
            try {
                model.update(ctxEdit.getPileCourante());
            } catch (Exception e) {
                // System.out.println(e.getMessage());
            }
        });
        descPile.textProperty().addListener((observable, oldValue, newValue) -> {
            ctxEdit.getPileCourante().setDescription(newValue);
            try {
                model.update(ctxEdit.getPileCourante());
            } catch (Exception e) {
                // System.out.println(e.getMessage());
            }
        });
        tagField.textProperty().addListener((observable, oldValue, newValue) -> {
            // If it ends with a ';', save tag and clear field
            if (newValue.endsWith(";")) {
                String tag = newValue.substring(0, newValue.length() - 1);
                if (!tag.isEmpty()) {
                    try {
                        if (ctxEdit.getPileCourante().getTags().size() < 10
                                && !(ctxEdit.getPileCourante().hasTag(tag))) {
                            model.create(ctxEdit.getPileCourante(), tag);
                        }
                    } catch (Exception e) {
                        // System.out.println(e.getMessage());
                    }
                    update();
                    Platform.runLater(() -> {
                        tagField.positionCaret(0);
                        tagField.clear();
                    });
                }
            }
        });

        // Validate on enter
        tagField.setOnAction(event -> {
            String tag = tagField.getText();
            if (!tag.isEmpty()) {
                try {
                    if (ctxEdit.getPileCourante().getTags().size() < 10
                            && !(ctxEdit.getPileCourante().hasTag(tag))) {
                        model.create(ctxEdit.getPileCourante(), tag);
                    }
                } catch (Exception e) {
                    // System.out.println(e.getMessage());
                }
                update();
                Platform.runLater(() -> {
                    tagField.positionCaret(0);
                    tagField.clear();
                });
            }
        });

        searchAvailable.textProperty().addListener((observable, oldValue, newValue) -> {
            update();
        });

        searchChosen.textProperty().addListener((observable, oldValue, newValue) -> {
            update();
        });

        update();
    }

    public void emptyLists() {
        availableCards.getChildren().clear();
        chosenCards.getChildren().clear();
    }

    public Button createCard(Carte c) {
        Button card = new Button();
        card.setPrefSize(100, 100);
        card.setId(c.getId().toString());
        card.setText(c.getTitre());

        // add text wrapping
        card.setWrapText(true);

        card.setOnDragDetected(event -> {
            Dragboard dragboard = card.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(card.getId());
            dragboard.setContent(content);
            event.consume();
        });

        card.setOnAction(event -> {
            try {
                App.setRoot("editionCarte", c, ctxEdit.getPileCourante());
            } catch (Exception e) {
                // System.out.println(e.getMessage());
            }
        });

        ctxEdit.putCarte(card.getId(), c);
        return card;
    }

    public Button createAddCard() {
        Button card = new Button();
        card.setPrefSize(100, 100);
        card.setId("add");
        card.setText("+");
        // bold and bigger font
        card.setStyle("-fx-font-weight: bold; -fx-font-size: 20px;");

        // add a new card to the pile when clicked
        card.setOnAction(event -> {
            Carte c = new Carte("Nouvelle carte", "Question", "R??ponse", "", "Description");
            try {
                model.create(c);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Button newCard = createCard(c);
            availableCards.getChildren().add(newCard);
        });

        return card;
    }

    @FXML
    public void retourListe() {
        try {
            App.setRoot("listePile");
        } catch (Exception e) {
            // System.out.println(e.getMessage());
        }
    }

    @FXML
    public void exporterPile() {
        try {
            App.exportPile(this.ctxEdit.getPileCourante());
        } catch (Exception e) {
            // System.out.println(e.getMessage());
        }
    }

}
