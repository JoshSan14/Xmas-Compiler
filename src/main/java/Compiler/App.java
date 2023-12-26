package Compiler;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class App
{
    public static void GenerarLexerParser() throws Exception{
        String basePath, fullPathLexer, fullPathParser, jlexer, jparser, jsym, parserlexerdir;
        basePath = System.getProperty("user.dir");
        System.out.println(basePath);

        fullPathLexer = basePath + "\\src\\main\\jflex\\lexer.flex";
        fullPathParser = basePath + "\\src\\main\\cup\\parser.cup";

        jparser = "\\parser.java";
        jlexer = "\\lexer.java";
        jsym = "\\sym.java";
        parserlexerdir = "\\ParserLexer";
        Files.deleteIfExists(Paths.get(basePath + "\\src\\main\\java\\" + parserlexerdir + jparser));
        Files.deleteIfExists(Paths.get(basePath + "\\src\\main\\java\\" + parserlexerdir + jlexer));
        Files.deleteIfExists(Paths.get(basePath + "\\src\\main\\java\\" + parserlexerdir + jsym));

        //Crear analizador lexico y sintactico.
        MainJFlexCup mfjc = new MainJFlexCup();
        String[] strArrayParser = {fullPathParser};
        mfjc.InitLexerParser(fullPathLexer, strArrayParser);

        //Mover archivos.
        Files.move(Paths.get(basePath + "\\sym.java"), Paths.get(basePath + "\\src\\main\\java" + parserlexerdir + jsym));
        Files.move(Paths.get(basePath + jparser), Paths.get(basePath + "\\src\\main\\java" + parserlexerdir + jparser));
        Files.move(Paths.get(basePath + "\\src\\main\\jflex" + jlexer), Paths.get(basePath + "\\src\\main\\java" + parserlexerdir + jlexer));
    }

    public static void main( String[] args ) throws Exception {
        GenerarLexerParser();
        MainJFlexCup mfjc = new MainJFlexCup();
        mfjc.LexerTest(System.getProperty("user.dir")+"\\src\\main\\java\\ParserLexer\\pruebaparser.txt");
        mfjc.ParserTest1(System.getProperty("user.dir")+"\\src\\main\\java\\ParserLexer\\pruebaparser.txt");
    }
}
