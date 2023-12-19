package SymbolTable;

import java.util.LinkedList;

class SymbolTableEntry {
    String name;
    String type;
    int scope;

    public SymbolTableEntry(String name, String type, int scope) {
        this.name = name;
        this.type = type;
        this.scope = scope;
    }
}

class SymbolTable {
    private LinkedList<SymbolTableEntry> table;

    public SymbolTable() {
        table = new LinkedList<>();
    }

    public void addEntry(String name, String type, int scope) {
        SymbolTableEntry entry = new SymbolTableEntry(name, type, scope);
        table.add(entry);
    }

    public SymbolTableEntry getEntry(String name) {
        for (SymbolTableEntry entry : table) {
            if (entry.name.equals(name)) {
                return entry;
            }
        }
        return null;
    }

    public SymbolTableEntry updateEntry(String name, String type, int scope) {
        SymbolTableEntry ste = getEntry(name);
        ste.type = type;
        return ste;
    }

}
