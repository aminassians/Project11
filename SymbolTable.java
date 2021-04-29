package nand2P11;

import java.util.HashMap;


public class SymbolTable {

    private HashMap<String, Symbol> classes;
    private HashMap<String, Symbol> methods;
    private HashMap<String, Integer> ints;

    public SymbolTable() {
        classes = new HashMap<>();
        methods = new HashMap<>();
        ints = new HashMap<>();
        
        ints.put("static", 0);
        ints.put("field", 0);
        ints.put("argument", 0);
        ints.put("var", 0);


    }

    public void startSubroutine() {
        methods.clear();
        ints.put("argument", 0);
        ints.put("var", 0);


    }

    public void define(String strName, String strType, String strKind) {
        int index = ints.get(strKind);
        Symbol symbol = new Symbol(strType, strKind, index);
        index++;
        ints.put(strKind, index);
       
        if (strKind.equals("argument") || strKind.equals("var")) {
            methods.put(strName, symbol);
        }
     
        else if (strKind.equals("static") || strKind.equals("field")) {
            classes.put(strName, symbol);
        }

    }


    public int varCount(String strKind) {
        return ints.get(strKind);
    }


    public String kindOf(String strName) {
        String kind;
        if (methods.containsKey(strName)) {
            kind = methods.get(strName).getKind();
        } else if (classes.containsKey(strName)) {
            kind = classes.get(strName).getKind();
        } else {
            kind = "none";
        }
        return kind;

    }


    public String typeOf(String strName) {
        String type;
  
        if (methods.containsKey(strName)) {
            type = methods.get(strName).getType();

        }
       
        else if (classes.containsKey(strName)) {
            type = classes.get(strName).getType();
        } else {
            type = "";
        }
        return type;

    }

  
    public int indexOf(String strName) {
        Symbol symbol = null;
        int index;
        if (methods.containsKey(strName)) {
            symbol = methods.get(strName);
        } else if (classes.containsKey(strName)) {
            symbol = classes.get(strName);
        }
     
        if (symbol == null) {
            index = -1;
        } else {
            index = symbol.getNumber();

        }
        return index;

    }
}
