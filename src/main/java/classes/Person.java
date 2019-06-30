package classes;
/*
Класс содержащий дату и год рождения пациента
 */
public class Person {
    private String name;
    private String year;
    public Person(String name, String year){
        this.name = name;
        this.year = year;
    }
    public String getName(){
        return name;
    }
    public String getYear(){
        return year;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) return false;
        if(obj instanceof Person){
            return ((Person)obj).getName().equals(name);
        }
        else return false;
    }
}
