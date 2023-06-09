package com.wrmanager.wrmanagerfx.controllers;

import com.wrmanager.wrmanagerfx.Main;
import com.wrmanager.wrmanagerfx.entities.Categorie;
import com.wrmanager.wrmanagerfx.entities.Fournisseur;
import com.wrmanager.wrmanagerfx.entities.Produit;
import com.wrmanager.wrmanagerfx.entities.Stock;
import com.wrmanager.wrmanagerfx.repositories.ProduitDAO;
import com.wrmanager.wrmanagerfx.requests.PipelineRequests;
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
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.wrmanager.wrmanagerfx.Constants.*;
import static com.wrmanager.wrmanagerfx.Constants.fournisseurService;
import static com.wrmanager.wrmanagerfx.controllers.SideBarController.prefs;
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

    private Produit produit;

    @FXML
    void CameraButtonOnAction(ActionEvent event) {

        try {
            SideBarController.BlurBackground();
            FXMLLoader fxmlLoader =new FXMLLoader(Main.class.getResource("fxml/CaptureCameraDialog.fxml"));
            DialogPane CaptureCameraDialog= fxmlLoader.load();
            CaptureCameraDialog.getStylesheets().add(
                    Main.class.getResource("images/dialog.css").toExternalForm());
            CaptureCameraDialog.setStyle(
                    "primary:"+prefs.get("PrimaryColor","rgba(35, 140, 131, 1)")
                            .replaceAll("0x","#")
                            +";"+"secondary:"+prefs.get("SecondaryColor","#C8E2E0")
                            .replaceAll("0x","#")+";");
            CaptureCameraController captureCameraController  =fxmlLoader.getController();
            Dialog<ButtonType> dialog =new Dialog<>();
            dialog.setDialogPane(CaptureCameraDialog);
            dialog.initStyle(StageStyle.TRANSPARENT);
            captureCameraController.setDialog(dialog);
            CaptureCameraDialog.getScene().setFill(Color.rgb(0,0,0,0));
            BoxBlur boxBlur=new BoxBlur();
            boxBlur.setIterations(10);
            boxBlur.setHeight(7);
            boxBlur.setWidth(7);
            this.dialog.getDialogPane().setEffect(boxBlur);
            var result =  dialog.showAndWait();

            if(result.isPresent() && result.get().equals(ButtonType.YES)){
                System.out.println("rah dkhal");
                ExecutorService executor = Executors.newSingleThreadExecutor();

                executor.submit(() -> {
                    System.out.println("rah baghi yexcuter");
                    var r = PipelineRequests.getStockFromImage("./captured.jpg");
                    System.out.println("r here : " + r);
                    ppaTfd.setText(r.getPpa().toString());

                    lotTfd.setText(r.getLot());

                    var id = r.getProduct_id();


                    if(id!=0) {
                        produit = produitDAO.getById(r.getProduct_id()).get();
                        designationTfd.setText(produit.getDesignation());
                        formTfd.setText(produit.getForme());
                        dosageTfd.setText(produit.getDosage());
                    }

                    DatePicker.setText(r.getDate().toString());





                });

                // Other code or tasks to be executed concurrently

                System.out.println("kolesh kml");
                // Shutdown the executor when you're done with the task
                executor.shutdown();


            }
            this.dialog.getDialogPane().setEffect(null);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }




    private BooleanProperty onEditMode = new SimpleBooleanProperty(false);



    private Boolean addStock(){

        var designation = designationTfd.getText();
        var dosage = dosageTfd.getText();
        var form = formTfd.getText();

        var ppa = Float.valueOf(ppaTfd.getText());
        var lot = lotTfd.getText();
        var date = Date.valueOf(DatePicker.getCurrentDate());
        var qty = Integer.valueOf(qtyTfd.getText());
        var fourniisseur=fournisseurTfd.getText();

        Stock stock = Stock.builder().ppa(ppa.floatValue()).lot(lot).expirationDate(date).qty(qty).fournisseur(fourniisseur).build();
        stockService.save(produit,stock);

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
        var fournisseur = fournisseurTfd.getText();
        Optional<Produit> p = produitDAO.getById(Long.valueOf(2));
        Stock stock = Stock.builder().ppa(ppa.floatValue()).lot(lot).expirationDate(date).qty(qty).fournisseur(fournisseur).produit(getPassedStock().getProduit()).build();

        stockService.update(stock);
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
        qtyTfd.setText(String.valueOf(passedStock.getQty()));

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
