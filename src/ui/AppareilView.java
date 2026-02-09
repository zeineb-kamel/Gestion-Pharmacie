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
import model.AppareilMedical;
import service.Pharmacie;
import exception.SaisieInvalideException;

import java.util.List;
import java.util.Optional;

/**
 * Interface graphique pour la gestion des appareils medicaux.
 */
public class AppareilView {
    
    private Pharmacie pharmacie;
    private Stage primaryStage;
    private VBox root;
    private TableView<AppareilMedical> tableView;
    private ObservableList<AppareilMedical> appareilsList;
    
    // Champs de formulaire
    private TextField txtNom, txtPrix;
    private Spinner<Integer> spinnerStock;
    private TextField txtRecherche;
    
    public AppareilView(Pharmacie pharmacie, Stage primaryStage) {
        this.pharmacie = pharmacie;
        this.primaryStage = primaryStage;
        this.appareilsList = FXCollections.observableArrayList(pharmacie.getListeAppareils());
        createView();
    }
    
    private void createView() {
        root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f5f5f5;");
        
        HBox header = createHeader();
        HBox searchBox = createSearchBox();
        tableView = createTableView();
        VBox formBox = createFormBox();
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
        
        Label titre = new Label("Gestion des Appareils Medicaux");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titre.setTextFill(Color.web("#333"));
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label count = new Label("Total: " + appareilsList.size() + " appareils");
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
        
        searchBox.getChildren().addAll(lblRecherche, txtRecherche, btnRechercher, btnReset);
        
        return searchBox;
    }
    
    private TableView<AppareilMedical> createTableView() {
        TableView<AppareilMedical> table = new TableView<>();
        table.setItems(appareilsList);
        
        TableColumn<AppareilMedical, Long> colCode = new TableColumn<>("Code");
        colCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        colCode.setPrefWidth(80);
        
        TableColumn<AppareilMedical, String> colNom = new TableColumn<>("Nom");
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colNom.setPrefWidth(250);
        
        TableColumn<AppareilMedical, Double> colPrix = new TableColumn<>("Prix (DT)");
        colPrix.setCellValueFactory(new PropertyValueFactory<>("prix"));
        colPrix.setPrefWidth(100);
        
        TableColumn<AppareilMedical, Double> colTranche = new TableColumn<>("Tranche (DT)");
        colTranche.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleDoubleProperty(
                        cellData.getValue().getTranche(true)).asObject());
        colTranche.setPrefWidth(100);
        
        TableColumn<AppareilMedical, Integer> colStock = new TableColumn<>("Stock");
        colStock.setCellValueFactory(new PropertyValueFactory<>("quantiteStock"));
        colStock.setPrefWidth(80);
        
        table.getColumns().addAll(colCode, colNom, colPrix, colTranche, colStock);
        
        return table;
    }
    
    private VBox createFormBox() {
        VBox formBox = new VBox(10);
        formBox.setPadding(new Insets(15));
        formBox.setStyle("-fx-background-color: white; -fx-background-radius: 5;");
        
        Label lblForm = new Label("Ajouter un nouvel appareil medical");
        lblForm.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        HBox ligne1 = new HBox(15);
        ligne1.setAlignment(Pos.CENTER_LEFT);
        
        txtNom = new TextField();
        txtNom.setPromptText("Nom de l'appareil");
        txtNom.setPrefWidth(200);
        
        txtPrix = new TextField();
        txtPrix.setPromptText("Prix (DT)");
        txtPrix.setPrefWidth(100);
        
        spinnerStock = new Spinner<>(0, 10000, 0);
        spinnerStock.setPrefWidth(80);
        
        Button btnAjouter = new Button("Ajouter l'appareil");
        btnAjouter.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-weight: bold;");
        btnAjouter.setOnAction(e -> ajouterAppareil());
        
        ligne1.getChildren().addAll(
                new Label("Nom:"), txtNom,
                new Label("Prix:"), txtPrix,
                new Label("Stock:"), spinnerStock,
                btnAjouter
        );
        
        // Note sur la facilite de paiement
        Label note = new Label("Note: Les clients fideles peuvent payer en 3 tranches (facilite de paiement)");
        note.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        note.setTextFill(Color.GRAY);
        
        formBox.getChildren().addAll(lblForm, ligne1, note);
        
        return formBox;
    }
    
    private HBox createActionButtons() {
        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER);
        
        Button btnSupprimer = new Button("Supprimer selection");
        btnSupprimer.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        btnSupprimer.setOnAction(e -> supprimerAppareil());
        
        Button btnModifier = new Button("Modifier selection");
        btnModifier.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white;");
        btnModifier.setOnAction(e -> modifierAppareil());
        
        buttons.getChildren().addAll(btnSupprimer, btnModifier);
        
        return buttons;
    }
    
    private void rechercher() {
        String recherche = txtRecherche.getText().trim();
        if (recherche.isEmpty()) {
            refreshTable();
            return;
        }
        
        List<AppareilMedical> resultats = pharmacie.getAppareilDAO().rechercherParNom(recherche);
        appareilsList.setAll(resultats);
    }
    
    private void ajouterAppareil() {
        try {
            validerFormulaire();
            
            String nom = txtNom.getText().trim();
            double prix = Double.parseDouble(txtPrix.getText().trim());
            int stock = spinnerStock.getValue();
            
            AppareilMedical appareil = new AppareilMedical(nom, prix);
            appareil.setQuantiteStock(stock);
            
            pharmacie.ajouterAppareil(appareil);
            refreshTable();
            clearForm();
            showInfo("Appareil ajoute avec succes!");
            
        } catch (NumberFormatException e) {
            showError("Erreur de saisie", "Le prix doit etre un nombre valide.");
        } catch (SaisieInvalideException e) {
            showError("Erreur de validation", e.getMessage());
        }
    }
    
    private void validerFormulaire() throws SaisieInvalideException {
        if (txtNom.getText().trim().isEmpty()) {
            throw new SaisieInvalideException("Nom", "Le nom est obligatoire");
        }
        if (txtPrix.getText().trim().isEmpty()) {
            throw new SaisieInvalideException("Prix", "Le prix est obligatoire");
        }
        try {
            double prix = Double.parseDouble(txtPrix.getText().trim());
            if (prix < 0) {
                throw new SaisieInvalideException("Prix", "Le prix doit etre positif");
            }
        } catch (NumberFormatException e) {
            throw new SaisieInvalideException("Prix", "Le prix doit etre un nombre valide");
        }
    }
    
    private void supprimerAppareil() {
        AppareilMedical selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Selection requise", "Veuillez selectionner un appareil a supprimer.");
            return;
        }
        
        Optional<ButtonType> result = showConfirmation(
                "Confirmer la suppression",
                "Etes-vous sur de vouloir supprimer l'appareil: " + selected.getNom() + "?");
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            pharmacie.supprimerAppareil(selected.getCode());
            refreshTable();
            showInfo("Appareil supprime avec succes!");
        }
    }
    
    private void modifierAppareil() {
        AppareilMedical selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Selection requise", "Veuillez selectionner un appareil a modifier.");
            return;
        }
        
        txtNom.setText(selected.getNom());
        txtPrix.setText(String.valueOf(selected.getPrix()));
        spinnerStock.getValueFactory().setValue(selected.getQuantiteStock());
        
        showInfo("Modifiez les champs puis cliquez sur 'Ajouter' pour enregistrer.");
    }
    
    private void refreshTable() {
        pharmacie.chargerDonnees();
        appareilsList.setAll(pharmacie.getListeAppareils());
    }
    
    private void clearForm() {
        txtNom.clear();
        txtPrix.clear();
        spinnerStock.getValueFactory().setValue(0);
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
