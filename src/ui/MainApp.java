package ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import service.Pharmacie;

/**
 * Classe principale de l'application JavaFX.
 * Interface d'accueil de la pharmacie.
 */
public class MainApp extends Application {
    
    private Pharmacie pharmacie;
    private Stage primaryStage;
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.pharmacie = new Pharmacie();
        
        // Charger les donnees depuis la base
        try {
            pharmacie.chargerDonnees();
        } catch (Exception e) {
            System.err.println("Attention: Impossible de charger les donnees de la base.");
            System.err.println("L'application fonctionnera en mode local.");
        }
        
        // Creer l'interface d'accueil
        VBox root = createAccueil();
        
        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/ui/style.css") != null ? 
                getClass().getResource("/ui/style.css").toExternalForm() : "");
        
        primaryStage.setTitle("Pharmacie - Gestion");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.show();
    }
    
    /**
     * Cree l'interface d'accueil
     */
    private VBox createAccueil() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #667eea, #764ba2);");
        
        // Titre
        Label titre = new Label("PHARMACIE");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        titre.setTextFill(Color.WHITE);
        
        Label sousTitre = new Label("Systeme de Gestion");
        sousTitre.setFont(Font.font("Arial", FontWeight.NORMAL, 24));
        sousTitre.setTextFill(Color.WHITE);
        
        // Statistiques
        HBox statsBox = createStatsBox();
        
        // Boutons de navigation
        VBox buttonsBox = createButtonsBox();
        
        // Pied de page
        Label footer = new Label("Mini Projet Java - 2eme annee Genie Informatique");
        footer.setFont(Font.font("Arial", 12));
        footer.setTextFill(Color.WHITE);
        
        root.getChildren().addAll(titre, sousTitre, statsBox, buttonsBox, footer);
        VBox.setMargin(statsBox, new Insets(20, 0, 20, 0));
        
        return root;
    }
    
    /**
     * Cree la boite de statistiques
     */
    private HBox createStatsBox() {
        HBox statsBox = new HBox(30);
        statsBox.setAlignment(Pos.CENTER);
        
        // Nombre de medicaments
        VBox medStats = createStatCard("Medicaments", String.valueOf(pharmacie.getListeMedicaments().size()));
        
        // Nombre de clients
        VBox clientStats = createStatCard("Clients Fideles", String.valueOf(pharmacie.getListeClientsFideles().size()));
        
        // Nombre d'appareils
        VBox appareilStats = createStatCard("Appareils", String.valueOf(pharmacie.getListeAppareils().size()));
        
        statsBox.getChildren().addAll(medStats, clientStats, appareilStats);
        
        return statsBox;
    }
    
    /**
     * Cree une carte de statistique
     */
    private VBox createStatCard(String label, String value) {
        VBox card = new VBox(5);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-background-radius: 10;");
        card.setPrefWidth(150);
        
        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        valueLabel.setTextFill(Color.WHITE);
        
        Label nameLabel = new Label(label);
        nameLabel.setFont(Font.font("Arial", 14));
        nameLabel.setTextFill(Color.WHITE);
        
        card.getChildren().addAll(valueLabel, nameLabel);
        
        return card;
    }
    
    /**
     * Cree les boutons de navigation
     */
    private VBox createButtonsBox() {
        VBox buttonsBox = new VBox(15);
        buttonsBox.setAlignment(Pos.CENTER);
        
        // Bouton Gestion Medicaments
        Button btnMedicaments = createMenuButton("Gestion des Medicaments", "#4CAF50");
        btnMedicaments.setOnAction(e -> ouvrirGestionMedicaments());
        
        // Bouton Gestion Clients
        Button btnClients = createMenuButton("Gestion des Clients Fideles", "#2196F3");
        btnClients.setOnAction(e -> ouvrirGestionClients());
        
        // Bouton Gestion Appareils
        Button btnAppareils = createMenuButton("Gestion des Appareils Medicaux", "#FF9800");
        btnAppareils.setOnAction(e -> ouvrirGestionAppareils());
        
        // Bouton Vente
        Button btnVente = createMenuButton("Effectuer une Vente", "#9C27B0");
        btnVente.setOnAction(e -> ouvrirVente());
        
        // Bouton Quitter
        Button btnQuitter = createMenuButton("Quitter", "#f44336");
        btnQuitter.setOnAction(e -> primaryStage.close());
        
        buttonsBox.getChildren().addAll(btnMedicaments, btnClients, btnAppareils, btnVente, btnQuitter);
        
        return buttonsBox;
    }
    
    /**
     * Cree un bouton de menu stylise
     */
    private Button createMenuButton(String text, String color) {
        Button btn = new Button(text);
        btn.setPrefWidth(350);
        btn.setPrefHeight(50);
        btn.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        btn.setStyle(String.format(
                "-fx-background-color: %s; " +
                "-fx-text-fill: white; " +
                "-fx-background-radius: 25; " +
                "-fx-cursor: hand;", color));
        
        // Effet hover
        btn.setOnMouseEntered(e -> btn.setStyle(String.format(
                "-fx-background-color: derive(%s, -20%%); " +
                "-fx-text-fill: white; " +
                "-fx-background-radius: 25; " +
                "-fx-cursor: hand;", color)));
        
        btn.setOnMouseExited(e -> btn.setStyle(String.format(
                "-fx-background-color: %s; " +
                "-fx-text-fill: white; " +
                "-fx-background-radius: 25; " +
                "-fx-cursor: hand;", color)));
        
        return btn;
    }
    
    /**
     * Ouvre la fenetre de gestion des medicaments
     */
    private void ouvrirGestionMedicaments() {
        MedicamentView medicamentView = new MedicamentView(pharmacie, primaryStage);
        primaryStage.getScene().setRoot(medicamentView.getView());
    }
    
    /**
     * Ouvre la fenetre de gestion des clients
     */
    private void ouvrirGestionClients() {
        ClientView clientView = new ClientView(pharmacie, primaryStage);
        primaryStage.getScene().setRoot(clientView.getView());
    }
    
    /**
     * Ouvre la fenetre de gestion des appareils
     */
    private void ouvrirGestionAppareils() {
        AppareilView appareilView = new AppareilView(pharmacie, primaryStage);
        primaryStage.getScene().setRoot(appareilView.getView());
    }
    
    /**
     * Ouvre la fenetre de vente
     */
    private void ouvrirVente() {
        VenteView venteView = new VenteView(pharmacie, primaryStage);
        primaryStage.getScene().setRoot(venteView.getView());
    }
    
    /**
     * Retourne a l'accueil
     */
    public void retourAccueil() {
        pharmacie.chargerDonnees(); // Recharger les donnees
        primaryStage.getScene().setRoot(createAccueil());
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
