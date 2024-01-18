package SymbolTable;

import Utils.Utils;

import java.util.ArrayList;
import java.util.Map;

public class SymbolTable {
    private ArrayList<String> paramTypes;
    private String name;
    private String returnType;
    private Map<String, TabSymbol> symbols;

    /**
     * Constructor de la clase SymbolTable.
     *
     * @param name       Nombre de la tabla de símbolos.
     * @param returnType Tipo de retorno asociado a la función representada por la tabla de símbolos.
     * @param symbols    Mapa que contiene los símbolos asociados a la tabla.
     */
    public SymbolTable(String name, String returnType, Map<String, TabSymbol> symbols, ArrayList<String> paramTypes) {
        this.name = name;
        this.returnType = returnType;
        this.symbols = symbols;
        this.paramTypes = paramTypes;
    }

    /**
     * Obtiene el nombre de la tabla de símbolos.
     *
     * @return Nombre de la tabla de símbolos.
     */
    public String getName() {
        return name;
    }

    /**
     * Establece el nombre de la tabla de símbolos.
     *
     * @param name Nuevo nombre para la tabla de símbolos.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Obtiene el tipo de retorno asociado a la función representada por la tabla de símbolos.
     *
     * @return Tipo de retorno de la función.
     */
    public String getReturnType() {
        return returnType;
    }

    /**
     * Establece el tipo de retorno de la función representada por la tabla de símbolos.
     *
     * @param returnType Nuevo tipo de retorno para la función.
     */
    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    /**
     * Obtiene el mapa de símbolos asociado a la tabla.
     *
     * @return Mapa de símbolos.
     */
    public Map<String, TabSymbol> getSymbols() {
        return symbols;
    }

    /**
     * Agrega un símbolo a la tabla de símbolos.
     *
     * @param tabSymbol Símbolo a agregar a la tabla.
     */
    public void addSymbol(TabSymbol tabSymbol) {
        symbols.put(tabSymbol.getName(), tabSymbol);
    }

    /**
     * Obtiene un símbolo específico por su nombre.
     *
     * @param symName Nombre del símbolo a obtener.
     * @return Símbolo correspondiente al nombre proporcionado.
     */
    public TabSymbol getSymbol(String symName) {
        return symbols.get(symName);
    }

    public ArrayList<String> getParamTypes() {
        return paramTypes;
    }

    public void setParamTypes(ArrayList<String> paramTypes) {
        this.paramTypes = paramTypes;
    }

    /**
     * Imprime en consola la información de la tabla de símbolos, incluyendo nombre, tipo de retorno y símbolos.
     */
    public void printSymbols() {
        System.out.println("*------------------------------------------------------*");
        System.out.println("Function: " + Utils.ANSI_BRIGHT_BLUE + name + Utils.ANSI_RESET);
        System.out.println("Return Type: " + Utils.ANSI_BLUE + returnType + Utils.ANSI_RESET);
        System.out.println("Symbols:");

        for (Map.Entry<String, TabSymbol> entry : symbols.entrySet()) {
            String symName = entry.getKey();
            TabSymbol tabSymbol = entry.getValue();
            System.out.println("  " + symName + ": " + tabSymbol);
        }

        System.out.println(this.paramTypes);

    }

}