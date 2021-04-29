package nand2P11;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;



public class VMWriter {
    private FileWriter fileWriter;

    public VMWriter(File fileOut) {
        try {
            fileWriter = new FileWriter(fileOut);
        } catch (IOException exception) {
            exception.printStackTrace();
        }

    }

    public void writePush(String str, int index) {
        if (str.equals("var")) {
            str = "local";
        }
        if (str.equals("field")) {
            str = "this";
        }
        try {
            fileWriter.write("push " + str + " " + index + "\n");
        } catch (IOException exception) {
            exception.printStackTrace();
        }


    }

    public void writePop(String str, int index) {
        if (str.equals("var")) {
            str = "local";
        }
        if (str.equals("field")) {
            str = "this";
        }
        try {
            fileWriter.write("pop " + str + " " + index + "\n");
        } catch (IOException exception) {
            exception.printStackTrace();
        }

    }

    public void writeArithmetic(String command) {
        try {
            fileWriter.write(command + "\n");
        } catch (IOException exception) {
            exception.printStackTrace();
        }

    }

    public void writeLabel(String label) {
        try {
            fileWriter.write("label " + label + "\n");
        } catch (IOException exception) {
            exception.printStackTrace();
        }

    }

    public void writeGoto(String label) {
        try {
            fileWriter.write("goto " + label + "\n");
        } catch (IOException exception) {
            exception.printStackTrace();
        }

    }

    public void writeIf(String label) {
        try {
            fileWriter.write("if-goto " + label + "\n");
        } catch (IOException exception) {
            exception.printStackTrace();
        }

    }

    public void writeCall(String name, int num) {
        try {
            fileWriter.write("call " + name + " " + num + "\n");
        } catch (IOException exception) {
            exception.printStackTrace();
        }

    }

    public void writeFunction(String name, int num) {
        try {
            fileWriter.write("function " + name + " " + num + "\n");
        } catch (IOException exception) {
            exception.printStackTrace();
        }

    }

    public void writeReturn() {
        try {
            fileWriter.write("return\n");
        } catch (IOException exception) {
            exception.printStackTrace();
        }

    }

    public void close() {
        try {
            fileWriter.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }

    }
}
