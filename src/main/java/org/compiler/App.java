package org.compiler;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class App
{
    public static void GenerarLexerParser() throws Exception{
        String basePath, fullPathLexer, fullPathParser, jlexer, jparser, jlexerDir;
        MainJFlexCup mfjc = new MainJFlexCup();

        basePath = System.getProperty("user.dir");

        fullPathLexer = basePath+"\\src\\main\\jflex\\lexer.flex";
        fullPathParser = basePath+"\\src\\main\\cup\\parser.cup";
        jparser = "parser.java";
        jlexer = "lexer.java";
        //jlexerDir = "ParserLexer";

        Path pathParser = Paths.get(basePath + "\\src\\main\\java\\org\\compiler\\" + jparser);
        Path pathLexer = Paths.get(basePath + "\\src\\main\\java\\org\\compiler\\" + jlexer);

        Files.deleteIfExists(pathParser);
        Files.deleteIfExists(pathLexer);

        String[] strArrayParser = {fullPathParser};
        mfjc.InitLexerParser(fullPathLexer, strArrayParser);

        //mover archivos
        Files.move(Paths.get(basePath+"\\sym.java"), Paths.get(basePath+"\\src\\main\\java\\org\\compiler\\sym.java"));
        Files.move(Paths.get(basePath+"\\"+jparser), pathParser);
        Files.move(Paths.get(basePath + "\\src\\main\\jflex\\" + jlexer), pathLexer);

    }

    public static void main( String[] args ) throws Exception {
        //GenerarLexerParser();
        MainJFlexCup mjfc = new MainJFlexCup();
        mjfc.LexerTest(System.getProperty("user.dir")+ "\\src\\main\\java\\ParserLexer\\prueba.txt");
    }
}
