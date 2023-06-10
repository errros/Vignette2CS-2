package com.wrmanager.wrmanagerfx.controllers;


import com.wrmanager.wrmanagerfx.Constants;

import com.wrmanager.wrmanagerfx.Main;
import com.wrmanager.wrmanagerfx.entities.*;


import com.wrmanager.wrmanagerfx.repositories.ProduitDAO;
import com.wrmanager.wrmanagerfx.requests.PipelineRequests;
import com.wrmanager.wrmanagerfx.services.CommandeService;
import com.wrmanager.wrmanagerfx.services.ProduitService;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;


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
        ProduitsTable.getItems().clear();
        ProduitsTable.refresh();

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


///start from here
    @FXML
    void PlayButtonOnAction(ActionEvent event) {

        thread = new Thread(() -> {
            frame=new Mat();
            while (true) {
                synchronized (lock) {
                    video = new VideoCapture(CAMERA_IP);
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
        ImageProcessing.saveCapturedImage(frame);
        thread.stop();
        thread.stop();
        thread.stop();

        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.submit(() -> {
            System.out.println("rah baghi yexcuter");
            var r = PipelineRequests.getVenteFromImage("./captured.jpg");
            var stock = Constants.venteService.getStockFromCamera(r);
            System.out.println("r here : " + r);
            if (stock.isPresent()){
                System.out.println("yaaaaaaaaaaaaw raha mchat"+stock.get().getProduit().getDesignation());
                try {
                    addProduitToCurrentCaisse(stock.get());
                    setupTotaleTF();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            };
        });
    }

    public TableView<Stock> getProduitsTable() {
        return ProduitsTable;
    }

    private TableColumn<Stock, String> designationColumn = new TableColumn<>("designation");
    private TableColumn<Stock, String> dosageColumn = new TableColumn<>("dosage");
    private TableColumn<Stock, String> qtyColumn = new TableColumn<Stock, String>("qty");
    private TableColumn<Stock, String> ppaColumn = new TableColumn<Stock, String>("ppa");


    @FXML
    void SupprimerBtnOnAction(ActionEvent event) {
        var s = ProduitsTable.getSelectionModel().getSelectedItem();
        ProduitsTable.getItems().remove(s);
        ProduitsTable.refresh();
        setupTotaleTF();
    }

    @FXML
    void ValiderBtnOnAction(ActionEvent event) throws FileNotFoundException, JRException {
        List<Stock> modifiedStocks = ProduitsTable.getItems().stream().map(stock -> {
            stock.setQty(stock.getQty() - stock.getQtyEnCaisse());
            System.out.println(stock.getQty());
            Constants.stockService.update(stock);
            return stock;
        }).collect(Collectors.toList());
        ProduitsTable.getItems().clear();
        ProduitsTable.refresh();
        TotaleTF.setText("0 DA");
    }


    public void addProduitToCurrentCaisse(Stock stock) throws IOException {
        var s=ProduitsTable.getItems().stream().collect(Collectors.toList()).indexOf(stock);
        System.out.println("this is s "+s);
        if (s != -1 )
        {
           var stockToModifie= ProduitsTable.getItems().get(s);
           ProduitsTable.getItems().remove(stockToModifie);
           stockToModifie.setQtyEnCaisse(stockToModifie.getQtyEnCaisse()+1);
           ProduitsTable.getItems().add(stockToModifie);

        }
        else {stock.setQtyEnCaisse(stock.getQtyEnCaisse()+1);
            ProduitsTable.getItems().add(stock);}
        ProduitsTable.refresh();
    }


    private void setupTotaleTF() {
        Platform.runLater(() -> {
            var sumPrice = ProduitsTable.getItems().stream()
                    .mapToDouble(ss -> ss.getQtyEnCaisse() * ss.getPpa())
                    .sum();
            TotaleTF.setText(sumPrice + " DA");
        });
    }


    private void divideTableWidthOnColumns() {

        Double size = Double.valueOf(ProduitsTable.getColumns().size());
        //calculate the size of each column
        double sizeCoulumn = 1 / (size);
        //set the size of each column
        ppaColumn.prefWidthProperty().bind(ProduitsTable.widthProperty().multiply(0.3));
        designationColumn.prefWidthProperty().bind(ProduitsTable.widthProperty().multiply(0.23));
        qtyColumn.prefWidthProperty().bind(ProduitsTable.widthProperty().multiply(0.1));
        dosageColumn.prefWidthProperty().bind(ProduitsTable.widthProperty().multiply(0.26));

    }


    private void setupValueFactories() {
        //define the cells factory
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
                return new SimpleStringProperty(produitCaisseStringCellDataFeatures.getValue().getQtyEnCaisse().toString());

            }
        });

        ppaColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Stock, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue call(TableColumn.CellDataFeatures<Stock, String> produitCaisseStringCellDataFeatures) {
                return new SimpleStringProperty(produitCaisseStringCellDataFeatures.getValue().getPpa().toString());

            }
        });


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

    private void setupTable() {

        ProduitsTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        ProduitsTable.getColumns().addAll( designationColumn, dosageColumn, ppaColumn, qtyColumn);
        setupValueFactories();
        divideTableWidthOnColumns();
    }





    @Override
    public void initialize(URL location, ResourceBundle resources) {

        setupTable();
        CurrentTimeLabel();

    }


}
