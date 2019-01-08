import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.wp.usermodel.Paragraph;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import javax.imageio.ImageIO;


import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.util.Properties;

public class Main extends Application {
    private static String granURL = null;
    private static String rbcURL = null;
    private static Properties props;
    private Task docWorker;

    public void init(){
        System.out.println("init");
        props = new Properties();
        //initProperties(properties);
        try(InputStream is = new FileInputStream("config.properties")){
            props.load(is);
        }
        catch (IOException e){
            System.out.println("could not load properties.");
        }
    }

    @Override
    public void start(Stage primaryStage) throws IOException{
        GridPane group = new GridPane();
        group.setHgap(10);
        group.setVgap(10);
        group.setPadding(new Insets(10));
        Scene scene = new Scene(group, 700,500);
        ColumnConstraints cs1 = new ColumnConstraints();
        ColumnConstraints cs2 = new ColumnConstraints();
        group.getColumnConstraints().addAll(cs1, cs2);
        cs1.setPrefWidth(400);
        Label name = new Label("ФИО:");
        group.add(name, 0, 0);
        TextField nameTextField = new TextField();
        group.add(nameTextField, 1, 0);
        group.add(new Label("год рождения:"), 0, 1);
        TextField yearTextField = new TextField();
        group.add(yearTextField, 1,1);
        group.add(new Label("Отделение:"), 0,2);
        TextField departmentTextField = new TextField();
        group.add(departmentTextField,1,2);
        group.add(new Label("ПНГ I тип эритроциты:"), 0, 3);
        TextField PnhRBCi = new TextField();
        group.add(PnhRBCi, 1,3);
        group.add(new Label("ПНГ II тип эритроциты:"), 0, 4);
        TextField PnhRBCii = new TextField();
        group.add(PnhRBCii, 1,4);
        group.add(new Label("ПНГ гранулоциты:"), 0, 5);
        TextField PnhGrans = new TextField();
        group.add(PnhGrans, 1,5);
        group.add(new Label("ПНГ моноциты:"), 0, 6);
        TextField PnhMono = new TextField();
        group.add(PnhMono, 1,6);
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
group.add(Granhbox, 0 , 7);

        Button granButton = new Button("Gran and Mono region select");
        granButton.setOnAction((e)->{
            if(granURL != null) {
                PDFtoImage gran = new PDFtoImage(new File(granURL));
                String granImage = null;
                try {
                    granImage = gran.getImageUrlFromPDF();
                } catch (IOException exc) {
                    System.out.print(exc);
                }
                Stage stage = StagesFactory.pdfImageView(granImage);
                stage.show();
            }
        });
        group.add(granButton, 1, 7);

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
        group.add(Rbchbox, 0 , 8);
        Button rbcButton = new Button("RBC region select");
        rbcButton.setOnAction((e)->{
            if(rbcURL != null) {
                PDFtoImage rbc = new PDFtoImage(new File(rbcURL));
                String rbcImage = null;
                try {
                    rbcImage = rbc.getImageUrlFromPDF();
                } catch (IOException exc) {
                    System.out.print(exc);
                }
                Stage stage = StagesFactory.pdfImageView(rbcImage);
                stage.show();
            }
        });
        group.add(rbcButton, 1, 8);

        Button saveFile = new Button("Save File");
        Text textArea = new Text("...");
        saveFile.setOnAction((e) -> {

            textArea.setText("");
            FormDocFile formDocFile = new FormDocFile("name", "year", "department", "date");
            docWorker = pnhDocWorker(granURL, rbcURL, formDocFile);
            docWorker.messageProperty().addListener((obsv, ov, nv) ->{
                textArea.setText(nv);
            });
            new Thread(docWorker).start();

        });
        group.add(saveFile,0,9);
        group.add(textArea, 1, 9);

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
        /*System.setProperty("sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider");
        File file = new File("C://PdfBox_Examples//Blokh_Dzh_-_Java_Effektivnoe_programmirovanie_2_izdanie_-_2008.pdf");
        //Saving the document
        PDDocument document = PDDocument.load(file);
        document.save("C://PdfBox_Examples//my_doc.pdf");
        PDFRenderer renderer = new PDFRenderer(document);

        //Rendering an image from the PDF document
        BufferedImage image = renderer.renderImage(0);
BufferedImage smallImage = image.getSubimage(100,100,300,300);
        //Writing the image to a file
        ImageIO.write(smallImage, "JPEG", new File("C://PdfBox_Examples//myimage.jpg"));

        System.out.println("Image created");
        System.out.println("PDF created");

        //Closing the document
        document.close();

        XWPFDocument document1 = new XWPFDocument();
        XWPFParagraph par = document1.createParagraph();
        XWPFRun run = par.createRun();
        InputStream is = new FileInputStream("C://PdfBox_Examples//myimage.jpg");
        run.addPicture(is, XWPFDocument.PICTURE_TYPE_JPEG, "C://PdfBox_Examples//myimage.jpg",Units.toEMU(200), Units.toEMU(200));

        //Write the Document in file system
        FileOutputStream out = new FileOutputStream( new File("C://PdfBox_Examples//createdocument.docx"));
        is.close();
        document1.write(out);
        out.close();
        FormDocFile newFile = new FormDocFile(granURL, rbcURL);
        newFile.writeToDocFile();
        */
    }
    private boolean isValidGranFile(String url){
        return url.contains("gran") && url.endsWith(".pdf");
    }
    private boolean isValidRbcFile(String url){
        return url.contains("rbc") && url.endsWith(".pdf");
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
                PDFtoImage rbcFile = new PDFtoImage(new File(rbcURL));
                updateMessage("making rbc image");
                String rbcImageUrl = rbcFile.getSubImage("rbc");
                updateMessage("making doc file");
                formDocFile.setGranImageUrl(granImageUrl);
                formDocFile.setMonoImageUrl(monoImageUrl);
                formDocFile.setRbcImageUrl(rbcImageUrl);
                formDocFile.setGranURL(granURL);
                formDocFile.writeToDocFile();

                return true;
            }
        };
    }
}
