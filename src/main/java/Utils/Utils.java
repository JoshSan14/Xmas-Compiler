package Utils;

import SymbolTable.*;

import java.util.*;


public class Utils {
    public static List<String> splitMessage(String message, String symbol) {
        List<String> parts = new ArrayList<>();

        // Check if the message is not null and not empty
        if (message != null && !message.isEmpty()) {
            // Split the message using the provided symbol
            String[] splitParts = message.split(symbol);

            // Add the parts to the list
            for (String part : splitParts) {
                parts.add(part);
            }
        }

        return parts;
    }

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

    public static void splitAddFunc(String message, Map<String, TabSymbol> symbols, SymbolTableManager manager) {
        List<String> symList = splitMessage(message, "::");
        System.out.println(message);
        Map<String, TabSymbol> funcSyms = new HashMap<String, TabSymbol>(symbols);
        SymbolTable symTab = new SymbolTable(symList.get(2), symList.get(1), funcSyms);
        manager.addSymbolTable(symTab);
        symbols.clear();
    }


}
