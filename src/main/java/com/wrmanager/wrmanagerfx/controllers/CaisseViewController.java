package com.wrmanager.wrmanagerfx.controllers;


import com.wrmanager.wrmanagerfx.Constants;
import com.wrmanager.wrmanagerfx.Main;
import com.wrmanager.wrmanagerfx.controllers.OuiNonDialogController;
import com.wrmanager.wrmanagerfx.controllers.SideBarController;

import com.wrmanager.wrmanagerfx.entities.*;


import com.wrmanager.wrmanagerfx.models.SystemMeasure;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.effects.DepthLevel;
import javafx.animation.AnimationTimer;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import javafx.util.Duration;
import net.sf.jasperreports.engine.JRException;
import org.apache.poi.ss.formula.functions.Code;
import org.controlsfx.control.Notifications;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import java.awt.Button;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

import static com.wrmanager.wrmanagerfx.Constants.*;


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


    private static final String CAMERA_IP = "http://192.168.43.1:8080/video";
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
    private TableColumn <Stock, String> qtyColumn = new TableColumn<>("qty");
    private TableColumn<Stock, Integer> prixUniteColumn = new TableColumn<>("ppa");



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

    static ObservableList<GroupeFavoris> groupesTmp = FXCollections.observableArrayList();

    private final ObservableList<MFXButton> groupesButtons = FXCollections.observableArrayList();


    static public MFXButton currentGroupeBtn;


    private AutoCompletionBinding<String> autocompletion;


    private Preferences preferences;
    Vente vente;

    private String keyboardInput = "";


    static Vente vente1 = Vente.builder().build();
    static Vente vente2 = Vente.builder().build();
    static Vente vente3 = Vente.builder().build();


    //Produit??
    //static public Produit p = Produit.builder().designation("Produit??").qtyUnite(0f).prixVenteUnite(0).id(0l).build();


    ObservableList<Notification> notificationsQueue = FXCollections.observableArrayList();

/*
    @FXML
    void HomeAppBtnOnAction(ActionEvent event) throws IOException {

       Stage currentStage = (Stage) HomeAppBtn.getScene().getWindow();

        Stage homeAppStage = new Stage();
        Main.setupApplication(homeAppStage);

        currentStage.close();



    }

*/

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
/*
        var p = ProduitsTable.getSelectionModel().getSelectedItem();

        ObservableList<ProduitCaisse> list = (ObservableList<ProduitCaisse>) caisses[currentCaisseIndex.get()].getUserData();

        //case of produit??
        if (p.getProduit().equals(this.p)) {
            p.setPrixTotale(Integer.valueOf(keyboardInput));
            list.remove(p);
            list.add(0, p);
        }
        else if (p.getProduit().getSystemMeasure() == SystemMeasure.UNITE ) {
            var qty = Float.valueOf(keyboardInput);
            if (validateByQty(p.getProduit(), qty) || isStockNegativeActivated() || openStockNegativeOuiNonDialog()) {
                p.setQty(qty);
                p.calculatTotale();
                //refresh
                list.remove(p);
                list.add(0, p);
            }
        } else if (p.getProduit().getSystemMeasure() == SystemMeasure.POIDS) {
            var oldQty = p.getQty();
            var oldTotale = p.getPrixTotale();
            p.setPrixTotale(Integer.valueOf(keyboardInput));
            var qty = p.calculatPoids();

            if (validateByPoids(p.getProduit(), qty) || isStockNegativeActivated() || openStockNegativeOuiNonDialog()) {
                p.setQty(qty);
                //refresh
                list.remove(p);
                list.add(0, p);
            } else {
                p.setQty(oldQty);
                p.setPrixTotale(oldTotale);
            }
        }
        else if (p.getProduit().getSystemMeasure() == SystemMeasure.METRIQUE) {

            Float qty = Float.valueOf(keyboardInput);


            if (validateByQty(p.getProduit(), qty) || isStockNegativeActivated() || openStockNegativeOuiNonDialog()) {
                p.setQty(qty);
                p.calculatTotale();
                //refresh
                list.remove(p);
                list.add(0, p);
            }






        }

        keyboardInput = "";
        ProduitsTable.refresh();
        CodeBarreTF.requestFocus();

 */
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

        int index = currentCaisseIndex.getValue();
        ObservableList<Produit> list = (ObservableList<Produit>) caisses[index].getUserData();

        list.clear();

    }
/*
    @FXML
    void Caisse1OnAction(ActionEvent event) {
        currentCaisseIndex.setValue(0);
        //refreshing problem
        ObservableList<Produit> list = (ObservableList<Produit>) caisses[0].getUserData();
        if (list.isEmpty()) {
            TotaleTF.setText("0 DA");

        } else {
            TotaleTF.setText(list.stream().map(ProduitCaisse::getPrixTotale).reduce(0, Integer::sum).toString() +" "+ bundle.getString("DA"));

        }

        ProduitsTable.refresh();

        ClientTfd.setText(bundle.getString("caisse.client") + (Optional.ofNullable(vente1.getClient()).isPresent() ? vente1.getClient() : ""));

    }

    @FXML
    void Caisse2OnAction(ActionEvent event) {
        currentCaisseIndex.setValue(1);

        //refreshing problem
        ObservableList<ProduitCaisse> list = (ObservableList<ProduitCaisse>) caisses[1].getUserData();
        if (list.isEmpty()) {
            TotaleTF.setText(bundle.getString("zeroDA"));

        } else {

            TotaleTF.setText(list.stream().map(ProduitCaisse::getPrixTotale).reduce(0, Integer::sum).toString() +" "+ bundle.getString("DA"));

        }

        ProduitsTable.refresh();

        ClientTfd.setText(bundle.getString("caisse.client") + (Optional.ofNullable(vente2.getClient()).isPresent() ? vente2.getClient() : ""));

    }

    @FXML
    void Caisse3OnAction(ActionEvent event) {
        currentCaisseIndex.setValue(2);
        //refreshing problem
        ObservableList<ProduitCaisse> list = (ObservableList<ProduitCaisse>) caisses[2].getUserData();
        if (list.isEmpty()) {
            TotaleTF.setText(bundle.getString("zeroDA"));
        } else {
            TotaleTF.setText(list.stream().map(ProduitCaisse::getPrixTotale).reduce(0, Integer::sum).toString() +" "+ bundle.getString("DA"));
        }

        ProduitsTable.refresh();

        ClientTfd.setText(bundle.getString("caisse.client")+ (Optional.ofNullable(vente3.getClient()).isPresent() ? vente3.getClient() : ""));

    }
*/


/*
    @FXML
    void SupprimerBtnOnAction(ActionEvent event) {

        var produit = ProduitsTable.getSelectionModel().getSelectedItem();
        int index = currentCaisseIndex.getValue();
        ObservableList<ProduitCaisse> list = (ObservableList<ProduitCaisse>) caisses[index].getUserData();
        list.remove(produit);


    }

    @FXML
    void ValiderBtnOnAction(ActionEvent event) throws FileNotFoundException, JRException {


        var produits = (ObservableList<ProduitCaisse>) caisses[currentCaisseIndex.get()].getUserData();
        if (!produits.isEmpty()) {
            ObservableList<ProduitCaisse> produitsTmp = FXCollections.observableArrayList();
            produits.forEach(produitCaisse ->
                    {
                        var pc = new ProduitCaisse(produitCaisse.getOrdre(), produitCaisse.getProduit(), produitCaisse.getQty());
                        pc.setPrixTotale(produitCaisse.getPrixTotale());
                       pc.setPrixUnite(produitCaisse.getPrixUnite());
                        produitsTmp.add(pc);
                    }
            )
            ;
            var client = ClientTfd.getText();
            Vente vente = Vente.builder().build();
            vente.setTotale(Integer.valueOf(TotaleTF.getText().split(" ")[0]));
            this.vente = venteService.save(vente, produits, client);

            if (isBonVenteDialogPreferenceSelected()) {
                openBonVenteDialog(produitsTmp);

            }
            prefs.putInt("LastVente", Math.toIntExact(vente.getId()));

            if (notificationService.isNotificationPopupPreferenceSelected()) {
                produitsTmp.stream().sorted(Comparator.comparing(p -> p.getProduit().getDesignation().length())).forEach(pc -> {
                    var produit = pc.getProduit();
                    //check if popup notification
                    if (!produit.equals(CaisseViewController.p) &&
                            produit.getQtyTotale() <= produit.getQtyAlerte()) {
                        popupNotifQtyAlerte(produit);
                    }
                });


            }

            SetupBonLbl();
            produits.clear();

        }

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
        int index = currentCaisseIndex.getValue();
        ObservableList<ProduitCaisse> list = (ObservableList<ProduitCaisse>) caisses[index].getUserData();
        var existOpt = list.stream().filter(pc -> pc.getProduit().equals(produit)).findAny();

        if (existOpt.isPresent()) {

            if (validateByQty(existOpt.get().getProduit(), existOpt.get().getQty() + 1) ||
                    isStockNegativeActivated() ||
                    openStockNegativeOuiNonDialog()) {

                existOpt.get().setQty(existOpt.get().getQty() + 1);
                existOpt.get().calculatTotale();


                //to refresh the TotaleTF
                list.remove(existOpt.get());
                list.add(existOpt.get().getOrdre() - 1, existOpt.get());
                ProduitsTable.getSelectionModel().select(existOpt.get());
            }
        } else {
            ProduitCaisse pc = new ProduitCaisse(list.size() + 1, produit, 1f);
            if (validateByQty(pc.getProduit(), pc.getQty())
                    || isStockNegativeActivated()
                    || openStockNegativeOuiNonDialog()) {
                pc.calculatTotale();
                list.add(0, pc);
                ProduitsTable.getSelectionModel().select(pc);
            }
        }
        ProduitsTable.refresh();
    }

    private void setupTotaleTF() {
        ObservableList<ProduitCaisse> list1 = (ObservableList<ProduitCaisse>) caisses[0].getUserData();
        ObservableList<ProduitCaisse> list2 = (ObservableList<ProduitCaisse>) caisses[1].getUserData();
        ObservableList<ProduitCaisse> list3 = (ObservableList<ProduitCaisse>) caisses[2].getUserData();

        TotaleTF.setText(bundle.getString("zeroDA"));


        list1.addListener(new ListChangeListener<ProduitCaisse>() {
            @Override
            public void onChanged(Change<? extends ProduitCaisse> change) {
                if (currentCaisseIndex.get() == 0) {
                    TotaleTF.setText(list1.stream().map(ProduitCaisse::getPrixTotale).reduce(0, Integer::sum).toString() +" "+ bundle.getString("DA"));
                }
            }
        });

        list2.addListener(new ListChangeListener<ProduitCaisse>() {
            @Override
            public void onChanged(Change<? extends ProduitCaisse> change) {
                if (currentCaisseIndex.get() == 1) {
                    TotaleTF.setText(list2.stream().map(ProduitCaisse::getPrixTotale).reduce(0, Integer::sum).toString()+" " + bundle.getString("DA"));
                }
            }
        });
        list3.addListener(new ListChangeListener<ProduitCaisse>() {
            @Override
            public void onChanged(Change<? extends ProduitCaisse> change) {
                if (currentCaisseIndex.get() == 2) {
                    TotaleTF.setText(list3.stream().map(ProduitCaisse::getPrixTotale).reduce(0, Integer::sum).toString()+" " + bundle.getString("DA"));
                }
            }
        });


    }

    private void setupDesignationAutoCompleteFillTable() {

        autocompletion = TextFields.bindAutoCompletion(DesignationTF, produitsList.stream().map(Produit::getDesignation).collect(Collectors.toList()));
        produitsList.addListener(new ListChangeListener<Produit>() {
            @Override
            public void onChanged(Change<? extends Produit> change) {
                updateSuggestions();
            }
        });

        autocompletion.setOnAutoCompleted(new EventHandler<AutoCompletionBinding.AutoCompletionEvent<String>>() {
            @Override
            public void handle(AutoCompletionBinding.AutoCompletionEvent<String> stringAutoCompletionEvent) {
                Produit p = produitsList.stream().filter(d -> d.getDesignation().equals(stringAutoCompletionEvent.getCompletion())).findAny().get();
                try {
                    addProduitToCurrentCaisse(p);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                DesignationTF.setText("");
            }
        });

        DesignationTF.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                if (t1.equals("+")) {
                    ProduitsTable.requestFocus();
                    DesignationTF.setText("");
                    BtnPlus.fire();
                }
            }
        });

    }
*/
    private void updateSuggestions() {

        autocompletion.dispose();
        autocompletion = TextFields.bindAutoCompletion(DesignationTF, produitsList.stream().map(Produit::getDesignation).collect(Collectors.toList()));


    }

    private void divideTableWidthOnColumns() {
/*
        Double size = Double.valueOf(ProduitsTable.getColumns().size());
        //calculate the size of each column
        double sizeCoulumn = 1 / (size);
        //set the size of each column
        ordreColumn.prefWidthProperty().bind(ProduitsTable.widthProperty().multiply(0.1));
        prixUniteColumn.setSortable(true);
        prixUniteColumn.prefWidthProperty().bind(ProduitsTable.widthProperty().multiply(0.3));
        designationColumn.prefWidthProperty().bind(ProduitsTable.widthProperty().multiply(0.23));
        qtyColumn.prefWidthProperty().bind(ProduitsTable.widthProperty().multiply(0.1));
        prixTotaleColumn.prefWidthProperty().bind(ProduitsTable.widthProperty().multiply(0.26));

*/
    }


    private void setupValueFactories() {
        /*
        //define the cells factory
        ordreColumn.setCellValueFactory(
                new PropertyValueFactory<ProduitCaisse, Integer>("ordre"));

        designationColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ProduitCaisse, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<ProduitCaisse, String> produitCaisseStringCellDataFeatures) {
                return new SimpleStringProperty(produitCaisseStringCellDataFeatures.getValue().getProduit().getDesignation());

            }
        });
        prixUniteColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ProduitCaisse, Integer>, ObservableValue<Integer>>() {
            @Override
            public ObservableValue call(TableColumn.CellDataFeatures<ProduitCaisse, Integer> produitCaisseStringCellDataFeatures) {
                return new SimpleIntegerProperty(produitCaisseStringCellDataFeatures.getValue().getProduit().getPrixVenteUnite());

            }
        });

        qtyColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ProduitCaisse, Float>, ObservableValue>() {
            @Override
            public ObservableValue call(TableColumn.CellDataFeatures<ProduitCaisse, Float> produitCaisseFloatCellDataFeatures) {
                DecimalFormat df = new DecimalFormat();
                df.setMaximumFractionDigits(2);
                return new SimpleStringProperty(df.format(produitCaisseFloatCellDataFeatures.getValue().getQty()));
            }
        });
        prixTotaleColumn.setCellValueFactory(new PropertyValueFactory<ProduitCaisse, Integer>("prixTotale"));

        */
    }


    private void setupQtyResteTF() {
        /*
        ProduitsTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ProduitCaisse>() {
            @Override
            public void changed(ObservableValue<? extends ProduitCaisse> observableValue, ProduitCaisse produitCaisse, ProduitCaisse t1) {
                Optional.ofNullable(t1).ifPresentOrElse(t2 -> {

                    var restVal = (t2.getProduit().getQtyTotale() - t2.getQty());
                    DecimalFormat df = new DecimalFormat();
                    df.setMaximumFractionDigits(2);
                    var unit = t2.getProduit().getSystemMeasure() == SystemMeasure.UNITE ? bundle.getString("unites") : bundle.getString("kg");

                    QtyRestTF.setText(bundle.getString("caisse.qtyReste") + df.format(restVal) + unit);

                }, new Runnable() {
                    @Override
                    public void run() {
                        QtyRestTF.setText(bundle.getString("caisse.qtyReste"));
                    }
                });


            }
        });

         */

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

    private void setupCodeBarreFillTable() {
        /*
        CodeBarreTF.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                if (t1.length() == 13) {
                    var p = produitsList.stream().filter(produit -> t1.equals(produit.getCodeBarre())).findAny();
                    p.ifPresent(produit -> {
                        try {
                            addProduitToCurrentCaisse(produit);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        CodeBarreTF.setText("");
                    });

                } else if (t1.equals("+")) {
                    ProduitsTable.requestFocus();
                    CodeBarreTF.setText("");
                    BtnPlus.fire();
                }

            }

            ;

        });

         */
    }



    @Override
    public void initialize(URL location, ResourceBundle resources) {

        caisses = new MFXButton[]{Caisse1, Caisse2, Caisse3};


        Caisse1.setUserData(FXCollections.observableArrayList());
        Caisse2.setUserData(FXCollections.observableArrayList());
        Caisse3.setUserData(FXCollections.observableArrayList());



        /*
        setupTable();
        setupDesignationAutoCompleteFillTable();
        setupCodeBarreFillTable();

        CurrentTimeLabel();


        setupTotaleTF();
        setupQtyResteTF();
        */


    }


}
