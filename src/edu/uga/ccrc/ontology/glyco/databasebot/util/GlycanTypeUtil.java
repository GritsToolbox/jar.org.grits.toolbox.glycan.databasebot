package edu.uga.ccrc.ontology.glyco.databasebot.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eurocarbdb.MolecularFramework.io.SugarImporterException;
import org.eurocarbdb.MolecularFramework.io.GlycoCT.SugarImporterGlycoCTCondensed;
import org.eurocarbdb.MolecularFramework.sugar.Sugar;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class GlycanTypeUtil
{
    @SuppressWarnings("unchecked")
    public static HashMap<String, List<Sugar>> getGlycanTypes(InputStream a_file) throws JDOMException, IOException
    {
        HashMap<String, List<Sugar>> t_result = new HashMap<String, List<Sugar>>();
        Document t_objDocument = null;
        SAXBuilder t_builder = new SAXBuilder();
        t_objDocument = t_builder.build(a_file);
        Element t_root = t_objDocument.getRootElement();
        if ( !t_root.getName().equals("glycan_type_definition") )
        {
            throw new JDOMException("Missing tag glycan_type_definition in Glycan type definiton.");
        }
        List<Element> t_children = t_root.getChildren("glycan_type");
        try
        {
            for (Element t_element : t_children)
            {
                t_result.put(t_element.getAttributeValue("name"),GlycanTypeUtil.getSingleGlycanType(t_element));
            }
        } 
        catch (SugarImporterException t_exception)
        {
            throw new IOException("Error in Glycan type definition file: " + t_exception.getMessage(),t_exception);
        }
        return t_result;
    }

    @SuppressWarnings("unchecked")
    private static List<Sugar> getSingleGlycanType(Element a_element) throws SugarImporterException
    {
        SugarImporterGlycoCTCondensed t_importer = new SugarImporterGlycoCTCondensed();
        List<Sugar> t_result = new ArrayList<Sugar>();
        List<Element> t_children = a_element.getChildren("sequence");
        for (Element t_element : t_children)
        {
            String t_sequence = t_element.getValue();
            t_result.add(t_importer.parse(t_sequence));
        }
        return t_result;
    }
}
