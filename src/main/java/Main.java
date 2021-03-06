import classes.Analysis;
import classes.Person;
import classes.Result;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Path;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import java.io.*;
import java.net.MalformedURLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Main extends Application {
    private static String granURL = null; //URL of granulocyte image
    private static String rbcURL = null; //URL of RBC image
    private static Properties props; // Properties for making images
    private Task docWorker; //Task for making .doc file
    private String fileName = ""; //Name for resulting .doc file
    private String fileDate = ""; //Date for resulting .doc file
    private static Logger logger = Logger.getLogger(Main.class.getName()); //Logger

    /*
    инициализируются логгер и конфигурация
    из конфигурации получают размеры, положение и номер страницы прямоугольников для вырезания из .pdf
    logger создаётся с названием "log" + миллисекунды
     */

    public void init(){
        try {
            LogManager.getLogManager().readConfiguration(Main.class.getResourceAsStream("logging.properties"));
            Date date = new Date();
            logger.addHandler(new FileHandler("logs/log" + date.getTime() + ".txt"));
            logger.info("logger properties loaded.");
        }
        catch (IOException e){
            logger.log(Level.SEVERE,"Exception: ", e);
        }
        props = new Properties();
        try(InputStream is = new FileInputStream("config.properties")){
            props.load(is);
            logger.info("props loaded.");
        }
        catch (IOException e){
            logger.log(Level.SEVERE, "Exception: ", e);
        }
    }

    @Override
        /*
    сцена с двумя колонками
    в левой - lable
    в правой - TextField
    далее место для прикрепления файлов - нужно перетащить файлы из эксплорера
    кнопка Save - создает .docs файл
    кнопка Refresh удаляет все поля, файлы
     */

    public void start(Stage primaryStage){

        GridPane group = new GridPane();
        group.setHgap(10);
        group.setVgap(10);
        group.setPadding(new Insets(10));
        Scene scene = new Scene(group, 700,650);
        ColumnConstraints cs1 = new ColumnConstraints();
        ColumnConstraints cs2 = new ColumnConstraints();
        group.getColumnConstraints().addAll(cs1, cs2);
        cs1.setPrefWidth(400);

        //Заполнение колонок

        Label name = new Label("ФИО:");
        group.add(name, 0, 0);
        TextField nameTextField = new TextField();
        nameTextField.setPrefWidth(500);
        group.add(nameTextField, 1, 0);

        group.add(new Label("год рождения:"), 0, 1);
        TextField yearTextField = new TextField();
        group.add(yearTextField, 1,1);

        group.add(new Label("Отделение:"), 0,2);
        TextField departmentTextField = new TextField();
        group.add(departmentTextField,1,2);

        group.add(new Label("Дата:"), 0, 3);
        DatePicker date = new DatePicker();
        group.add(date,1,3);

        group.add(new Label("ПНГ II тип эритроциты:"), 0, 4);
        TextField PnhRBCi = new TextField("0.0");
        group.add(PnhRBCi, 1,4);

        group.add(new Label("ПНГ III тип эритроциты:"), 0, 5);
        TextField PnhRBCii = new TextField("0.0");
        group.add(PnhRBCii, 1,5);

        group.add(new Label("ПНГ гранулоциты:"), 0, 6);
        TextField PnhGrans = new TextField("0.0");
        group.add(PnhGrans, 1,6);

        group.add(new Label("ПНГ моноциты:"), 0, 7);
        TextField PnhMono = new TextField("0.0");
        group.add(PnhMono, 1,7);

        /*
        По умолчанию в полях для количества гранулоцитов, моноцитов и эритроцитов стоят 0
        При наведении фокуса в TextField удаляет текст и вводится значение
        Если поле при выводе фокуса остаётся пустым - возвращает значения на 0
         */

        PnhGrans.focusedProperty().addListener((obs, ov, nv)->{
            if(nv) {
                if (PnhGrans.getText().equals("0.0")) {
                    PnhGrans.setText("");
                }
            }
            else{
                if(PnhGrans.getText().equals("")){
                    PnhGrans.setText("0.0");
                }
            }
        });
        PnhMono.focusedProperty().addListener((obs, ov, nv)->{
            if(nv) {
                if (PnhMono.getText().equals("0.0")) {
                    PnhMono.setText("");
                }
            }
            else{
                if(PnhMono.getText().equals("")){
                    PnhMono.setText("0.0");
                }
            }
        });

        PnhRBCi.focusedProperty().addListener((obs, ov, nv)->{
            if(nv) {
                if (PnhRBCi.getText().equals("0.0")) {
                    PnhRBCi.setText("");
                }
            }
            else{
                if(PnhRBCi.getText().equals("")){
                    PnhRBCi.setText("0.0");
                }
            }
        });
        PnhRBCii.focusedProperty().addListener((obs, ov, nv)->{
            if(nv) {
                if (PnhRBCii.getText().equals("0.0")) {
                    PnhRBCii.setText("");
                }
            }
            else{
                if(PnhRBCii.getText().equals("")){
                    PnhRBCii.setText("0.0");
                }
            }
        });

        /*
        Область для загружаемых файлов
        В первой колонке Label, который изначально имеет значение "no file"
        При переносе файла сменяется на его название.
        Далее идёт иконка, появляющаяся при перетаскивании файла с нужным названием
        (содержит gran или RBC и имеет формат .pdf)
        за иконкой .pdf файла - иконка, удаляющая его и обнуляющая granURL или rbcURL
        Вторая колонка - кнопки для загрузки тестовых картинок
         */

        Label granLabel = new Label("No file");
        Shape granIcon = pdfIcon();
        granIcon.setVisible(false);
        SVGPath deniedGranIcon = new SVGPath();
        deniedGranIcon.setFill(Color.rgb(255, 0, 0, .9));
        deniedGranIcon.setStroke(Color.WHITE);
        deniedGranIcon.setContent("M24.778,21.419 19.276,15.917 24.777,10.415 21.949,7.585 16.447,13.087 10.945,7.585 8.117,10.415 13.618,15.917 8.116,21.419 10.946,24.248 16.447,18.746 21.948,24.248z");
        deniedGranIcon.setVisible(false);
        deniedGranIcon.setOnMouseClicked((e) ->{
            if(!e.isPrimaryButtonDown()){
                deniedGranIcon.setVisible(false);
                granIcon.setVisible(false);
                granURL = null;
                granLabel.setText("No file");
            }
        });

        HBox Granhbox = new HBox();
        Granhbox.setSpacing(10);
        Granhbox.setAlignment(Pos.CENTER_LEFT);
        Granhbox.getChildren().addAll(granLabel, granIcon, deniedGranIcon);
        group.add(Granhbox, 0 , 8);
        
        /*
        Кнопка для загрузки картинок, рендеренных из .pdf (granURL)
        Создет объект PDFtoImage, далее ArrayList<String> из адресов картинок, 
        Далее для каждой страницы .pdf файла получает URL соответствующей картинки и сохраняет их в ArrayList
        Картинки создаются с названием N.jpg, где N - номер листа в .pdf
        После создания картинок в помощью метода getImageUrlFromPDF
        Создется сцена, которой передаётся массив с ссылками на картинки
        */
        
        Button granButton = new Button("Gran and Mono region select");
        granButton.setOnAction((e)->{
            if(granURL != null) {
                PDFtoImage gran = new PDFtoImage(new File(granURL));
                ArrayList<String> imgUrl = new ArrayList<>();
                logger.log(Level.INFO, "making {0} image files from {1}", new Object[] {new Integer(gran.getPageNumber()), granURL});
                for(int i = 0; i < gran.getPageNumber(); i++){
                    try{
                        imgUrl.add(gran.getImageUrlFromPDF(i, Integer.toString(i)));
                        }
                    catch(IOException exc){
                        logger.log(Level.SEVERE, "exception during making image number {0} : {1}", new Object[]{new Integer(i), exc});
                        }
                }

                Stage stage = StagesFactory.pdfImageView(imgUrl);
                stage.show();
                logger.log(Level.INFO,"Opening view of granulocytes .pdf file {0}", granURL);
                stage.setOnCloseRequest((closeReq)->{
                    logger.log(Level.INFO, "Closing view of granulocytes file {0}", granURL);

                    gran.closeDoc();
                    stage.close();
                    System.gc();
                    for(String str : imgUrl){
                        File file = new File(str.substring(6));
                        file.delete();
                    }
                    System.out.println("111");
                });
            }
        });
        
        group.add(granButton, 1, 8);
        /*
        Создание панели загрузки файлов, аналогичной гранулоцитам
        */
        Label rbcLabel = new Label("No file");
        Shape rbcIcon = pdfIcon();
        rbcIcon.setVisible(false);
        SVGPath deniedRBCIcon = new SVGPath();
        deniedRBCIcon.setFill(Color.rgb(255, 0, 0, .9));
        deniedRBCIcon.setStroke(Color.WHITE);
        deniedRBCIcon.setContent("M24.778,21.419 19.276,15.917 24.777,10.415 21.949,7.585 16.447,13.087 10.945,7.585 8.117,10.415 13.618,15.917 8.116,21.419 10.946,24.248 16.447,18.746 21.948,24.248z");
        deniedRBCIcon.setVisible(false);
        deniedRBCIcon.setOnMouseClicked((e) ->{
            if(!e.isPrimaryButtonDown()){
                deniedRBCIcon.setVisible(false);
                rbcIcon.setVisible(false);
                rbcURL = null;
                rbcLabel.setText("No file");
            }
        });

        HBox Rbchbox = new HBox();
        Rbchbox.setSpacing(10);
        Rbchbox.setAlignment(Pos.CENTER_LEFT);
        Rbchbox.getChildren().addAll(rbcLabel, rbcIcon, deniedRBCIcon);
        group.add(Rbchbox, 0 , 9);
        Button rbcButton = new Button("RBC region select");
        rbcButton.setOnAction((e)->{
            if(rbcURL != null) {
                PDFtoImage rbc = new PDFtoImage(new File(rbcURL));
                ArrayList<String> imgUrl = new ArrayList<>();
                logger.log(Level.INFO, "making {0} image files from {1}", new Object[]{new Integer(rbc.getPageNumber()), rbcURL});
                for(int i = 0; i < rbc.getPageNumber(); i++){
                    try{
                        imgUrl.add(rbc.getImageUrlFromPDF(i, Integer.toString(i)));
                    }
                    catch(IOException exc){
                       logger.log(Level.SEVERE, "exception during making image number {0} : {1}", new Object[]{new Integer(i), exc});
                    }
                }

                Stage stage = StagesFactory.pdfImageView(imgUrl);
                stage.show();
                stage.setOnCloseRequest((closeReq)->{

                    logger.log(Level.INFO,"Closing view of RBC file {0}", rbcURL);
                    rbc.closeDoc();
                    stage.close();
                    for(String str : imgUrl){
                        File file = new File(str.substring(6));
                        file.delete();
                    }
                    System.gc();
                });
            }
        });
        group.add(rbcButton, 1, 9);
/*
Поле fileTextField заполняется автоматиечски и состоит из двух частей:
корректно созданого имени - в виде "Xxxxx Yyyyy Zzzzzz" - из него получается "Xxxxx YZ"
выбранной даты - в виде ddMMyy
*/
        group.add(new Label("File name"), 0, 10);
        TextField fileTextField = new TextField();
        group.add(fileTextField, 1, 10);
        //при снятии фокуса с поля имени обновляет имя файла    
        nameTextField.focusedProperty().addListener((obs, ov, nv) -> {
            if(!nv && nameTextField.getText().matches("[а-яА-Я]{1,}[\\s][а-яА-Я]{1,}[\\s][а-яА-Я]{1,}")){
                String[] str = nameTextField.getText().split("\\s");
                fileName = str[0] + " " +str[1].substring(0,1) + str[2].substring(0,1);
            }
            else if(!nv){
                fileName = "";                
            }
            fileTextField.setText(fileName + " " + fileDate);

        });
        //при снятии фокуса с даты обновляет имя файла
        date.focusedProperty().addListener((obs, ov, nv) -> {
            if(!nv && date.getValue() != null){
                LocalDate localDate = date.getValue();
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("ddMMyy");
                fileDate = localDate.format(dtf);
                fileTextField.setText(fileName + " " + fileDate);
            }
        });
        
        /*
        
        */
        Button saveFile = new Button("Save File");
        Text textArea = new Text("...");

        /*
        Кнопка для сохранения .doc файлов
        если поля заполнены, то формирует doc файл
        Name - не пустое,
        Department - не пустое,
        Год рождения - четырёхзначный,
        Дата выбрана,
        Все проценты в виде десятичных чисел до от 0.0 до 99.99
        Имя файла - не пустое,
         */

        saveFile.setOnAction((e) -> {
            if(!nameTextField.getText().equals("") &&
                 date.getValue() != null &&
                !departmentTextField.getText().equals("") &&
                !yearTextField.getText().equals("") && yearTextField.getText().matches("[0-9]{4}") &&
                !PnhGrans.getText().equals("") && PnhGrans.getText().matches("[0-9]{1,2}[\\.\\,][0-9]{1,2}") &&
                !PnhMono.getText().equals("") && PnhMono.getText().matches("[0-9]{1,2}[\\.\\,][0-9]{1,2}") &&
                !PnhRBCi.getText().equals("") && PnhRBCi.getText().matches("[0-9]{1,2}[\\.\\,][0-9]{1,2}") &&
                !PnhRBCii.getText().equals("") && PnhRBCii.getText().matches("[0-9]{1,2}[\\.\\,][0-9]{1,2}") &&
                !fileTextField.getText().equals("")) 
            {
                textArea.setText("");
                Person person = new Person(nameTextField.getText(), yearTextField.getText());
                Result result = new Result(PnhGrans.getText(), PnhMono.getText(), PnhRBCi.getText(), PnhRBCii.getText());
                Analysis analysis = new Analysis(person, departmentTextField.getText(), result, date.getValue());

                FormDocFile formDocFile = new FormDocFile(analysis);
                logger.log(Level.INFO, "Forming .doc file: \n ФИО: {0} \n Отделение: {1} \n Дата: {2} \n Год рождения: {3} \n ПНГ Эритроциты II {4} \n ПНГ Эритроциты III {5}\n ПНГ Гранулоциты {6}\n ПНГ Моноциты {7}", new Object[]{nameTextField.getText(), departmentTextField.getText(), date.getValue(), yearTextField.getText(), PnhRBCi.getText(), PnhRBCii.getText(), PnhGrans.getText(), PnhMono.getText()});
                //formDocFile.setDate(date.getValue());
                //formDocFile.setName(nameTextField.getText());
                //formDocFile.setDepartment(departmentTextField.getText());
                //formDocFile.setYear(yearTextField.getText());
                //formDocFile.setPnhGran(PnhGrans.getText());
                //formDocFile.setPnhMono(PnhMono.getText());
                //formDocFile.setPnhRBC1(PnhRBCi.getText());
                //formDocFile.setPnhRBC2(PnhRBCii.getText());
                formDocFile.setFileName(fileTextField.getText());
                docWorker = pnhDocWorker(granURL, rbcURL, formDocFile);
                docWorker.messageProperty().addListener((obsv, ov, nv) -> {
                    textArea.setText(nv);
                });
                new Thread(docWorker).start();
            }
            else{
                String alertText = "";
                if(nameTextField.getText().equals("")) alertText = alertText+"не заполнено ФИО \n";
                if(departmentTextField.getText().equals("")) alertText = alertText + "не заполнено отделение\n";
                if(date.getValue() == null) alertText = alertText + "не заполнена дата\n";
                if(yearTextField.getText().equals("")) alertText = alertText + "не заполнен год рождения (нужно четырёхзначное число)\n";
                if(PnhGrans.getText().matches("[0-9]{1,2}[\\.\\,][0-9]{1,2}"))alertText = alertText + "некорректное значение у гранулоцитов (должно быть число в десятичном формате от 0.0 до 99.99, не более двух знаков после запятой) \n";
                if(PnhMono.getText().matches("[0-9]{1,2}[\\.\\,][0-9]{1,2}"))alertText = alertText + "некорректное значение у моноцитов(должно быть число в десятичном формате от 0.0 до 99.99, не более двух знаков после запятой) \n";
                if(PnhRBCi.getText().matches("[0-9]{1,2}[\\.\\,][0-9]{1,2}"))alertText = alertText + "некорректное значение у эритроцитов второго типа(должно быть число в десятичном формате от 0.0 до 99.99, не более двух знаков после запятой) \n";
                if(PnhRBCii.getText().matches("[0-9]{1,2}[\\.\\,][0-9]{1,2}"))alertText = alertText + "некорректное значение у эритроцитов третьего типа(должно быть число в десятичном формате от 0.0 до 99.99, не более двух знаков после запятой) \n";
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText(alertText);
                alert.setHeaderText(null);
                alert.showAndWait();
            }
        });


        group.add(saveFile,0,11);
        group.add(textArea, 1, 11);
        Button refresh = new Button("refresh");
        group.add(refresh,1, 12);
        refresh.setOnAction((e) -> {
            deniedRBCIcon.setVisible(false);
            rbcIcon.setVisible(false);
            rbcURL = null;
            rbcLabel.setText("No file");
            deniedGranIcon.setVisible(false);
            granIcon.setVisible(false);
            granURL = null;
            granLabel.setText("No file");
            nameTextField.setText("");
            yearTextField.setText("");
            departmentTextField.setText("");
            date.setValue(null);
            fileTextField.setText("");
            PnhGrans.setText("0.0");
            PnhMono.setText("0.0");
            PnhRBCi.setText("0.0");
            PnhRBCii.setText("0.0");
});
        scene.setOnDragOver((DragEvent event) -> {
            Dragboard db = event.getDragboard();
            if ( db.hasFiles()) {
                event.acceptTransferModes(TransferMode.LINK);
            }
            else {
                event.consume();
            }
        });
        // Dropping over surface
        scene.setOnDragDropped((DragEvent event) -> {
            Dragboard db = event.getDragboard();
            // image from the local file system.
            if (db.hasFiles() ) {
                db.getFiles().stream().forEach( file -> {
                    try {
                        if(isValidGranFile(file.toURI().toURL().toString())){

                            granIcon.setVisible(true);
                            deniedGranIcon.setVisible(true);
                            granURL = file.getAbsolutePath();
                            granLabel.setText(file.getName());
                        }
                        if(isValidRbcFile(file.toURI().toURL().toString())){
                            rbcIcon.setVisible(true);
                            deniedRBCIcon.setVisible(true);
                            rbcURL = file.getAbsolutePath();
                            rbcLabel.setText(file.getName());
                        }

                    }
                    catch (MalformedURLException ex) {
                        ex.printStackTrace();
                    }
                });
            }
            event.setDropCompleted(true);
            event.consume();
        });
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) throws IOException, InvalidFormatException {
        launch(args);
    }
    private boolean isValidGranFile(String url){
        return url.contains("gran") && url.endsWith(".pdf");
    }
    private boolean isValidRbcFile(String url){
        return (url.contains("rbc")||url.contains("RBC")) && url.endsWith(".pdf");
    }
    private Shape pdfIcon(){
    SVGPath Icon1 = new SVGPath();
    Icon1.setFill(Color.TRANSPARENT);
    Icon1.setStroke(Color.RED);

    Icon1.setContent("M52.626 36.843c-0.853-0.84-2.745-1.285-5.623-1.323-1.949-0.022-4.294 0.15-6.761 0.495-1.105-0.637-2.243-1.331-3.137-2.166-2.404-2.245-4.41-5.361-5.661-8.787 0.081-0.32 0.151-0.601 0.215-0.888 0 0 1.354-7.691 0.996-10.292-0.050-0.357-0.080-0.46-0.175-0.737l-0.118-0.302c-0.368-0.85-1.090-1.75-2.223-1.7l-0.682-0.021c-1.263 0-2.291 0.646-2.562 1.611-0.821 3.027 0.026 7.556 1.561 13.421l-0.393 0.955c-1.099 2.68-2.477 5.379-3.692 7.76l-0.158 0.31c-1.279 2.502-2.439 4.627-3.491 6.426l-1.086 0.574c-0.079 0.042-1.94 1.026-2.377 1.29-3.704 2.211-6.158 4.721-6.565 6.714-0.13 0.636-0.033 1.449 0.626 1.826l1.050 0.529c0.456 0.228 0.936 0.344 1.428 0.344 2.638 0 5.7-3.286 9.919-10.648 4.871-1.586 10.416-2.904 15.276-3.631 3.704 2.086 8.259 3.534 11.134 3.534 0.511 0 0.951-0.049 1.308-0.143 0.551-0.146 1.016-0.461 1.3-0.887 0.558-0.84 0.671-1.996 0.52-3.18-0.045-0.351-0.326-0.786-0.629-1.083zM13.228 50.878c0.481-1.315 2.385-3.915 5.2-6.222 0.177-0.144 0.613-0.552 1.012-0.931-2.944 4.695-4.915 6.567-6.213 7.154zM29.902 12.48c0.848 0 1.33 2.137 1.37 4.141s-0.429 3.41-1.010 4.451c-0.481-1.541-0.714-3.969-0.714-5.556 0 0-0.035-3.035 0.354-3.035v0zM24.928 39.843c0.591-1.057 1.205-2.172 1.833-3.355 1.531-2.895 2.497-5.16 3.217-7.022 1.432 2.605 3.215 4.82 5.312 6.594 0.262 0.221 0.539 0.444 0.83 0.666-4.263 0.843-7.948 1.869-11.192 3.116v0zM51.806 39.603c-0.26 0.162-1.003 0.256-1.482 0.256-1.544 0-3.455-0.706-6.133-1.854 1.029-0.076 1.973-0.115 2.819-0.115 1.549 0 2.008-0.007 3.522 0.38s1.534 1.171 1.274 1.333v0z ");
    SVGPath Icon2 = new SVGPath();
    Icon2.setFill(Color.TRANSPARENT);
    Icon2.setStroke(Color.RED);
    Icon2.setContent("M57.363 14.317c-1.388-1.893-3.323-4.106-5.449-6.231s-4.338-4.060-6.231-5.449c-3.223-2.364-4.787-2.637-5.683-2.637h-31c-2.757 0-5 2.243-5 5v54c0 2.757 2.243 5 5 5h46c2.757 0 5-2.243 5-5v-39c0-0.896-0.273-2.459-2.637-5.683v0zM49.086 10.914c1.919 1.919 3.425 3.65 4.536 5.086h-9.622v-9.622c1.436 1.111 3.167 2.617 5.086 4.536v0zM56 59c0 0.542-0.458 1-1 1h-46c-0.542 0-1-0.458-1-1v-54c0-0.542 0.458-1 1-1 0 0 30.997-0 31 0v14c0 1.105 0.895 2 2 2h14v39z");
    Shape shape = Path.union(Icon2, Icon1);
    shape.setStroke(Color.BLACK);
    shape.setFill(Color.RED);
    return shape;
}
public static Properties getProps(){
        return props;
}
private Task pnhDocWorker(String granURL, String rbcURL, FormDocFile formDocFile) {
        return new Task() {
            @Override
            protected Object call() throws Exception
            {
                updateMessage("...");

                PDFtoImage granFile = new PDFtoImage(new File(granURL));
                updateMessage("making gran image");
                String granImageUrl = granFile.getSubImage("gran");
                updateMessage("making mono image");
                String monoImageUrl = granFile.getSubImage("mono");
                granFile.closeDoc();
                PDFtoImage rbcFile = new PDFtoImage(new File(rbcURL));
                updateMessage("making rbc image");
                String rbcImageUrl = rbcFile.getSubImage("rbc");
                rbcFile.closeDoc();
                updateMessage("making doc file");
                formDocFile.setGranImageUrl(granImageUrl);
                formDocFile.setMonoImageUrl(monoImageUrl);
                formDocFile.setRbcImageUrl(rbcImageUrl);
                formDocFile.setGranURL(granURL);
                formDocFile.writeToDocFile();
                updateMessage("done!");
                return true;
            }
        };
    }
}
