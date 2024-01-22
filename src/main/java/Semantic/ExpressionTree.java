package Semantic;

import SymbolTable.SymbolTable;
import SymbolTable.SymbolTableManager;
import SymbolTable.TabSymbol;
import Utils.Utils;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class ExpressionTree {

    /**
     * Realiza la verificación semántica del tipo de expresión contenida en una cadena XML.
     *
     * @param xmlString   Cadena XML que representa la expresión a verificar.
     * @param typeCheck    Tipo esperado para la expresión.
     * @param symbols      Mapa de símbolos que contiene información sobre las variables.
     * @param manager      Gestor de la tabla de símbolos.
     * @return Nombre de la etiqueta raíz de la expresión.
     * @throws Exception   Excepción en caso de error durante el análisis o la verificación semántica.
     */
    public static String checkExpressionType(String xmlString, String typeCheck, Map<String, TabSymbol> symbols, SymbolTableManager manager) throws Exception {

        // Construir el analizador de documentos XML
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

        // Analizar la cadena XML y obtener el documento
        Document document = builder.parse(new InputSource(new StringReader(xmlString)));

        // Obtener el elemento raíz del documento XML
        Element rootElement = document.getDocumentElement();
        String rootTagName = rootElement.getTagName();

        // Imprimir información sobre el documento XML analizado
        //System.out.println("Documento XML analizado:\n" + convertDocumentToString(document));

        // Realizar la verificación semántica según la etiqueta raíz de la expresión
        if ("ARRAY".equals(rootTagName) && ((typeCheck.equals("int") || typeCheck.equals("char")))) {
            processArray(rootElement, typeCheck, symbols, manager);
        } else if ("ARR_ELEM".equals(rootTagName)) {
            processArrElem(rootElement, symbols, typeCheck, manager);
        } else if (("LOGIC".equals(rootTagName) && typeCheck.equals("bool")) || ("REL".equals(rootTagName)) || (("ARITH").equals(rootTagName) && (typeCheck.equals("int") || typeCheck.equals("float")))) {
            traverseNode(rootElement, typeCheck, symbols, manager);
        } else {
            String rootText = rootElement.getTextContent();
            switch (rootTagName) {
                case "LITERAL" -> {
                    if (!Semantic.litTypeCheck(rootText, typeCheck)) {
                        System.out.println(Utils.ANSI_RED + "Error Semántico: El literal no es de tipo \"" + typeCheck + "\"" + Utils.ANSI_RESET);
                    }
                }
                case "ID" -> {
                    if (!Semantic.idTypeCheck(symbols, rootText, typeCheck)) {
                        System.out.println(Utils.ANSI_RED + "Error Semántico: La variable \"" + rootText + "\" no es de tipo \"" + typeCheck + "\"" + Utils.ANSI_RESET);
                    }
                }
                case "CALL" -> {
                    String funcName = rootElement.getElementsByTagName("FUNC").item(0).getTextContent();
                    if (!Semantic.funcTypeCheck(funcName, manager, typeCheck)) {
                        System.out.println(Utils.ANSI_RED + "Error Semántico en expresión aritmética: La función \"" + funcName + "\" retorna tipo \"" + typeCheck + "\"" + Utils.ANSI_RESET);
                    }
                    processCall(rootElement, symbols, manager);
                }
            }
        }

        return rootTagName;
    }

    /**
     * Recorre un nodo XML y realiza análisis semántico según la etiqueta del nodo y su contenido.
     *
     * @param node       Nodo XML que se va a recorrer.
     * @param typeCheck  Tipo esperado para la expresión.
     * @param symbols    Mapa de símbolos que contiene información sobre las variables.
     * @param manager    Gestor de la tabla de símbolos.
     * @return Resultado del recorrido, incluyendo información sobre elementos OP1, OP2 y SYM.
     */
    private static String traverseNode(Node node, String typeCheck, Map<String, TabSymbol> symbols, SymbolTableManager manager) {
        StringBuilder result = new StringBuilder();

        // Verificar si el nodo es de tipo ELEMENT_NODE
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;

            // Verificar si el elemento es OP1, OP2 o SYM y extraer sus valores
            if ("OP1".equals(element.getTagName()) || "OP2".equals(element.getTagName()) || "SYM".equals(element.getTagName())) {
                result.append(element.getTagName()).append(": ").append(extractValue(element)).append("\n");
            } else if ("LOGIC".equals(element.getTagName())) {
                // Realizar análisis checkLogicChildren para el elemento "LOGIC"
                try {
                    checkLogicExpr(element, typeCheck, symbols, manager);
                } catch (Exception e) {
                    // Manejar la excepción si es necesario
                    e.printStackTrace();
                }
            } else if ("REL".equals(element.getTagName())) {
                // Realizar análisis checkRelExpr para el elemento "REL"
                try {
                    checkRelExpr(element, typeCheck, symbols, manager);
                } catch (Exception e) {
                    // Manejar la excepción si es necesario
                    e.printStackTrace();
                }
            } else if ("ARITH".equals(element.getTagName())) {
                // Realizar análisis checkArithExpr para el elemento "ARITH"
                try {
                    checkArithExpr(element, typeCheck, symbols, manager);
                } catch (Exception e) {
                    // Manejar la excepción si es necesario
                    e.printStackTrace();
                }
            }
        }

        // Recorrer de manera recursiva los nodos hijos
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);
            result.append(traverseNode(childNode, typeCheck, symbols, manager));
        }

        return result.toString();
    }

    /**
     * Extrae y concatena el contenido de texto de un elemento XML.
     *
     * @param element Elemento XML del cual se extrae el contenido de texto.
     * @return Contenido de texto del elemento XML.
     */
    private static String extractValue(Element element) {
        NodeList textNodes = element.getChildNodes();
        StringBuilder value = new StringBuilder();

        // Recorrer los nodos hijos y extraer el contenido de texto
        for (int i = 0; i < textNodes.getLength(); i++) {
            Node textNode = textNodes.item(i);
            if (textNode.getNodeType() == Node.TEXT_NODE) {
                value.append(textNode.getTextContent().trim());
            }
        }

        // Verificar si el elemento tiene elementos hijos (nodo no hoja)
        if (element.getFirstChild() != null && element.getFirstChild().getNodeType() == Node.ELEMENT_NODE) {
            return "";
        }

        return value.toString();
    }

    /**
     * Convierte un objeto Document XML a una cadena de texto con formato.
     *
     * @param document Documento XML que se va a convertir.
     * @return Cadena de texto con formato que representa el documento XML.
     */
    public static String convertDocumentToString(Document document) {
        try {
            javax.xml.transform.TransformerFactory transformerFactory = javax.xml.transform.TransformerFactory.newInstance();
            javax.xml.transform.Transformer transformer = transformerFactory.newTransformer();

            // Configurar opciones de formato
            transformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            java.io.StringWriter writer = new java.io.StringWriter();
            transformer.transform(new javax.xml.transform.dom.DOMSource(document), new javax.xml.transform.stream.StreamResult(writer));
            return writer.toString();
        } catch (javax.xml.transform.TransformerException e) {
            e.printStackTrace();
            return "Error convirtiendo el Documento a Cadena de Texto: " + e.getMessage();
        }
    }

    /**
     * Realiza la verificación semántica de una expresión lógica.
     *
     * @param logicElement Elemento XML que representa la expresión lógica.
     * @param typeCheck    Tipo esperado de la expresión lógica.
     * @param symbols      Mapa de símbolos para verificar tipos de variables.
     * @param manager      Gestor de la tabla de símbolos para funciones.
     * @throws Exception   Excepción lanzada en caso de error semántico.
     */
    private static void checkLogicExpr(Element logicElement, String typeCheck, Map<String, TabSymbol> symbols, SymbolTableManager manager) throws Exception {
        if (typeCheck.equals("bool")) {
            // Obtener directamente los elementos "OP1" y "OP2"
            Element op1Element = (Element) logicElement.getElementsByTagName("OP1").item(0);
            Element op2Element = (Element) logicElement.getElementsByTagName("OP2").item(0);

            // Verificar la expresión semántica de "OP1" y "OP2"
            checkSemanticExpr(op1Element, symbols, typeCheck, manager);
            checkSemanticExpr(op2Element, symbols, typeCheck, manager);

            // Obtener los tipos de los hijos
            String childType1 = getChildTag(op1Element, symbols, manager);
            String childType2 = getChildTag(op2Element, symbols, manager);

            // Patrón permitido para expresiones lógicas
            ArrayList<String> logicPattern = new ArrayList<>(Arrays.asList("REL", "LOGIC", "bool"));

            // Verificar la estructura de la expresión lógica
            if (childType2 != null) {
                if (!(logicPattern.contains(childType1) && logicPattern.contains(childType2))) {
                    System.out.println(Utils.ANSI_RED + "Error Semántico: Estructura inválida: Ambos OP1 y OP2 deben ser expresiones LÓGICAS o RELACIONALES." + Utils.ANSI_RESET);
                }
            } else {
                if (!(logicPattern.contains(childType1))) {
                    System.out.println(Utils.ANSI_RED + "Error Semántico: Estructura inválida: Ambos OP1 y OP2 deben ser expresiones LÓGICAS o RELACIONALES." + Utils.ANSI_RESET);
                }
            }

        } else {
            System.out.println(Utils.ANSI_RED + "Error Semántico: La expresión no es booleana" + Utils.ANSI_RESET);
        }
    }

    /**
     * Realiza la verificación semántica de una expresión relacional.
     *
     * @param element   Elemento XML que representa la expresión relacional.
     * @param typeCheck Tipo esperado de la expresión relacional.
     * @param symbols   Mapa de símbolos para verificar tipos de variables.
     * @param manager   Gestor de la tabla de símbolos para funciones.
     * @throws Exception Excepción lanzada en caso de error semántico.
     */
    private static void checkRelExpr(Element element, String typeCheck, Map<String, TabSymbol> symbols, SymbolTableManager manager) throws Exception {
        if (typeCheck.equals("bool")) {

            // Obtener elementos "OP1", "SYM" y "OP2"
            Element op1Element = (Element) element.getElementsByTagName("OP1").item(0);
            Element symElement = (Element) element.getElementsByTagName("SYM").item(0);
            Element op2Element = (Element) element.getElementsByTagName("OP2").item(0);

            // Obtener los tipos de los hijos
            String childType1 = getChildTag(op1Element, symbols, manager);
            String symType = symElement.getTextContent();
            String childType2 = getChildTag(op2Element, symbols, manager);

            // Patrones y operadores relacionales permitidos
            ArrayList<String> arithPattern = new ArrayList<>(Arrays.asList("ARITH", "int", "float"));
            ArrayList<String> arithRelOps = new ArrayList<>(Arrays.asList("l", "g", "le", "ge"));
            ArrayList<String> allRelOps = new ArrayList<>(Arrays.asList("eq", "ne"));

            // Verificar la estructura de la expresión relacional
            if (arithRelOps.contains(symType) && arithPattern.contains(childType1) && arithPattern.contains(childType2)) {
                if ((childType1.equals("int") || childType1.equals("float")) && childType2.equals("ARITH")) {
                    checkSemanticExpr(op1Element, symbols, childType1, manager);
                } else if (childType1.equals("ARITH") && (childType2.equals("int") || childType2.equals("float"))) {
                    checkSemanticExpr(op2Element, symbols, childType2, manager);
                } else if ((childType1.equals("int") || childType1.equals("float") && childType2.equals(childType1))) {
                    checkSemanticExpr(op1Element, symbols, childType1, manager);
                    checkSemanticExpr(op2Element, symbols, childType1, manager);
                } else {
                    System.out.println(Utils.ANSI_RED + "Error Semántico: Estructura inválida: Las expresiones relacionales de tipo <, >, <=, >= deben ser ARITMÉTICAS." + Utils.ANSI_RESET);
                }
            } else if (allRelOps.contains(symType)) {
                if (childType1.equals("ARITH") && childType2.equals("ARITH")) {
                    return;
                } else if ((childType1.equals("int") || childType1.equals("float")) && childType2.equals("ARITH")) {
                    checkSemanticExpr(op1Element, symbols, childType1, manager);
                } else if (childType1.equals("ARITH") && (childType2.equals("int") || childType2.equals("float"))) {
                    checkSemanticExpr(op2Element, symbols, childType2, manager);
                } else if ((childType1.equals("LOGIC") || childType1.equals("REL")) && (childType2.equals("LOGIC") || childType2.equals("REL"))) {
                    return;
                } else if ((childType1.equals("bool")) && (childType2.equals("LOGIC") || childType2.equals("REL"))) {
                    checkSemanticExpr(op1Element, symbols, childType1, manager);
                } else if ((childType1.equals("LOGIC") || childType1.equals("REL")) && (childType2.equals("bool"))) {
                    checkSemanticExpr(op2Element, symbols, childType2, manager);
                } else if ((childType1.equals("int") || childType1.equals("float") || childType1.equals("char") || childType1.equals("string") || childType1.equals("bool")) && childType2.equals(childType1)) {
                    checkSemanticExpr(op1Element, symbols, childType1, manager);
                    checkSemanticExpr(op2Element, symbols, childType1, manager);
                } else {
                    System.out.println(Utils.ANSI_RED + "Error Semántico: Estructura inválida: Las expresiones relacionales de tipo == o != deben ser del mismo tipo de expresión." + Utils.ANSI_RESET);
                }
            } else {
                System.out.println(Utils.ANSI_RED + "Error Semántico: Estructura inválida: La expresión relacional no es válida" + Utils.ANSI_RESET);
            }

        } else {
            System.out.println(Utils.ANSI_RED + "Error Semántico: La expresión no es booleana" + Utils.ANSI_RESET);
        }
    }

    /**
     * Realiza la verificación semántica de una expresión aritmética.
     *
     * @param element   Elemento XML que representa la expresión aritmética.
     * @param typeCheck Tipo esperado de la expresión aritmética (int o float).
     * @param symbols   Mapa de símbolos para verificar tipos de variables.
     * @param manager   Gestor de la tabla de símbolos para funciones.
     * @throws Exception Excepción lanzada en caso de error semántico.
     */
    private static void checkArithExpr(Element element, String typeCheck, Map<String, TabSymbol> symbols, SymbolTableManager manager) throws Exception {
        if ((typeCheck.equals("int") || typeCheck.equals("float"))) {

            // Obtener elementos "OP1" y "OP2"
            Element op1Element = (Element) element.getElementsByTagName("OP1").item(0);
            Element op2Element = (Element) element.getElementsByTagName("OP2").item(0);

            // Realizar análisis semántico de los hijos
            checkSemanticExpr(op1Element, symbols, typeCheck, manager);
            checkSemanticExpr(op2Element, symbols, typeCheck, manager);

            // Obtener los tipos de los hijos
            String childType1 = getChildTag(op1Element, symbols, manager);
            String childType2 = getChildTag(op2Element, symbols, manager);

            // Patrón permitido para expresiones aritméticas
            ArrayList<String> logicPattern = new ArrayList<>(Arrays.asList("ARITH", "int", "float"));

            // Verificar la estructura de la expresión aritmética
            if (childType2 != null){
                if (!(logicPattern.contains(childType1) && logicPattern.contains(childType2))){
                    System.out.println(Utils.ANSI_RED + "Error Semántico: Estructura inválida: Ambos OP1 y OP2 deben ser expresiones aritméticas." + Utils.ANSI_RESET);
                }
            } else {
                if (!(logicPattern.contains(childType1))){
                    System.out.println(Utils.ANSI_RED + "Error Semántico: Estructura inválida: Ambos OP1 y OP2 deben ser expresiones aritméticas." + Utils.ANSI_RESET);
                }
            }

        } else {
            System.out.println(Utils.ANSI_RED + "Error Semántico: La expresión no es aritmética." + Utils.ANSI_RESET);
        }
    }


    /**
     * Realiza la verificación semántica de una expresión.
     *
     * @param element   Elemento XML que representa la expresión.
     * @param symbols   Mapa de símbolos para verificar tipos de variables.
     * @param typeCheck Tipo esperado de la expresión.
     * @param manager   Gestor de la tabla de símbolos para funciones.
     * @throws Exception Excepción lanzada en caso de error semántico.
     */
    public static void checkSemanticExpr(Element element, Map<String, TabSymbol> symbols, String typeCheck, SymbolTableManager manager) throws Exception {
        if ((element != null) && (element.getFirstChild().getNodeType() == Node.ELEMENT_NODE)) {

            // Obtener información del primer hijo
            String textContent = element.getFirstChild().getTextContent();
            String tagName = element.getFirstChild().getNodeName();

            // Realizar acciones basadas en el tipo de elemento
            switch (tagName) {
                case "ID" -> {
                    // Verificar el tipo de la variable identificada
                    if (!Semantic.idTypeCheck(symbols, textContent, typeCheck)) {
                        System.out.println(Utils.ANSI_RED + "Error Semántico en expresión: La variable \"" + textContent + "\" no es de tipo \"" + typeCheck + "\"" + Utils.ANSI_RESET);
                    }
                }
                case "CALL" -> {
                    // Verificar el tipo de retorno de la función llamada
                    String funcName = element.getElementsByTagName("FUNC").item(0).getTextContent();
                    if(!Semantic.funcTypeCheck(funcName, manager, typeCheck)){
                        System.out.println(Utils.ANSI_RED + "Error Semántico en expresión: La función \"" + funcName + "\" no retorna tipo \"" + typeCheck + "\"" + Utils.ANSI_RESET);
                    }
                    // Procesar la llamada a la función
                    processCall(element, symbols, manager);
                }
                case "LITERAL" -> {
                    // Verificar el tipo del literal
                    if (!Semantic.litTypeCheck(textContent, typeCheck)) {
                        System.out.println(Utils.ANSI_RED + "Error Semántico en expresión: El literal no es de tipo \"" + typeCheck + "\"" + Utils.ANSI_RESET);
                    }
                }
                case "ARR_ELEM" -> {
                    // Procesar elemento de array
                    processArrElem(element, symbols, typeCheck, manager);
                }
                case "ARITH" -> {
                    // Verificar expresión aritmética
                    checkArithExpr(element, typeCheck, symbols, manager);
                }
                case "REL" -> {
                    // Verificar expresión relacional
                    checkRelExpr(element, typeCheck, symbols, manager);
                }
                case "LOGIC" -> {
                    // Verificar expresión lógica
                    checkLogicExpr(element, typeCheck, symbols, manager);
                }
            }
        }
    }

    /**
     * Obtiene la etiqueta del primer hijo del elemento padre y realiza acciones
     * basadas en el tipo y contenido del hijo.
     *
     * @param parentElement Elemento padre del cual se obtendrá el primer hijo.
     * @param symbols       Mapa de símbolos para verificar tipos de variables.
     * @param manager       Gestor de la tabla de símbolos para funciones.
     * @return Etiqueta del primer hijo o null si no hay hijo o la etiqueta no es reconocida.
     */
    static String getChildTag(Element parentElement, Map<String, TabSymbol> symbols, SymbolTableManager manager) {
        if (parentElement != null) {
            Node child = parentElement.getFirstChild();

            // Verificar el primer nodo hijo y realizar acciones basadas en su tipo y contenido
            if (child != null && child.getNodeType() == Node.ELEMENT_NODE) {
                String childTagName = child.getNodeName();
                System.out.println("Elemento Nodo: " + childTagName);

                // Realizar acciones específicas para ciertas etiquetas
                if (childTagName.equals("LOGIC") || childTagName.equals("REL") || childTagName.equals("ARITH")) {
                    return childTagName;
                } else {
                    String childVal;
                    switch (childTagName) {
                        case "ID" -> {
                            // Si es una ID, devolver el tipo de la variable
                            childVal = child.getTextContent();
                            return symbols.get(childVal).getType();
                        }
                        case "LITERAL" -> {
                            // Si es un literal, devolver el tipo del literal
                            childVal = child.getTextContent();
                            return Utils.splitMessage(childVal, "=").get(0);
                        }
                        case "CALL" -> {
                            // Si es una llamada a función, devolver el tipo de retorno de la función
                            childVal = child.getFirstChild().getTextContent();
                            return manager.getSymbolTable(childVal).getReturnType();
                        }
                        case "ARR_ELEM" -> {
                            // Si es un elemento de array, devolver el tipo de la variable de array
                            childVal = child.getFirstChild().getTextContent();
                            return symbols.get(childVal).getType();
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Procesa una llamada a función (CALL) y verifica la validez de los argumentos según los
     * tipos esperados por la función.
     *
     * @param callNode  Nodo que representa la llamada a función.
     * @param symbols   Mapa de símbolos para verificar tipos de variables.
     * @param manager   Gestor de la tabla de símbolos para funciones.
     * @throws Exception Si hay errores semánticos en la llamada a función.
     */
    private static void processCall(Node callNode, Map<String, TabSymbol> symbols, SymbolTableManager manager) throws Exception {
        if (callNode.getNodeType() == Node.ELEMENT_NODE) {
            Element callElement = (Element) callNode;
            NodeList funcElement = callElement.getElementsByTagName("FUNC");
            String funcName = funcElement.item(0).getTextContent();

            // Encontrar el primer tag ARGUMENTS dentro del tag CALL
            NodeList argumentsList = callElement.getElementsByTagName("ARGUMENTS");

            if (argumentsList.getLength() > 0) {
                Element argumentsElement = (Element) argumentsList.item(0);

                // Encontrar nodos ARG hijos directos dentro de ARGUMENTS
                NodeList argList = argumentsElement.getChildNodes();

                // Procesar cada nodo ARG hijo directo
                for (int i = 0; i < argList.getLength(); i++) {
                    Node argNode = argList.item(i);

                    if (argNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element argElement = (Element) argNode;

                        // Determinar el tipo del tag ARG
                        String argType = argElement.getFirstChild().getNodeName();

                        // Realizar acción basada en el tipo del tag ARG

                        String op, type, typeCheck;
                        SymbolTable function = manager.getSymbolTable(funcName);
                        ArrayList<String> paramTypes = function.getParamTypes();

                        typeCheck = paramTypes.get(i);

                        switch (argType) {
                            case "ID" -> {
                                op = argElement.getTextContent().trim();
                                type = symbols.get(op).getType();
                                if (!type.equals(typeCheck)) {
                                    System.out.println(Utils.ANSI_RED + "Error Semántico en llamada a función: La variable \"" + op + "\" no es de tipo \"" + typeCheck + "\"" + Utils.ANSI_RESET);
                                }
                            }
                            case "LITERAL" -> {
                                // TODO: OBTENER EL TIPO Y COMPARAR
                                op = argElement.getTextContent().trim();
                                type = Utils.splitMessage(op, "=").get(0);
                                if (!type.equals(typeCheck)) {
                                    System.out.println(Utils.ANSI_RED + "Error Semántico en llamada a función: El literal no es de tipo \"" + typeCheck + "\"" + Utils.ANSI_RESET);
                                }
                            }
                            case "CALL" -> {
                                // Procesar de forma recursiva los tags ARG dentro del tag CALL
                                processCall(argElement, symbols, manager);
                            }
                            case "ARITH" -> {
                                checkArithExpr(argElement, typeCheck, symbols, manager);
                            }
                            case "REL" -> {
                                checkRelExpr(argElement, typeCheck, symbols, manager);
                            }
                            case "LOGIC" -> {
                                checkLogicExpr(argElement, typeCheck, symbols, manager);
                            }
                            case "ARR_ELEM" -> {
                                processArrElem(argElement, symbols, typeCheck, manager);
                            }
                            default -> System.out.println("Tipo desconocido: " + argType);
                        }
                    }
                }
            }
        }
    }

    /**
     * Procesa una expresión (EXPR) y verifica la validez semántica según el tipo esperado.
     *
     * @param exprElement Elemento que representa la expresión.
     * @param typeCheck   Tipo esperado para la expresión.
     * @param symbols     Mapa de símbolos para verificar tipos de variables.
     * @param manager     Gestor de la tabla de símbolos para funciones.
     * @throws Exception  Si hay errores semánticos en la expresión.
     */
    private static void processExpr(Element exprElement, String typeCheck, Map<String, TabSymbol> symbols, SymbolTableManager manager) throws Exception {
        Node childNode = exprElement.getFirstChild();

        if (childNode != null && childNode.getNodeType() == Node.ELEMENT_NODE) {
            Element childElement = (Element) childNode;
            String childTagName = childElement.getTagName();

            switch (childTagName) {
                case "ID" -> {
                    String id = childElement.getTextContent();
                    if (!Semantic.idTypeCheck(symbols, id, typeCheck)) {
                        System.out.println(Utils.ANSI_RED + "Error Semántico en expresión: La variable \"" + id + "\" no es de tipo \"" + typeCheck + "\"" + Utils.ANSI_RESET);
                    }
                }
                case "LITERAL" -> {
                    String literal = childElement.getTextContent();
                    if (!Semantic.litTypeCheck(literal, typeCheck)) {
                        System.out.println(Utils.ANSI_RED + "Error Semántico en expresión: El literal no es de tipo \"" + typeCheck + "\"" + Utils.ANSI_RESET);
                    }
                }
                case "CALL" -> {
                    processCall(childElement, symbols, manager);
                }
                case "ARITH" -> {
                    checkArithExpr(childElement, typeCheck, symbols, manager);
                }
                case "REL" -> {
                    checkRelExpr(childElement, typeCheck, symbols, manager);
                }
                case "LOGIC" -> {
                    checkLogicExpr(childElement, typeCheck, symbols, manager);
                }
                case "ARR_ELEM" -> {
                    processArrElem(childElement, symbols, typeCheck, manager);
                }
                default -> System.out.println(Utils.ANSI_RED + "Tag no soportado dentro de EXPR: " + childTagName + Utils.ANSI_RESET);
            }
        }
    }

    /**
     * Procesa un elemento de arreglo (ARRAY) verificando la validez semántica de cada expresión.
     *
     * @param arrayElement Elemento que representa el arreglo.
     * @param typeCheck    Tipo esperado para las expresiones dentro del arreglo.
     * @param symbols      Mapa de símbolos para verificar tipos de variables.
     * @param manager      Gestor de la tabla de símbolos para funciones.
     * @throws Exception   Si hay errores semánticos en alguna de las expresiones del arreglo.
     */
    private static void processArray(Element arrayElement, String typeCheck, Map<String, TabSymbol> symbols, SymbolTableManager manager) throws Exception {
        NodeList exprNodes = arrayElement.getElementsByTagName("EXPR");

        for (int i = 0; i < exprNodes.getLength(); i++) {
            Node exprNode = exprNodes.item(i);
            if (exprNode.getNodeType() == Node.ELEMENT_NODE) {
                Element exprElement = (Element) exprNode;
                processExpr(exprElement, typeCheck, symbols, manager);
            }
        }
    }

    /**
     * Procesa un elemento de arreglo (ARR_ELEM), realizando análisis semántico en la etiqueta ID y EXPR.
     *
     * @param arrElemElement Elemento que representa un elemento de arreglo.
     * @param symbols       Mapa de símbolos para verificar tipos de variables.
     * @param typeCheck     Tipo esperado para el elemento de arreglo.
     * @param manager       Gestor de la tabla de símbolos para funciones.
     * @throws Exception    Si hay errores semánticos en la etiqueta ID o EXPR del elemento de arreglo.
     */
    private static void processArrElem(Element arrElemElement, Map<String, TabSymbol> symbols, String typeCheck, SymbolTableManager manager) throws Exception {
        // Procesa la etiqueta <ID>
        Element idElement = (Element) arrElemElement.getElementsByTagName("ID").item(0);
        String idText = idElement.getTextContent();

        // Verifica el análisis semántico para la etiqueta <ID>
        if (!Semantic.idTypeCheck(symbols, idText, typeCheck)) {
            System.out.println(Utils.ANSI_RED + "Error Semántico: La variable \"" + idText + "\" no es de tipo \"" + typeCheck + "\"");
        }

        // Procesa la etiqueta <EXPR>
        Element exprElement = (Element) arrElemElement.getElementsByTagName("EXPR").item(0);

        // Verifica el análisis semántico para la etiqueta <EXPR>
        checkSemanticExpr(exprElement, symbols, typeCheck, manager);
    }
}