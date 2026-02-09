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
import model.ClientFidele;
import service.Pharmacie;
import exception.SaisieInvalideException;

import java.util.List;
import java.util.Optional;

/**
 * Interface graphique pour la gestion des clients fideles.
 * Permet d'ajouter, supprimer, rechercher et afficher les clients.
 */
public class ClientView {
    
    private Pharmacie pharmacie;
    private Stage primaryStage;
    private VBox root;
    private TableView<ClientFidele> tableView;
    private ObservableList<ClientFidele> clientsList;
    
    // Champs de formulaire
    private TextField txtCin, txtNom, txtPrenom;
    private TextField txtRecherche;
    
    public ClientView(Pharmacie pharmacie, Stage primaryStage) {
        this.pharmacie = pharmacie;
        this.primaryStage = primaryStage;
        this.clientsList = FXCollections.observableArrayList(pharmacie.getListeClientsFideles());
        createView();
    }
    
    private void createView() {
        root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f5f5f5;");
        
        // En-tete
        HBox header = createHeader();
        
        // Zone de recherche
        HBox searchBox = createSearchBox();
        
        // Tableau des clients
        tableView = createTableView();
        
        // Formulaire d'ajout
        VBox formBox = createFormBox();
        
        // Boutons d'action
        HBox actionButtons = createActionButtons();
        
        root.getChildren().addAll(header, searchBox, tableView, formBox, actionButtons);
        VBox.setVgrow(tableView, Priority.ALWAYS);
    }
    
    private HBox createHeader() {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(20);
        
        Button btnRetour = new Button("← Retour");
        btnRetour.setStyle("-fx-background-color: #607D8B; -fx-text-fill: white;");
        btnRetour.setOnAction(e -> retourAccueil());
        
        Label titre = new Label("Gestion des Clients Fideles");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titre.setTextFill(Color.web("#333"));
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label count = new Label("Total: " + clientsList.size() + " clients");
        count.setFont(Font.font("Arial", 14));
        
        header.getChildren().addAll(btnRetour, titre, spacer, count);
        
        return header;
    }
    
    private HBox createSearchBox() {
        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.setPadding(new Insets(10));
        searchBox.setStyle("-fx-background-color: white; -fx-background-radius: 5;");
        
        Label lblRecherche = new Label("Rechercher par nom:");
        txtRecherche = new TextField();
        txtRecherche.setPromptText("Entrez le nom...");
        txtRecherche.setPrefWidth(250);
        
        Button btnRechercher = new Button("Rechercher");
        btnRechercher.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        btnRechercher.setOnAction(e -> rechercher());
        
        Button btnReset = new Button("Reinitialiser");
        btnReset.setStyle("-fx-background-color: #9E9E9E; -fx-text-fill: white;");
        btnReset.setOnAction(e -> {
            txtRecherche.clear();
            refreshTable();
        });
        
        Button btnTrierNom = new Button("Trier par nom");
        btnTrierNom.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        btnTrierNom.setOnAction(e -> trierParNom());
        
        searchBox.getChildren().addAll(lblRecherche, txtRecherche, btnRechercher, btnReset, btnTrierNom);
        
        return searchBox;
    }
    
    private TableView<ClientFidele> createTableView() {
        TableView<ClientFidele> table = new TableView<>();
        table.setItems(clientsList);
        
        TableColumn<ClientFidele, Long> colCin = new TableColumn<>("CIN");
        colCin.setCellValueFactory(new PropertyValueFactory<>("cin"));
        colCin.setPrefWidth(100);
        
        TableColumn<ClientFidele, String> colNom = new TableColumn<>("Nom");
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colNom.setPrefWidth(150);
        
        TableColumn<ClientFidele, String> colPrenom = new TableColumn<>("Prenom");
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colPrenom.setPrefWidth(150);
        
        TableColumn<ClientFidele, Double> colCredit = new TableColumn<>("Credit (DT)");
        colCredit.setCellValueFactory(new PropertyValueFactory<>("credit"));
        colCredit.setPrefWidth(100);
        
        TableColumn<ClientFidele, Double> colMontant = new TableColumn<>("Total Achats (DT)");
        colMontant.setCellValueFactory(new PropertyValueFactory<>("montantTotalAchats"));
        colMontant.setPrefWidth(120);
        
        // Colonne pour indiquer si reduction applicable
        TableColumn<ClientFidele, String> colReduction = new TableColumn<>("Reduction");
        colReduction.setCellValueFactory(cellData -> {
            boolean aReduction = cellData.getValue().getMontantTotalAchats() >= 100;
            return new javafx.beans.property.SimpleStringProperty(aReduction ? "15% applicable" : "-");
        });
        colReduction.setPrefWidth(100);
        
        table.getColumns().addAll(colCin, colNom, colPrenom, colCredit, colMontant, colReduction);
        
        // Style pour les clients avec reduction
        table.setRowFactory(tv -> new TableRow<ClientFidele>() {
            @Override
            protected void updateItem(ClientFidele item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else if (item.getMontantTotalAchats() >= 100) {
                    setStyle("-fx-background-color: #C8E6C9;"); // Vert clair
                } else {
                    setStyle("");
                }
            }
        });
        
        return table;
    }
    
    private VBox createFormBox() {
        VBox formBox = new VBox(10);
        formBox.setPadding(new Insets(15));
        formBox.setStyle("-fx-background-color: white; -fx-background-radius: 5;");
        
        Label lblForm = new Label("Ajouter un nouveau client fidele");
        lblForm.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        HBox ligne1 = new HBox(15);
        ligne1.setAlignment(Pos.CENTER_LEFT);
        
        txtCin = new TextField();
        txtCin.setPromptText("CIN (8 chiffres)");
        txtCin.setPrefWidth(150);
        
        txtNom = new TextField();
        txtNom.setPromptText("Nom");
        txtNom.setPrefWidth(150);
        
        txtPrenom = new TextField();
        txtPrenom.setPromptText("Prenom");
        txtPrenom.setPrefWidth(150);
        
        Button btnAjouter = new Button("Ajouter le client");
        btnAjouter.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        btnAjouter.setOnAction(e -> ajouterClient());
        
        ligne1.getChildren().addAll(
                new Label("CIN:"), txtCin,
                new Label("Nom:"), txtNom,
                new Label("Prenom:"), txtPrenom,
                btnAjouter
        );
        
        formBox.getChildren().addAll(lblForm, ligne1);
        
        return formBox;
    }
    
    private HBox createActionButtons() {
        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER);
        
        Button btnSupprimer = new Button("Supprimer selection");
        btnSupprimer.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        btnSupprimer.setOnAction(e -> supprimerClient());
        
        Button btnModifier = new Button("Modifier selection");
        btnModifier.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white;");
        btnModifier.setOnAction(e -> modifierClient());
        
        Button btnDetails = new Button("Voir details");
        btnDetails.setStyle("-fx-background-color: #9C27B0; -fx-text-fill: white;");
        btnDetails.setOnAction(e -> voirDetails());
        
        buttons.getChildren().addAll(btnSupprimer, btnModifier, btnDetails);
        
        return buttons;
    }
    
    private void rechercher() {
        String recherche = txtRecherche.getText().trim();
        if (recherche.isEmpty()) {
            refreshTable();
            return;
        }
        
        List<ClientFidele> resultats = pharmacie.getClientDAO().rechercherParNom(recherche);
        clientsList.setAll(resultats);
    }
    
    private void trierParNom() {
        List<ClientFidele> tries = pharmacie.trierClientsParNom();
        clientsList.setAll(tries);
    }
    
    private void ajouterClient() {
        try {
            validerFormulaire();
            
            long cin = Long.parseLong(txtCin.getText().trim());
            String nom = txtNom.getText().trim();
            String prenom = txtPrenom.getText().trim();
            
            ClientFidele client = new ClientFidele(cin, nom, prenom);
            pharmacie.ajouterClient(client);
            refreshTable();
            clearForm();
            showInfo("Client ajoute avec succes!");
            
        } catch (NumberFormatException e) {
            showError("Erreur de saisie", "Le CIN doit etre un nombre de 8 chiffres.");
        } catch (SaisieInvalideException e) {
            showError("Erreur de validation", e.getMessage());
        } catch (Exception e) {
            showError("Erreur", e.getMessage());
        }
    }
    
    private void validerFormulaire() throws SaisieInvalideException {
        String cin = txtCin.getText().trim();
        if (cin.isEmpty()) {
            throw new SaisieInvalideException("CIN", "Le CIN est obligatoire");
        }
        if (cin.length() != 8) {
            throw new SaisieInvalideException("CIN", "Le CIN doit contenir 8 chiffres");
        }
        if (txtNom.getText().trim().isEmpty()) {
            throw new SaisieInvalideException("Nom", "Le nom est obligatoire");
        }
        if (txtPrenom.getText().trim().isEmpty()) {
            throw new SaisieInvalideException("Prenom", "Le prenom est obligatoire");
        }
    }
    
    private void supprimerClient() {
        ClientFidele selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Selection requise", "Veuillez selectionner un client a supprimer.");
            return;
        }
        
        Optional<ButtonType> result = showConfirmation(
                "Confirmer la suppression",
                "Etes-vous sur de vouloir supprimer le client: " + selected.getNomComplet() + "?");
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            pharmacie.supprimerClient(selected.getCin());
            refreshTable();
            showInfo("Client supprime avec succes!");
        }
    }
    
    private void modifierClient() {
        ClientFidele selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Selection requise", "Veuillez selectionner un client a modifier.");
            return;
        }
        
        // Remplir le formulaire avec les donnees du client selectionne
        txtCin.setText(String.valueOf(selected.getCin()));
        txtCin.setDisable(true); // CIN non modifiable
        txtNom.setText(selected.getNom());
        txtPrenom.setText(selected.getPrenom());
        
        showInfo("Modifiez les champs puis cliquez sur 'Ajouter' pour enregistrer.");
    }
    
    private void voirDetails() {
        ClientFidele selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Selection requise", "Veuillez selectionner un client.");
            return;
        }
        
        String details = String.format(
                "=== DETAILS DU CLIENT ===\n\n" +
                "CIN: %d\n" +
                "Nom complet: %s\n" +
                "Credit: %.2f DT\n" +
                "Total des achats: %.2f DT\n\n" +
                "Statut reduction: %s",
                selected.getCin(),
                selected.getNomComplet(),
                selected.getCredit(),
                selected.getMontantTotalAchats(),
                selected.aReduction() ? "REDUCTION 15% DISPONIBLE!" : "Pas de reduction (achats < 100 DT)"
        );
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Details du client");
        alert.setHeaderText(selected.getNomComplet());
        alert.setContentText(details);
        alert.showAndWait();
    }
    
    private void refreshTable() {
        pharmacie.chargerDonnees();
        clientsList.setAll(pharmacie.getListeClientsFideles());
        txtCin.setDisable(false);
    }
    
    private void clearForm() {
        txtCin.clear();
        txtCin.setDisable(false);
        txtNom.clear();
        txtPrenom.clear();
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
    
    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private Optional<ButtonType> showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait();
    }
    
    public VBox getView() {
        return root;
    }
}
