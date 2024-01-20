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
    public final static String intRegex = "int=(-?\\d+)";
    public final static String floatRegex = "float=(-?\\d+(\\.\\d+)?)";
    public final static String boolRegex = "bool=(true|false)";
    public final static String stringRegex = "string=\"(?:[^\"]|\\.)*\"";
    public final static String charRegex = "char='([^'\\\\]|\\\\.)'";
    public final static String literalRegex = "(" + intRegex + ")|(" + floatRegex + ")|(" + boolRegex + ")|(" + stringRegex + ")|(" + charRegex + ")";
    public final static String callRegex = "call=[\\w]+\\([^)]*\\)";
    public final static String idRegex = "id=[\\w]+";
    public final static String allRegex = "(" + literalRegex + ")|(" + callRegex + ")|(" + idRegex + ")";

    public static List<String> semanticArray(String input, String valueType) throws IllegalArgumentException {
        List<String> result = new ArrayList<String>();

        // Validate valueType
        if (!("int".equals(valueType) || "char".equals(valueType))) {
            throw new IllegalArgumentException("Error Semántico: Tipo inválido especificado: " + valueType + ". Tipos aceptados: int o char.");
        }

        if (input.startsWith("[") && input.endsWith("]")) {
            String elemsString = input.substring(1, input.length() - 1);
            String[] elems = elemsString.split(", ");

            for (String elem : elems) {
                Pattern pattern = Pattern.compile(valueType + "=(\\S+)");
                Matcher matcher = pattern.matcher(elem);

                if (matcher.find()) {
                    result.add(valueType + "=" + matcher.group(1));
                } else {
                    throw new IllegalArgumentException("Error Semántico: Tipo inválido especificado: " + elem + ", array de tipo: " + valueType + ". Tipos aceptados: int o char.");
                }
            }
        }

        return result;
    }

    public static boolean funcTypeCheck(String funcName, SymbolTableManager manager, String typeCheck) {
        SymbolTable function = manager.getSymbolTable(funcName);
        String funcType = function.getReturnType();
        return funcType.equals(typeCheck);
    }

    public static boolean idTypeCheck(Map<String, TabSymbol> symbols, String idName, String typeCheck) throws IllegalArgumentException {
        TabSymbol id = symbols.get(idName);
        String idType = id.getType();
        return idType.equals(typeCheck);
    }

    public static boolean litTypeCheck(String literal, String typeCheck) {
        String litType = Utils.splitMessage(literal, "=").get(0);
        return litType.equals(typeCheck);
    }

   }







