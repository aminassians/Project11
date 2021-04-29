package nand2P11;

import java.io.File;


public class CompilationEngine {
	
    String strClassName = "";
    String strSubRoutineName = "";
    
    JackTokenizer toknizer;
    SymbolTable symboltable;
    VMWriter vmWriter;

    int index;

    public CompilationEngine(File fileIn, File fileOut) 
    {
        toknizer = new JackTokenizer(fileIn);
        symboltable = new SymbolTable();
        vmWriter = new VMWriter(fileOut);
        index = 0;
    }

    public void compileClass() {
        toknizer.advance();
        toknizer.advance();

        strClassName = toknizer.identifier();
        toknizer.advance();
        compileClassExtra();
        compileSubRoutine();

        vmWriter.close();

    }

    public void compileClassExtra() 
    {
        toknizer.advance();
        while (toknizer.keyWord().equals("static") || toknizer.keyWord().equals("field")) 
        {
            String strType;
            String kind;
            if (toknizer.keyWord().equals("static")) 
            {
                strType = "static";
            }
            
            else {
                strType = "field";
            }
            
            toknizer.advance();
            if (toknizer.tokenType().equals("IDENTIFIER")) 
            {
                kind = toknizer.identifier();
            }
            else 
            {
                kind = toknizer.keyWord();
            }
            toknizer.advance();
            symboltable.define(toknizer.identifier(), kind, strType);
            toknizer.advance();
            
            while (toknizer.symbol() == ',')
            {
                toknizer.advance();
                symboltable.define(toknizer.identifier(), kind, strType);
                toknizer.advance();
            }
            toknizer.advance();
        }

        if (toknizer.keyWord().equals("function") || toknizer.keyWord().equals("method") || toknizer.keyWord().equals("constructor")) {
            toknizer.decrementPointer();
            return;
        }


    }

    public void compileSubRoutine() {
        toknizer.advance();
        if (toknizer.symbol() == '}' && toknizer.tokenType().equals("SYMBOL")) {
            return;
        }
        String strKeyword = "";
        if (toknizer.keyWord().equals("function") || toknizer.keyWord().equals("method") || toknizer.keyWord().equals("constructor")) {
            strKeyword = toknizer.keyWord();
            symboltable.startSubroutine();
            if (toknizer.keyWord().equals("method")) {
                symboltable.define("this", strClassName, "argument");

            }
            toknizer.advance();
        }
        String strType;
        if (toknizer.tokenType().equals("KEYWORD") && toknizer.keyWord().equals("void")) {
            strType = "void";
            toknizer.advance();
        } else if (toknizer.tokenType().equals("KEYWORD") && (toknizer.keyWord().equals("int") || toknizer.keyWord().equals("boolean") || toknizer.keyWord().equals("char"))) {
            strType = toknizer.keyWord();
            toknizer.advance();
        }
        else {
            strType = toknizer.identifier();
            toknizer.advance();
        }
        if (toknizer.tokenType().equals("IDENTIFIER")) {
            strSubRoutineName = toknizer.identifier();
            toknizer.advance();
        }

        if (toknizer.symbol() == '(') {


            compileParameterList();


        }
        toknizer.advance();
        if (toknizer.symbol() == '{') {

            toknizer.advance();
        }
        while (toknizer.keyWord().equals("var") && (toknizer.tokenType().equals("KEYWORD"))) {
            toknizer.decrementPointer();
            compileVarDec();
        }
        String strFunction = "";
        if (strClassName.length() != 0 && strSubRoutineName.length() != 0) {
            strFunction += strClassName + "." + strSubRoutineName;
        }
        vmWriter.writeFunction(strFunction, symboltable.varCount("var"));
        if (strKeyword.equals("method")) {
            vmWriter.writePush("argument", 0);
            vmWriter.writePop("pointer", 0);

        } else if (strKeyword.equals("constructor")) {
            vmWriter.writePush("constant", symboltable.varCount("field"));
            vmWriter.writeCall("Memory.alloc", 1);
            vmWriter.writePop("pointer", 0);
        }

        compileStatements();


        compileSubRoutine();

    }


    public void compileParameterList() {
        toknizer.advance();
        String type = "";
        String name = "";
        
        boolean bHasParam = false;
        while (!(toknizer.tokenType().equals("SYMBOL") && toknizer.symbol() == ')')) {
            if (toknizer.tokenType().equals("KEYWORD")) {
                bHasParam = true;
                type = toknizer.keyWord();
            } else if (toknizer.tokenType().equals("IDENTIFIER")) {
                type = toknizer.identifier();
            }
            toknizer.advance();

            if (toknizer.tokenType().equals("IDENTIFIER")) {
                name = toknizer.identifier();
            }
            toknizer.advance();
            if (toknizer.tokenType().equals("SYMBOL") && toknizer.symbol() == ',') {
                symboltable.define(name, type, "argument");
                toknizer.advance();
            }


        }
        if (bHasParam) {
            symboltable.define(name, type, "argument");

        }


    }

    public void compileVarDec() {
        toknizer.advance();
        String type = "";
        String name = "";
        if (toknizer.keyWord().equals("var") && (toknizer.tokenType().equals("KEYWORD"))) {
            toknizer.advance();
        }
        if (toknizer.tokenType().equals("IDENTIFIER")) {
            type = toknizer.identifier();
            toknizer.advance();
        }
        else if (toknizer.tokenType().equals("KEYWORD")) {
            type = toknizer.keyWord();
            toknizer.advance();
        }
        if (toknizer.tokenType().equals("IDENTIFIER")) {
            name = toknizer.identifier();
            toknizer.advance();

        }
        symboltable.define(name, type, "var");

        while ((toknizer.tokenType().equals("SYMBOL")) && (toknizer.symbol() == ',')) {
            toknizer.advance();
            name = toknizer.identifier();
            symboltable.define(name, type, "var");

            toknizer.advance();
        }
        if ((toknizer.tokenType().equals("SYMBOL")) && (toknizer.symbol() == ';')) {
            toknizer.advance();

        }

    }

    public void compileStatements() {
        if (toknizer.symbol() == '}' && (toknizer.tokenType().equals("SYMBOL"))) {
            return;
        } else if (toknizer.keyWord().equals("do") && (toknizer.tokenType().equals("KEYWORD"))) {
            compileDo();

        } else if (toknizer.keyWord().equals("let") && (toknizer.tokenType().equals("KEYWORD"))) {
            compileLet();
        } else if (toknizer.keyWord().equals("if") && (toknizer.tokenType().equals("KEYWORD"))) {
            compileIf();
        } else if (toknizer.keyWord().equals("while") && (toknizer.tokenType().equals("KEYWORD"))) {
            compileWhile();
        } else if (toknizer.keyWord().equals("return") && (toknizer.tokenType().equals("KEYWORD"))) {
            compileReturn();
        }
        toknizer.advance();
        compileStatements();

    }

    public void compileDo() {
        if (toknizer.keyWord().equals("do")) {
        }
        compileCall();
        toknizer.advance();
        vmWriter.writePop("temp", 0);


    }

    private void compileCall() {
        toknizer.advance();
        String first = toknizer.identifier();
        int nArguments = 0;
        toknizer.advance();
        if ((toknizer.tokenType().equals("SYMBOL")) && (toknizer.symbol() == '.')) {
            String objectName = first;

            toknizer.advance();
            toknizer.advance();
            first = toknizer.identifier();
            String strType = symboltable.typeOf(objectName);
            if (strType.equals("")) {
                first = objectName + "." + first;
            } else {
                nArguments = 1;
                vmWriter.writePush(symboltable.kindOf(objectName), symboltable.indexOf(objectName));
                first = symboltable.typeOf(objectName) + "." + first;
            }

            nArguments += compileExpressionList();
            toknizer.advance();
            vmWriter.writeCall(first, nArguments);


        }
        else if ((toknizer.tokenType().equals("SYMBOL")) && (toknizer.symbol() == '(')) {
            vmWriter.writePush("pointer", 0);

            nArguments = compileExpressionList() + 1;
            toknizer.advance();
            vmWriter.writeCall(strClassName + "." + first, nArguments);


        }
    }

    public void compileLet() {

        toknizer.advance();
        String strVariableName = toknizer.identifier();
        toknizer.advance();
        boolean bArray = false;
        if ((toknizer.tokenType().equals("SYMBOL")) && (toknizer.symbol() == '[')) {
            bArray = true;
            vmWriter.writePush(symboltable.kindOf(strVariableName), symboltable.indexOf(strVariableName));
            compileExpression();
            toknizer.advance();
            if ((toknizer.tokenType().equals("SYMBOL")) && ((toknizer.symbol() == ']'))) {
            }
            vmWriter.writeArithmetic("add");
            toknizer.advance();

        }


        compileExpression();
        toknizer.advance();
        if (bArray) {
            vmWriter.writePop("temp", 0);
            vmWriter.writePop("pointer", 1);
            vmWriter.writePush("temp", 0);
            vmWriter.writePop("that", 0);
        } else {
            vmWriter.writePop(symboltable.kindOf(strVariableName), symboltable.indexOf(strVariableName));
        }
    }


    public void compileWhile() {
        String secondLabel = "LABEL_" + index++;
        String firstLabel = "LABEL_" + index++;
        vmWriter.writeLabel(firstLabel);
        toknizer.advance();
        
        compileExpression();
        
        toknizer.advance();
     
        vmWriter.writeArithmetic("not");
        vmWriter.writeIf(secondLabel);
        toknizer.advance();
     
        compileStatements();
       
        vmWriter.writeGoto(firstLabel);
      
        vmWriter.writeLabel(secondLabel);

    }


    public void compileReturn() {
        toknizer.advance();
        if (!((toknizer.tokenType().equals("SYMBOL") && toknizer.symbol() == ';'))) {
            toknizer.decrementPointer();
            compileExpression();
        } else if (toknizer.tokenType().equals("SYMBOL") && toknizer.symbol() == ';') {
            vmWriter.writePush("constant", 0);
        }
        vmWriter.writeReturn();


    }

    
    public void compileIf() {
        String strLabelElse = "LABEL_" + index++;
        String strLabelEnd = "LABEL_" + index++;
        toknizer.advance();
      
        compileExpression();
        toknizer.advance();
    
        vmWriter.writeArithmetic("not");
        vmWriter.writeIf(strLabelElse);
        toknizer.advance();

        compileStatements();
    
        vmWriter.writeGoto(strLabelEnd);
        vmWriter.writeLabel(strLabelElse);
        toknizer.advance();

        if (toknizer.tokenType().equals("KEYWORD") && toknizer.keyWord().equals("else")) {
            toknizer.advance();
            toknizer.advance();
       
            compileStatements();
        } else {
          
            toknizer.decrementPointer();
        }
        vmWriter.writeLabel(strLabelEnd);


    }


    public void compileExpression() {
        compileTerm();
        while (true) {
            toknizer.advance();
            
            if (toknizer.tokenType().equals("SYMBOL") && toknizer.isOperation()) {

                if (toknizer.symbol() == '<') {
                    compileTerm();
                    vmWriter.writeArithmetic("lt");
                } else if (toknizer.symbol() == '>') {
                    compileTerm();
                    vmWriter.writeArithmetic("gt");
                } else if (toknizer.symbol() == '&') {
                    compileTerm();
                    vmWriter.writeArithmetic("and");

                } else if (toknizer.symbol() == '+') {
                    compileTerm();
                    vmWriter.writeArithmetic("add");

                } else if (toknizer.symbol() == '-') {
                    compileTerm();
                    vmWriter.writeArithmetic("sub");

                } else if (toknizer.symbol() == '*') {
                    compileTerm();
                    vmWriter.writeCall("Math.multiply", 2);
                } else if (toknizer.symbol() == '/') {
                    compileTerm();
                    vmWriter.writeCall("Math.divide", 2);

                } else if (toknizer.symbol() == '=') {
                    compileTerm();
                    vmWriter.writeArithmetic("eq");


                } else if (toknizer.symbol() == '|') {
                    compileTerm();
                    vmWriter.writeArithmetic("or");

                }

            } else {
                toknizer.decrementPointer();
                break;
            }
        }


    }


    public void compileTerm() {
        toknizer.advance();
        if (toknizer.tokenType().equals("IDENTIFIER")) {
            String prevIdentifier = toknizer.identifier();
            toknizer.advance();
           
            if (toknizer.tokenType().equals("SYMBOL") && toknizer.symbol() == '[') {
           
                vmWriter.writePush(symboltable.kindOf(prevIdentifier), symboltable.indexOf(prevIdentifier));
                compileExpression();
                toknizer.advance();
                
                vmWriter.writeArithmetic("add");
                vmWriter.writePop("pointer", 1);
                vmWriter.writePush("that", 0);
            }
            else if (toknizer.tokenType().equals("SYMBOL") && (toknizer.symbol() == '(' || toknizer.symbol() == '.')) {
                toknizer.decrementPointer();
                toknizer.decrementPointer();
                compileCall();

            } else {
                toknizer.decrementPointer();
                vmWriter.writePush(symboltable.kindOf(prevIdentifier), symboltable.indexOf(prevIdentifier));
            }
        } else {
     
            if (toknizer.tokenType().equals("INT_CONST")) {
                vmWriter.writePush("constant", toknizer.intVal());

            }

            else if (toknizer.tokenType().equals("STRING_CONST")) {
                String strToken = toknizer.stringVal();
                vmWriter.writePush("constant", strToken.length());
                vmWriter.writeCall("String.new", 1);
                for (int i = 0; i < strToken.length(); i++) {
                    vmWriter.writePush("constant", (int) strToken.charAt(i));
                    vmWriter.writeCall("String.appendChar", 2);
                }
            }

            else if (toknizer.tokenType().equals("KEYWORD") && toknizer.keyWord().equals("this")) {
                vmWriter.writePush("pointer", 0);
            }
     
            else if (toknizer.tokenType().equals("KEYWORD") && (toknizer.keyWord().equals("null") || toknizer.keyWord().equals("false"))) {
                vmWriter.writePush("constant", 0);

            }
  
            else if (toknizer.tokenType().equals("KEYWORD") && toknizer.keyWord().equals("true")) {
                vmWriter.writePush("constant", 0);
                vmWriter.writeArithmetic("not");
            }

    
            else if (toknizer.tokenType().equals("SYMBOL") && toknizer.symbol() == '(') {
                compileExpression();
                toknizer.advance();
            }
   
            else if (toknizer.tokenType().equals("SYMBOL") && (toknizer.symbol() == '-' || toknizer.symbol() == '~')) {
                char symbol = toknizer.symbol();
                
                compileTerm();
                if (symbol == '-') {
                    vmWriter.writeArithmetic("neg");
                } else if (symbol == '~') {
                    vmWriter.writeArithmetic("not");
                }
            }
        }

    }

    public int compileExpressionList() {
        int nArguments = 0;
        toknizer.advance();
   
        if (toknizer.symbol() == ')' && toknizer.tokenType().equals("SYMBOL")) {
            toknizer.decrementPointer();
        } else {
            nArguments = 1;
            toknizer.decrementPointer();
            compileExpression();
        }
        while (true) {
            toknizer.advance();
            if (toknizer.tokenType().equals("SYMBOL") && toknizer.symbol() == ',') {
                compileExpression();
                nArguments++;
            } else {
                toknizer.decrementPointer();
                break;
            }
        }
        return nArguments;

    }
}
