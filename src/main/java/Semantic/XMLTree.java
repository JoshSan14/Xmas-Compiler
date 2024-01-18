package Semantic;

import SymbolTable.TabSymbol;
import Utils.Utils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XMLTree {

    public static String checkExpressionType(String xmlString, String type, Map<String, TabSymbol> symbols) throws Exception {

            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(xmlString)));

            Element rootElement = document.getDocumentElement();
            String rootTagName = rootElement.getTagName();

            System.out.println("Parsed XML Document:\n" + convertDocumentToString(document));


            if (("LOGIC".equals(rootTagName) && type.equals("bool")) || ("REL".equals(rootTagName)) || (("ARITH").equals(rootTagName) && (type.equals("int") || type.equals("float")))) {
                traverseNode(rootElement, type);
            } else {
                String rootText = rootElement.getTextContent();
                switch (rootTagName){
                    case "LITERAL"  -> {
                        if(!Semantic.litTypeCheck(Utils.splitMessage(rootText,"=").get(0), type)){
                            throw new IllegalArgumentException("NOT N");
                        }
                    }
                    case "ID" -> {
                        if(!Semantic.idTypeCheck(symbols, Utils.splitMessage(rootText,"=").get(1), type)){
                            throw new IllegalArgumentException("NOT VALID");
                        }
                    }
                    case "CALL" -> {
                        System.out.println("CALLL");
                    }
                }
            }

            return rootTagName;
    }

    private static String traverseNode(Node node, String type) {
        StringBuilder result = new StringBuilder();

        // Check for OP1, OP2, and SYM elements and extract their values
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;

            if ("OP1".equals(element.getTagName()) || "OP2".equals(element.getTagName()) || "SYM".equals(element.getTagName())) {
                result.append(element.getTagName()).append(": ").append(extractValue(element)).append("\n");
            } else if ("LOGIC".equals(element.getTagName())) {
                // Perform checkLogicChildren analysis for "LOGIC" element
                try {
                    checkLogicChildren(element);
                } catch (Exception e) {
                    // Handle the exception if needed
                    e.printStackTrace();
                }
            } else if ("REL".equals(element.getTagName())) {
                // Perform checkLogicChildren analysis for "LOGIC" element
                try {
                    checkRelChildren(element, type);
                } catch (Exception e) {
                    // Handle the exception if needed
                    e.printStackTrace();
                }
            } else if ("ARITH".equals(element.getTagName())) {
                // Perform checkLogicChildren analysis for "LOGIC" element
                try {
                    checkArithChildren(element, type);
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
            result.append(traverseNode(childNode, type));
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

    private static void checkLogicChildren(Element logicElement) throws Exception {
        Element op1Element = null;
        Element op2Element = null;

        // Find "OP1" and "OP2" elements
        NodeList childNodes = logicElement.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);
            if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element) childNode;
                if ("OP1".equals(childElement.getTagName())) {
                    op1Element = childElement;
                } else if ("OP2".equals(childElement.getTagName())) {
                    op2Element = childElement;
                }
            }
        }

        // Extract text content from "OP1" and "OP2"
        assert op1Element != null;
        String op1Text = op1Element.getFirstChild().getNodeValue();
        assert op2Element != null;
        String op2Text = op2Element.getFirstChild().getNodeValue();

        if (op1Element.getFirstChild().getNodeType() == Node.TEXT_NODE) {
            String op1TagName = op1Element.getFirstChild().getNodeName();
            switch (op1TagName) {
                case "ID" -> System.out.println("OP1 is an id: " + op1Text);
                case "CALL" -> System.out.println("OP1 is a call: " + op1Text);
                case "LITERAL" -> System.out.println("OP1 is a literal: " + op1Text);
            }
        }

        if (op2Element.getFirstChild().getNodeType() == Node.TEXT_NODE) {
            String op2TagName = op2Element.getFirstChild().getNodeName();
            switch (op2TagName) {
                case "ID" ->System.out.println("OP2 is an id: " + op2Text);
                case "CALL" ->System.out.println("OP2 is a call: " + op2Text);
                case "LITERAL" ->System.out.println("OP2 is a literal: " + op2Text);
            }
        }

        String logicPattern = "REL|LOGIC|LITERAL|CALL|ID|" + Semantic.boolRegex + "|" + Semantic.callRegex + "|" + Semantic.idRegex;

        // Check if "OP1" is a "REL" expression
        boolean op1IsRel = hasChildElement(op1Element, logicPattern);

        // Check if "OP2" is a "REL" expression (if it exists)
        boolean op2IsRel = hasChildElement(op2Element, logicPattern);

        // Throw an exception if either "OP1" or "OP2" is not a "REL" expression
        if (!op1IsRel || !op2IsRel) {
            throw new IllegalArgumentException("Invalid structure: Both OP1 and OP2 should be REL expressions.");
        }

        // Handle the case when both "OP1" and "OP2" are "REL" expressions
        System.out.println("Both OP1 and OP2 are REL expressions.");
    }

    private static void checkRelChildren(Element logicElement, String type) throws Exception {
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
                    case "ID" ->System.out.println("OP1 is an id: " + op1Text);
                    case "CALL" -> System.out.println("OP1 is a call: " + op1Text);
                    case "LITERAL" ->System.out.println("OP1 is a bool literal: " + op1Text);
                }
            }

            // Perform operations based on the text content
            if (op2Element.getFirstChild().getNodeType() == Node.TEXT_NODE) {
                String op2TagName = op2Element.getFirstChild().getNodeName();
                switch (op2TagName) {
                    case "ID" ->System.out.println("OP2 is an id: " + op1Text);
                    case "CALL" -> System.out.println("OP2 is a call: " + op1Text);
                    case "LITERAL" ->System.out.println("OP2 is a bool literal: " + op1Text);
                }
            }

        } else if (symText.matches("!=|==")) {

            if (op1Element.getFirstChild().getNodeType() == Node.TEXT_NODE) {
                String op1TagName = op1Element.getFirstChild().getNodeName();
                switch (op1TagName) {
                    case "ID" ->System.out.println("OP1 is an id: " + op1Text);
                    case "CALL" -> System.out.println("OP1 is a call: " + op1Text);
                    case "LITERAL" ->System.out.println("OP1 is a bool literal: " + op1Text);
                }
            }

            // Perform operations based on the text content
            if (op2Element.getFirstChild().getNodeType() == Node.TEXT_NODE) {
                String op2TagName = op2Element.getFirstChild().getNodeName();
                switch (op2TagName) {
                    case "ID" ->System.out.println("OP2 is an id: " + op1Text);
                    case "CALL" -> System.out.println("OP2 is a call: " + op1Text);
                    case "LITERAL" ->System.out.println("OP2 is a bool literal: " + op1Text);
                }
            }
        }

        String logicPattern = "ARITH|LITERAL|CALL|ID|" + Semantic.allRegex;

        // Check if "OP1" is a "REL" expression
        boolean op1IsRel = hasChildElement(op1Element, logicPattern);

        // Check if "OP2" is a "REL" expression (if it exists)
        boolean op2IsRel = hasChildElement(op2Element, logicPattern);

        // Throw an exception if either "OP1" or "OP2" is not a "REL" expression
        if (!op1IsRel || !op2IsRel) {
            throw new IllegalArgumentException("Invalid structure: Both OP1 and OP2 should be ARITH expressions.");
        }

        // Handle the case when both "OP1" and "OP2" are "REL" expressions
        System.out.println("Both OP1 and OP2 are ARITH expressions.");
    }

    private static void checkArithChildren(Element logicElement, String type) throws Exception {
        Element op1Element = null;
        Element op2Element = null;

        // Find "OP1" and "OP2" elements
        NodeList childNodes = logicElement.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);
            if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element) childNode;
                if ("OP1".equals(childElement.getTagName())) {
                    op1Element = childElement;
                } else if ("OP2".equals(childElement.getTagName())) {
                    op2Element = childElement;
                }
            }
        }

        // Extract text content from "OP1" and "OP2"
        assert op1Element != null;
        String op1Text = op1Element.getFirstChild().getTextContent();
        assert op2Element != null;
        String op2Text = op2Element.getFirstChild().getTextContent();

        if (op1Element.getFirstChild().getNodeType() == Node.ELEMENT_NODE) {
            String op1TagName = op1Element.getFirstChild().getNodeName();
            switch (op1TagName) {
                case "ID" -> {
                    System.out.println("OP1 is an id: " + op1Text);
                }
                case "CALL" -> {
                    System.out.println("OP1 is a call: " + op1Text);
                }
                case "LITERAL" -> {
                    System.out.println("OP1 is literal: " + op1Text);
                    if(!type.equals(Utils.splitMessage(op1Text,"=").get(0))){
                        throw new IllegalArgumentException("NOT USABLE");
                    }
                }
            }
        }

        // Perform operations based on the text content
        if (op2Element.getFirstChild().getNodeType() == Node.ELEMENT_NODE) {
            String op2TagName = op2Element.getFirstChild().getNodeName();
            switch (op2TagName) {
                case "ID" -> {
                    System.out.println("OP2 is an id: " + op2Text);
                }
                case "CALL" -> {
                    System.out.println("OP2 is a call: " + op2Text);
                }
                case "LITERAL" -> {
                    System.out.println("OP2 is a bool literal: " + op2Text);
                    if(!type.equals(Utils.splitMessage(op2Text,"=").get(0))){
                        throw new IllegalArgumentException("NOT USABLE");
                    }
                }
            }
        }

        String logicPattern = "ARITH|LITERAL|CALL|ID|" + Semantic.callRegex + "|" + Semantic.idRegex + "|" + Semantic.intRegex + "|" + Semantic.floatRegex;

        // Check if "OP1" is a "REL" expression
        boolean op1IsRel = hasChildElement(op1Element, logicPattern);

        // Check if "OP2" is a "REL" expression (if it exists)
        boolean op2IsRel = hasChildElement(op2Element, logicPattern);

        // Throw an exception if either "OP1" or "OP2" is not a "REL" expression
        if (!op1IsRel || !op2IsRel) {
            throw new IllegalArgumentException("Invalid structure: Both OP1 and OP2 should be ARITH expressions.");
        }

        // Handle the case when both "OP1" and "OP2" are "REL" expressions
        System.out.println("Both OP1 and OP2 are REL expressions.");
    }

    private static boolean hasChildElement(Element parentElement, String childTagNamePattern) {
        if (parentElement == null) {
            return false;
        }

        NodeList childNodes = parentElement.getChildNodes();
        Pattern pattern = Pattern.compile(childTagNamePattern);

        for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);
            if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                String childTagName = childNode.getNodeName();
                Matcher matcher = pattern.matcher(childTagName);

                if (matcher.matches()) {
                    return true;
                }
            } else if (childNode.getNodeType() == Node.TEXT_NODE) {
                // If the node is a text node, check if it matches the boolean expression pattern
                String textContent = childNode.getTextContent().trim();
                Matcher matcher = pattern.matcher(textContent);

                if (matcher.matches()) {
                    return true;
                }
            }
        }

        return false;
    }
}
