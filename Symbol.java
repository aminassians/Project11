package nand2P11;

public class Symbol {

    private String type;
    private String kind;
    private int number;

    public Symbol(String strType, String strKind, int num) {
        type = strType;
        kind = strKind;
        number = num;


    }

    public String getType() {
        return type;
    }


    public String getKind() {
        return kind;
    }


    public int getNumber() {
        return number;
    }


}
