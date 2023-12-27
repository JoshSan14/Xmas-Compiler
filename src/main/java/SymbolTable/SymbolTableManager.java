package SymbolTable;

import java.util.HashMap;
import java.util.Map;

public class SymbolTableManager {

    private Map<String, SymbolTable> functions;

    /**
     * Constructor de la clase SymbolTableManager. Inicializa el mapa de funciones.
     */
    public SymbolTableManager() {
        this.functions = new HashMap<>();
    }

    /**
     * Agrega una tabla de símbolos asociada a una función al gestor de tablas de símbolos.
     *
     * @param function Tabla de símbolos a agregar.
     */
    public void addSymbolTable(SymbolTable function) {
        functions.put(function.getName(), function);
    }

    /**
     * Obtiene la tabla de símbolos asociada a una función por su nombre.
     *
     * @param functionName Nombre de la función para la cual se busca la tabla de símbolos.
     * @return Tabla de símbolos asociada a la función, o null si no se encuentra.
     */
    public SymbolTable getSymbolTable(String functionName) {
        return functions.get(functionName);
    }

    /**
     * Imprime en consola todas las tablas de símbolos almacenadas en el gestor.
     */
    public void printAllTables() {
        System.out.println("\nSymbol Tables:");

        for (Map.Entry<String, SymbolTable> entry : functions.entrySet()) {
            String functionName = entry.getKey();
            SymbolTable symbolTable = entry.getValue();

            symbolTable.printSymbols();
        }
    }

}