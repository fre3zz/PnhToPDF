import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FormDocFile {
    String name = "файл";
    String year;
    String department;
    String granURL;

    XWPFDocument document;
    String granImageUrl;
    String monoImageUrl;
    String rbcImageUrl;
    LocalDate date;
    String fileName;
    String pnhGran;
    String pnhMono;
    String pnhRBC1;
    String pnhRBC2;


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

    public FormDocFile(){

    }
    public FormDocFile(String name, String year, String department, LocalDate date){
        this.name = name;
        this.year = year;
        this.department = department;
        this.date = date;

    }
    public void writeToDocFile() throws IOException, InvalidFormatException {
        File template = new File("template.docx");
        InputStream is = new FileInputStream(template);
        document = new XWPFDocument(is);
        changeString("name",name);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        changeString("date", date.format(dtf));
        changeString("year", year);
        changeString("department", department);

        File granFile = new File(granURL);

        XWPFTableCell GRANcell = getCellFromTable(1,1,1);
        XWPFParagraph par = GRANcell.getParagraphs().get(0);
        XWPFRun run = par.createRun();
        InputStream granIs = new FileInputStream(granImageUrl);
        run.addPicture(granIs, XWPFDocument.PICTURE_TYPE_JPEG, granImageUrl,Units.toEMU(200), Units.toEMU(200));


        XWPFTableCell MONOcell = getCellFromTable(1,3,1);
        par = MONOcell.getParagraphs().get(0);
        run = par.createRun();
        InputStream monoIs = new FileInputStream(monoImageUrl);
        run.addPicture(monoIs, XWPFDocument.PICTURE_TYPE_JPEG, monoImageUrl,Units.toEMU(200), Units.toEMU(200));


        XWPFTableCell RBCcell = getCellFromTable(1,1,0);
        par = RBCcell.getParagraphs().get(0);
        run = par.createRun();
        InputStream rbcIs = new FileInputStream(rbcImageUrl);
        run.addPicture(rbcIs, XWPFDocument.PICTURE_TYPE_JPEG, rbcImageUrl,Units.toEMU(200), Units.toEMU(200));

        //Write the Document in file system



        String resultFileURL = granFile.getAbsolutePath().substring(0, granFile.getAbsolutePath().length() - granFile.getName().length()) + fileName +".docx";
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getGranURL() {
        return granURL;
    }

    public XWPFDocument getDocument() {
        return document;
    }

    public void setDocument(XWPFDocument document) {
        this.document = document;
    }

    public String getGranImageUrl() {
        return granImageUrl;
    }

    public String getMonoImageUrl() {
        return monoImageUrl;
    }

    public String getRbcImageUrl() {
        return rbcImageUrl;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getPnhGran() {
        return pnhGran;
    }

    public void setPnhGran(String pnhGran) {
        this.pnhGran = pnhGran;
    }

    public String getPnhMono() {
        return pnhMono;
    }

    public void setPnhMono(String pnhMono) {
        this.pnhMono = pnhMono;
    }

    public String getPnhRBC1() {
        return pnhRBC1;
    }

    public void setPnhRBC1(String pnhRBC1) {
        this.pnhRBC1 = pnhRBC1;
    }

    public String getPnhRBC2() {
        return pnhRBC2;
    }

    public void setPnhRBC2(String pnhRBC2) {
        this.pnhRBC2 = pnhRBC2;
    }

    public void changeString(String initialText, String targetText){
        for (XWPFParagraph p : document.getParagraphs()) {
            List<XWPFRun> runs = p.getRuns();
            if (runs != null) {

                for (XWPFRun r : runs) {
                    String text = r.getText(0);

                    if (text != null && (text.contains(initialText) || text.equalsIgnoreCase(initialText))) {
                        System.out.println("replacing text");
                        text = text.replace(initialText, targetText);

                        r.setText(text, 0);
                    }
                }
            }
        }
    }
}
