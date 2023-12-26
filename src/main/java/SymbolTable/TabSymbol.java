package SymbolTable;

public class TabSymbol {
    private String kind;
    private String name;
    private String type;
    private String size;

    // Constructor de parámetros y variables
    public TabSymbol(String kind, String name, String type) {
        this.kind = kind;
        this.name = name;
        this.type = type;
    }

    // Constructor de arreglos
    public TabSymbol(String kind, String name, String type, String size) {
        this.kind = kind;
        this.name = name;
        this.type = type;
        this.size = size;
    }

    public String getKind() {
        return kind;
    }
    public void setKind(String kind) {
        this.kind = kind;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getSize() {
        return size;
    }
    public void setSize(String size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "Type: " + type + ", Kind: " + kind ;
    }

}