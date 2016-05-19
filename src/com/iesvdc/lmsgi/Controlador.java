/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iesvdc.lmsgi;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import net.sf.saxon.s9api.DocumentBuilder;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.SchemaManager;
import net.sf.saxon.s9api.SchemaValidator;
import net.sf.saxon.s9api.Serializer;
import net.sf.saxon.s9api.WhitespaceStrippingPolicy;
import net.sf.saxon.s9api.XPathCompiler;
import net.sf.saxon.s9api.XPathSelector;
import net.sf.saxon.s9api.XdmItem;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmValue;
import net.sf.saxon.s9api.XsltCompiler;
import net.sf.saxon.s9api.XsltExecutable;
import net.sf.saxon.s9api.XsltTransformer;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @author matinal
 */
public class Controlador {
    private File file;
    private File fileXSD;
    private File fileDTD;
    private File fileXSL;
    private File fileHTML;

    

    public File getFileDTD() {
        return fileDTD;
    }

    public void setFileDTD(File fileDTD) {
        this.fileDTD = fileDTD;
    }

    public File getFileXSL() {
        return fileXSL;
    }

    public void setFileXSL(File fileXSL) {
        this.fileXSL = fileXSL;
    }

    public File getFileHTML() {
        return fileHTML;
    }

    public void setFileHTML(File fileHTML) {
        this.fileHTML = fileHTML;
    }

    public Controlador() {
        this.file = null;
        this.fileXSD = null;
        this.fileDTD = null;
        this.fileXSL = null;
        this.fileHTML = null;
    }    

    public Controlador(File file) {
        this.file = file;
        this.fileXSD = null;
        this.fileDTD = null;
        this.fileXSL = null;
        this.fileHTML = null;
    }

    public File getFileXSD() {
        return fileXSD;
    }

    public void setFileXSD(File fileXSD) {
        this.fileXSD = fileXSD;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
    public String xPathEvaluate(String stringXPath){
        String resultado="";
        try {
            
            Processor proc=new Processor(false);
            
            DocumentBuilder builder = proc.newDocumentBuilder();
            
            builder.setLineNumbering(true);
            builder.setWhitespaceStrippingPolicy(WhitespaceStrippingPolicy.ALL);
            
            XdmNode documentoXML = builder.build(file);
            XPathCompiler xpath = proc.newXPathCompiler();
            XPathSelector selector = xpath.compile(stringXPath).load();
            
            selector.setContextItem(documentoXML);
            XdmValue evaluate = selector.evaluate();
            
            for(XdmItem item : evaluate){
                
                resultado += item.getStringValue()+"\n";
            }
            
            
            
        } catch (SaxonApiException ex) {
            Logger.getLogger(Controlador.class.getName()).log(Level.SEVERE, null, ex);
        }
        return resultado;

    }
    public String ValidateXSD(File file, File fileXSD){
        String resultado="";
        if (file==null)
            resultado="Falta archivo XML";
        else if (fileXSD==null)
            resultado="Falta archivo XSD";
        else{
            try {
                Processor proc = new Processor(true);
                SchemaManager sm = proc.getSchemaManager();
                sm.load(new StreamSource(fileXSD));
                SchemaValidator validator = sm.newSchemaValidator();
                XdmNode source = proc.newDocumentBuilder().build(new StreamSource(file));
                proc.writeXdmValue(source, validator);
                resultado="Validación correcta";
            } catch (SaxonApiException ex) {

            }
        }
        return resultado;
    }
   /* String validateXSD(){
        String resultado="Validación XSD correcta";
        try {
            Document doc = DomUtil.parseXSD(this.file, this fileXSD);
        } catch (ParserConfigurationException | IOException | SAXException ex) {
            resultado=ex.getLocalizedMessage();
        }
        return resultado;
    }*/
    String validateDTD(){
        String resultado;
        if (file==null)
            resultado="Falta archivo XML";
        else{
            resultado="Validación DTD correcta";
            try {
                DomUtil.parse(file, true);
            } catch (ParserConfigurationException | IOException | SAXException ex) {
                Logger.getLogger(Controlador.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return resultado;
    }
    public static String xslTransform (
        File xmlFile,
        File xslFile,
        File htmlOut){
        String resultado="Transformación completada correctamente";
        if (xmlFile!=null && xslFile!=null && htmlOut!=null){
                try {
                Processor proc = new Processor(false);
                XsltCompiler comp = proc.newXsltCompiler();
                XsltExecutable exp = comp.compile(new StreamSource(xslFile));
                XdmNode source = proc.newDocumentBuilder().build(new StreamSource(xmlFile));
                Serializer out = proc.newSerializer(htmlOut);
                out.setOutputProperty(Serializer.Property.METHOD, "html");
                out.setOutputProperty(Serializer.Property.INDENT, "yes");
                XsltTransformer trans = exp.load();
                trans.setInitialContextNode(source);
                trans.setDestination(out);
                trans.transform();
            } catch (SaxonApiException ex) {
               // Logger.getLogger(Controlador.class.getName()).log(Level.SEVERE, null, ex);
                resultado=ex.getLocalizedMessage();
            }
        }
        else {
            resultado="Error procesando ficheros";
        }
        return resultado;

    }
    
}
