package classes;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

/*
Класс, содежащий ввобдимые как String данные,
и возвращающий данные в формате String или Double в зависимости от запроса
 */
public class Result {
    private String pnhGran;
    private String pnhMono;
    private String pnhRBC1;
    private String pnhRBC2;
    private String pnhRBCtot;
    private double doubleGran;
    private double doubleMono;
    private double doubleRBC1;
    private double doubleRBC2;
    private double rbcTot;
    private final static String NEGATIVE = "ПНГ-клон не выявляется.";
    private final static String POSITIVE = "ПНГ-клон выявляется.";
    private final static String MINOR = "Выявляется минорный ПНГ-клон.";
    private String result;
    public Result(String pnhGran, String pnhMono, String pnhRBC1, String pnhRBC2){
        doubleGran = parseString(pnhGran);
        doubleMono = parseString(pnhMono);
        doubleRBC1 = parseString(pnhRBC1);
        doubleRBC2 = parseString(pnhRBC2);
        rbcTot = doubleRBC1+doubleRBC2;
        this.pnhGran = formatDouble(doubleGran);
        this.pnhMono = formatDouble(doubleMono);
        this.pnhRBC1 = formatDouble(doubleRBC1);
        this.pnhRBC2 = formatDouble(doubleRBC2);
        pnhRBCtot = formatDouble(rbcTot);
        if(rbcTot == 0d && doubleGran == 0d && doubleMono == 0d)result = NEGATIVE;
        else if(rbcTot < 1d && doubleGran < 1d && doubleMono < 1d) result = MINOR;
        else result = POSITIVE;
    }

    public String getPnhRBCtot() {
        return pnhRBCtot;
    }

    public String getPnhGran() {
        return pnhGran;
    }

    public String getResult() {
        return result;
    }

    public String getPnhMono() {
        return pnhMono;

    }

    public String getPnhRBC1() {
        return pnhRBC1;
    }

    public String getPnhRBC2() {
        return pnhRBC2;
    }

    public double getDoubleGran() {
        return doubleGran;
    }

    public double getDoubleMono() {
        return doubleMono;
    }

    public double getDoubleRBC1() {
        return doubleRBC1;
    }

    public double getDoubleRBC2() {
        return doubleRBC2;
    }

    private double parseString(String str){
        double res = 0;
        if (str.contains(",")) {
            try {
                NumberFormat nf_in = NumberFormat.getNumberInstance(Locale.GERMANY);
                res = nf_in.parse(str).doubleValue();
            }
            catch (ParseException pe){
                System.out.println("could not parse");
            }
        }
        else res = Double.parseDouble(str);
        return res;
    }
    private String formatDouble(double d){
        NumberFormat nf_out = NumberFormat.getNumberInstance(Locale.GERMANY);
        nf_out.setMaximumFractionDigits(2);
        nf_out.setMinimumFractionDigits(1);
        nf_out.setMinimumIntegerDigits(1);
        String output = nf_out.format(d);
        return output;
    }
}
