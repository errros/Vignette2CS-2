package com.wrmanager.wrmanagerfx.controllers;

import com.wrmanager.wrmanagerfx.entities.Categorie;
import com.wrmanager.wrmanagerfx.entities.Fournisseur;
import com.wrmanager.wrmanagerfx.entities.Produit;
import com.wrmanager.wrmanagerfx.entities.Stock;
import com.wrmanager.wrmanagerfx.repositories.ProduitDAO;
import com.wrmanager.wrmanagerfx.services.ProduitService;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.sql.Date;
import java.util.Optional;
import java.util.ResourceBundle;

import static com.wrmanager.wrmanagerfx.Constants.*;
import static com.wrmanager.wrmanagerfx.Constants.fournisseurService;
import static com.wrmanager.wrmanagerfx.validation.Validators.*;
import static com.wrmanager.wrmanagerfx.validation.Validators.isNumValid;

public class AjouterStockDialogController implements Initializable {



    private Dialog<ButtonType> dialog;
    //passed from the the first
    private Stock passedStock;


    @FXML
    private MFXButton AjouterButton;

    @FXML
    private MFXButton AjouterEtResterButton;


    @FXML
    private MFXButton AnnulerButton;

    @FXML
    private Button CameraButton;

    @FXML
    private Label Cod;

    @FXML
    private HBox buttonsHb;

    @FXML
    private TextField designationTfd;

    @FXML
    private TextField dosageTfd;

    @FXML
    private TextField formTfd;

    @FXML
    private TextField fournisseurTfd;

    @FXML
    private TextField lotTfd;

    @FXML
    private TextField ppaTfd;

    @FXML
    private TextField qtyTfd;

    @FXML
    private Label titleLabel;

    @FXML
    private MFXDatePicker DatePicker;

    @FXML
    private VBox vbox;


    @FXML
    void CameraButtonOnAction(ActionEvent event) {

    }




    private BooleanProperty onEditMode = new SimpleBooleanProperty(false);



    private Boolean addStock(){
        var designation = designationTfd.getText();
        var dosage = dosageTfd.getText();
        var form = formTfd.getText();

        var ppa = Integer.valueOf(ppaTfd.getText());
        var lot = lotTfd.getText();
        var date = Date.valueOf(DatePicker.getCurrentDate());
        var qty = Integer.valueOf(qtyTfd.getText());
        Optional<Produit> p = produitDAO.getById(Long.valueOf(2));

        Stock stock = Stock.builder().ppa(ppa).lot(lot).expirationDate(date).qty(qty).produit(p.get()).build();

        return true ;


    }

    //Modifier button too
    @FXML
    void AjouterButtonOnAction(ActionEvent event) {

        if(addStock()) {
            dialog.setResult(ButtonType.CLOSE);
            dialog.close();

        }
    }


    EventHandler<ActionEvent> ModifierButtonOnActionOnEditMode() {

        return new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if(updateFournisseur()) {
                    dialog.setResult(ButtonType.CLOSE);
                    dialog.close();

                }

            }


        };

    }

    //on editMode
    private Boolean updateFournisseur() {
        var designation = designationTfd.getText();
        var dosage = dosageTfd.getText();
        var form = formTfd.getText();

        var ppa = Integer.valueOf(ppaTfd.getText());
        var lot = lotTfd.getText();
        var date = Date.valueOf(DatePicker.getCurrentDate());
        var qty = Integer.valueOf(qtyTfd.getText());
        Optional<Produit> p = produitDAO.getById(Long.valueOf(2));
        Stock stock = Stock.builder().ppa(ppa).lot(lot).expirationDate(date).qty(qty).produit(p.get()).build();

        return true ;



    }


    @FXML
    void AjouterEtResterButtonOnAction(ActionEvent event) {


        if(addStock()){
            viderInput();
        }
    }

    private void viderInput(){

        designationTfd.setText("");
        formTfd.setText("");
        dosageTfd.setText("");
        lotTfd.setText("");
        DatePicker.setText("");
        ppaTfd.setText("");
        fournisseurTfd.setText("");
    }

    @FXML
    void AnnulerButtonOnAction(ActionEvent event) {
        dialog.setResult(ButtonType.CLOSE);
        dialog.close();
    }
    public void setDialog(Dialog<ButtonType> dialog) {
        this.dialog = dialog;
    }


    public Stock getPassedStock() {
        return passedStock;
    }

    private void updateInputTextFields(){
        designationTfd.setText(passedStock.getProduit().getDesignation());
        formTfd.setText(passedStock.getProduit().getForme());
        dosageTfd.setText(passedStock.getProduit().getDosage());
        lotTfd.setText(passedStock.getLot());
        DatePicker.setText(String.valueOf(passedStock.getExpirationDate().toLocalDate()));
        ppaTfd.setText(String.valueOf(passedStock.getPpa()));
        fournisseurTfd.setText(passedStock.getFournisseur());

    }

    public void setPassedStock(Stock passedStock) {
        this.passedStock = passedStock;

        updateInputTextFields();

    }

    public boolean isOnEditMode() {
        return onEditMode.get();
    }

    public BooleanProperty onEditModeProperty() {
        return onEditMode;
    }

    public void setOnEditMode(boolean onEditMode) {
        this.onEditMode.set(onEditMode);
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        //Ajouter btn turn into ModifierBTN

        onEditMode.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                if (t1) {
                    titleLabel.setText("Modifier un Stock");
                    AjouterButton.setText("Modifier");
                    AjouterButton.setOnAction(ModifierButtonOnActionOnEditMode());
                    buttonsHb.getChildren().remove(AjouterEtResterButton);
                }

            }
        });

    }





}
