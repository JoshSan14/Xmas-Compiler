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
    public static String checkExpressionType(String xmlString, String typeCheck, Map<String, TabSymbol> symbols, SymbolTableManager manager) throws Exception {

        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(new InputSource(new StringReader(xmlString)));

        Element rootElement = document.getDocumentElement();
        String rootTagName = rootElement.getTagName();

        System.out.println("Parsed XML Document:\n" + convertDocumentToString(document));

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
                        throw new IllegalArgumentException("Error Semántico: El literal no es de tipo \"" + typeCheck + "\"");
                    }
                }
                case "ID" -> {
                    if (!Semantic.idTypeCheck(symbols, rootText, typeCheck)) {
                        throw new IllegalArgumentException("Error Semántico: La variable \"" + rootText + "\" no es de tipo \"" + typeCheck + "\"");
                    }
                }
                case "CALL" -> {
                    String funcName = rootElement.getElementsByTagName("FUNC").item(0).getTextContent();
                    if (!Semantic.funcTypeCheck(funcName, manager, typeCheck)) {
                        throw new IllegalArgumentException("Error Semántico en expresión aritmética: La funcion \"" + funcName + "\" retorna tipo \"" + typeCheck + "\"");
                    }
                    processCall(rootElement, symbols, manager);
                }

            }
        }

        return rootTagName;
    }

    private static String traverseNode(Node node, String typeCheck, Map<String, TabSymbol> symbols, SymbolTableManager manager) {
        StringBuilder result = new StringBuilder();

        // Check for OP1, OP2, and SYM elements and extract their values
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;

            if ("OP1".equals(element.getTagName()) || "OP2".equals(element.getTagName()) || "SYM".equals(element.getTagName())) {
                result.append(element.getTagName()).append(": ").append(extractValue(element)).append("\n");
            } else if ("LOGIC".equals(element.getTagName())) {
                // Perform checkLogicChildren analysis for "LOGIC" element
                try {
                    checkLogicExpr(element, typeCheck, symbols, manager);
                } catch (Exception e) {
                    // Handle the exception if needed
                    e.printStackTrace();
                }
            } else if ("REL".equals(element.getTagName())) {
                // Perform checkLogicChildren analysis for "LOGIC" element
                try {
                    checkRelExpr(element, typeCheck, symbols, manager);
                } catch (Exception e) {
                    // Handle the exception if needed
                    e.printStackTrace();
                }
            } else if ("ARITH".equals(element.getTagName())) {
                // Perform checkLogicChildren analysis for "LOGIC" element
                try {
                    checkArithExpr(element, typeCheck, symbols, manager);
                } catch (Exception e) {
                    // Handle the exception if needed
                    e.printStackTrace();
                }
            }
        }

        // Recursively traverse child nodes
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);
            result.append(traverseNode(childNode, typeCheck, symbols, manager));
        }

        return result.toString();
    }

    private static String extractValue(Element element) {
        NodeList textNodes = element.getChildNodes();
        StringBuilder value = new StringBuilder();

        for (int i = 0; i < textNodes.getLength(); i++) {
            Node textNode = textNodes.item(i);
            if (textNode.getNodeType() == Node.TEXT_NODE) {
                value.append(textNode.getTextContent().trim());
            }
        }

        // Check if the element has child elements (non-leaf node)
        if (element.getFirstChild() != null && element.getFirstChild().getNodeType() == Node.ELEMENT_NODE) {
            return "";
        }

        return value.toString();
    }

    private static String convertDocumentToString(Document document) {
        try {
            javax.xml.transform.TransformerFactory transformerFactory = javax.xml.transform.TransformerFactory.newInstance();
            javax.xml.transform.Transformer transformer = transformerFactory.newTransformer();

            // Set up formatting options
            transformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            java.io.StringWriter writer = new java.io.StringWriter();
            transformer.transform(new javax.xml.transform.dom.DOMSource(document), new javax.xml.transform.stream.StreamResult(writer));
            return writer.toString();
        } catch (javax.xml.transform.TransformerException e) {
            e.printStackTrace();
            return "Error converting Document to String: " + e.getMessage();
        }
    }

    private static void checkLogicExpr(Element logicElement, String typeCheck, Map<String, TabSymbol> symbols, SymbolTableManager manager) throws Exception {
        if (typeCheck.equals("bool")) {
            // Directly get "OP1" and "OP2" elements
            Element op1Element = (Element) logicElement.getElementsByTagName("OP1").item(0);
            Element op2Element = (Element) logicElement.getElementsByTagName("OP2").item(0);

            checkSemanticExpr(op1Element, symbols, typeCheck, manager);
            checkSemanticExpr(op2Element, symbols, typeCheck, manager);

            String childType1 = getChildTag(op1Element, symbols, manager);
            String childType2 = getChildTag(op2Element, symbols, manager);

            ArrayList<String> logicPattern = new ArrayList<>(Arrays.asList("REL", "LOGIC", "bool"));

            if (childType2 != null) {
                if (!(logicPattern.contains(childType1) && logicPattern.contains(childType2))) {
                    throw new IllegalArgumentException("Invalid structure: Both OP1 and OP2 should be LOGIC expressions.");
                }
            } else {
                if (!(logicPattern.contains(childType1))) {
                    throw new IllegalArgumentException("Invalid structure: Both OP1 and OP2 should be LOGIC expressions.");
                }
            }

        } else {
            throw new IllegalArgumentException("Error Semántico: La expresión no es booleana");
        }
    }

    private static void checkRelExpr(Element element, String typeCheck, Map<String, TabSymbol> symbols, SymbolTableManager manager) throws Exception {
        if (typeCheck.equals("bool")) {

            Element op1Element = (Element) element.getElementsByTagName("OP1").item(0);
            Element symElement = (Element) element.getElementsByTagName("SYM").item(0);
            Element op2Element = (Element) element.getElementsByTagName("OP2").item(0);

            String childType1 = getChildTag(op1Element, symbols, manager);
            String symType = symElement.getTextContent();
            String childType2 = getChildTag(op2Element, symbols, manager);

            ArrayList<String> arithPattern = new ArrayList<>(Arrays.asList("ARITH", "int", "float"));
            ArrayList<String> arithRelOps = new ArrayList<>(Arrays.asList("l", "g", "le", "ge"));
            ArrayList<String> allRelOps = new ArrayList<>(Arrays.asList("eq", "ne"));

            if (arithRelOps.contains(symType) && arithPattern.contains(childType1) && arithPattern.contains(childType2)) {
                if ((childType1.equals("int") || childType1.equals("float")) && childType2.equals("ARITH")) {
                    checkSemanticExpr(op1Element, symbols, childType1, manager);
                } else if (childType1.equals("ARITH") && (childType2.equals("int") || childType2.equals("float"))) {
                    checkSemanticExpr(op2Element, symbols, childType2, manager);
                } else if ((childType1.equals("int") || childType1.equals("float") && childType2.equals(childType1))) {
                    checkSemanticExpr(op1Element, symbols, childType1, manager);
                    checkSemanticExpr(op2Element, symbols, childType1, manager);
                } else {
                    throw new IllegalArgumentException("Invalid structure: BOOOOYAH");
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
                    throw new IllegalArgumentException("Invalid structure: BOOOOYAH");
                }
            } else {
                throw new IllegalArgumentException("Invalid structure: BOOOOYAH");
            }

        } else {
            throw new IllegalArgumentException("Invalid structure: BOOOOYAH");
        }
    }

    private static void checkArithExpr(Element element, String typeCheck, Map<String, TabSymbol> symbols, SymbolTableManager manager) throws Exception {
        if ((typeCheck.equals("int") || typeCheck.equals("float"))) {
            // Directly get "OP1" and "OP2" elements
            Element op1Element = (Element) element.getElementsByTagName("OP1").item(0);
            Element op2Element = (Element) element.getElementsByTagName("OP2").item(0);

            checkSemanticExpr(op1Element, symbols, typeCheck, manager);
            checkSemanticExpr(op2Element, symbols, typeCheck, manager);

            String childType1 = getChildTag(op1Element, symbols, manager);
            String childType2 = getChildTag(op2Element, symbols, manager);

            ArrayList<String> logicPattern = new ArrayList<>(Arrays.asList("ARITH", "int", "float"));

            if (childType2 != null){
                if (!(logicPattern.contains(childType1) && logicPattern.contains(childType2))){
                    throw new IllegalArgumentException("Invalid structure: Both OP1 and OP2 should be LOGIC expressions.");
                }
            } else {
                if (!(logicPattern.contains(childType1))){
                    throw new IllegalArgumentException("Invalid structure: Both OP1 and OP2 should be LOGIC expressions.");
                }
            }

        } else {
            throw new IllegalArgumentException("Error Semántico: La expresión no es aritmética.");
        }
    }

    private static void checkSemanticExpr(Element element, Map<String, TabSymbol> symbols, String typeCheck, SymbolTableManager manager) throws Exception {
        if ((element != null) && (element.getFirstChild().getNodeType() == Node.ELEMENT_NODE)) {
            String textContent = element.getFirstChild().getTextContent();
            String tagName = element.getFirstChild().getNodeName();
            switch (tagName) {
                case "ID" -> {
                        if (!Semantic.idTypeCheck(symbols, textContent, typeCheck)) {
                        throw new IllegalArgumentException("Error Semántico en expresión: La variable \"" + textContent + "\" no es de tipo \"" + typeCheck + "\"");
                    }
                }
                case "CALL" -> {
                    String funcName = element.getElementsByTagName("FUNC").item(0).getTextContent();
                    if(!Semantic.funcTypeCheck(funcName, manager, typeCheck)){
                        throw new IllegalArgumentException("Error Semántico en expresión: La funcion \"" + funcName + "\" no retorna tipo \"" + typeCheck + "\"");
                    }
                    processCall(element, symbols, manager);
                }
                case "LITERAL" -> {
                    if (!Semantic.litTypeCheck(textContent, typeCheck)) {
                        throw new IllegalArgumentException("Error Semántico en expresión: El literal no es de tipo \"" + typeCheck + "\"");
                    }
                }
                case "ARR_ELEM" -> {
                    processArrElem(element, symbols, typeCheck, manager);
                }
                case "ARITH" -> {
                    checkArithExpr(element, typeCheck, symbols, manager);
                }
                case "REL" -> {
                    checkRelExpr(element, typeCheck, symbols, manager);
                }
                case "LOGIC" -> {
                    checkLogicExpr(element, typeCheck, symbols, manager);
                }
            }
        }
    }

    private static String getChildTag(Element parentElement, Map<String, TabSymbol> symbols, SymbolTableManager manager) {
        if (parentElement != null) {
            Node child = parentElement.getFirstChild();

            // Check the first child node and perform actions based on its type and content
            if (child != null && child.getNodeType() == Node.ELEMENT_NODE) {
                String childTagName = child.getNodeName();
                System.out.println("Element Node: " + childTagName);

                if (childTagName.equals("LOGIC") || childTagName.equals("REL") || childTagName.equals("ARITH")) {
                    return childTagName;
                } else {
                    String childVal;
                    switch (childTagName) {
                        case "ID" -> {
                            childVal = child.getTextContent();
                            return symbols.get(childVal).getType();
                        }
                        case "LITERAL" -> {
                            childVal = child.getTextContent();
                            return Utils.splitMessage(childVal, "=").get(0);
                        }
                        case "CALL" -> {
                            childVal = child.getFirstChild().getTextContent();
                            return manager.getSymbolTable(childVal).getReturnType();
                        }
                        case "ARR_ELEM" -> {
                            childVal = child.getFirstChild().getTextContent();
                            return symbols.get(childVal).getType();
                        }
                    }
                }
            }
        }
        return null;
    }

    private static void processCall(Node callNode, Map<String, TabSymbol> symbols, SymbolTableManager manager) throws Exception {
        //TODO ACTUALIZAR SWITCH
        if (callNode.getNodeType() == Node.ELEMENT_NODE) {
            Element callElement = (Element) callNode;
            NodeList funcElement = callElement.getElementsByTagName("FUNC");
            String funcName = funcElement.item(0).getTextContent();

            // Find the first ARGUMENTS tag within the CALL tag
            NodeList argumentsList = callElement.getElementsByTagName("ARGUMENTS");

            if (argumentsList.getLength() > 0) {
                Element argumentsElement = (Element) argumentsList.item(0);

                // Find direct child ARG nodes within ARGUMENTS
                NodeList argList = argumentsElement.getChildNodes();

                // Process each direct child ARG tag
                for (int i = 0; i < argList.getLength(); i++) {
                    Node argNode = argList.item(i);

                    if (argNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element argElement = (Element) argNode;

                        // Determine the type of the ARG tag
                        String argType = argElement.getFirstChild().getNodeName();

                        // Perform action based on the type of ARG tag

                        String op, type, typeCheck;
                        SymbolTable function = manager.getSymbolTable(funcName);
                        ArrayList<String> paramTypes = function.getParamTypes();

                        typeCheck = paramTypes.get(i);

                        switch (argType) {
                            case "ID" -> {
                                op = argElement.getTextContent().trim();
                                type = symbols.get(op).getType();
                                if (!type.equals(typeCheck)) {
                                    throw new IllegalArgumentException("NOT ID USABLE");
                                }
                            }
                            case "LITERAL" -> {
                                // TODO: GET THE TYPE AND COMPARE
                                op = argElement.getTextContent().trim();
                                type = Utils.splitMessage(op, "=").get(0);
                                if (!type.equals(typeCheck)) {
                                    throw new IllegalArgumentException("NOT LITERAL USABLE");
                                }
                            }
                            case "CALL" -> {
                                // Recursively process ARG tags within the CALL tag
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
                            default -> System.out.println("Unknown type: " + argType);
                        }
                    }
                }
            }
        }
    }

    private static void processExpr(Element exprElement, String typeCheck, Map<String, TabSymbol> symbols, SymbolTableManager manager) throws Exception {
        Node childNode = exprElement.getFirstChild();

        if (childNode != null && childNode.getNodeType() == Node.ELEMENT_NODE) {
            Element childElement = (Element) childNode;
            String childTagName = childElement.getTagName();

            switch (childTagName) {
                case "ID" -> {
                    String id = childElement.getTextContent();
                    if (!Semantic.idTypeCheck(symbols, id, typeCheck)) {
                        throw new IllegalArgumentException("Error Semántico en expresión: La variable \"" + id + "\" no es de tipo \"" + typeCheck + "\"");
                    }
                }
                case "LITERAL" -> {
                    String literal = childElement.getTextContent();
                    if (!Semantic.litTypeCheck(literal, typeCheck)) {
                        throw new IllegalArgumentException("Error Semántico en expresión: El literal no es de tipo \"" + typeCheck + "\"");
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
                default -> throw new IllegalArgumentException("Unsupported tag within EXPR: " + childTagName);
            }
        }
    }

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

    private static void processArrElem(Element arrElemElement, Map<String, TabSymbol> symbols, String typeCheck, SymbolTableManager manager) throws Exception {
        // Process the <ID> tag
        Element idElement = (Element) arrElemElement.getElementsByTagName("ID").item(0);
        String idText = idElement.getTextContent();

        // Check semantic analysis for the <ID> tag
        if (!Semantic.idTypeCheck(symbols, idText, typeCheck)) {
            throw new IllegalArgumentException("Error Semántico: La variable \"" + idText + "\" no es de tipo \"" + typeCheck + "\"");
        }

        // Process the <EXPR> tag
        Element exprElement = (Element) arrElemElement.getElementsByTagName("EXPR").item(0);

        // Check semantic analysis for the <EXPR> tag
        checkSemanticExpr(exprElement, symbols, typeCheck, manager);

    }
}

