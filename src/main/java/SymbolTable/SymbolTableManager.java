package SymbolTable;

import java.util.HashMap;
import java.util.Map;

public class SymbolTableManager {

    private Map<String, SymbolTable> functions;

    public SymbolTableManager() {
        this.functions = new HashMap<>();
    }

    public void addSymbolTable(SymbolTable function) {
        functions.put(function.getName(), function);
    }

    public SymbolTable getSymbolTable(String functionName) {
        return functions.get(functionName);
    }

    public void printAllTables() {
        System.out.println("\nSymbol Tables:");

        for (Map.Entry<String, SymbolTable> entry : functions.entrySet()) {
            String functionName = entry.getKey();
            SymbolTable symbolTable = entry.getValue();

            symbolTable.printSymbols();
        }
    }

}