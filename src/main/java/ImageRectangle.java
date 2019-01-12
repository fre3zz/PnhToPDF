import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

public class ImageRectangle {
    private double width;
    private double height;
    private double x;
    private double y;
    private int pageNumber;

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
    public void setPageNumber(int pageNumber){
        this.pageNumber = pageNumber;
    }
    public void initializeGrans(Properties properties){
        this.x = Double.parseDouble(properties.getProperty("GRAN_X"));
        this.y = Double.parseDouble(properties.getProperty("GRAN_Y"));
        this.width = Double.parseDouble(properties.getProperty("GRAN_Width"));
        this.height = Double.parseDouble(properties.getProperty("GRAN_Height"));
        this.pageNumber = Integer.parseInt(properties.getProperty("GRAN_Page"));
    }
    public void initializeRBC(Properties properties){
        this.x = Double.parseDouble(properties.getProperty("RBC_X"));
        this.y = Double.parseDouble(properties.getProperty("RBC_Y"));
        this.width = Double.parseDouble(properties.getProperty("RBC_Width"));
        this.height = Double.parseDouble(properties.getProperty("RBC_Height"));
        this.pageNumber = Integer.parseInt(properties.getProperty("RBC_Page"));
    }
    public void initializeMono(Properties properties){
        this.x = Double.parseDouble(properties.getProperty("MONO_X"));
        this.y = Double.parseDouble(properties.getProperty("MONO_Y"));
        this.width = Double.parseDouble(properties.getProperty("MONO_Width"));
        this.height = Double.parseDouble(properties.getProperty("MONO_Height"));
        this.pageNumber = Integer.parseInt(properties.getProperty("MONO_Page"));
    }
    public void storeGrans(Properties properties){
        try(OutputStream os = new FileOutputStream("config.properties")){
            properties.setProperty("GRAN_X", Double.toString(this.x));
            properties.setProperty("GRAN_Y", Double.toString(this.y));
            properties.setProperty("GRAN_Width", Double.toString(this.width));
            properties.setProperty("GRAN_Height", Double.toString(this.height));
            properties.setProperty("GRAN_Page", Integer.toString(this.pageNumber));
           properties.store(os,null);
        }
        catch (IOException exc){
            System.out.println("Could not store properties...");
        }
    }
    public void storeRBC(Properties properties){
        try(OutputStream os = new FileOutputStream("config.properties")){
            properties.setProperty("RBC_X", Double.toString(this.x));
            properties.setProperty("RBC_Y", Double.toString(this.y));
            properties.setProperty("RBC_Width", Double.toString(this.width));
            properties.setProperty("RBC_Height", Double.toString(this.height));
            properties.setProperty("RBC_Page", Integer.toString(this.pageNumber));
            properties.store(os,null);
        }
        catch (IOException exc){
            System.out.println("Could not store properties...");
        }
    }
    public void storeMono(Properties properties){
        try(OutputStream os = new FileOutputStream("config.properties")){
            properties.setProperty("MONO_X", Double.toString(this.x));
            properties.setProperty("MONO_Y", Double.toString(this.y));
            properties.setProperty("MONO_Width", Double.toString(this.width));
            properties.setProperty("MONO_Height", Double.toString(this.height));
            properties.setProperty("MONO_Page", Integer.toString(this.pageNumber));
            properties.store(os,null);
        }
        catch (IOException exc){
            System.out.println("Could not store properties...");
        }
    }
}
