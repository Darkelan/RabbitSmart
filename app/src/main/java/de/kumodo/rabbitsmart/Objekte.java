package de.kumodo.rabbitsmart;

import android.widget.Toast;

/**
 * Created by l.schmidt on 07.03.2017.
 */

public class Objekte {

    private String name, an_datum, kosten, sn, anwender;
    private int number;
    private long id;
    private boolean checked;

    public Objekte(String name, int number, long id, boolean checked, String sn, String an_datum, String kosten, String anwender ) {
        this.name = name;
        this.number = number;
        this.an_datum = an_datum;
        this.kosten = kosten;
        this.sn = sn;
        this.anwender = anwender;
        this.id = id;
        this.checked = checked;
    }

    //Setters
    public void setAnwender(String anwender) {
        this.anwender = anwender;
    }

    public void setAn_datum(String an_datum) {
        this.an_datum = an_datum;
    }

    public void setKosten(String kosten) {
        this.kosten = kosten;
    }

    public void setSN(String sn) {
        this.sn = sn;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setChecked (boolean checked) {
        this.checked = checked;
    }

    //Getters
    public String getAn_datum() {
        return an_datum;
    }

    public String getName() {
        return name;
    }

    public String getKosten() {
        return kosten;
    }

    public String getSN() {
        return sn;
    }

    public String getAnwender() {
        return anwender;
    }

    public int getNumber() {
        return number;
    }

    public long getId() {
        return id;
    }

    public boolean isChecked() {
            return checked;
    }

    //Methode toString überschreiben
    @Override
    public String toString() {
       String output = number + " -> " + name + " -> " + sn;

       return output;
    }
}
