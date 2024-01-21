package Utils;
import SymbolTable.*;
import java.util.*;

import static Semantic.Semantic.semanticArray;


public class Utils {

    /**
     * Colores ANSI para resaltar la salida en la consola.
     */
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PINK = "\u001B[35m";
    public static final String ANSI_BRIGHT_BLUE = "\u001B[34;1m";
    public static final String ANSI_ORANGE = "\u001B[38;5;208m";


    /**
     * Divide un mensaje en partes utilizando un símbolo dado y devuelve una lista de las partes resultantes.
     *
     * @param message El mensaje que se va a dividir.
     * @param symbol El símbolo utilizado para dividir el mensaje.
     * @return Una lista de partes del mensaje.
     */
    public static ArrayList<String> splitMessage(String message, String symbol) {
        ArrayList<String> parts = new ArrayList<>();

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
     * Divide y añade un símbolo al mapa de símbolos.
     *
     * @param message  Mensaje que contiene información sobre el símbolo.
     * @param symbols  Mapa de símbolos actual.
     */
    public static void splitAddSym(String message, Map<String, TabSymbol> symbols) {
        // Dividir el mensaje usando "::"
        List<String> symList = splitMessage(message, "::");
        TabSymbol sym;

        if (symList.size() > 4) {
            if (symList.get(0).equals("parameter")) {
                if (symList.get(3).equals("UND")) {
                    // Crear un símbolo con tipo "int" y tamaño basado en el array semántico
                    sym = new TabSymbol(symList.get(0), symList.get(2), symList.get(1), "int=" + semanticArray(symList.get(3), symList.get(1)).size(), Integer.parseInt(symList.get(4)));
                } else {
                    // Crear un símbolo con tipo y tamaño especificados
                    sym = new TabSymbol(symList.get(0), symList.get(2), symList.get(1), symList.get(3), Integer.parseInt(symList.get(4)));
                }
            } else {
                if (symList.get(3).equals("UND")) {
                    // Crear un símbolo con tipo "int" y tamaño basado en el array semántico
                    sym = new TabSymbol(symList.get(0), symList.get(2), symList.get(1), "int=" + semanticArray(symList.get(3), symList.get(1)).size());
                } else {
                    // Crear un símbolo con tipo especificado
                    sym = new TabSymbol(symList.get(0), symList.get(2), symList.get(1), symList.get(3));
                }
            }
        } else {
            if (symList.get(0).equals("parameter")) {
                // Crear un símbolo con tipo "parameter" y tamaño especificado
                sym = new TabSymbol(symList.get(0), symList.get(2), symList.get(1), Integer.parseInt(symList.get(3)));
            } else {
                // Crear un símbolo con tipo "parameter"
                sym = new TabSymbol(symList.get(0), symList.get(2), symList.get(1));
            }
        }

        // Verificar duplicados
        if (symbols.containsKey(sym.getName())) {
            // Manejar la duplicación (mostrar un mensaje, lanzar una excepción, etc.)
            System.out.println("Símbolo duplicado: " + sym.getName());
            // Puedes modificar esta parte para adaptarla a tus requisitos específicos para manejar duplicados
        } else {
            // Si no es un duplicado, agregar el símbolo al mapa
            symbols.put(sym.getName(), sym);
        }
    }

    /**
     * Divide y añade una función al gestor de tablas de símbolos.
     *
     * @param message      Mensaje que contiene información sobre la función.
     * @param symbols      Mapa de símbolos actual.
     * @param manager      Gestor de la tabla de símbolos.
     * @param parameters   Lista de parámetros de la función.
     */
    public static void splitAddFunc(String message, Map<String, TabSymbol> symbols, SymbolTableManager manager, ArrayList<String> parameters) {
        // Dividir el mensaje usando "::"
        List<String> symList = splitMessage(message, "::");
        String funcName = symList.get(2);

        // Verificar si la función ya existe en el gestor
        if (manager.getSymbolTable(symList.get(2)) != null) {
            // Manejar la duplicación (mostrar un mensaje, lanzar una excepción, etc.)
            System.out.println("Función duplicada: " + symList.get(2) + " ya declarada.");
            // Puedes modificar esta parte para adaptarla a tus requisitos específicos para manejar duplicados
            return; // Salir del método si se encuentra un duplicado
        }

        // Crear la tabla de símbolos y agregarla al gestor
        Map<String, TabSymbol> funcSyms = new HashMap<>(symbols);
        SymbolTable symTab = new SymbolTable(symList.get(2), symList.get(1), funcSyms, parameters);
        manager.addSymbolTable(symTab);

        // Limpiar el mapa de símbolos
        symbols.clear();
    }

}
