package SymbolTable;

import java.util.Map;

public class SymbolTable {
    private String name;
    private String returnType;
    private Map<String, TabSymbol> symbols;

    public SymbolTable(String name, String returnType, Map<String, TabSymbol> symbols) {
        this.name = name;
        this.returnType = returnType;
        this.symbols = symbols;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public Map<String, TabSymbol> getSymbols() {
        return symbols;
    }

    public void addSymbol(TabSymbol tabSymbol) {
        symbols.put(tabSymbol.getName(), tabSymbol);
    }

    public TabSymbol getSymbol(String symName) {
        return symbols.get(symName);
    }

    public void printSymbols() {
        System.out.println("*------------------------------------------------------*");
        System.out.println("Function: " + name);
        System.out.println("Return Type: " + returnType);
        System.out.println("Symbols:");

        for (Map.Entry<String, TabSymbol> entry : symbols.entrySet()) {
            String symName = entry.getKey();
            TabSymbol tabSymbol = entry.getValue();
            System.out.println("  " + symName + ": " + tabSymbol);
        }
    }

}