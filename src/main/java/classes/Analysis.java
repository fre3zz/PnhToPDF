package classes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Analysis {
    private Person person;
    private String department;
    private Result result;
    private LocalDate date;
    private String dateString;

    public Analysis(Person person, String department, Result result, LocalDate date){
        this.person = person;
        this.department = department;
        this.result = result;
        this.date = date;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        dateString = dtf.format(date);
    }

    public String getDateString() {
        return dateString;
    }

    public Person getPerson() {
        return person;
    }

    public String getDepartment() {
        return department;
    }

    public Result getResult() {
        return result;
    }

    public LocalDate getDate() {
        return date;
    }
}
