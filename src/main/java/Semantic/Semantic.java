package Semantic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static Utils.Utils.splitMessage;

public class Semantic {

    public static List<String> parseElems(String elemsString) {
        List<String> elems = new ArrayList<>();

        if (elemsString.startsWith("[") && elemsString.endsWith("]")) {
            elemsString = elemsString.substring(1, elemsString.length() - 1);
            elems.addAll(Arrays.asList(elemsString.split(", ")));
        }

        return elems;
    }

    public static boolean checkDataType(String dataType, List<String> elems) {
        for (String elem : elems) {
            if (!elem.startsWith(dataType + "=")) {
                return false;
            }
        }
        return true;
    }

    public static boolean processInputString(String input) {
        List<String> parts = splitMessage(input, "::");

        if (parts.size() >= 5) {
            String dataType = parts.get(1);  // Get the data type from the first list
            List<String> elems = parseElems(parts.get(4));  // Get the list of elements

            return checkDataType(dataType, elems);
        } else {
            System.out.println("Error Semántico: La lista no tiene tipos válidos.");
            return false;
        }
    }


}
