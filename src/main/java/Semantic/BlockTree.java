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

import static Semantic.ExpressionTree.*;

public class BlockTree {

    /**
     * Procesa un bloque de función representado por una cadena XML.
     *
     * @param xmlString      Cadena XML que representa el bloque de función.
     * @param funcName       Nombre de la función.
     * @param funcType       Tipo de retorno de la función.
     * @param symbols        Mapa de símbolos que contiene información sobre las variables.
     * @param manager        Gestor de la tabla de símbolos.
     * @throws Exception     Excepción general que puede ocurrir durante el procesamiento del bloque de función.
     */
    public static void processFuncBlock(String xmlString, String funcName, String funcType, Map<String, TabSymbol> symbols, SymbolTableManager manager) throws Exception {
        // Parsear la cadena XML en un Documento
        Document document = parseXmlString(xmlString);

        // Verificar si el documento representa una estructura XML válida
        if (document.getDocumentElement() != null && "BLOCK".equals(document.getDocumentElement().getTagName())) {
            // Bandera para hacer un seguimiento de la presencia de un elemento RETURN
            boolean hasReturn = false;

            // Procesar cada nodo hijo del elemento <BLOCK>
            NodeList childNodes = document.getDocumentElement().getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node childNode = childNodes.item(i);

                // Procesar cada elemento hijo específico según su nombre de etiqueta
                if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element childElement = (Element) childNode;
                    String tagName = childElement.getTagName();

                    if ("RETURN".equals(tagName)) {
                        // Procesar el elemento RETURN
                        processReturn(childElement, funcType, symbols, manager);
                        hasReturn = true;
                    }
                }
            }

            // Verificar si el BLOQUE tiene un elemento RETURN
            if (!hasReturn) {
                System.out.println("Error: El bloque de función para " + funcName + " no contiene una declaración RETURN.");
            }

            // Si necesita convertir el documento a una cadena para fines de depuración o registro
            String blockString = convertDocumentToString(document);
            //System.out.println("XML del Bloque de Función:\n" + blockString);
        } else {
            System.out.println(Utils.ANSI_RED + "Error: Estructura XML no válida para el bloque de función." + Utils.ANSI_RESET);
        }
    }

    /**
     * Convierte una cadena XML en un objeto Document para su posterior procesamiento.
     *
     * @param xmlString       Cadena XML que se va a convertir en un Document.
     * @return                Document que representa la estructura del XML.
     * @throws Exception      Excepción general que puede ocurrir durante el análisis y conversión XML.
     */
    private static Document parseXmlString(String xmlString) throws Exception {
        // Configurar el analizador y el constructor de documentos XML
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        // Parsear la cadena XML y construir el Document
        return builder.parse(new InputSource(new StringReader(xmlString)));
    }

    /**
     * Procesa una declaración de retorno (<RETURN>) en una función, realizando análisis
     * semántico según el tipo especificado y los símbolos en el contexto.
     *
     * @param returnElement   Elemento XML que representa la declaración de retorno.
     * @param typeCheck       Tipo esperado para el resultado de la expresión de retorno.
     * @param symbols         Mapa que contiene los símbolos y sus tipos en el contexto actual.
     * @param manager         El gestor de la tabla de símbolos.
     * @throws Exception      Excepción general que puede ocurrir durante el procesamiento.
     */
    private static void processReturn(Element returnElement, String typeCheck, Map<String, TabSymbol> symbols, SymbolTableManager manager) throws Exception {
        Node firstChild = returnElement.getFirstChild();

        if (firstChild != null && firstChild.getNodeType() == Node.ELEMENT_NODE) {
            Element childElement = (Element) firstChild;

            // Obtener el tipo de retorno basado en el análisis del primer hijo
            String returnType = getChildTag(returnElement, symbols, manager);

            if (returnType != null) {
                // Suponiendo que tienes un método como checkSemanticExpr, puedes llamarlo aquí
                checkSemanticExpr(returnElement, symbols, typeCheck, manager);
            } else {
                System.out.println(Utils.ANSI_RED + "Error: No se pudo determinar el tipo de retorno de la expresión en la declaración RETURN." + Utils.ANSI_RESET);
            }
        } else {
            System.out.println(Utils.ANSI_RED + "Error: No se encontró ningún elemento hijo en la declaración RETURN." + Utils.ANSI_RESET);
        }
    }
    /**
     * Procesa una declaración de asignación (<ASSG>) que puede tener un identificador (<ID>)
     * o un elemento de array (<ARR_ELEM>) como su primer hijo.
     *
     * @param assignXmlString  Cadena XML que representa la declaración de asignación.
     * @param symbols          Mapa que contiene los símbolos y sus tipos.
     * @param manager          El gestor de la tabla de símbolos.
     * @throws Exception       Excepción general que puede ocurrir durante el procesamiento.
     */
    public static void processAssign(String assignXmlString, Map<String, TabSymbol> symbols, SymbolTableManager manager) throws Exception {
        // Parsear la cadena XML en un Documento
        Document assignDocument = parseXmlString(assignXmlString);

        // Obtener el elemento raíz del Documento
        Element assignElement = assignDocument.getDocumentElement();

        // Obtener el primer elemento hijo del elemento "ASSG"
        Node firstChild = assignElement.getFirstChild();

        if (firstChild != null && firstChild.getNodeType() == Node.ELEMENT_NODE) {
            Element firstChildElement = (Element) firstChild;

            // Obtener el nombre de la etiqueta del primer elemento hijo
            String firstChildTagName = firstChildElement.getTagName();

            // Realizar diferentes operaciones según el primer elemento hijo
            switch (firstChildTagName) {
                case "ID" ->
                    // Procesar la asignación con un identificador (ID)
                        processIdAssignment(firstChildElement, assignElement, symbols, manager);
                case "ARR_ELEM" ->
                    // Procesar la asignación con un elemento de array (ARR_ELEM)
                        processArrElemAssignment(firstChildElement, assignElement, symbols, manager);
                default ->
                        System.out.println(Utils.ANSI_RED + "Error: Elemento hijo inesperado en la declaración de asignación." + Utils.ANSI_RESET);
            }
        } else {
            System.out.println(Utils.ANSI_RED + "Error: No se encontró ningún elemento hijo en la declaración de asignación." + Utils.ANSI_RESET);
        }
    }

    /**
     * Procesa una asignación de identificador (<ASSG>) cuando el primer elemento hijo es <ID>.
     *
     * @param idElement        El elemento <ID> que contiene el nombre de la variable a asignar.
     * @param assignElement    El elemento <ASSG> que representa la asignación completa.
     * @param symbols          Mapa que contiene los símbolos y sus tipos.
     * @param manager          El gestor de la tabla de símbolos.
     * @throws Exception       Excepción general que puede ocurrir durante el procesamiento.
     */
    private static void processIdAssignment(Element idElement, Element assignElement, Map<String, TabSymbol> symbols, SymbolTableManager manager) throws Exception {
        // Obtener el contenido de texto del elemento <ID>
        String variableName = idElement.getTextContent();
        String typeCheck = symbols.get(variableName).getType();

        // Ahora, puede utilizar variableName según sea necesario para el identificador

        // Continuar procesando el segundo elemento hijo (expresión) en <ASSG>
        Element expressionElement = (Element) assignElement.getElementsByTagName("EXPR").item(0);
        if (expressionElement != null) {
            // Procesar la expresión utilizando checkSemanticExpr u otra lógica
            checkSemanticExpr(expressionElement, symbols, typeCheck, manager);
        } else {
            System.out.println("Error: No se encontró el elemento <EXPR> en la declaración de asignación.");
        }
    }

    /**
     * Procesa una asignación de elemento de arreglo (<ASSG>) cuando el primer elemento hijo es <ARR_ELEM>.
     *
     * @param arrElemElement   El elemento <ARR_ELEM> que contiene la asignación del elemento de arreglo.
     * @param assignElement    El elemento <ASSG> que representa la asignación completa.
     * @param symbols          Mapa que contiene los símbolos y sus tipos.
     * @param manager          El gestor de la tabla de símbolos.
     * @throws Exception       Excepción general que puede ocurrir durante el procesamiento.
     */
    private static void processArrElemAssignment(Element arrElemElement, Element assignElement, Map<String, TabSymbol> symbols, SymbolTableManager manager) throws Exception {
        // Obtener el contenido de texto del elemento <ID> dentro de <ARR_ELEM>
        String arrayName = arrElemElement.getElementsByTagName("ID").item(0).getTextContent();

        // Procesar el <EXPR> dentro de <ARR_ELEM> (tipo fijo: "int")
        Element arrElemExprElement = (Element) arrElemElement.getElementsByTagName("EXPR").item(0);
        if (arrElemExprElement != null) {
            checkSemanticExpr(arrElemExprElement, symbols, "int", manager);
        } else {
            System.out.println(Utils.ANSI_RED + "Error: No se encontró el elemento <EXPR> dentro de <ARR_ELEM> en la declaración de asignación." + Utils.ANSI_RESET);
        }

        // Continuar procesando el segundo elemento hijo (expresión) en <ASSG>
        Element exprElement = (Element) assignElement.getElementsByTagName("EXPR").item(1);
        if (exprElement != null) {
            // Procesar la expresión utilizando checkSemanticExpr u otra lógica
            String arrayType = symbols.get(arrayName).getType();
            checkSemanticExpr(exprElement, symbols, arrayType, manager);
        } else {
            System.out.println(Utils.ANSI_RED + "Error: No se encontró el elemento <EXPR> en la declaración de asignación." + Utils.ANSI_RESET);
        }
    }
}