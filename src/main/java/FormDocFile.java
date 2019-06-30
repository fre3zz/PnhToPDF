import classes.Analysis;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;
import java.awt.*;
import java.io.*;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

class FormDocFile {
    private String name = "файл";
    private String year;
    private String department;
    private String granURL;
    private static final String TEMPLATE_FILE = "template.docx";
    private XWPFDocument document;
    private String granImageUrl;
    private String monoImageUrl;
    private String rbcImageUrl;
    private LocalDate date;
    private String fileName;
    private String pnhGran;
    private String pnhMono;
    private String pnhRBC1;
    private String pnhRBC2;
    private Analysis analysis;
    private static Logger logger = Logger.getLogger(FormDocFile.class.getName());

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

    public FormDocFile(Analysis analysis){
        logger.info("FormDocFile created");
        this.analysis = analysis;
    }

    //Метод для записи docx файла
    public void writeToDocFile() throws IOException, InvalidFormatException {

        File granFile = new File(granURL);
        File template = new File(TEMPLATE_FILE);
        InputStream is = new FileInputStream(template);
        document = new XWPFDocument(is);

        changeString("name", analysis.getPerson().getName());
        changeString("date", analysis.getDateString());
        changeString("year", analysis.getPerson().getYear());
        changeString("department", analysis.getDepartment());

        XWPFTable table = document.getTables().get(0);

        changeString("RBC1", analysis.getResult().getPnhRBC1(), table);
        changeString("RBC2", analysis.getResult().getPnhRBC2(), table);
        changeString("MONO", analysis.getResult().getPnhMono(), table);
        changeString("GRAN", analysis.getResult().getPnhGran(), table);
        changeString("WTF", analysis.getResult().getPnhRBCtot(), table);
        changeString("result", analysis.getResult().getResult());

        XWPFTableCell GRANcell = getCellFromTable(1, 1, 1);
        XWPFParagraph par = GRANcell.getParagraphs().get(0);
        XWPFRun run = par.createRun();
        InputStream granIs = new FileInputStream(granImageUrl);
        run.addPicture(granIs, XWPFDocument.PICTURE_TYPE_JPEG, granImageUrl, Units.toEMU(155), Units.toEMU(155));

        XWPFTableCell MONOcell = getCellFromTable(1, 3, 1);
        par = MONOcell.getParagraphs().get(0);
        run = par.createRun();
        InputStream monoIs = new FileInputStream(monoImageUrl);
        run.addPicture(monoIs, XWPFDocument.PICTURE_TYPE_JPEG, monoImageUrl, Units.toEMU(155), Units.toEMU(155));

        XWPFTableCell RBCcell = getCellFromTable(1, 1, 0);
        par = RBCcell.getParagraphs().get(0);
        run = par.createRun();
        InputStream rbcIs = new FileInputStream(rbcImageUrl);
        run.addPicture(rbcIs, XWPFDocument.PICTURE_TYPE_JPEG, rbcImageUrl, Units.toEMU(300), Units.toEMU(300));

        //Write the Document in file system

        System.out.print(granFile.getParentFile().getAbsolutePath());
        String resultFileURL =  granFile.getParentFile().getAbsolutePath()+ "\\" + fileName + ".docx";
        FileOutputStream out = new FileOutputStream(resultFileURL);
        
        granIs.close();
        monoIs.close();
        rbcIs.close();
        document.write(out);
        out.close();
        Desktop.getDesktop().open(new File(resultFileURL));
        File file = new File(granImageUrl);
        file.delete();
        file = new File(monoImageUrl);
        file.delete();
        file = new File(rbcImageUrl);
        file.delete();
    }

    private XWPFTableCell getCellFromTable(int tableNumber, int rowNumber, int cellNumber) {
        XWPFTable table = document.getTables().get(tableNumber);
        XWPFTableRow row = table.getRow(rowNumber);
        XWPFTableCell cell = row.getCell(cellNumber);
        return cell;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    private void changeString(String initialText, String targetText) {
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

    private void changeString(String initialText, String targetText, XWPFTable table) {
        for (XWPFTableRow row : table.getRows()) {
            for (XWPFTableCell cell : row.getTableCells()) {
                for (XWPFParagraph par : cell.getParagraphs()) {
                    List<XWPFRun> runs = par.getRuns();
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
    }
}
