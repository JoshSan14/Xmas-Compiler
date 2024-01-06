package Compiler;
import ParserLexer.lexer;
import ParserLexer.parser;
import Utils.Utils;
import java_cup.internal_error;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java_cup.runtime.Symbol;
import jflex.exceptions.SilentExit;

public class MainJFlexCup {

    /**
     * Inicializa el lexer y el parser generando los archivos correspondientes.
     *
     * @param lexerRoute Ruta para generar el archivo del lexer.
     * @param strArrParser Arreglo de argumentos para generar los archivos del parser.
     * @throws internal_error Si se produce un error interno en el proceso.
     * @throws Exception Si se produce un error general durante la inicialización.
     */
    public void InitLexerParser(String lexerRoute, String[] strArrParser) throws internal_error, Exception {
        Generate_Lexer(lexerRoute);
        Generate_Parser(strArrParser);
    }

    /**
     * Genera el archivo del lexer utilizando JFlex.
     *
     * @param route Ruta donde se generará el archivo del lexer.
     * @throws IOException Si se produce un error de lectura o escritura.
     * @throws SilentExit Si se produce un error silencioso en JFlex.
     */
    public void Generate_Lexer(String route) throws IOException, SilentExit{
        String[] strArr = {route};
        jflex.Main.generate(strArr);
    }

    /**
     * Genera los archivos del parser utilizando JavaCup.
     *
     * @param strArr Arreglo de argumentos para generar los archivos del parser.
     * @throws internal_error Si se produce un error interno en el proceso.
     * @throws IOException Si se produce un error de lectura o escritura.
     * @throws Exception Si se produce un error general durante la generación del parser.
     */
    public void Generate_Parser(String[] strArr) throws internal_error, IOException, Exception{
        java_cup.Main.main(strArr);
    }

    /**
     * Realiza una prueba del lexer utilizando un archivo de entrada.
     *
     * @param scannerRoute Ruta del archivo de entrada para la prueba del lexer.
     * @throws IOException Si se produce un error de lectura o escritura.
     */
    public void LexerTest(String scannerRoute) throws IOException {
        Reader reader = new BufferedReader(new FileReader(scannerRoute));
        reader.read();
        lexer lex = new lexer(reader);
        int totalLexemes = 0;
        int validLexemes = 0;
        int invalidLexemes = 0;
        Symbol token;
        while (true){
            token = lex.next_token();
            if(token.sym != 0) {
                totalLexemes++;
                System.out.println("Token: " + token.sym + ", Value: " + (token.value==null?lex.yytext():token.value.toString())
                        + ", Line: " + token.left + ", Column: " + token.right);
                if (token.sym==1){
                    invalidLexemes++;
                }else {
                    validLexemes++;
                }
            }
            else {
                System.out.println("Cantidad total de lexemas: " + totalLexemes);
                System.out.println("Cantidad de lexemas validos: " + validLexemes);
                System.out.println("Cantidad de lexemas invalidos: " + invalidLexemes);
                return;
            }
        }
    }

    /**
     * Realiza una prueba del parser utilizando un archivo de entrada.
     *
     * @param parseRoute Ruta del archivo de entrada para la prueba del parser.
     * @throws Exception Si se produce un error durante el análisis sintáctico.
     */
    public void ParserTest(String parseRoute) throws Exception {
        Reader inputLexer = new FileReader(parseRoute);
        lexer myLexer = new lexer(inputLexer);
        parser myParser = new parser(myLexer);
        System.out.println("\n" + Utils.ANSI_BRIGHT_BLUE + "ANÁLISIS SINTÁCTICO" + Utils.ANSI_RESET);
        myParser.parse();
    }


}
