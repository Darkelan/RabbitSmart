package de.kumodo.rabbitsmart;

import android.widget.Toast;

/**
 * Created by l.schmidt on 07.03.2017.
 */

public class Objekte {

    private String name;
    private int number;
    private long id;
    private boolean checked;
    private String an_datum;
    private float kosten;
    private String serial_number;
    private String anwender;


    public Objekte(String name, int number, long id, boolean checked) {
        this.name = name;
        this.number = number;
        this.an_datum = an_datum;
        this.kosten = kosten;
        this.serial_number = serial_number;
        this.anwender = anwender;
        this.id = id;
        this.checked = checked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAn_datum() {
        return an_datum;
    }

    public void setAn_datum(String an_datum) {
        this.an_datum = an_datum;
    }

    public float getKosten() {
        return kosten;
    }

    public void setKosten(float kosten) {
        this.kosten = kosten;
    }

    public String getSerial_number() {
        return serial_number;
    }

    public void setSerial_number(String serial_number) {
        this.serial_number = serial_number;
    }

    public String getAnwender() {
        return anwender;
    }

    public void setAnwender(String anwender) {
        this.anwender = anwender;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isChecked() {
            return checked;
    }

    public void setChecked (boolean checked) {
        this.checked = checked;
    }

    @Override
    public String toString() {
        String output = number + " -> " + name + " -> " + serial_number;

        return output;
    }
}
