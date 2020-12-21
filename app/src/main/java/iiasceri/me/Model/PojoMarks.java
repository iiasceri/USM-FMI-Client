package iiasceri.me.Model;

public class PojoMarks {

    private final String denumire;
    private final String nota;

    public PojoMarks(String denumire, String nota) {
        this.denumire = denumire;
        this.nota = nota;
    }

    public String getDenumire() {
        return denumire;
    }

    public String getNota() {
        return nota;
    }
}
