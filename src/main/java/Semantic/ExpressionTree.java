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
    //TODO: VERIFICAR QUE EL TIPO DE LA FUNCION RETORNE UN VALOR VALIDO CON EL TYPECHECK
    public static String checkExpressionType(String xmlString, String typeCheck, Map<String, TabSymbol> symbols, SymbolTableManager manager) throws Exception {

        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(new InputSource(new StringReader(xmlString)));

        Element rootElement = document.getDocumentElement();
        String rootTagName = rootElement.getTagName();

        System.out.println("Parsed XML Document:\n" + convertDocumentToString(document));

        if (("LOGIC".equals(rootTagName) && typeCheck.equals("bool")) || ("REL".equals(rootTagName)) || (("ARITH").equals(rootTagName) && (typeCheck.equals("int") || typeCheck.equals("float")))) {
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
                    if(!Semantic.funcTypeCheck(funcName, manager, typeCheck)){
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
                    checkRelChildren(element, typeCheck, symbols, manager);
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

            String logicPattern = "REL|LOGIC|LITERAL|CALL|ID";

            // Check if "OP1" is a "REL" expression
//            boolean op1IsRel = hasChildElement(op1Element, logicPattern);
//
//            // Check if "OP2" is a "REL" expression (if it exists)
//            boolean op2IsRel = hasChildElement(op2Element, logicPattern);
//
//            // Throw an exception if either "OP1" or "OP2" is not a "REL" expression
//            if (!op1IsRel || !op2IsRel) {
//                throw new IllegalArgumentException("Invalid structure: Both OP1 and OP2 should be REL expressions.");
//            }
        } else {
            throw new IllegalArgumentException("Error Semántico: La expresión no es booleana");
        }
    }

    private static void checkRelChildren(Element logicElement, String typeCheck, Map<String, TabSymbol> symbols, SymbolTableManager manager) throws Exception {
        Element op1Element = null;
        Element op2Element = null;
        Element symElement = null;

        // Find "OP1", "OP2", and "SYM" elements
        NodeList childNodes = logicElement.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);
            if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element) childNode;
                if ("OP1".equals(childElement.getTagName())) {
                    op1Element = childElement;
                } else if ("OP2".equals(childElement.getTagName())) {
                    op2Element = childElement;
                } else if ("SYM".equals(childElement.getTagName())) {
                    symElement = childElement;
                }
            }
        }
        // Extract text content from "OP1" and "OP2"
        assert op1Element != null;
        String op1Text = op1Element.getFirstChild().getNodeValue();
        assert op2Element != null;
        String op2Text = op2Element.getFirstChild().getNodeValue();
        assert symElement != null;
        String symText = extractValue(symElement);
        System.out.println(op2Text);

        if (symText.matches("l|g|le|ge")) {

            if (op1Element.getFirstChild().getNodeType() == Node.TEXT_NODE) {
                String op1TagName = op1Element.getFirstChild().getNodeName();
                switch (op1TagName) {
                    case "ID" -> System.out.println("OP1 is an id: " + op1Text);
                    case "CALL" -> System.out.println("OP1 is a call: " + op1Text);
                    case "LITERAL" -> System.out.println("OP1 is a bool literal: " + op1Text);
                }
            }

            // Perform operations based on the text content
            if (op2Element.getFirstChild().getNodeType() == Node.TEXT_NODE) {
                String op2TagName = op2Element.getFirstChild().getNodeName();
                switch (op2TagName) {
                    case "ID" -> System.out.println("OP2 is an id: " + op1Text);
                    case "CALL" -> System.out.println("OP2 is a call: " + op1Text);
                    case "LITERAL" -> System.out.println("OP2 is a bool literal: " + op1Text);
                }
            }

        } else if (symText.matches("!=|==")) {

            if (op1Element.getFirstChild().getNodeType() == Node.TEXT_NODE) {
                String op1TagName = op1Element.getFirstChild().getNodeName();
                switch (op1TagName) {
                    case "ID" -> System.out.println("OP1 is an id: " + op1Text);
                    case "CALL" -> System.out.println("OP1 is a call: " + op1Text);
                    case "LITERAL" -> System.out.println("OP1 is a bool literal: " + op1Text);
                }
            }

            // Perform operations based on the text content
            if (op2Element.getFirstChild().getNodeType() == Node.TEXT_NODE) {
                String op2TagName = op2Element.getFirstChild().getNodeName();
                switch (op2TagName) {
                    case "ID" -> System.out.println("OP2 is an id: " + op1Text);
                    case "CALL" -> System.out.println("OP2 is a call: " + op1Text);
                    case "LITERAL" -> System.out.println("OP2 is a bool literal: " + op1Text);
                }
            }
        }

        String logicPattern = "ARITH|LITERAL|CALL|ID|" + Semantic.allRegex;

//        // Check if "OP1" is a "REL" expression
//        boolean op1IsRel = hasChildElement(op1Element, logicPattern);
//
//        // Check if "OP2" is a "REL" expression (if it exists)
//        boolean op2IsRel = hasChildElement(op2Element, logicPattern);
//
//        // Throw an exception if either "OP1" or "OP2" is not a "REL" expression
//        if (!op1IsRel || !op2IsRel) {
//            throw new IllegalArgumentException("Invalid structure: Both OP1 and OP2 should be ARITH expressions.");
//        }

        // Handle the case when both "OP1" and "OP2" are "REL" expressions
        System.out.println("Both OP1 and OP2 are ARITH expressions.");
    }

    private static void checkArithExpr(Element logicElement, String typeCheck, Map<String, TabSymbol> symbols, SymbolTableManager manager) throws Exception {
        if ((typeCheck.equals("int") || typeCheck.equals("float"))) {
            // Directly get "OP1" and "OP2" elements
            Element op1Element = (Element) logicElement.getElementsByTagName("OP1").item(0);
            Element op2Element = (Element) logicElement.getElementsByTagName("OP2").item(0);

            checkSemanticExpr(op1Element, symbols, typeCheck, manager);
            checkSemanticExpr(op2Element, symbols, typeCheck, manager);

            String childType1 = getChildTag(op1Element, symbols, manager);
            String childType2 = getChildTag(op2Element, symbols, manager);


            ArrayList<String> logicPattern = new ArrayList<>(Arrays.asList("ARITH", "ID", "INT", "FLOAT"));

            if (!(logicPattern.contains(childType1) && logicPattern.contains(childType2))){
                throw new IllegalArgumentException("Invalid structure: Both OP1 and OP2 should be ARITH expressions.");
            }

        } else {
            throw new IllegalArgumentException("Error Semántico: La expresión no es aritmética.");
        }
    }

    private static void checkSemanticExpr(Element element, Map<String, TabSymbol> symbols, String typeCheck, SymbolTableManager manager) throws Exception {
        if (element.getFirstChild().getNodeType() == Node.ELEMENT_NODE) {
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
            }
        }
    }

    private static String getChildTag(Element parentElement, Map<String, TabSymbol> symbols, SymbolTableManager manager) {
        Node child = parentElement.getFirstChild();

        // Check the first child node and perform actions based on its type and content
        if (child != null && child.getNodeType() == Node.ELEMENT_NODE) {
            String childTagName = child.getNodeName();
            System.out.println("Element Node: " + childTagName);

            if (childTagName.equals("LOGIC") || childTagName.equals("REL") || childTagName.equals("ARITH")){
                return childTagName;
            } else {
                String childVal;
                switch (childTagName) {
                    case "ID" -> {
                        childVal = child.getTextContent();
                        return symbols.get(childVal).getType().toUpperCase();
                    }
                    case "LITERAL" -> {
                        childVal = child.getTextContent();
                        return Utils.splitMessage(childVal, "=").get(0).toUpperCase();
                    }
                    case "CALL" -> {
                        childVal = child.getFirstChild().getTextContent();
                        return manager.getSymbolTable(childVal).getReturnType().toUpperCase();
                    }
                }
            }
        }

        return "No Child Nodes or Not an Element Node";
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
                                // TODO: COMPARE WITH VALUE IN LIST OF ARGS OF FUNCTION
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
                                if (!(typeCheck.equals("int") || typeCheck.equals("float"))) {
                                    throw new IllegalArgumentException("NOT ARITH ");
                                }
                                System.out.println(argElement.getAttributes());
                                checkArithExpr(argElement, typeCheck, symbols, manager);
                            }

                            // Add cases for other tag types (ARITH, REL, LOGIC) if needed
                            default -> System.out.println("Unknown type: " + argType);
                        }
                    }
                }
            }
        }
    }
}

