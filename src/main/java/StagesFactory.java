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

public class StagesFactory {
    public static Stage pdfImageView(String imgUrl){
        Stage stage = new Stage();
        Group group = new Group();

        Image image = new Image(imgUrl, false);

        ImageView imageView = new ImageView(image);

        group.getChildren().add(imageView);
        Scene scene = new Scene(group, image.getWidth(), image.getHeight());
        //group.prefHeight(image.getHeight());
        //group.prefWidth(image.getWidth());
        stage.setScene(scene);
        Canvas canvas = new Canvas();
        canvas.widthProperty().bind(stage.widthProperty());
        canvas.heightProperty().bind(stage.heightProperty());
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setLineWidth(2);

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

        group.getChildren().add(gp);




        Rectangle rect = new Rectangle();
        canvas.setOnMousePressed((e)->{
            gc.clearRect(0,0,stage.getWidth(), stage.getHeight());
            gc.setStroke(Color.BLACK);
            gc.setFill(Color.TRANSPARENT);
            gc.setLineDashes(15, 5);
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

    gc.fillRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
    gc.strokeRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
    System.out.printf("x: %f ; \n y: %f ; \n width: %f ; \n height: %f ;", rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
});

        group.getChildren().addAll(canvas);
        stage.setWidth(image.getWidth());
        stage.setHeight(image.getHeight());
stage.setResizable(false);

        return stage;
    }
}
