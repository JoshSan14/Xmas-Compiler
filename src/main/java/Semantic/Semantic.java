package Semantic;

import SymbolTable.TabSymbol;
import SymbolTable.SymbolTable;
import SymbolTable.SymbolTableManager;
import Utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Semantic {
    /**
     * Expresión regular para coincidir con un literal de tipo entero.
     * Formato esperado: int=123
     */
    public final static String intRegex = "int=(-?\\d+)";

    /**
     * Expresión regular para coincidir con un literal de tipo flotante.
     * Formato esperado: float=123.45
     */
    public final static String floatRegex = "float=(-?\\d+(\\.\\d+)?)";

    /**
     * Expresión regular para coincidir con un literal booleano.
     * Formato esperado: bool=true o bool=false
     */
    public final static String boolRegex = "bool=(true|false)";

    /**
     * Expresión regular para coincidir con un literal de cadena.
     * Formato esperado: string="cadena de texto"
     */
    public final static String stringRegex = "string=\"(?:[^\"]|\\.)*\"";

    /**
     * Expresión regular para coincidir con un literal de carácter.
     * Formato esperado: char='a' o char='\n'
     */
    public final static String charRegex = "char='([^'\\\\]|\\\\.)'";

    /**
     * Expresión regular para coincidir con cualquier tipo de literal.
     */
    public final static String literalRegex = "(" + intRegex + ")|(" + floatRegex + ")|(" + boolRegex + ")|(" + stringRegex + ")|(" + charRegex + ")";

    /**
     * Expresión regular para coincidir con una llamada a función.
     * Formato esperado: call=nombreFuncion(arg1, arg2, ..., argN)
     */
    public final static String callRegex = "call=[\\w]+\\([^)]*\\)";

    /**
     * Expresión regular para coincidir con un identificador.
     * Formato esperado: id=nombreId
     */
    public final static String idRegex = "id=[\\w]+";

    /**
     * Expresión regular que combina todas las expresiones regulares anteriores.
     */
    public final static String allRegex = "(" + literalRegex + ")|(" + callRegex + ")|(" + idRegex + ")";


    /**
     * Realiza análisis semántico en una cadena de entrada que representa un array y verifica si los elementos son del tipo especificado.
     *
     * @param input     Cadena de entrada que representa un array.
     * @param typeCheck Tipo esperado para los elementos del array.
     * @return Lista de elementos del array con el tipo especificado.
     * @throws IllegalArgumentException Si hay errores semánticos en la cadena de entrada o el tipo especificado no es "int" o "char".
     */
    public static List<String> semanticArray(String input, String typeCheck) throws IllegalArgumentException {
        List<String> result = new ArrayList<String>();

        // Valida el tipo de valor
        if (!("int".equals(typeCheck) || "char".equals(typeCheck))) {
            System.out.println(Utils.ANSI_RED + "Error Semántico: Tipo inválido especificado: " + typeCheck + ". Tipos aceptados: int o char." + Utils.ANSI_RESET);
        }

        // Verifica si la cadena de entrada comienza y termina con corchetes
        if (input.startsWith("[") && input.endsWith("]")) {
            // Obtiene la cadena de elementos dentro de los corchetes
            String elemsString = input.substring(1, input.length() - 1);
            // Divide la cadena en elementos utilizando ", " como delimitador
            String[] elems = elemsString.split(", ");

            // Procesa cada elemento
            for (String elem : elems) {
                // Utiliza expresiones regulares para extraer el tipo y el valor del elemento
                Pattern pattern = Pattern.compile(typeCheck + "=(\\S+)");
                Matcher matcher = pattern.matcher(elem);

                // Verifica si el elemento cumple con el formato esperado
                if (matcher.find()) {
                    // Agrega el elemento a la lista de resultados
                    result.add(typeCheck + "=" + matcher.group(1));
                } else {
                    // Lanza una excepción si el formato del elemento no es válido
                    System.out.println(Utils.ANSI_RED + "Error Semántico: Tipo inválido especificado: " + typeCheck + ". Tipos aceptados: int o char." + Utils.ANSI_RESET);
                }
            }
        }

        return result;
    }

    /**
     * Verifica si el tipo de retorno de una función coincide con el tipo especificado.
     *
     * @param funcName Nombre de la función.
     * @param manager  Gestor de tablas de símbolos que contiene la información de la función.
     * @param typeCheck Tipo esperado para el retorno de la función.
     * @return True si el tipo de retorno coincide, False en caso contrario.
     */
    public static boolean funcTypeCheck(String funcName, SymbolTableManager manager, String typeCheck) {
        // Obtiene la tabla de símbolos de la función
        SymbolTable function = manager.getSymbolTable(funcName);
        // Obtiene el tipo de retorno de la función
        String funcType = function.getReturnType();
        // Verifica si el tipo de retorno coincide con el tipo especificado
        return funcType.equals(typeCheck);
    }

    /**
     * Verifica si el tipo de una variable identificada (ID) coincide con el tipo especificado.
     *
     * @param symbols  Mapa de símbolos que contiene información sobre las variables.
     * @param idName   Nombre de la variable identificada (ID).
     * @param typeCheck Tipo esperado para la variable identificada.
     * @return True si el tipo de la variable identificada coincide, False en caso contrario.
     * @throws IllegalArgumentException Si no se encuentra la variable identificada en el mapa de símbolos.
     */
    public static boolean idTypeCheck(Map<String, TabSymbol> symbols, String idName, String typeCheck) throws IllegalArgumentException {
        // Obtiene la información de la variable identificada (ID) desde el mapa de símbolos
        TabSymbol id = symbols.get(idName);

        if (id == null) {
            throw new IllegalArgumentException("Error Semántico: La variable \"" + idName + "\" no está definida en el ámbito actual.");
        }

        // Obtiene el tipo de la variable identificada
        String idType = id.getType();
        // Verifica si el tipo de la variable identificada coincide con el tipo especificado
        return idType.equals(typeCheck);
    }

    /**
     * Verifica si el tipo de un literal coincide con el tipo especificado.
     *
     * @param literal   Literal que se va a verificar.
     * @param typeCheck Tipo esperado para el literal.
     * @return True si el tipo del literal coincide, False en caso contrario.
     * @throws IllegalArgumentException Si el formato del literal es incorrecto.
     */
    public static boolean litTypeCheck(String literal, String typeCheck) throws IllegalArgumentException {
        // Obtiene el tipo del literal dividiendo la cadena por el signo "="
        String litType = Utils.splitMessage(literal, "=").get(0);

        if (litType.isEmpty()) {
            throw new IllegalArgumentException("Error Semántico: El formato del literal es incorrecto: " + literal);
        }

        // Verifica si el tipo del literal coincide con el tipo especificado
        return litType.equals(typeCheck);
    }

}