package de.kumodo.rabbitsmart;

/**
 * Created by l.schmidt on 07.03.2017.
 */

public class Objekte {

    private String name;
    private int number;
    private long id;
    private boolean checked;

    public Objekte(String name, int number, long id, boolean checked) {
        this.name = name;
        this.number = number;
        this.id = id;
        this.checked = checked;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        String output = number + " -> " + name;

        return output;
    }
}
