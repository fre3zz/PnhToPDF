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
    File imgFile = new File("C://PdfBox_Examples//"+ file.getName().substring(0,file.getName().length() - 4) + ".jpg");
    //File imgFile = new File("C://PdfBox_Examples//123.jpg");
    ImageIO.write(image, "JPEG", imgFile);
return imgFile.toURI().toURL().toString();
}

}
