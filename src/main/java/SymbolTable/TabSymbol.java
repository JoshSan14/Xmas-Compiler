package SymbolTable;

import Utils.Utils;

public class TabSymbol {
    private String kind;
    private String name;
    private String type;
    private String size;

    /**
     * Constructor para parámetros y variables.
     *
     * @param kind Tipo de símbolo (parámetro o variable).
     * @param name Nombre del símbolo.
     * @param type Tipo de datos del símbolo.
     */
    public TabSymbol(String kind, String name, String type) {
        this.kind = kind;
        this.name = name;
        this.type = type;
    }

    /**
     * Constructor para arreglos.
     *
     * @param kind Tipo de símbolo (arreglo).
     * @param name Nombre del símbolo.
     * @param type Tipo de datos del símbolo.
     * @param size Tamaño del arreglo.
     */
    public TabSymbol(String kind, String name, String type, String size) {
        this.kind = kind;
        this.name = name;
        this.type = type;
        this.size = size;
    }

    /**
     * Obtiene el tipo de símbolo (parámetro, variable, o arreglo).
     *
     * @return Tipo de símbolo.
     */
    public String getKind() {
        return kind;
    }

    /**
     * Establece el tipo de símbolo.
     *
     * @param kind Nuevo tipo de símbolo.
     */
    public void setKind(String kind) {
        this.kind = kind;
    }

    /**
     * Obtiene el nombre del símbolo.
     *
     * @return Nombre del símbolo.
     */
    public String getName() {
        return name;
    }

    /**
     * Establece el nombre del símbolo.
     *
     * @param name Nuevo nombre del símbolo.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Obtiene el tipo de datos del símbolo.
     *
     * @return Tipo de datos del símbolo.
     */
    public String getType() {
        return type;
    }

    /**
     * Establece el tipo de datos del símbolo.
     *
     * @param type Nuevo tipo de datos del símbolo.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Obtiene el tamaño del arreglo (si aplica).
     *
     * @return Tamaño del arreglo o null si no es un arreglo.
     */
    public String getSize() {
        return size;
    }

    /**
     * Establece el tamaño del arreglo.
     *
     * @param size Nuevo tamaño del arreglo.
     */
    public void setSize(String size) {
        this.size = size;
    }

    /**
     * Devuelve una representación en cadena del objeto TabSymbol.
     *
     * @return Cadena que representa el objeto TabSymbol.
     */
    @Override
    public String toString() {
        String str = "Type: " + type + ", Kind: " + kind;
        if (kind.equals("array")){
            return Utils.ANSI_GREEN + str +", Size: " + size + Utils.ANSI_RESET;
        } else if (kind.equals("variable")){
            return Utils.ANSI_ORANGE + str + Utils.ANSI_RESET;
        } else {
            return Utils.ANSI_PINK + str + Utils.ANSI_RESET;        }
    }

}