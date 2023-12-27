package Utils;
import SymbolTable.*;
import java.util.*;


public class Utils {

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
        if (Objects.equals(symList.get(0), "array")) {
             sym = new TabSymbol(symList.get(0), symList.get(2), symList.get(1), symList.get(3));
        }
        else {
             sym = new TabSymbol(symList.get(0), symList.get(2), symList.get(1));
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


}
