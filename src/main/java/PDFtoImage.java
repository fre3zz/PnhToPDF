import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PDFtoImage {
    private File file;
public PDFtoImage(File file){
    this.file = file;
    System.setProperty("sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider");
}
public String getImageUrlFromPDF() throws IOException {
    PDDocument document = PDDocument.load(file);
    PDFRenderer renderer = new PDFRenderer(document);
    BufferedImage image = renderer.renderImage(0);

    File imgFile = new File(file.getName().substring(0,file.getName().length() - 4) + ".jpg");
    ImageIO.write(image, "JPEG", imgFile);
    document.close();
return imgFile.toURI().toURL().toString();
}
public String getSubImageURLFromPDF(ImageRectangle imageRectangle, String fileName) throws IOException{
    PDDocument document = PDDocument.load(file);
    PDFRenderer renderer = new PDFRenderer(document);
    BufferedImage image = renderer.renderImage(0);
    BufferedImage subImage = image.getSubimage((int)imageRectangle.getX(), (int)imageRectangle.getY(),(int)imageRectangle.getWidth(), (int)imageRectangle.getHeight());
    File imgFile = new File(fileName + ".jpg");
    ImageIO.write(subImage, "JPEG", imgFile);
    document.close();

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
}
