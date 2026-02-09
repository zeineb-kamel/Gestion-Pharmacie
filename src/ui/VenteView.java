package ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import model.*;
import service.Pharmacie;
import exception.*;

import java.util.Optional;

/**
 * Interface graphique pour effectuer une vente.
 * Permet a un client fidele d'acheter des medicaments ou appareils medicaux.
 */
public class VenteView {
    
    private Pharmacie pharmacie;
    private Stage primaryStage;
    private VBox root;
    
    private ComboBox<ClientFidele> comboClient;
    private TableView<Vendable> tableVendables;
    private ObservableList<Vendable> vendablesList;
    private Label lblTotal;
    private Label lblReduction;
    private double totalPanier = 0;
    
    public VenteView(Pharmacie pharmacie, Stage primaryStage) {
        this.pharmacie = pharmacie;
        this.primaryStage = primaryStage;
        this.vendablesList = FXCollections.observableArrayList(pharmacie.getTousVendables());
        createView();
    }
    
    private void createView() {
        root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f5f5f5;");
        
        HBox header = createHeader();
        HBox clientBox = createClientSelection();
        tableVendables = createTableView();
        HBox panierBox = createPanierBox();
        HBox actionButtons = createActionButtons();
        
        root.getChildren().addAll(header, clientBox, tableVendables, panierBox, actionButtons);
        VBox.setVgrow(tableVendables, Priority.ALWAYS);
    }
    
    private HBox createHeader() {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(20);
        
        Button btnRetour = new Button("← Retour");
        btnRetour.setStyle("-fx-background-color: #607D8B; -fx-text-fill: white;");
        btnRetour.setOnAction(e -> retourAccueil());
        
        Label titre = new Label("Effectuer une Vente");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titre.setTextFill(Color.web("#333"));
        
        header.getChildren().addAll(btnRetour, titre);
        
        return header;
    }
    
    private HBox createClientSelection() {
        HBox clientBox = new HBox(15);
        clientBox.setAlignment(Pos.CENTER_LEFT);
        clientBox.setPadding(new Insets(15));
        clientBox.setStyle("-fx-background-color: white; -fx-background-radius: 5;");
        
        Label lblClient = new Label("Selectionner le client:");
        lblClient.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        
        comboClient = new ComboBox<>();
        comboClient.getItems().addAll(pharmacie.getListeClientsFideles());
        comboClient.setPromptText("Choisir un client fidele...");
        comboClient.setPrefWidth(250);
        
        // Afficher le nom complet dans le combo
        comboClient.setCellFactory(param -> new ListCell<ClientFidele>() {
            @Override
            protected void updateItem(ClientFidele item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNomComplet() + " (CIN: " + item.getCin() + ")");
                }
            }
        });
        comboClient.setButtonCell(new ListCell<ClientFidele>() {
            @Override
            protected void updateItem(ClientFidele item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("Choisir un client...");
                } else {
                    setText(item.getNomComplet());
                }
            }
        });
        
        comboClient.setOnAction(e -> updateReductionLabel());
        
        lblReduction = new Label("");
        lblReduction.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        
        clientBox.getChildren().addAll(lblClient, comboClient, lblReduction);
        
        return clientBox;
    }
    
    private void updateReductionLabel() {
        ClientFidele client = comboClient.getValue();
        if (client != null && client.aReduction()) {
            lblReduction.setText("⭐ REDUCTION 15% DISPONIBLE!");
            lblReduction.setTextFill(Color.GREEN);
        } else if (client != null) {
            double restant = 100 - client.getMontantTotalAchats();
            lblReduction.setText(String.format("Encore %.2f DT pour reduction 15%%", restant));
            lblReduction.setTextFill(Color.ORANGE);
        } else {
            lblReduction.setText("");
        }
    }
    
    private TableView<Vendable> createTableView() {
        TableView<Vendable> table = new TableView<>();
        table.setItems(vendablesList);
        
        TableColumn<Vendable, String> colNom = new TableColumn<>("Nom");
        colNom.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNomVendable()));
        colNom.setPrefWidth(200);
        
        TableColumn<Vendable, String> colType = new TableColumn<>("Type");
        colType.setCellValueFactory(cellData -> {
            Vendable v = cellData.getValue();
            String type;
            if (v instanceof MedicamentChimique) {
                type = "Medicament Chimique";
            } else if (v instanceof MedicamentHomeopathique) {
                type = "Medicament Homeo.";
            } else {
                type = "Appareil Medical";
            }
            return new javafx.beans.property.SimpleStringProperty(type);
        });
        colType.setPrefWidth(150);
        
        TableColumn<Vendable, Double> colPrix = new TableColumn<>("Prix (DT)");
        colPrix.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleDoubleProperty(
                        cellData.getValue().getPrixVendable()).asObject());
        colPrix.setPrefWidth(100);
        
        TableColumn<Vendable, Double> colPrixFidele = new TableColumn<>("Prix Fidele (DT)");
        colPrixFidele.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleDoubleProperty(
                        cellData.getValue().getTranche(true)).asObject());
        colPrixFidele.setPrefWidth(120);
        
        TableColumn<Vendable, String> colReduction = new TableColumn<>("Reduction");
        colReduction.setCellValueFactory(cellData -> {
            Vendable v = cellData.getValue();
            String reduction;
            if (v instanceof MedicamentChimique) {
                reduction = "-20%";
            } else if (v instanceof MedicamentHomeopathique) {
                reduction = "-10%";
            } else {
                reduction = "3 tranches";
            }
            return new javafx.beans.property.SimpleStringProperty(reduction);
        });
        colReduction.setPrefWidth(100);
        
        table.getColumns().addAll(colNom, colType, colPrix, colPrixFidele, colReduction);
        
        return table;
    }
    
    private HBox createPanierBox() {
        HBox panierBox = new HBox(20);
        panierBox.setAlignment(Pos.CENTER);
        panierBox.setPadding(new Insets(15));
        panierBox.setStyle("-fx-background-color: #E8F5E9; -fx-background-radius: 5;");
        
        lblTotal = new Label("Total: 0.00 DT");
        lblTotal.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        lblTotal.setTextFill(Color.web("#2E7D32"));
        
        panierBox.getChildren().add(lblTotal);
        
        return panierBox;
    }
    
    private HBox createActionButtons() {
        HBox buttons = new HBox(15);
        buttons.setAlignment(Pos.CENTER);
        
        Button btnAcheter = new Button("Acheter le produit selectionne");
        btnAcheter.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14; -fx-font-weight: bold;");
        btnAcheter.setPrefWidth(250);
        btnAcheter.setOnAction(e -> effectuerAchat());
        
        Button btnHistorique = new Button("Voir historique client");
        btnHistorique.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        btnHistorique.setOnAction(e -> voirHistorique());
        
        buttons.getChildren().addAll(btnAcheter, btnHistorique);
        
        return buttons;
    }
    
    private void effectuerAchat() {
        ClientFidele client = comboClient.getValue();
        if (client == null) {
            showError("Client requis", "Veuillez selectionner un client fidele.");
            return;
        }
        
        Vendable selected = tableVendables.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Produit requis", "Veuillez selectionner un produit a acheter.");
            return;
        }
        
        try {
            double prixPaye;
            String nomProduit = selected.getNomVendable();
            
            if (selected instanceof Medicament m) {
                // Verifier le stock
                if (m.getQuantiteStock() <= 0) {
                    throw new StockInsuffisantException(m.getNom(), 0, 1);
                }
                
                // Calculer le prix avec reductions
                prixPaye = m.getTranche(true);
                
                // Reduction supplementaire de 15% si applicable
                if (client.aReduction()) {
                    prixPaye = prixPaye * 0.85;
                    client.appliquerReductionEtReinitialiser();
                }
                
                // Mettre a jour le stock
                m.setQuantiteStock(m.getQuantiteStock() - 1);
                pharmacie.getMedicamentDAO().updateStock(m.getCode(), m.getQuantiteStock());
                
            } else if (selected instanceof AppareilMedical a) {
                if (a.getQuantiteStock() <= 0) {
                    throw new StockInsuffisantException(a.getNom(), 0, 1);
                }
                
                prixPaye = a.getTranche(true); // Prix d'une tranche
                
                a.setQuantiteStock(a.getQuantiteStock() - 1);
                pharmacie.getAppareilDAO().updateStock(a.getCode(), a.getQuantiteStock());
                
            } else {
                prixPaye = selected.getTranche(true);
            }
            
            // Mettre a jour le montant des achats du client
            client.ajouterAchat(prixPaye);
            pharmacie.getClientDAO().update(client);
            
            // Mettre a jour le total affiche
            totalPanier += prixPaye;
            lblTotal.setText(String.format("Total: %.2f DT", totalPanier));
            
            // Mettre a jour le label de reduction
            updateReductionLabel();
            
            // Rafraichir la table
            vendablesList.setAll(pharmacie.getTousVendables());
            
            // Afficher confirmation
            String message = String.format(
                    "Achat effectue avec succes!\n\n" +
                    "Produit: %s\n" +
                    "Prix paye: %.2f DT\n" +
                    "Client: %s",
                    nomProduit, prixPaye, client.getNomComplet()
            );
            
            if (selected instanceof AppareilMedical) {
                message += "\n\n(Facilite: 3 tranches de " + String.format("%.2f", prixPaye) + " DT)";
            }
            
            showInfo("Achat reussi", message);
            
        } catch (StockInsuffisantException e) {
            showError("Stock insuffisant", e.getMessage());
        } catch (Exception e) {
            showError("Erreur", "Une erreur est survenue: " + e.getMessage());
        }
    }
    
    private void voirHistorique() {
        ClientFidele client = comboClient.getValue();
        if (client == null) {
            showError("Client requis", "Veuillez selectionner un client.");
            return;
        }
        
        String details = String.format(
                "=== HISTORIQUE DU CLIENT ===\n\n" +
                "Nom: %s\n" +
                "CIN: %d\n\n" +
                "Total des achats: %.2f DT\n" +
                "Credit: %.2f DT\n\n" +
                "Statut: %s",
                client.getNomComplet(),
                client.getCin(),
                client.getMontantTotalAchats(),
                client.getCredit(),
                client.aReduction() ? "REDUCTION 15% DISPONIBLE!" : "Pas de reduction active"
        );
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Historique client");
        alert.setHeaderText(client.getNomComplet());
        alert.setContentText(details);
        alert.showAndWait();
    }
    
    private void retourAccueil() {
        MainApp mainApp = new MainApp();
        try {
            mainApp.start(primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public VBox getView() {
        return root;
    }
}
