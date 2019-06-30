import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class PDFtoImage {
    private File file;
    private PDDocument document;
    private PDFRenderer renderer;
    private static Logger logger = Logger.getLogger(StagesFactory.class.getName());
    public PDFtoImage(File file){
    this.file = file;
    System.setProperty("sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider");
    try {
        document = PDDocument.load(file);
    }
    catch (IOException e){
        System.out.println("Could not load file");

    }
    renderer = new PDFRenderer(document);

}
public String getImageUrlFromPDF() throws IOException {
    return getImageUrlFromPDF(0);
}
    
public String getImageUrlFromPDF(int pageNumber) throws IOException {
    String fileName = "resources\\" +file.getName().substring(0,file.getName().length() - 4) + ".jpg";
    return getImageUrlFromPDF(pageNumber, fileName);
}

public String getImageUrlFromPDF(int pageNumber, String fileName) throws IOException {

        renderer = new PDFRenderer(document);
        BufferedImage image = renderer.renderImage(pageNumber, 1f);

        File imgFile = new File(fileName + ".jpg");
        ImageIO.write(image, "JPEG", imgFile);
        //document.close();
        return imgFile.toURI().toURL().toString();
}

public String getSubImageURLFromPDF(ImageRectangle imageRectangle, String fileName) throws IOException{
    return getSubImageURLFromPDF(imageRectangle, fileName, imageRectangle.getPageNumber());
}
public String getSubImageURLFromPDF(ImageRectangle imageRectangle, String fileName, int pageNumber) throws IOException{
    renderer = new PDFRenderer(document);
    BufferedImage image = renderer.renderImage(pageNumber,3);
    BufferedImage subImage = image.getSubimage((int)imageRectangle.getX()*3, (int)imageRectangle.getY()*3,(int)imageRectangle.getWidth()*3, (int)imageRectangle.getHeight()*3);
    File imgFile = new File(fileName + ".jpg");
    ImageIO.write(subImage, "JPEG", imgFile);
    //document.close();

    return imgFile.getAbsolutePath();
}

public String getSubImage(String fileName) throws IOException{
    ImageRectangle imageRectangle = new ImageRectangle();
    switch (fileName){
        case "mono":
            imageRectangle.initializeMono(Main.getProps());
            break;
        case "gran":
            imageRectangle.initializeGrans(Main.getProps());
            break;
        case "rbc":
            imageRectangle.initializeRBC(Main.getProps());
            break;
    }
    return getSubImageURLFromPDF(imageRectangle, fileName);
}
public void closeDoc(){
    try {
        document.close();
        System.out.println("document ii closed");
    }
    catch(IOException e){
        System.out.println("could not close file");
    }

}
public int getPageNumber(){
    return document.getNumberOfPages();
}
}
