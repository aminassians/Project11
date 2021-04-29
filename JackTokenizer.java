package nand2P11;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;


public class JackTokenizer {
    
    static ArrayList<String> keyWords;
    static String symbols;
    static String operations;
    ArrayList<String> tokens;
    String jackcode;
    String tokenType;
    String keyWordType;
    char symbolType;   
    String stringValue;
    int intValue;
    String identifier;
    static ArrayList<String> libraries;
    
    int pointer;
    boolean bool;

    Scanner scnr;

    public JackTokenizer(File file) {
        try {
            scnr = new Scanner(new FileReader(file));
        } catch (FileNotFoundException exception) {
            exception.printStackTrace();
        }
        jackcode = "";
        while (scnr.hasNextLine()) {
            String strLine = scnr.nextLine();
            while (strLine.equals("") || hasComments(strLine)) {
                if (hasComments(strLine)) {
                    strLine = removeComments(strLine);
                }
                if (strLine.trim().equals("")) {
                    if (scnr.hasNextLine()) {
                        strLine = scnr.nextLine();
                    } else {
                        break;
                    }
                }
            }
            jackcode += strLine.trim();
        }
        tokens = new ArrayList<String>();
        while (jackcode.length() > 0) {
            while (jackcode.charAt(0) == ' ') {
                jackcode = jackcode.substring(1);
            }
            for (int i = 0; i < keyWords.size(); i++) {
                if (jackcode.startsWith(keyWords.get(i).toString() + " ")) {
                    String keyword = keyWords.get(i).toString();
                    tokens.add(keyword);
                    jackcode = jackcode.substring(keyword.length());
                }

            }
            if (symbols.contains(jackcode.substring(0, 1))) {
                char symbol = jackcode.charAt(0);
                tokens.add(Character.toString(symbol));
                jackcode = jackcode.substring(1);
            }
            else if (Character.isDigit(jackcode.charAt(0))) {
                String value = jackcode.substring(0, 1);
                jackcode = jackcode.substring(1);
                while (Character.isDigit(jackcode.charAt(0))) {
                    value += jackcode.substring(0, 1);
                    jackcode = jackcode.substring(1);

                }
                tokens.add(value);

            }
            else if (jackcode.substring(0, 1).equals("\"")) {
                jackcode = jackcode.substring(1);
                String strString = "\"";
                while ((jackcode.charAt(0) != '\"')) {
                    strString += jackcode.charAt(0);
                    jackcode = jackcode.substring(1);

                }
                strString = strString + "\"";
                tokens.add(strString);
                jackcode = jackcode.substring(1);

            }
            else if (Character.isLetter(jackcode.charAt(0)) || (jackcode.substring(0, 1).equals("_"))) {
                String strIdentifier = jackcode.substring(0, 1);
                jackcode = jackcode.substring(1);
                while ((Character.isLetter(jackcode.charAt(0))) || (jackcode.substring(0, 1).equals("_"))) {
                    strIdentifier += jackcode.substring(0, 1);
                    jackcode = jackcode.substring(1);
                }

                tokens.add(strIdentifier);

            }
            bool = true;
            pointer = 0;


        }
    }



    static {
        keyWords = new ArrayList<String>();
        keyWords.add("class");
        keyWords.add("constructor");
        keyWords.add("function");
        keyWords.add("method");
        keyWords.add("field");
        keyWords.add("static");
        keyWords.add("var");
        keyWords.add("int");
        keyWords.add("char");
        keyWords.add("boolean");
        keyWords.add("void");
        keyWords.add("true");
        keyWords.add("false");
        keyWords.add("null");
        keyWords.add("this");
        keyWords.add("do");
        keyWords.add("if");
        keyWords.add("else");
        keyWords.add("while");
        keyWords.add("return");
        keyWords.add("let");
        operations = "+-*/&|<>=";
        symbols = "{}()[].,;+-*/&|<>=-~";
        libraries = new ArrayList<String>();
        libraries.add("Array");
        libraries.add("Math");
        libraries.add("String");
        libraries.add("Array");
        libraries.add("Output");
        libraries.add("Screen");
        libraries.add("Keyboard");
        libraries.add("Memory");
        libraries.add("Sys");
        libraries.add("Square");
        libraries.add("SquareGame");


    }

    public boolean hasMoreTokens() {
        boolean hasMore = false;
        if (pointer < tokens.size() - 1) {
            hasMore = true;
        }
        return hasMore;

    }

    public void advance() {
        if (hasMoreTokens()) {
            if (!bool) {
                pointer++;
            }
            else if (bool) {
                bool = false;
            }
            String currentItem = tokens.get(pointer);
            if (keyWords.contains(currentItem)) {
                tokenType = "KEYWORD";
                keyWordType = currentItem;
            } else if (symbols.contains(currentItem)) {
                symbolType = currentItem.charAt(0);
                tokenType = "SYMBOL";
            } else if (Character.isDigit(currentItem.charAt(0))) {
                intValue = Integer.parseInt(currentItem);
                tokenType = "INT_CONST";
            } else if (currentItem.substring(0, 1).equals("\"")) {
                tokenType = "STRING_CONST";
                stringValue = currentItem.substring(1, currentItem.length() - 1);
            } else if ((Character.isLetter(currentItem.charAt(0))) || (currentItem.charAt(0) == '_')) {
                tokenType = "IDENTIFIER";
                identifier = currentItem;
            }
        } else {
            return;
        }


    }

    public void decrementPointer() {
        if (pointer > 0) {
            pointer--;
        }

    }


    private boolean hasComments(String strLine) {
        boolean bHasComments = false;
        if (strLine.contains("//") || strLine.contains("/*") || strLine.trim().startsWith("*")) {
            bHasComments = true;
        }
        return bHasComments;

    }

    private String removeComments(String strLine) {
        String strNoComments = strLine;
        if (hasComments(strLine)) {
            int offSet;
            if (strLine.trim().startsWith("*")) {
                offSet = strLine.indexOf("*");
            } else if (strLine.contains("/*")) {
                offSet = strLine.indexOf("/*");
            } else {
                offSet = strLine.indexOf("//");
            }
            strNoComments = strLine.substring(0, offSet).trim();

        }
        return strNoComments;
    }

    public String tokenType() {
        return tokenType;

    }

    public String keyWord() {
        return keyWordType;
    }

    public char symbol() {
        return symbolType;
    }

    public String identifier() {
        return identifier;
    }

    public int intVal() {
        return intValue;
    }

    public String stringVal() {
        return stringValue;
    }

    public boolean isOperation() {
        for (int i = 0; i < operations.length(); i++) {
            if (operations.charAt(i) == symbolType) {
                return true;
            }
        }
        return false;
    }


}
