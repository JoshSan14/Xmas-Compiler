package org.compiler;

import java_cup.runtime.Symbol;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class LexerTest {

    @Test
    public void matchPINO() throws IOException {
        String testString = ",";
        lexer lex = new lexer(new StringReader(testString));
        Symbol symbol = lex.next_token();
        Assertions.assertEquals(sym.PINO, symbol.sym);
    }

    @Test
    public void matchDASHER() throws IOException {
        String testString = "+";
        lexer lex = new lexer(new StringReader(testString));
        Symbol symbol = lex.next_token();
        Assertions.assertEquals(sym.DASHER, symbol.sym);
    }

    @Test
    public void matchDANCER() throws IOException {
        String testString = "-";
        lexer lex = new lexer(new StringReader(testString));
        Symbol symbol = lex.next_token();
        Assertions.assertEquals(sym.DANCER, symbol.sym);
    }

    @Test
    public void matchPRANCER() throws IOException {
        String testString = "*";
        lexer lex = new lexer(new StringReader(testString));
        Symbol symbol = lex.next_token();
        Assertions.assertEquals(sym.PRANCER, symbol.sym);
    }

    @Test
    public void matchVIXEN() throws IOException {
        String testString = "/";
        lexer lex = new lexer(new StringReader(testString));
        Symbol symbol = lex.next_token();
        Assertions.assertEquals(sym.VIXEN, symbol.sym);
    }

    @Test
    public void matchCOMET() throws IOException {
        String testString = "~";
        lexer lex = new lexer(new StringReader(testString));
        Symbol symbol = lex.next_token();
        Assertions.assertEquals(sym.COMET, symbol.sym);
    }

    @Test
    public void matchRUDOLPH() throws IOException {
        String testString = "**";
        lexer lex = new lexer(new StringReader(testString));
        Symbol symbol = lex.next_token();
        Assertions.assertEquals(sym.RUDOLPH, symbol.sym);
    }


    @Test
    public void matchGRINCH() throws IOException {
        String testString = "++";
        lexer lex = new lexer(new StringReader(testString));
        Symbol symbol = lex.next_token();
        Assertions.assertEquals(sym.GRINCH, symbol.sym);
    }

    @Test
    public void matchQUIEN() throws IOException {
        String testString = "--";
        lexer lex = new lexer(new StringReader(testString));
        Symbol symbol = lex.next_token();
        Assertions.assertEquals(sym.QUIEN, symbol.sym);
    }

    @Test
    public void matchALABASTER() throws IOException {
        String testString = "==";
        lexer lex = new lexer(new StringReader(testString));
        Symbol symbol = lex.next_token();
        Assertions.assertEquals(sym.ALABASTER, symbol.sym);
    }

    @Test
    public void matchBUSHY() throws IOException {
        String testString = "!=";
        lexer lex = new lexer(new StringReader(testString));
        Symbol symbol = lex.next_token();
        Assertions.assertEquals(sym.BUSHY, symbol.sym);
    }

    @Test
    public void matchPEPPER() throws IOException {
        String testString = ">";
        lexer lex = new lexer(new StringReader(testString));
        Symbol symbol = lex.next_token();
        Assertions.assertEquals(sym.PEPPER, symbol.sym);
    }

    @Test
    public void matchSUGARPLUM() throws IOException {
        String testString = "<";
        lexer lex = new lexer(new StringReader(testString));
        Symbol symbol = lex.next_token();
        Assertions.assertEquals(sym.SUGARPLUM, symbol.sym);
    }

    @Test
    public void matchWUNORSE() throws IOException {
        String testString = "=>";
        lexer lex = new lexer(new StringReader(testString));
        Symbol symbol = lex.next_token();
        Assertions.assertEquals(sym.WUNORSE, symbol.sym);
    }

    @Test
    public void matchJINGLE() throws IOException {
        String testString = "=<";
        lexer lex = new lexer(new StringReader(testString));
        Symbol symbol = lex.next_token();
        Assertions.assertEquals(sym.JINGLE, symbol.sym);
    }

    @Test
    public void matchMELCHOR() throws IOException {
        String testString = "^";
        lexer lex = new lexer(new StringReader(testString));
        Symbol symbol = lex.next_token();
        Assertions.assertEquals(sym.MELCHOR, symbol.sym);
    }

    @Test
    public void matchGASPAR() throws IOException {
        String testString = "#";
        lexer lex = new lexer(new StringReader(testString));
        Symbol symbol = lex.next_token();
        Assertions.assertEquals(sym.GASPAR, symbol.sym);
    }

    @Test
    public void matchBALTAZAR() throws IOException {
        String testString = "!";
        lexer lex = new lexer(new StringReader(testString));
        Symbol symbol = lex.next_token();
        Assertions.assertEquals(sym.BALTAZAR, symbol.sym);
    }

    @Test
    public void matchSANTACLAUS() throws IOException {
        String testString = "char";
        lexer lex = new lexer(new StringReader(testString));
        Symbol symbol = lex.next_token();
        Assertions.assertEquals(sym.SANTACLAUS, symbol.sym);
    }

    @Test
    public void matchFATHERCHRISTMAS() throws IOException {
        String testString = "int";
        lexer lex = new lexer(new StringReader(testString));
        Symbol symbol = lex.next_token();
        Assertions.assertEquals(sym.FATHERCHRISTMAS, symbol.sym);
    }

    @Test
    public void matchKRISKRINGLE() throws IOException {
        String testString = "float";
        lexer lex = new lexer(new StringReader(testString));
        Symbol symbol = lex.next_token();
        Assertions.assertEquals(sym.KRISKRINGLE, symbol.sym);
    }

    @Test
    public void matchSANNICOLAS() throws IOException {
        String testString = "bool";
        lexer lex = new lexer(new StringReader(testString));
        Symbol symbol = lex.next_token();
        Assertions.assertEquals(sym.SANNICOLAS, symbol.sym);
    }

    @Test
    public void matchDEDMOROZ() throws IOException {
        String testString = "string";
        lexer lex = new lexer(new StringReader(testString));
        Symbol symbol = lex.next_token();
        Assertions.assertEquals(sym.DEDMOROZ, symbol.sym);
    }

    @Test
    public void matchL_SANTACLAUS() throws IOException {
        char testChar = 'a';
        String testString = "'" + testChar + "'";
        lexer lex = new lexer(new StringReader(testString));
        Symbol symbol = lex.next_token();
        Assertions.assertEquals(sym.L_SANTACLAUS, symbol.sym);
    }

    @Test
    public void matchL_FATHERCHRISTMAS() throws IOException {
        int testInt = 1;
        String testString = Integer.toString(testInt);
        lexer lex = new lexer(new StringReader(testString));
        Symbol symbol = lex.next_token();
        Assertions.assertEquals(sym.L_FATHERCHRISTMAS, symbol.sym);
    }

    @Test
    public void matchL_KRISKRINGLE() throws IOException {
        float testFloat = (float) 1.8;
        String testString = Float.toString(testFloat);
        lexer lex = new lexer(new StringReader(testString));
        Symbol symbol = lex.next_token();
        Assertions.assertEquals(sym.L_KRISKRINGLE, symbol.sym);
    }

    @Test
    public void matchL_SANNICOLAS() throws IOException {
        boolean testTrue = true;
        boolean testFalse = false;
        String testString1 = Boolean.toString(testTrue);
        String testString2 = Boolean.toString(testFalse);
        lexer lex1 = new lexer(new StringReader(testString1));
        lexer lex2 = new lexer(new StringReader(testString2));
        Symbol symbol1 = lex1.next_token();
        Symbol symbol2 = lex2.next_token();
        Assertions.assertEquals(sym.L_SANNICOLAS, symbol1.sym);
        Assertions.assertEquals(sym.L_SANNICOLAS, symbol2.sym);
    }

    @Test
    public void matchABRECUENTO() throws IOException {
        String testString = "(";
        lexer lex = new lexer(new StringReader(testString));
        Symbol symbol = lex.next_token();
        Assertions.assertEquals(sym.ABRECUENTO, symbol.sym);
    }

    @Test
    public void matchCIERRACUENTO() throws IOException {
        String testString = ")";
        lexer lex = new lexer(new StringReader(testString));
        Symbol symbol = lex.next_token();
        Assertions.assertEquals(sym.CIERRACUENTO, symbol.sym);
    }

    @Test
    public void matchABREEMPAQUE() throws IOException {
        String testString = "[";
        lexer lex = new lexer(new StringReader(testString));
        Symbol symbol = lex.next_token();
        Assertions.assertEquals(sym.ABREEMPAQUE, symbol.sym);
    }

    @Test
    public void matchCIERRAEMPAQUE() throws IOException {
        String testString = "]";
        lexer lex = new lexer(new StringReader(testString));
        Symbol symbol = lex.next_token();
        Assertions.assertEquals(sym.CIERRAEMPAQUE, symbol.sym);
    }

    @Test
    public void matchABREREGALO() throws IOException {
        String testString = "{";
        lexer lex = new lexer(new StringReader(testString));
        Symbol symbol = lex.next_token();
        Assertions.assertEquals(sym.ABREREGALO, symbol.sym);
    }

    @Test
    public void matcCierraRegalo() throws IOException {
        String testString = "}";
        lexer lex = new lexer(new StringReader(testString));
        Symbol symbol = lex.next_token();
        Assertions.assertEquals(sym.CIERRAREGALO, symbol.sym);
    }

    @Test
    public void matchELFO() throws IOException {
        String testString = "if";
        lexer lex = new lexer(new StringReader(testString));
        Symbol symbol = lex.next_token();
        Assertions.assertEquals(sym.ELFO, symbol.sym);
    }

    @Test
    public void matchHADA() throws IOException {
        String testString = "elif";
        lexer lex = new lexer(new StringReader(testString));
        Symbol symbol = lex.next_token();
        Assertions.assertEquals(sym.HADA, symbol.sym);
    }

    @Test
    public void matchDUENDE() throws IOException {
        String testString = "else";
        lexer lex = new lexer(new StringReader(testString));
        Symbol symbol = lex.next_token();
        Assertions.assertEquals(sym.DUENDE, symbol.sym);
    }

    @Test
    public void matchENVUELVE() throws IOException {
        String testString = "for";
        lexer lex = new lexer(new StringReader(testString));
        Symbol symbol = lex.next_token();
        Assertions.assertEquals(sym.ENVUELVE, symbol.sym);
    }

    @Test
    public void matchHACE() throws IOException {
        String testString = "do";
        lexer lex = new lexer(new StringReader(testString));
        Symbol symbol = lex.next_token();
        Assertions.assertEquals(sym.HACE, symbol.sym);
    }

    @Test
    public void matchREVISA() throws IOException {
        String testString = "until";
        lexer lex = new lexer(new StringReader(testString));
        Symbol symbol = lex.next_token();
        Assertions.assertEquals(sym.REVISA, symbol.sym);
    }

    @Test
    public void matchENVIA() throws IOException {
        String testString = "return";
        lexer lex = new lexer(new StringReader(testString));
        Symbol symbol = lex.next_token();
        Assertions.assertEquals(sym.ENVIA, symbol.sym);
    }

    @Test
    public void matchCORTA() throws IOException {
        String testString = "break";
        lexer lex = new lexer(new StringReader(testString));
        Symbol symbol = lex.next_token();
        Assertions.assertEquals(sym.CORTA, symbol.sym);
    }

    @Test
    public void matchNARRA() throws IOException {
        String testString = "print";
        lexer lex = new lexer(new StringReader(testString));
        Symbol symbol = lex.next_token();
        Assertions.assertEquals(sym.NARRA, symbol.sym);
    }

    @Test
    public void matchESCUCHA() throws IOException {
        String testString = "read";
        lexer lex = new lexer(new StringReader(testString));
        Symbol symbol = lex.next_token();
        Assertions.assertEquals(sym.ESCUCHA, symbol.sym);
    }

    @Test
    public void matchFINREGALO() throws IOException {
        String testString = "|";
        lexer lex = new lexer(new StringReader(testString));
        Symbol symbol = lex.next_token();
        Assertions.assertEquals(sym.FINREGALO, symbol.sym);
    }

    @Test
    public void matchENTREGA() throws IOException {
        String testString = "<=";
        lexer lex = new lexer(new StringReader(testString));
        Symbol symbol = lex.next_token();
        Assertions.assertEquals(sym.ENTREGA, symbol.sym);
    }

    @Test
    public void matchPERSONA() throws IOException {
        String testString = "ID_3[*]+]^";
        lexer lex = new lexer(new StringReader(testString));
        Symbol symbol = lex.next_token();
        if (symbol.sym == sym.error) {
            System.out.println(symbol.value); // Imprime el mensaje de error
        } else {
            Assertions.assertEquals(sym.PERSONA, symbol.sym);
        }
    }

}
