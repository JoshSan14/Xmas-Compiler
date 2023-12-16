package org.compiler;

import java_cup.Lexer;
import java_cup.internal_error;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;


import java_cup.runtime.Symbol;
import jflex.exceptions.SilentExit;

//import ParserLexer.*;

public class MainJFlexCup {

    public void InitLexerParser(String lexerRoute, String[] strArrParser) throws internal_error, Exception {
        Generate_Lexer(lexerRoute);
        Generate_Parser(strArrParser);
    }

    // Genera el archivo del lexer
    public void Generate_Lexer(String route) throws IOException, SilentExit{
        String[] strArr = {route};
        jflex.Main.generate(strArr);
    }

    // Genera los archivos del parser
    public void Generate_Parser(String[] strArr) throws internal_error, IOException, Exception{
        java_cup.Main.main(strArr);
    }


    public void LexerTest(String scannerRoute) throws IOException {
        Reader reader = new BufferedReader(new FileReader(scannerRoute));
        reader.read();
        Lexer lex = new Lexer(reader);
        int i = 0;
        Symbol token;
        while (true){
            token = lex.next_token();
            if(token.sym != 0) {
                System.out.println("Token: " + token.sym + ", Value: " + (token.value==null?lex.yytext():token.value.toString())
                        + ", Line: " + token.left + ", Column: " + token.right);
            }
            else {
                System.out.println("Quantity of found lexemes: " + i);
                return;
            }
            i++;
        }

    }



}
