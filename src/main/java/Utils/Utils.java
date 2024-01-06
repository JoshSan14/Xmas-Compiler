package Utils;
import SymbolTable.*;
import java.util.*;


public class Utils {

    //Colores
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PINK = "\u001B[35m";
    public static final String ANSI_BRIGHT_BLUE = "\u001B[34;1m";
    public static final String ANSI_ORANGE = "\u001B[38;5;208m"; // A mix of red and yellow


    /**
     * Divide un mensaje en partes utilizando un símbolo dado y devuelve una lista de las partes resultantes.
     *
     * @param message El mensaje que se va a dividir.
     * @param symbol El símbolo utilizado para dividir el mensaje.
     * @return Una lista de partes del mensaje.
     */
    public static List<String> splitMessage(String message, String symbol) {
        List<String> parts = new ArrayList<>();

        // Check if the message is not null and not empty
        if (message != null && !message.isEmpty()) {
            // Split the message using the provided symbol
            String[] splitParts = message.split(symbol);

            // Add the parts to the list
            parts.addAll(Arrays.asList(splitParts));
        }

        return parts;
    }

    /**
     * Divide un mensaje y agrega un símbolo correspondiente al mapa de símbolos.
     *
     * @param message El mensaje que se va a dividir y agregar al mapa de símbolos.
     * @param symbols El mapa de símbolos al que se agregarán los símbolos divididos.
     */
    public static void splitAddSym(String message, Map<String, TabSymbol> symbols) {
        List<String> symList = splitMessage(message, "::");
        System.out.println(message);
        TabSymbol sym;
        sym = new TabSymbol(symList.get(0), symList.get(2), symList.get(1));
        symbols.put(sym.getName(), sym);
    }

    public static void splitAddArray(String message, Map<String, TabSymbol> symbols) {
        List<String> symList = splitMessage(message, "::");
        TabSymbol sym;
        System.out.println(symList.get(4));
        if (symList.get(3).equals("UND")) {
            sym = new TabSymbol(symList.get(0), symList.get(2), symList.get(1), "int=" + Utils.getArray(symList.get(4)).size());
        } else {
            sym = new TabSymbol(symList.get(0), symList.get(2), symList.get(1), symList.get(3));
        }
        symbols.put(sym.getName(), sym);
    }

    /**
     * Divide un mensaje y agrega una tabla de símbolos de función correspondiente al gestor de tablas de símbolos.
     *
     * @param message El mensaje que se va a dividir y agregar como una tabla de símbolos de función.
     * @param symbols El mapa de símbolos actual que se limpiará después de agregar la tabla de símbolos de función.
     * @param manager El gestor de tablas de símbolos al que se agregará la nueva tabla de símbolos de función.
     */
    public static void splitAddFunc(String message, Map<String, TabSymbol> symbols, SymbolTableManager manager) {
        List<String> symList = splitMessage(message, "::");
        System.out.println(message);
        Map<String, TabSymbol> funcSyms = new HashMap<String, TabSymbol>(symbols);
        SymbolTable symTab = new SymbolTable(symList.get(2), symList.get(1), funcSyms);
        manager.addSymbolTable(symTab);
        symbols.clear();
    }

    public static List<String> getArray(String input) {

        // Remove square brackets and split by ", " to get individual elements
        String[] elements = input.substring(1, input.length() - 1).split(", ");

        // Create a new ArrayList and add elements to it
        List<String> stringList = new ArrayList<>(Arrays.asList(elements));

        // Print the resulting list
        return stringList;
    }






}
