import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;



import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class StagesFactory {
    public static Stage pdfImageView(ArrayList<String> imageUrl){
        Stage stage = new Stage();
        Group group = new Group();
        IntegerProperty pageNumber = new SimpleIntegerProperty();
        pageNumber.set(0);
        ArrayList<Image> imageList = new ArrayList<>();
        for(String s : imageUrl){
            imageList.add(new Image(s, false));
        }


        ImageView imageView = new ImageView(imageList.get(pageNumber.get()));

        group.getChildren().add(imageView);
        Scene scene = new Scene(group, imageList.get(0).getWidth(), imageList.get(0).getHeight());
        //group.prefHeight(image.getHeight());
        //group.prefWidth(image.getWidth());
        stage.setScene(scene);
        Canvas canvas = new Canvas();
        canvas.widthProperty().bind(stage.widthProperty());
        canvas.heightProperty().bind(stage.heightProperty());
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setLineWidth(2);
        gc.setStroke(Color.BLACK);
        gc.setFill(Color.TRANSPARENT);
        gc.setLineDashes(15, 5);

        Button showRBC = new Button("show RBC");
        Button showGran = new Button("show Grans");
        Button showMono = new Button("show Monos");

        Button storeRBC = new Button("store RBC");
        Button storeGran = new Button("store Grans");
        Button storeMono = new Button("store Monos");

        GridPane gp = new GridPane();
        gp.setPadding(new Insets(3));
        gp.setVgap(3);
        gp.setHgap(3);
        gp.setAlignment(Pos.CENTER);
        ColumnConstraints cs1 = new ColumnConstraints();
        ColumnConstraints cs2 = new ColumnConstraints();
        ColumnConstraints cs3 = new ColumnConstraints();
        gp.getColumnConstraints().addAll(cs1,cs2,cs3);
        gp.add(showRBC, 0, 0);
        gp.add(showGran, 1, 0);
        gp.add(showMono, 2, 0);
        gp.add(storeRBC, 0, 1);
        gp.add(storeGran, 1,1);
        gp.add(storeMono, 2,1);
        gp.setLayoutY(15);
        gp.setLayoutX(15);
        Button nextImage = new Button(">");
        Button prevImage = new Button("<");
        HBox hbox = new HBox();
        hbox.getChildren().addAll(prevImage, nextImage);
        hbox.setLayoutX(imageList.get(0).getWidth() - 70);
        hbox.setLayoutY(imageList.get(0).getHeight() - 40);


nextImage.setOnAction((e)->{
    if(pageNumber.get() < imageUrl.size() - 1){
        gc.clearRect(0,0,stage.getWidth(), stage.getHeight());
        pageNumber.setValue(pageNumber.getValue() + 1);
        imageView.setImage(imageList.get(pageNumber.getValue()));
    }
});
prevImage.setOnAction((e)->{
    if(pageNumber.get() != 0){
        gc.clearRect(0,0,stage.getWidth(), stage.getHeight());
        pageNumber.setValue(pageNumber.getValue() - 1);
        imageView.setImage(imageList.get(pageNumber.getValue()));
    }
});


        Rectangle rect = new Rectangle();
        ImageRectangle imageRectangle = new ImageRectangle();

        showGran.setOnAction((e)->{
            gc.clearRect(0,0,stage.getWidth(), stage.getHeight());
            imageRectangle.initializeGrans(Main.getProps());
            if(imageRectangle.getPageNumber() != pageNumber.get()){
                pageNumber.set(imageRectangle.getPageNumber());
                imageView.setImage(imageList.get(pageNumber.get()));
            }
            gc.strokeRect(imageRectangle.getX(), imageRectangle.getY(), imageRectangle.getWidth(), imageRectangle.getHeight());
        });
        storeGran.setOnAction((e) ->{
            imageRectangle.setPageNumber(pageNumber.get());
            imageRectangle.storeGrans(Main.getProps());
        });
        showMono.setOnAction((e)->{
            gc.clearRect(0,0,stage.getWidth(), stage.getHeight());
            imageRectangle.initializeMono(Main.getProps());
            if(imageRectangle.getPageNumber() != pageNumber.get()){
                pageNumber.set(imageRectangle.getPageNumber());
                imageView.setImage(imageList.get(pageNumber.get()));
            }
            gc.strokeRect(imageRectangle.getX(), imageRectangle.getY(), imageRectangle.getWidth(), imageRectangle.getHeight());
        });
        storeMono.setOnAction((e) ->{
            imageRectangle.setPageNumber(pageNumber.get());
            imageRectangle.storeMono(Main.getProps());
        });
        showRBC.setOnAction((e)->{
            gc.clearRect(0,0,stage.getWidth(), stage.getHeight());
            imageRectangle.initializeRBC(Main.getProps());
            if(imageRectangle.getPageNumber() != pageNumber.get()){
                pageNumber.set(imageRectangle.getPageNumber());
                imageView.setImage(imageList.get(pageNumber.get()));
            }
            gc.strokeRect(imageRectangle.getX(), imageRectangle.getY(), imageRectangle.getWidth(), imageRectangle.getHeight());
        });
        storeRBC.setOnAction((e) ->{
            imageRectangle.setPageNumber(pageNumber.get());
            imageRectangle.storeRBC(Main.getProps());

        });
        canvas.setOnMousePressed((e)->{
            gc.clearRect(0,0,stage.getWidth(), stage.getHeight());

            rect.setX(e.getX());
            rect.setY(e.getY());
        });
        canvas.setOnMouseReleased((e)->{
            rect.setWidth(Math.abs((e.getX() - rect.getX())));
            rect.setHeight(Math.abs((e.getY() - rect.getY())));
            //rect.setX((rect.getX() > e.getX()) ? e.getX(): rect.getX());
            if(rect.getX() > e.getX()) {
                rect.setX(e.getX());
            }
    //rect.setY((rect.getY() > e.getY()) ? e.getY(): rect.getY());
            if(rect.getY() > e.getY()) {
                rect.setY(e.getY());
            }

            imageRectangle.setX(rect.getX());
            imageRectangle.setY(rect.getY());
            imageRectangle.setWidth(rect.getWidth());
            imageRectangle.setHeight(rect.getHeight());

            gc.fillRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
            gc.strokeRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
    //System.out.printf("x: %f ; \n y: %f ; \n width: %f ; \n height: %f ;", rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
});

        group.getChildren().addAll(canvas, gp,hbox);
        stage.setWidth(imageList.get(0).getWidth());
        stage.setHeight(imageList.get(0).getHeight());
stage.setResizable(false);
stage.setX(10);
stage.setY(10);
stage.sizeToScene();
        return stage;
    }
}
