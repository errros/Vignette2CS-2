package com.wrmanager.wrmanagerfx.controllers;


import com.wrmanager.wrmanagerfx.Constants;

import com.wrmanager.wrmanagerfx.Main;
import com.wrmanager.wrmanagerfx.entities.*;


import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.animation.AnimationTimer;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import net.sf.jasperreports.engine.JRException;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import java.awt.Button;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class CaisseViewController implements Initializable {


    @FXML
    private MFXButton AnnulerBtn;

    @FXML
    private Button AvatarIcon;

    @FXML
    private TextField BonTf;

    @FXML
    private MFXButton HomeAppBtn;



    @FXML
    private MFXButton Btn0;

    @FXML
    private MFXButton Btn1;

    @FXML
    private MFXButton Btn2;

    @FXML
    private MFXButton Btn3;

    @FXML
    private MFXButton Btn4;

    @FXML
    private MFXButton Btn5;

    @FXML
    private MFXButton Btn6;

    @FXML
    private MFXButton Btn7;

    @FXML
    private MFXButton Btn8;

    @FXML
    private MFXButton Btn9;

    @FXML
    private MFXButton BtnEnter;

    @FXML
    private MFXButton BtnPlus;

    @FXML
    private MFXButton BtnPoint;

    @FXML
    private MFXButton Caisse1;

    @FXML
    private MFXButton Caisse2;

    @FXML
    private MFXButton Caisse3;


    private MFXButton[] caisses;

    private IntegerProperty currentCaisseIndex = new SimpleIntegerProperty(0);

    @FXML
    private TextField ClientTfd;

    @FXML
    private TextField CodeBarreTF;

    @FXML
    private TextField DesignationTF;

    @FXML
    private GridPane FavorieItemGrid;

    @FXML
    private GridPane GroupItemGrid;

    @FXML
    private Button NotificationIcon;

    @FXML
    private TextField PoidsTF;

    @FXML
    private TableView<Stock> ProduitsTable;



    @FXML
    private ImageView imageView;


    private static final String CAMERA_IP = Main.CAMERA_IP;
    public Mat frame;
    public Image image;
    public static Thread thread;
    public static Object lock = new Object();
    public VideoCapture video;
    ImageProcessing imageProcessing = new ImageProcessing();

    @FXML
    void PlayButtonOnAction(ActionEvent event) {

        thread = new Thread(() -> {
            frame=new Mat();
            while (true) {
                synchronized (lock) {
                    video = new VideoCapture(CAMERA_IP);
                    if (video.isOpened()) System.out.println("it works");
                    video.read(frame);
                    Mat processedFrame = imageProcessing.processFrame(frame);
                    image = imageProcessing.matToImage(processedFrame);
                    imageView.setImage(image);
                }
            }
        });

        thread.start();
    }

    @FXML
    void CameraButtonOnAction(ActionEvent event){
        thread.stop();
        imageView.setImage(image);
        //ImageProcessing.saveCapturedImage(frame);;
        thread.stop();
        thread.stop();
        thread.stop();
    }

    public TableView<Stock> getProduitsTable() {
        return ProduitsTable;
    }

    private TableColumn<Stock, Integer> ordreColumn = new TableColumn<>("id");
    private TableColumn<Stock, String> designationColumn = new TableColumn<>("designation");
    private TableColumn<Stock, String> dosageColumn = new TableColumn<>("dosage");
    private TableColumn<Stock, String> qtyColumn = new TableColumn<Stock, String>("qty");
    private TableColumn<Stock, String> ppaColumn = new TableColumn<Stock, String>("ppa");



    @FXML
    private MFXButton NonExistingProductBtn;

    @FXML
    private AnchorPane RootAnchor;

    @FXML
    private MFXButton SupprimerBtn;

    @FXML
    private Label Time;

    @FXML
    private Label TotaleTF;

    @FXML
    private MFXButton ValiderBtn;

    @FXML
    private Label userLabel;

    @FXML
    private Label QtyRestTF;


    @FXML
    private MFXButton ChangerPrix;


    private String keyboardInput = "";

    ObservableList<Notification> notificationsQueue = FXCollections.observableArrayList();



    @FXML
    void Btn0OnAction(ActionEvent event) {
        keyboardInput = keyboardInput.concat("0");
    }

    @FXML
    void Btn1OnAction(ActionEvent event) {
        keyboardInput = keyboardInput.concat("1");
    }

    @FXML
    void Btn2OnAction(ActionEvent event) {
        keyboardInput = keyboardInput.concat("2");
    }

    @FXML
    void Btn3OnAction(ActionEvent event) {
        keyboardInput = keyboardInput.concat("3");
    }

    @FXML
    void Btn4OnAction(ActionEvent event) {
        keyboardInput = keyboardInput.concat("4");
    }

    @FXML
    void Btn5OnAction(ActionEvent event) {
        keyboardInput = keyboardInput.concat("5");
    }

    @FXML
    void Btn6OnAction(ActionEvent event) {
        keyboardInput = keyboardInput.concat("6");
    }

    @FXML
    void Btn7OnAction(ActionEvent event) {
        keyboardInput = keyboardInput.concat("7");
    }

    @FXML
    void Btn8OnAction(ActionEvent event) {
        keyboardInput = keyboardInput.concat("8");
    }

    @FXML
    void Btn9OnAction(ActionEvent event) {
        keyboardInput = keyboardInput.concat("9");
    }

    @FXML
    void BtnPointOnAction(ActionEvent event) {
        keyboardInput = keyboardInput.concat(".");
    }

    @FXML
    void BtnEnterOnAction(ActionEvent event) throws IOException {

    }



    @FXML
    void BtnPlusOnAction(ActionEvent event) {
        keyboardInput = "";
    }


    @FXML
    void SceneOnKeyPressed(KeyEvent event) {

        var key = event.getCode();
        switch (key) {
            case ADD -> {
                BtnPlus.fire();
                break;
            }
            case ENTER -> {
                BtnEnter.fire();
                break;
            }

            case NUMPAD0 -> {
                Btn0.fire();
                break;
            }
            case NUMPAD1 -> {
                Btn1.fire();
                break;
            }
            case NUMPAD2 -> {
                Btn2.fire();
                break;
            }
            case NUMPAD3 -> {
                Btn3.fire();
                break;
            }
            case NUMPAD4 -> {
                Btn4.fire();
                break;
            }
            case NUMPAD5 -> {
                Btn5.fire();
                break;
            }
            case NUMPAD6 -> {
                Btn6.fire();
                break;
            }
            case NUMPAD7 -> {
                Btn7.fire();
                break;
            }
            case NUMPAD8 -> {
                Btn8.fire();
                break;
            }
            case NUMPAD9 -> {
                Btn9.fire();
                break;
            }
            case DECIMAL -> {
                BtnPoint.fire();
                break;
            }


        }


    }

    @FXML
    void AnnulerBtnOnAction(ActionEvent event) {

    }

    @FXML
    void Caisse1OnAction(ActionEvent event) {

    }

    @FXML
    void Caisse2OnAction(ActionEvent event) {


    }

    @FXML
    void Caisse3OnAction(ActionEvent event) {

    }



    @FXML
    void SupprimerBtnOnAction(ActionEvent event) {

        var produit = ProduitsTable.getSelectionModel().getSelectedItem();
        int index = currentCaisseIndex.getValue();
        ObservableList<Stock> list = (ObservableList<Stock>) caisses[index].getUserData();
        list.remove(produit);


    }

    @FXML
    void ValiderBtnOnAction(ActionEvent event) throws FileNotFoundException, JRException {

    }



    private void CurrentTimeLabel() {
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                Time.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            }
        };
        timer.start();

    }




        public void addProduitToCurrentCaisse(Produit produit) throws IOException {

        }


        private void setupTotaleTF() {

        }


    private void divideTableWidthOnColumns() {

        Double size = Double.valueOf(ProduitsTable.getColumns().size());
        //calculate the size of each column
        double sizeCoulumn = 1 / (size);
        //set the size of each column
        ordreColumn.prefWidthProperty().bind(ProduitsTable.widthProperty().multiply(0.1));
        ppaColumn.prefWidthProperty().bind(ProduitsTable.widthProperty().multiply(0.3));
        designationColumn.prefWidthProperty().bind(ProduitsTable.widthProperty().multiply(0.23));
        qtyColumn.prefWidthProperty().bind(ProduitsTable.widthProperty().multiply(0.1));
        dosageColumn.prefWidthProperty().bind(ProduitsTable.widthProperty().multiply(0.26));

    }


    private void setupValueFactories() {
        //define the cells factory
        ordreColumn.setCellValueFactory(
                new PropertyValueFactory<Stock, Integer>("ordre"));

        designationColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Stock, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Stock, String> produitCaisseStringCellDataFeatures) {
                return new SimpleStringProperty(produitCaisseStringCellDataFeatures.getValue().getProduit().getDesignation());

            }
        });

        dosageColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Stock, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue call(TableColumn.CellDataFeatures<Stock, String> produitCaisseStringCellDataFeatures) {
                return new SimpleStringProperty(produitCaisseStringCellDataFeatures.getValue().getProduit().getDosage());

            }
        });

        qtyColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Stock, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue call(TableColumn.CellDataFeatures<Stock, String> produitCaisseStringCellDataFeatures) {
                return new SimpleStringProperty(produitCaisseStringCellDataFeatures.getValue().getPpa().toString());

            }
        });

        ppaColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Stock, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue call(TableColumn.CellDataFeatures<Stock, String> produitCaisseStringCellDataFeatures) {
                return new SimpleStringProperty(produitCaisseStringCellDataFeatures.getValue().getPpa().toString());

            }
        });


    }


    private void setupTable() {
        /*
        ProduitsTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        ProduitsTable.getColumns().addAll(ordreColumn, designationColumn, prixUniteColumn, qtyColumn, prixTotaleColumn);
        setupValueFactories();
        divideTableWidthOnColumns();

        ObservableList<ProduitCaisse> list = (ObservableList<ProduitCaisse>) caisses[0].getUserData();
        ProduitsTable.setItems(list);


        currentCaisseIndex.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                ObservableList<ProduitCaisse> list = (ObservableList<ProduitCaisse>) caisses[t1.intValue()].getUserData();
                ProduitsTable.setItems(list);
            }
        });



        ProduitsTable.selectionModelProperty().addListener(new ChangeListener<TableView.TableViewSelectionModel<ProduitCaisse>>() {
            @Override
            public void changed(ObservableValue<? extends TableView.TableViewSelectionModel<ProduitCaisse>> observableValue, TableView.TableViewSelectionModel<ProduitCaisse> produitCaisseTableViewSelectionModel, TableView.TableViewSelectionModel<ProduitCaisse> t1) {

               var selectedItem = t1.getSelectedItem();
                QtyRestTF.setText(Float.valueOf(selectedItem.getProduit().getQtyTotale() - selectedItem.getQty()).toString());
            }
        });



         */
    }





    @Override
    public void initialize(URL location, ResourceBundle resources) {



        /*
        setupTable();

        CurrentTimeLabel();


        setupTotaleTF();
        */


    }


}
