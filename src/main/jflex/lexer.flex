package ParserLexer;
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
single_line_comment = \@[^\n]*
multi_line_comment = \/\_[^]*\_\/
comment = {single_line_comment} | {multi_line_comment}
// Expresiones regulares para espacio en blanco
LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]
WhiteSpace = {LineTerminator} | [ \t\f]
// Expresion regular que obtiene los caracteres especiales
caracteresEspeciales = \+|\-|\/|\*|\||\°|\¬|\!|\#|\$|\%|\&|\(|\)|\=|\?|\¡|\¿|\´|\¨|\~|\{|\}|\[|\]|\^|\`|\;|\:|\<|\>|\.|\,+
// Identificador:
identifier = [a-zA-Z_][a-zA-Z0-9_]*
// Identificador no permitido (para manejo de errores):
secuenciaInvalida = [0-9]+{identifier} | {caracteresEspeciales}+[0-9]*{identifier}
// Entero pegado a letras (para manejo de errores)
EnteroConLetras = [0-9]+[a-zA-Z]+
// Literales:
l_char = \'[^\']\' // Char
l_int = {negative}(0|[1-9](\d*)) // Int
l_float = {negative}{l_int}(\.\d*)? // Float
l_boolean = true | false // Bool
l_string = \"

%state STRING
%state COMMENT

%%

/* keywords */
<YYINITIAL> {
    // Comentario
    {comment} { /* ignore */ }
    "\/\_" { yybegin(COMMENT); }
    // Espacio en blanco
    {WhiteSpace} { /* ignore */ }
    // Tipos de Datos
    "char" {return symbol(sym.SANTACLAUS, yytext());}
    "int" {return symbol(sym.FATHERCHRISTMAS, yytext());}
    "float" {return symbol(sym.KRISKRINGLE, yytext());}
    "bool" {return symbol(sym.SANNICOLAS, yytext());}
    "string" {return symbol(sym.DEDMOROZ, yytext());}
    // Literales
    {l_char} {return symbol(sym.L_SANTACLAUS, yytext());}
    {l_int} {return symbol(sym.L_FATHERCHRISTMAS, yytext());}
    {l_float} {return symbol(sym.L_KRISKRINGLE, yytext());}
    {l_boolean} {return symbol(sym.L_SANNICOLAS, yytext());}
    {l_string} {string.setLength(0); yybegin(STRING);}
    "(" {return symbol(sym.ABRECUENTO);}
    ")" {return symbol(sym.CIERRACUENTO);}
    "[" {return symbol(sym.ABREEMPAQUE);}
    "]" {return symbol(sym.CIERRAEMPAQUE);}
    "{" {return symbol(sym.ABREREGALO);}
    "}" {return symbol(sym.CIERRAREGALO);}
    // Lexemas de Estructuras de Control:
    "if" {return symbol(sym.ELFO, yytext());}
    "elif" {return symbol(sym.HADA, yytext());}
    "else" {return symbol(sym.DUENDE, yytext());}
    "for" {return symbol(sym.ENVUELVE, yytext());}
    "do" {return symbol(sym.HACE, yytext());}
    "until" {return symbol(sym.REVISA, yytext());}
    "return" {return symbol(sym.ENVIA, yytext());}
    "break" {return symbol(sym.CORTA, yytext());}
    // Lexemas de Lectura/Escritura
    "print" {return symbol(sym.NARRA, yytext());}
    "read" {return symbol(sym.ESCUCHA, yytext());}
    // Lexema de Fin de Expresión
    "|" {return symbol(sym.FINREGALO);}
    // Lexema de Asignación
    "<=" {return symbol(sym.ENTREGA, yytext());}
    // Local
    "local" {return symbol(sym.LOCAL, yytext());}
    // Funcion
    "function" {return symbol(sym.FUNCTION, yytext());}
    // Caracteres
    // Lexema Separador
    "," {return symbol(sym.PINO);}
    // Operadores Aritméticos Binarios
    "+" {return symbol(sym.DASHER, yytext());}
    "-" {return symbol(sym.DANCER, yytext());}
    "*" {return symbol(sym.PRANCER, yytext());}
    "/" {return symbol(sym.VIXEN, yytext());}
    "~" {return symbol(sym.COMET, yytext());}
    "**" {return symbol(sym.RUDOLPH, yytext());}
    // Operadores Aritméticos Unarios
    "++" {return symbol(sym.GRINCH, yytext());}
    "--" {return symbol(sym.QUIEN, yytext());}
    // Operadores Relacionales
    "==" {return symbol(sym.ALABASTER, yytext());}
    "!=" {return symbol(sym.BUSHY, yytext());}
    ">" {return symbol(sym.PEPPER, yytext());}
    "<" {return symbol(sym.SUGARPLUM, yytext());}
    "=>" {return symbol(sym.WUNORSE,yytext());}
    "=<" {return symbol(sym.JINGLE, yytext());}
    // Operadores Lógicos
    "^" {return symbol(sym.MELCHOR, yytext());}
    "#" {return symbol(sym.GASPAR, yytext());}
    "!" {return symbol(sym.BALTAZAR, yytext());}
    // Identificador
    {identifier} {return symbol(sym.PERSONA, yytext());}
    // Manejo de errores
      // Identificador invalido XXX
//      {secuenciaInvalida}  {
//              return symbol(sym.error, "Error: Secuencia de caracteres '" + yytext() + "' no permitida en la línea " + yyline + ", columna " + yycolumn + "XXX");}
      // Numero pegado a letras
//      {EnteroConLetras}  {
//            return symbol(sym.error, "Error: Entero no permitido '" + yytext() + "' en la línea " + yyline + ", columna " + yycolumn);
//      }

}

// Se maneja el error cuando se obtiene un string sin cerrar
<STRING> {
   \"               { yybegin(YYINITIAL); return symbol(sym.L_DEDMOROZ, string.toString()); }
   [^\n\r\"\\]+     { string.append( yytext() ); }
   \\t              { string.append('\t'); }
   \\n              { string.append('\n'); }
   \\r              { string.append('\r'); }
   \\\"             { string.append('\"'); }
   \\               { string.append('\\'); }
   <<EOF>>          {
   yybegin(YYINITIAL); // Volvemos a cambiar el estado del lexer de vuelta a YYINITIAL
   return symbol(sym.error, "Error: Cadena no cerrada en la línea " + yyline + ", columna " + yycolumn);
   }
}

// Se maneja el error cuando se obtiene un comentario multilinea sin cerrar
<COMMENT> {
    \_\/ { yybegin(YYINITIAL); /* end of multiline comment */ }
    [^\n\r]+ { /* ignore */ }
    \n|\r { /* ignore newlines */ }
    <<EOF>> {
        yybegin(YYINITIAL); // Volvemos a cambiar el estado del lexer de vuelta a YYINITIAL
        return symbol(sym.error, "Error: Comentario de múltiples líneas no cerrado en la línea " + yyline + ", columna " + yycolumn);
    }
}

/* error fallback */
[^] { System.out.println("Error: Caracter '" + yytext() + "' no reconocido en la línea " + yyline + ", columna " + yycolumn);}