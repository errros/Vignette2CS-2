package com.wrmanager.wrmanagerfx.controllers;

import com.wrmanager.wrmanagerfx.Constants;
import com.wrmanager.wrmanagerfx.Main;
import com.wrmanager.wrmanagerfx.entities.*;
import com.wrmanager.wrmanagerfx.entities.Stock;
import com.wrmanager.wrmanagerfx.models.SystemMeasure;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import javax.persistence.RollbackException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Comparator;
import java.util.ResourceBundle;
import java.util.function.Predicate;

import static com.wrmanager.wrmanagerfx.Constants.*;
import static com.wrmanager.wrmanagerfx.controllers.SideBarController.prefs;

public class StockViewController implements Initializable {

    @FXML
    private MFXButton AjouterButton;

    @FXML
    private Label Label1;

    @FXML
    private Label Label2;

    @FXML
    private Label Label3;

    @FXML
    private Label Label4;

    @FXML
    private TableView<Stock> StocksTable;

    public TableView<Stock> getStocksTable() {
        return StocksTable;
    }

    @FXML
    private TextField SearchBarTextField;

    @FXML
    private MFXButton TelechargerButton;

    @FXML
    private AnchorPane icon;

    @FXML
    private AnchorPane icon1;

    @FXML
    private AnchorPane icon2;

    @FXML
    private AnchorPane icon3;

    GridPane root;




    TableColumn lotColumn = new TableColumn("Lot");
    TableColumn designationColumn = new TableColumn("Designation");
    TableColumn dosageColumn = new TableColumn("Dosage");
    TableColumn formColumn = new TableColumn("Forme");
    TableColumn fournisseurColumn = new TableColumn("Fournisseur");
    TableColumn ppaColumn =new TableColumn("PPA");
    TableColumn qtyColumn =new TableColumn("Qty");
    TableColumn<Stock, Void> ActionsColumn = new TableColumn("Actions");





    @FXML
    void AjouterButtonOnAction(ActionEvent event) {
        try {
            SideBarController.BlurBackground();
            FXMLLoader fxmlLoader =new FXMLLoader(Main.class.getResource(Constants.AjouterStockDialogView));
            DialogPane AjouterStockDialog= fxmlLoader.load();
            AjouterStockDialog.getStylesheets().add(
                    Main.class.getResource("images/dialog.css").toExternalForm());
            AjouterStockDialog.setStyle(
                    "primary:"+prefs.get("PrimaryColor","rgba(35, 140, 131, 1)")
                            .replaceAll("0x","#")
                            +";"+"secondary:"+prefs.get("SecondaryColor","#C8E2E0")
                            .replaceAll("0x","#")+";");
            AjouterStockDialogController ajouterStockDialogController=fxmlLoader.getController();
            Dialog<ButtonType> dialog =new Dialog<>();
            dialog.setDialogPane(AjouterStockDialog);
            ajouterStockDialogController.setDialog(dialog);
            dialog.initStyle(StageStyle.TRANSPARENT);
            AjouterStockDialog.getScene().setFill(Color.rgb(0,0,0,0));
            ajouterStockDialogController.setDialog(dialog);
            dialog.showAndWait();
            SideBarController.DeleteBlurBackground();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }



    private Predicate<Stock> createFilteringPredicate(String searchText){
        return Stock -> {
            if (searchText == null || searchText.isEmpty()) return true;
            return searchFindsStock(Stock,searchText);
        };
    }

    private boolean searchFindsStock(Stock Stock, String searchText){
        return (Stock.getProduit().getDesignation().toLowerCase().contains(searchText.toLowerCase()));
    }






    private void divideTableWidthOnColumns(){

        Double size = Double.valueOf(StocksTable.getColumns().size());

        //calculate the size of each column
        double sizeCoulumn = 1 / (size);
        //set the size of each column
        lotColumn.prefWidthProperty().bind(StocksTable.widthProperty().multiply(sizeCoulumn));
        lotColumn.setSortable(true);
        designationColumn.prefWidthProperty().bind(StocksTable.widthProperty().multiply(sizeCoulumn));
        dosageColumn.prefWidthProperty().bind(StocksTable.widthProperty().multiply(sizeCoulumn));
        formColumn.prefWidthProperty().bind(StocksTable.widthProperty().multiply(sizeCoulumn));
        ppaColumn.prefWidthProperty().bind(StocksTable.widthProperty().multiply(sizeCoulumn));
        qtyColumn.prefWidthProperty().bind(StocksTable.widthProperty().multiply(sizeCoulumn));
        ActionsColumn.prefWidthProperty().bind(StocksTable.widthProperty().multiply((sizeCoulumn*1.5)));


    }

    private void setupValueFactories(){

        //define the cells factory
        lotColumn.setCellValueFactory(
                new PropertyValueFactory<Stock, String>("codeBarre"));
        designationColumn.setCellValueFactory(
                new PropertyValueFactory<Stock, String>("designation"));

        formColumn.setCellValueFactory(
                new PropertyValueFactory<Stock, SystemMeasure>("Forme"));
        dosageColumn.setCellValueFactory(
                new PropertyValueFactory<Stock, Integer>("Dosage"));

        ppaColumn.setCellValueFactory(
                new PropertyValueFactory<Stock, Integer>("PPA"));

        qtyColumn.setCellValueFactory(
                new PropertyValueFactory<Stock, Integer>("Qty"));

        setupActionsCellValueFactory();


    }





    private void setupActionsCellValueFactory() {
        Callback<TableColumn<Stock, Void>, TableCell<Stock, Void>> cellFactory = new Callback<TableColumn<Stock, Void>, TableCell<Stock, Void>>() {
            @Override
            public TableCell<Stock, Void> call(final TableColumn<Stock, Void> param) {
                final TableCell<Stock, Void> cell = new TableCell<Stock, Void>() {

                    private final MFXButton trushBt = new MFXButton("");
                    private final MFXButton editBt = new MFXButton("");

                    private final HBox hBox =new HBox();


                    {
                        trushBt.setStyle("-fx-background-color:rgba(0,0,0,0);");
                        FontAwesomeIconView trush =new FontAwesomeIconView(FontAwesomeIcon.TRASH);
                        trush.setSize("22.0");
                        trush.setFill(Color.rgb(193,39,39));
                        trushBt.setGraphic(trush);
                        trushBt.setOnAction((ActionEvent event) -> {
                            Stock data = getTableView().getItems().get(getIndex());

                            //Show the dialog pane
                            try {
                                SideBarController.BlurBackground();
                                FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(Constants.OuiNonDialogView));
                                DialogPane OuiNonDialog = fxmlLoader.load();
                                OuiNonDialog.getStylesheets().add(
                                        Main.class.getResource("images/dialog.css").toExternalForm());
                                OuiNonDialog.setStyle(
                                        "primary:"+prefs.get("PrimaryColor","rgba(35, 140, 131, 1)")
                                                .replaceAll("0x","#")
                                                +";"+"secondary:"+prefs.get("SecondaryColor","#C8E2E0")
                                                .replaceAll("0x","#")+";");
                                OuiNonDialogController OuiNonDialogController = fxmlLoader.getController();
                                Dialog<ButtonType> dialog = new Dialog<>();
                                dialog.setDialogPane(OuiNonDialog);
                                OuiNonDialogController.setDialog(dialog);
                                OuiNonDialogController.TitleLabel.setText("Supprimer Stock");
                                OuiNonDialogController.SubjectLabel.setText("Vous etes sur de la suppression de ce Stock ?");
                                dialog.initStyle(StageStyle.TRANSPARENT);
                                OuiNonDialog.getScene().setFill(Color.rgb(0, 0, 0, 0));
                                dialog.showAndWait();
                                if (dialog.getResult()==ButtonType.YES)
                                {


                                    StockService.delete(data);
                                }
                                SideBarController.DeleteBlurBackground();

                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        });



                        editBt.setStyle("-fx-background-color:rgba(0,0,0,0);");
                        FontAwesomeIconView pencil =new FontAwesomeIconView(FontAwesomeIcon.PENCIL_SQUARE_ALT);
                        pencil.setFill(Color.valueOf(prefs.get("PrimaryColor","rgba(35, 140, 131, 1)")));
                        pencil.setSize("19.0");
                        editBt.setGraphic(pencil);
                        editBt.setOnAction(event -> {
                            Stock data = getTableView().getItems().get(getIndex());
                            //// show the modification dialog
                            try {
                                SideBarController.BlurBackground();
                                FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(Constants.AjouterStockDialogView));
                                DialogPane AjouterStockDialog = fxmlLoader.load();
                                AjouterStockDialog.getStylesheets().add(
                                        Main.class.getResource("images/dialog.css").toExternalForm());
                                AjouterStockDialog.setStyle(
                                        "primary:"+prefs.get("PrimaryColor","rgba(35, 140, 131, 1)")
                                                .replaceAll("0x","#")
                                                +";"+"secondary:"+prefs.get("SecondaryColor","#C8E2E0")
                                                .replaceAll("0x","#")+";");
                                AjouterStockDialogController ajouterStockDialogController = fxmlLoader.getController();

                                ajouterStockDialogController.setPassedStock(data);
                                ajouterStockDialogController.setOnEditMode(true);
                                Dialog<ButtonType> dialog = new Dialog<>();
                                dialog.setDialogPane(AjouterStockDialog);
                                ajouterStockDialogController.setDialog(dialog);

                                dialog.initStyle(StageStyle.TRANSPARENT);
                                AjouterStockDialog.getScene().setFill(Color.rgb(0, 0, 0, 0));
                                ajouterStockDialogController.setDialog(dialog);
                                dialog.showAndWait();
                                SideBarController.DeleteBlurBackground();

                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                        });








                        trushBt.setAlignment(Pos.TOP_CENTER);
                        hBox.setSpacing(10);
                        hBox.setPadding(new Insets(0,0,0,0));
                        hBox.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(0), Insets.EMPTY)));
                        hBox.setPadding(new Insets(0,0, 0,0));
                        hBox.getChildren().addAll(trushBt,editBt);
                        hBox.setAlignment(Pos.CENTER);


                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(hBox);
                        }
                    }

                };
                return cell;
            }
        };


        ActionsColumn.setCellFactory(cellFactory);

    }



    @Override
    public void initialize(URL location, ResourceBundle resources) {



        StocksTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        //add columns to the table
        StocksTable.getColumns().addAll(lotColumn,designationColumn,dosageColumn,formColumn,ppaColumn,qtyColumn,ActionsColumn);



        //searchBarFilter(people, SearchBarTextField.textProperty(),StocksTable);


        FilteredList<Stock> filteredData = new FilteredList<>(StocksList);

        SearchBarTextField.textProperty().addListener((observable, oldValue, newValue) ->
                filteredData.setPredicate(createFilteringPredicate(newValue))
        );


        StocksTable.setItems(filteredData);
        setupValueFactories();
        divideTableWidthOnColumns();
        // added by rabah
        //

    }



}











