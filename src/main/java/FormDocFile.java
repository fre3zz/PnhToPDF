import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;

import java.io.*;
import java.util.List;

public class FormDocFile {
    String name = "файл";
    String year;
    String department;
    String granURL;
    String rbcURL;
    XWPFDocument document;
    String granImageUrl;
    String monoImageUrl;
    String rbcImageUrl;
    String date;

    public void setGranURL(String granURL) {
        this.granURL = granURL;
    }

    public void setGranImageUrl(String granImageUrl) {
        this.granImageUrl = granImageUrl;
    }

    public void setMonoImageUrl(String monoImageUrl) {
        this.monoImageUrl = monoImageUrl;
    }

    public void setRbcImageUrl(String rbcImageUrl) {
        this.rbcImageUrl = rbcImageUrl;
    }

    public FormDocFile(String granURL, String rbcUrl){

        this.granURL = granURL;
        this.rbcURL = rbcUrl;
    }
    public FormDocFile(String name, String year, String department, String date){
        this.name = name;
        this.year = year;
        this.department = department;
        this.date = date;

    }
    public void writeToDocFile() throws IOException, InvalidFormatException {
        File template = new File("template.docx");
        InputStream is = new FileInputStream(template);
        document = new XWPFDocument(is);
        changeString("pnh","бубубу");
        XWPFTableCell cell = getCellFromTable(0,1,1);
        XWPFParagraph par = cell.addParagraph();

        XWPFRun run = par.createRun();

        File granFile = new File(granURL);

        //PDFtoImage granFileImg = new PDFtoImage(new File(granURL));
        //String granImageUrl = granFileImg.getSubImage("gran");
        InputStream granIs = new FileInputStream(granImageUrl);
        run.addPicture(granIs, XWPFDocument.PICTURE_TYPE_JPEG, granImageUrl,Units.toEMU(200), Units.toEMU(200));


        //String monoImageUrl = granFileImg.getSubImage("mono");
        InputStream monoIs = new FileInputStream(monoImageUrl);
        run.addPicture(monoIs, XWPFDocument.PICTURE_TYPE_JPEG, monoImageUrl,Units.toEMU(200), Units.toEMU(200));


        //PDFtoImage rbcFileImg = new PDFtoImage(new File(rbcURL));
        //String rbcImageUrl = rbcFileImg.getSubImage("rbc");
        InputStream rbcIs = new FileInputStream(rbcImageUrl);
        run.addPicture(rbcIs, XWPFDocument.PICTURE_TYPE_JPEG, rbcImageUrl,Units.toEMU(200), Units.toEMU(200));

        //Write the Document in file system



        String resultFileURL = granFile.getAbsolutePath().substring(0, granFile.getAbsolutePath().length() - granFile.getName().length()) + name +".doc";
        FileOutputStream out = new FileOutputStream(resultFileURL);
        granIs.close();
        monoIs.close();
        rbcIs.close();
        document.write(out);
        out.close();
    }
    public XWPFTableCell getCellFromTable(int tableNumber, int rowNumber, int cellNumber){
     XWPFTable table = document.getTables().get(tableNumber);
     XWPFTableRow row = table.getRow(rowNumber);
     XWPFTableCell cell = row.getCell(cellNumber);
     return cell;
    }
    public void changeString(String initialText, String targetText){
        for (XWPFParagraph p : document.getParagraphs()) {
            List<XWPFRun> runs = p.getRuns();
            if (runs != null) {
                for (XWPFRun r : runs) {
                    String text = r.getText(0);
                    if (text != null && text.contains(initialText)) {
                        text = text.replace(initialText, targetText);
                        r.setText(text, 0);
                    }
                }
            }
        }
    }
}
