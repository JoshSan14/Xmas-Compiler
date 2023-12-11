package org.compiler;
import java.nio.file.Files;
import java.nio.file.Paths;

public class App 
{
    public static void GenerarLexerParser() throws Exception{
        String basePath, fullPathLexer, fullPathParser, jlexer, jparser, jlexerDir;
        //MainJFlexCup mfjc;

        basePath = System.getProperty("user.dir");

        fullPathLexer = basePath+"\\src\\ParserLexer\\jflex\\lexer.flex";
        fullPathParser = basePath+"\\src\\ParserLexer\\cup\\parser.cup";
        jparser = "Parser.java";
        jlexer = "Lexer.java";
        jlexerDir = "ParserLexer";
        Files.deleteIfExists(Paths.get(basePath+"\\src\\ParserLexer\\"+jparser));
        Files.deleteIfExists(Paths.get(basePath+"\\src\\ParserLexer\\"+jlexer));

    }

    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
    }
}
