package edu.uga.ccrc.ontology.glyco.databasebot.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.eurocarbdb.MolecularFramework.io.SugarImporterException;
import org.eurocarbdb.MolecularFramework.io.GlycoCT.SugarImporterGlycoCTCondensed;
import org.eurocarbdb.MolecularFramework.io.namespace.GlycoVisitorFromGlycoCT;
import org.eurocarbdb.MolecularFramework.sugar.Sugar;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorException;
import org.eurocarbdb.application.glycanbuilder.Glycan;
import org.eurocarbdb.application.glycanbuilder.GlycanRendererAWT;
import org.eurocarbdb.application.glycanbuilder.GlycoCTParser;
import org.eurocarbdb.application.glycanbuilder.IonCloud;
import org.eurocarbdb.application.glycanbuilder.MassOptions;
import org.eurocarbdb.application.glycanbuilder.ResidueDictionary;
import org.eurocarbdb.application.glycoworkbench.GlycanWorkspace;
import org.eurocarbdb.resourcesdb.Config;
import org.eurocarbdb.resourcesdb.GlycanNamescheme;
import org.eurocarbdb.resourcesdb.io.MonosaccharideConversion;
import org.eurocarbdb.resourcesdb.io.MonosaccharideConverter;

import edu.uga.ccrc.ontology.glyco.databasebot.data.GlycanInformation;

public class InformationGenerator
{
    protected GlycanWorkspace m_gwb = new GlycanWorkspace(null,false,new GlycanRendererAWT());
    private MonosaccharideConversion m_msdb = null;
    private MassOptions m_massOptions = null;

    public InformationGenerator()
    {
        Config t_objConf = new Config();
        this.m_msdb = new MonosaccharideConverter(t_objConf);

        this.m_massOptions = new MassOptions();
        this.m_massOptions.setDerivatization( MassOptions.NO_DERIVATIZATION );
        this.m_massOptions.setIsotope( MassOptions.ISOTOPE_MONO );
        this.m_massOptions.setReducingEndType( ResidueDictionary.findResidueType("freeEnd") );
        this.m_massOptions.ION_CLOUD = new IonCloud();
        this.m_massOptions.NEUTRAL_EXCHANGES = new IonCloud();
    }

    public GlycanInformation createInfo(String a_id, String a_sequence) throws GlycoVisitorException, SugarImporterException
    {
        // parse glyde
        SugarImporterGlycoCTCondensed t_importer = new SugarImporterGlycoCTCondensed();
        Sugar t_sugar = t_importer.parse(a_sequence);
        // calculate composition
        GlycoVisitorComposition t_visitor = new GlycoVisitorComposition();
        t_visitor.start(t_sugar);
        HashMap<String, Integer> t_composition = t_visitor.getComposition();
        String t_compositionName = this.getCompositionString(t_composition);
        // calculate mass
        Glycan t_glycan = null;
        double t_mass = 0;
        double t_massPme = 0;
        try 
        {
            t_glycan = this.sugarToGlycan(t_sugar);
            t_mass = this.getMass(t_glycan, MassOptions.NO_DERIVATIZATION);
            t_massPme = this.getMass(t_glycan, MassOptions.PERMETHYLATED);
        } 
        catch (Exception e) 
        {
            throw new GlycoVisitorException("Unable to load glycan to GWB: " + e.getMessage(),e);
        }
        GlycanInformation t_info = new GlycanInformation();
        t_info.setComposition(t_composition);
        t_info.setId(a_id);
        t_info.setMass(t_mass);
        t_info.setMassPme(t_massPme);
        t_info.setCompositionName(t_compositionName);
        t_info.setGwb(t_glycan.toString());
        t_info.setGlycoCT(a_sequence);
        return t_info;
    }

    private double getMass(Glycan a_glycan, String a_derivatization) 
    {
        this.m_massOptions.setDerivatization(a_derivatization);
        a_glycan.setMassOptions(this.m_massOptions);
        return a_glycan.computeMass();
    }

    private String getCompositionString(HashMap<String, Integer> a_composition)
    {
        String t_result = "";
        List<String> t_list = new ArrayList<String>();
        for (String t_string : a_composition.keySet())
        {
            t_list.add(t_string);
        }
        Collections.sort(t_list);
        for (String t_string : t_list)
        {
            t_result += InformationGenerator.formatComposition(t_string) + a_composition.get(t_string).toString() + " ";
        }
        return t_result;
    }

    private Glycan sugarToGlycan(Sugar a_sugar) throws Exception
    {
        GlycoVisitorFromGlycoCT t_visFromGlycoCT = new GlycoVisitorFromGlycoCT( this.m_msdb );
        t_visFromGlycoCT.setNameScheme(GlycanNamescheme.GWB);
        return GlycoCTParser.fromSugar(a_sugar,this.m_msdb,t_visFromGlycoCT,new MassOptions(),true);
    }

    public static String formatComposition(String a_string) 
    {
        String t_string = a_string;
        if ( a_string.equals("dnonA-ulop-n-acetyl"))
        {
            t_string = "NeuAc";
        }
        else if ( a_string.equals("hex-n-acetyl"))
        {
            t_string = "HexNAc";
        }
        else if ( a_string.equals("dnonA-ulop"))
        {
            t_string = "KDN";
        }
        else if ( a_string.equals("dnonA-ulop-n-glycolyl"))
        {
            t_string = "NeuGc";
        }
        else if ( a_string.equals("dhex"))
        {
            t_string = "dHex";
        }
        else if ( a_string.equals("hex"))
        {
            t_string = "Hex";
        }
        else
        {
            //            System.out.println(a_string);
        }
        return t_string;
    }

}