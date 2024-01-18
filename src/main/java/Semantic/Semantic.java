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
        List<String> result = new ArrayList<>();

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



    private static List<String> analyzePattern(String input, String regex) {
        List<String> resultList = new ArrayList<>();
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        while (matcher.find()) {
            resultList.add(matcher.group());
        }

        resultList = Utils.removeDuplicates(resultList);

        return resultList;
    }

    private static void extractAndAppend(String inputString, List<String> resultList, String regex) {
        // Define the pattern you want to match
        Pattern pattern = Pattern.compile(regex);

        // Create a matcher object using the pattern
        Matcher matcher = pattern.matcher(inputString);

        // Check if the pattern is found in the input string
        if (matcher.find()) {
            // Extract the matched part (group 1 in this case)
            String matchedPart = matcher.group(1);

            // Append the matched part to the result list
            resultList.add(matchedPart);
        }
    }

    private static boolean funcTypeCheck(SymbolTableManager manager, String funcName, String typeCheck) throws IllegalArgumentException {
        SymbolTable function = manager.getSymbolTable(funcName);

        if (function == null) {
            throw new IllegalArgumentException("Error Semántico: La función " + funcName + " no se encuentra declarada.");
        }

        return function.getReturnType().equals(typeCheck);
    }

    public static boolean idTypeCheck(Map<String, TabSymbol> symbols, String idName, String typeCheck) throws IllegalArgumentException {
        TabSymbol id = symbols.get(idName);
        System.out.println(symbols);
        if (id == null) {
            throw new IllegalArgumentException("Error Semántico: IDNF" + idName);
        }

        return id.getType().equals(typeCheck);
    }

    public static boolean litTypeCheck(String litType, String checkType) {
        return litType.equals(checkType);
    }


    public static void traverseExpression(String expression) {
        String regex = "(<(LOGIC|REL|ARITH)>((<OP1>.*</OP1>)?(<SYM>.{1,2}</SYM>)(<OP2>.*</OP2>))(</\\2>))";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(expression);

        while (matcher.find()) {
            String operationType = matcher.group(4);
            String operandContent = matcher.group(0);
            //String symbolContent = matcher.group(0);
            //String nestedOperation = matcher.group(0);

            System.out.println("Operation Type: " + operationType);
            System.out.println("Operand Content: " + operandContent);
            // System.out.println("Symbol Content: " + symbolContent);
            // System.out.println("Nested Operation: " + nestedOperation);
        }
    }

    private static void processOperandsAndSymbols(String content) {
        Pattern operandPattern = Pattern.compile("<OP>(.*?)</OP>");
        Pattern symbolPattern = Pattern.compile("<SYM>(.*?)</SYM>");

        Matcher operandMatcher = operandPattern.matcher(content);
        Matcher symbolMatcher = symbolPattern.matcher(content);

        int operandCount = 0;

        while (operandMatcher.find()) {
            operandCount++;
            System.out.println("  Operand " + operandCount + ": " + operandMatcher.group(1));
        }

        while (symbolMatcher.find()) {
            System.out.println("  Symbol: " + symbolMatcher.group(1));
        }
    }

}







