package nand2P11;

import java.io.File;
import java.util.ArrayList;

public class JackAnalyzer {

    public static void main(String[] args) {
        
        File jackFile = new File(args[0]);
        ArrayList<File> files = new ArrayList<>();
        if (jackFile.isFile() && args[0].endsWith(".jack")) 
        {
            files.add(jackFile);

        } else if (jackFile.isDirectory()) 
        {
            files = getJackFiles(jackFile);
        }

        for (File file : files) {
            String fileOutName = file.toString().substring(0, file.toString().length() - 5) + ".vm";
            File fileOutFile = new File(fileOutName);
            
            CompilationEngine compilationEngine = new CompilationEngine(file, fileOutFile);
            compilationEngine.compileClass();


        }


    }

    public static ArrayList<File> getJackFiles(File jackFile) 
    {
        File[] files = jackFile.listFiles();
        ArrayList<File> results = new ArrayList<>();
        if (files != null) for (File file : files) {
            if (file.getName().endsWith(".jack")) results.add(file);
        }
        return results;
    }

}
