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
import exception.SaisieInvalideException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Interface graphique pour la gestion des medicaments.
 * Permet d'ajouter, supprimer, rechercher et afficher les medicaments.
 */
public class MedicamentView {
    
    private Pharmacie pharmacie;
    private Stage primaryStage;
    private VBox root;
    private TableView<Medicament> tableView;
    private ObservableList<Medicament> medicamentsList;
    
    // Champs de formulaire
    private TextField txtNom, txtGenre, txtPrix, txtNumSerie;
    private TextField txtConstituant, txtPlante;
    private Spinner<Integer> spinnerAge, spinnerStock;
    private DatePicker dateExpiration;
    private ComboBox<String> comboType;
    private TextField txtRecherche;
    private ComboBox<String> comboRecherche;
    
    public MedicamentView(Pharmacie pharmacie, Stage primaryStage) {
        this.pharmacie = pharmacie;
        this.primaryStage = primaryStage;
        this.medicamentsList = FXCollections.observableArrayList(pharmacie.getListeMedicaments());
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
        
        // Tableau des medicaments
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
        
        Label titre = new Label("Gestion des Medicaments");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titre.setTextFill(Color.web("#333"));
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label count = new Label("Total: " + medicamentsList.size() + " medicaments");
        count.setFont(Font.font("Arial", 14));
        
        header.getChildren().addAll(btnRetour, titre, spacer, count);
        
        return header;
    }
    
    private HBox createSearchBox() {
        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.setPadding(new Insets(10));
        searchBox.setStyle("-fx-background-color: white; -fx-background-radius: 5;");
        
        Label lblRecherche = new Label("Rechercher:");
        txtRecherche = new TextField();
        txtRecherche.setPromptText("Entrez votre recherche...");
        txtRecherche.setPrefWidth(250);
        
        comboRecherche = new ComboBox<>();
        comboRecherche.getItems().addAll("Par nom", "Par categorie", "Par premieres lettres");
        comboRecherche.setValue("Par nom");
        
        Button btnRechercher = new Button("Rechercher");
        btnRechercher.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        btnRechercher.setOnAction(e -> rechercher());
        
        Button btnReset = new Button("Reinitialiser");
        btnReset.setStyle("-fx-background-color: #9E9E9E; -fx-text-fill: white;");
        btnReset.setOnAction(e -> {
            txtRecherche.clear();
            refreshTable();
        });
        
        Button btnExpirants = new Button("Medicaments expirants (2 mois)");
        btnExpirants.setStyle("-fx-background-color: #FF5722; -fx-text-fill: white;");
        btnExpirants.setOnAction(e -> afficherExpirants());
        
        searchBox.getChildren().addAll(lblRecherche, txtRecherche, comboRecherche, 
                btnRechercher, btnReset, btnExpirants);
        
        return searchBox;
    }
    
    private TableView<Medicament> createTableView() {
        TableView<Medicament> table = new TableView<>();
        table.setItems(medicamentsList);
        
        TableColumn<Medicament, Long> colCode = new TableColumn<>("Code");
        colCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        colCode.setPrefWidth(60);
        
        TableColumn<Medicament, String> colNom = new TableColumn<>("Nom");
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colNom.setPrefWidth(120);
        
        TableColumn<Medicament, String> colGenre = new TableColumn<>("Genre");
        colGenre.setCellValueFactory(new PropertyValueFactory<>("genre"));
        colGenre.setPrefWidth(100);
        
        TableColumn<Medicament, Double> colPrix = new TableColumn<>("Prix (DT)");
        colPrix.setCellValueFactory(new PropertyValueFactory<>("prix"));
        colPrix.setPrefWidth(80);
        
        TableColumn<Medicament, String> colType = new TableColumn<>("Type");
        colType.setCellValueFactory(new PropertyValueFactory<>("typeMedicament"));
        colType.setPrefWidth(100);
        
        TableColumn<Medicament, LocalDate> colExpiration = new TableColumn<>("Expiration");
        colExpiration.setCellValueFactory(new PropertyValueFactory<>("dateExpiration"));
        colExpiration.setPrefWidth(100);
        
        TableColumn<Medicament, Integer> colStock = new TableColumn<>("Stock");
        colStock.setCellValueFactory(new PropertyValueFactory<>("quantiteStock"));
        colStock.setPrefWidth(60);
        
        table.getColumns().addAll(colCode, colNom, colGenre, colPrix, colType, colExpiration, colStock);
        
        // Style pour les lignes expirant bientot
        table.setRowFactory(tv -> new TableRow<Medicament>() {
            @Override
            protected void updateItem(Medicament item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else if (item.expireDans(2)) {
                    setStyle("-fx-background-color: #FFCDD2;"); // Rouge clair
                } else if (item.expireDans(6)) {
                    setStyle("-fx-background-color: #FFF9C4;"); // Jaune clair
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
        
        Label lblForm = new Label("Ajouter un nouveau medicament");
        lblForm.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        // Ligne 1: Informations de base
        HBox ligne1 = new HBox(15);
        ligne1.setAlignment(Pos.CENTER_LEFT);
        
        txtNom = new TextField();
        txtNom.setPromptText("Nom");
        txtNom.setPrefWidth(150);
        
        txtGenre = new TextField();
        txtGenre.setPromptText("Genre");
        txtGenre.setPrefWidth(120);
        
        txtPrix = new TextField();
        txtPrix.setPromptText("Prix (DT)");
        txtPrix.setPrefWidth(80);
        
        txtNumSerie = new TextField();
        txtNumSerie.setPromptText("Num Serie");
        txtNumSerie.setPrefWidth(100);
        
        comboType = new ComboBox<>();
        comboType.getItems().addAll("CHIMIQUE", "HOMEOPATHIQUE");
        comboType.setValue("CHIMIQUE");
        comboType.setOnAction(e -> updateFormFields());
        
        ligne1.getChildren().addAll(
                new Label("Nom:"), txtNom,
                new Label("Genre:"), txtGenre,
                new Label("Prix:"), txtPrix,
                new Label("N° Serie:"), txtNumSerie,
                new Label("Type:"), comboType
        );
        
        // Ligne 2: Details specifiques
        HBox ligne2 = new HBox(15);
        ligne2.setAlignment(Pos.CENTER_LEFT);
        
        txtConstituant = new TextField();
        txtConstituant.setPromptText("Constituant chimique");
        txtConstituant.setPrefWidth(150);
        
        txtPlante = new TextField();
        txtPlante.setPromptText("Plante utilisee");
        txtPlante.setPrefWidth(150);
        txtPlante.setVisible(false);
        
        spinnerAge = new Spinner<>(0, 100, 0);
        spinnerAge.setPrefWidth(80);
        
        spinnerStock = new Spinner<>(0, 10000, 0);
        spinnerStock.setPrefWidth(80);
        
        dateExpiration = new DatePicker();
        dateExpiration.setValue(LocalDate.now().plusYears(1));
        
        Label lblConstituant = new Label("Constituant:");
        Label lblPlante = new Label("Plante:");
        lblPlante.setVisible(false);
        Label lblAge = new Label("Age min:");
        
        ligne2.getChildren().addAll(
                lblConstituant, txtConstituant,
                lblPlante, txtPlante,
                lblAge, spinnerAge,
                new Label("Stock:"), spinnerStock,
                new Label("Expiration:"), dateExpiration
        );
        
        // Bouton d'ajout
        Button btnAjouter = new Button("Ajouter le medicament");
        btnAjouter.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        btnAjouter.setOnAction(e -> ajouterMedicament());
        
        formBox.getChildren().addAll(lblForm, ligne1, ligne2, btnAjouter);
        
        return formBox;
    }
    
    private void updateFormFields() {
        boolean isChimique = "CHIMIQUE".equals(comboType.getValue());
        txtConstituant.setVisible(isChimique);
        txtPlante.setVisible(!isChimique);
        
        // Mettre a jour les labels
        ((HBox) txtConstituant.getParent()).getChildren().forEach(node -> {
            if (node instanceof Label lbl) {
                if (lbl.getText().equals("Constituant:")) lbl.setVisible(isChimique);
                if (lbl.getText().equals("Plante:")) lbl.setVisible(!isChimique);
                if (lbl.getText().equals("Age min:")) lbl.setVisible(isChimique);
            }
        });
        spinnerAge.setVisible(isChimique);
    }
    
    private HBox createActionButtons() {
        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER);
        
        Button btnSupprimer = new Button("Supprimer selection");
        btnSupprimer.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        btnSupprimer.setOnAction(e -> supprimerMedicament());
        
        Button btnModifier = new Button("Modifier selection");
        btnModifier.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white;");
        btnModifier.setOnAction(e -> modifierMedicament());
        
        Button btnRemise = new Button("Appliquer remise 30% (expirants)");
        btnRemise.setStyle("-fx-background-color: #9C27B0; -fx-text-fill: white;");
        btnRemise.setOnAction(e -> appliquerRemise());
        
        buttons.getChildren().addAll(btnSupprimer, btnModifier, btnRemise);
        
        return buttons;
    }
    
    private void rechercher() {
        String recherche = txtRecherche.getText().trim();
        if (recherche.isEmpty()) {
            refreshTable();
            return;
        }
        
        List<Medicament> resultats;
        switch (comboRecherche.getValue()) {
            case "Par categorie":
                resultats = pharmacie.rechercherMedicamentsParCategorie(recherche);
                break;
            case "Par premieres lettres":
                resultats = pharmacie.rechercherMedicamentsParPremieresLettres(recherche);
                break;
            default: // Par nom
                resultats = pharmacie.rechercherMedicamentsParNom(recherche);
                break;
        }
        
        medicamentsList.setAll(resultats);
    }
    
    private void afficherExpirants() {
        List<Medicament> expirants = pharmacie.getMedicamentsExpirantDans(2);
        medicamentsList.setAll(expirants);
        
        if (expirants.isEmpty()) {
            showInfo("Aucun medicament n'expire dans les 2 prochains mois.");
        } else {
            showInfo(expirants.size() + " medicament(s) expirent dans les 2 prochains mois.");
        }
    }
    
    private void ajouterMedicament() {
        try {
            validerFormulaire();
            
            String nom = txtNom.getText().trim();
            String genre = txtGenre.getText().trim();
            double prix = Double.parseDouble(txtPrix.getText().trim());
            long numSerie = Long.parseLong(txtNumSerie.getText().trim());
            LocalDate expiration = dateExpiration.getValue();
            int stock = spinnerStock.getValue();
            
            Medicament medicament;
            
            if ("CHIMIQUE".equals(comboType.getValue())) {
                String constituant = txtConstituant.getText().trim();
                int ageMin = spinnerAge.getValue();
                MedicamentChimique mc = new MedicamentChimique(nom, genre, prix, constituant, ageMin);
                mc.setNumSerie(numSerie);
                mc.setDateExpiration(expiration);
                mc.setQuantiteStock(stock);
                medicament = mc;
            } else {
                String plante = txtPlante.getText().trim();
                MedicamentHomeopathique mh = new MedicamentHomeopathique(nom, genre, prix, plante);
                mh.setNumSerie(numSerie);
                mh.setDateExpiration(expiration);
                mh.setQuantiteStock(stock);
                medicament = mh;
            }
            
            pharmacie.ajouterMedicament(medicament);
            refreshTable();
            clearForm();
            showInfo("Medicament ajoute avec succes!");
            
        } catch (NumberFormatException e) {
            showError("Erreur de saisie", "Veuillez entrer des valeurs numeriques valides pour le prix et le numero de serie.");
        } catch (SaisieInvalideException e) {
            showError("Erreur de validation", e.getMessage());
        }
    }
    
    private void validerFormulaire() throws SaisieInvalideException {
        if (txtNom.getText().trim().isEmpty()) {
            throw new SaisieInvalideException("Nom", "Le nom est obligatoire");
        }
        if (txtGenre.getText().trim().isEmpty()) {
            throw new SaisieInvalideException("Genre", "Le genre est obligatoire");
        }
        if (txtPrix.getText().trim().isEmpty()) {
            throw new SaisieInvalideException("Prix", "Le prix est obligatoire");
        }
        if (txtNumSerie.getText().trim().isEmpty()) {
            throw new SaisieInvalideException("Numero de serie", "Le numero de serie est obligatoire");
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
    
    private void supprimerMedicament() {
        Medicament selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Selection requise", "Veuillez selectionner un medicament a supprimer.");
            return;
        }
        
        Optional<ButtonType> result = showConfirmation(
                "Confirmer la suppression",
                "Etes-vous sur de vouloir supprimer le medicament: " + selected.getNom() + "?");
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            pharmacie.supprimerMedicament(selected.getNom());
            refreshTable();
            showInfo("Medicament supprime avec succes!");
        }
    }
    
    private void modifierMedicament() {
        Medicament selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Selection requise", "Veuillez selectionner un medicament a modifier.");
            return;
        }
        
        // Remplir le formulaire avec les donnees du medicament selectionne
        txtNom.setText(selected.getNom());
        txtGenre.setText(selected.getGenre());
        txtPrix.setText(String.valueOf(selected.getPrix()));
        txtNumSerie.setText(String.valueOf(selected.getNumSerie()));
        dateExpiration.setValue(selected.getDateExpiration());
        spinnerStock.getValueFactory().setValue(selected.getQuantiteStock());
        
        if (selected instanceof MedicamentChimique mc) {
            comboType.setValue("CHIMIQUE");
            txtConstituant.setText(mc.getConstituantChimique());
            spinnerAge.getValueFactory().setValue(mc.getAgeMinimum());
        } else if (selected instanceof MedicamentHomeopathique mh) {
            comboType.setValue("HOMEOPATHIQUE");
            txtPlante.setText(mh.getPlanteUtilisee());
        }
        updateFormFields();
        
        showInfo("Modifiez les champs puis cliquez sur 'Ajouter' pour enregistrer.");
    }
    
    private void appliquerRemise() {
        Optional<ButtonType> result = showConfirmation(
                "Confirmer la remise",
                "Appliquer une remise de 30% sur tous les medicaments expirant dans 1 mois?");
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            pharmacie.appliquerRemiseMedicamentsExpirants();
            refreshTable();
            showInfo("Remise appliquee avec succes!");
        }
    }
    
    private void refreshTable() {
        pharmacie.chargerDonnees();
        medicamentsList.setAll(pharmacie.getListeMedicaments());
    }
    
    private void clearForm() {
        txtNom.clear();
        txtGenre.clear();
        txtPrix.clear();
        txtNumSerie.clear();
        txtConstituant.clear();
        txtPlante.clear();
        spinnerAge.getValueFactory().setValue(0);
        spinnerStock.getValueFactory().setValue(0);
        dateExpiration.setValue(LocalDate.now().plusYears(1));
        comboType.setValue("CHIMIQUE");
        updateFormFields();
    }
    
    private void retourAccueil() {
        MainApp mainApp = new MainApp();
        try {
            mainApp.start(primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Methodes utilitaires pour les dialogues
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
