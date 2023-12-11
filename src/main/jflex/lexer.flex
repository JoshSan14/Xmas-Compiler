package org.compiler;
import java_cup.runtime.*;

%%

%class lexer
%public
%unicode
%cup
%line
%column

%{
  StringBuffer string = new StringBuffer();

  private Symbol symbol(int type) {
      return new Symbol(type, yyline, yycolumn);
  }
  private Symbol symbol(int type, Object value) {
      return new Symbol(type, yyline, yycolumn, value);
  }
%}

// Regex
negative = (\-)?
// Expresiones regulares para comentarios
single_line_comment = \@[^\n]*\n
multi_line_comment = (\/\_)\.*(\_\/)
comment = {single_line_comment} | {multi_line_comment}
// Identificador:
identifier = [a-zA-Z_][a-zA-Z0-9_]*
// Literales:
l_char = \'[^\']\' // Char
l_int = {negative}(0|[1-9](\d*)) // Int
l_float = {negative}{l_int}(\.\d*)? // Float
l_boolean = true | false // Bool
l_string = \"

%state STRING

%%

/* keywords */
<YYINITIAL> {
    // Comentario
    {comment} { /* ignore */ }
    // Lexema Separador
    "," {return symbol(sym.PINO);}
    // Operadores Aritméticos Binarios
    "+" {return symbol(sym.DASHER);}
    "-" {return symbol(sym.DANCER);}
    "*" {return symbol(sym.PRANCER);}
    "/" {return symbol(sym.VIXEN);}
    "~" {return symbol(sym.COMET);}
    "**" {return symbol(sym.RUDOLPH);}
    // Operadores Aritméticos Unarios
    "++" {return symbol(sym.GRINCH);}
    "--" {return symbol(sym.QUIEN);}
    // Operadores Relacionales
    "==" {return symbol(sym.ALABASTER);}
    "!=" {return symbol(sym.BUSHY);}
    ">" {return symbol(sym.PEPPER);}
    "<" {return symbol(sym.SUGARPLUM);}
    "=>" {return symbol(sym.WUNORSE);}
    "=<" {return symbol(sym.JINGLE);}
    // Operadores Lógicos
    "^" {return symbol(sym.MELCHOR);}
    "#" {return symbol(sym.GASPAR);}
    "!" {return symbol(sym.BALTAZAR);}
    // Scope
    "static" {return symbol(sym.STATIC);}
    // Tipos de Datos
    "char" {return symbol(sym.SANTACLAUS);}
    "int" {return symbol(sym.FATHERCHRISTMAS);}
    "float" {return symbol(sym.KRISKRINGLE);}
    "bool" {return symbol(sym.SANNICOLAS);}
    "string" {return symbol(sym.DEDMOROZ);}
    // Literales
    {l_char} {return symbol(sym.L_SANTACLAUS);}
    {l_int} {return symbol(sym.L_FATHERCHRISTMAS);}
    {l_float} {return symbol(sym.L_KRISKRINGLE);}
    {l_boolean} {return symbol(sym.L_SANNICOLAS);}
    {l_string} {string.setLength(0); yybegin(STRING);}
    // Paréntesis
    "(" {return symbol(sym.ABRECUENTO);}
    ")" {return symbol(sym.CIERRACUENTO);}
    "[" {return symbol(sym.ABREEMPAQUE);}
    "]" {return symbol(sym.CIERRAEMPAQUE);}
    "{" {return symbol(sym.ABREREGALO);}
    "}" {return symbol(sym.CIERRAREGALO);}
    // Lexemas de Estructuras de Control:
    "if" {return symbol(sym.ELFO);}
    "elif" {return symbol(sym.HADA);}
    "else" {return symbol(sym.DUENDE);}
    "for" {return symbol(sym.ENVUELVE);}
    "do" {return symbol(sym.HACE);}
    "until" {return symbol(sym.REVISA);}
    "return" {return symbol(sym.ENVIA);}
    "break" {return symbol(sym.CORTA);}
    // Lexemas de Lectura/Escritura
    "print" {return symbol(sym.NARRA);}
    "read" {return symbol(sym.ESCUCHA);}
    // Lexema de Fin de Expresión
    "|" {return symbol(sym.FINREGALO);}
    // Lexema de Asignación
    "<=" {return symbol(sym.ENTREGA);}
    // Identificador
    {identifier} {return symbol(sym.PERSONA);}
}

<STRING> {
    \"               { yybegin(YYINITIAL); return symbol(sym.L_DEDMOROZ, string.toString()); }
    [^\n\r\"\\]+     { string.append( yytext() ); }
    \\t              { string.append('\t'); }
    \\n              { string.append('\n'); }
    \\r              { string.append('\r'); }
    \\\"             { string.append('\"'); }
    \\               { string.append('\\'); }
}

/* error fallback */
[^] { throw new Error("Illegal character <"+ yytext()+">"); }