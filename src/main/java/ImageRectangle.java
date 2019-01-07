import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

public class ImageRectangle {
    private double width;
    private double height;
    private double x;
    private double y;

    public ImageRectangle(double x, double y, double width, double height){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    public ImageRectangle(){
        this.x = 0;
        this.y = 0;
        this.width = 0;
        this.height = 0;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void initializeGrans(Properties properties){
        this.x = Double.parseDouble(properties.getProperty("GRAN_X"));
        this.y = Double.parseDouble(properties.getProperty("GRAN_Y"));
        this.width = Double.parseDouble(properties.getProperty("GRAN_Width"));
        this.height = Double.parseDouble(properties.getProperty("GRAN_Height"));
    }
    public void storeGrans(Properties properties){
        try(OutputStream os = new FileOutputStream("config.properties")){
            properties.setProperty("GRAN_X", Double.toString(this.x));
            properties.setProperty("GRAN_Y", Double.toString(this.y));
            properties.setProperty("GRAN_Width", Double.toString(this.width));
            properties.setProperty("GRAN_Height", Double.toString(this.height));
           properties.store(os,null);
        }
        catch (IOException exc){
            System.out.println("Could not store properties...");
        }
    }
}
