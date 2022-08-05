package edu.uga.ccrc.ontology.glyco.databasebot.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.eurocarbdb.MolecularFramework.io.SugarImporterException;
import org.eurocarbdb.MolecularFramework.io.GlycoCT.SugarExporterGlycoCTCondensed;
import org.eurocarbdb.MolecularFramework.io.GlycoCT.SugarImporterGlycoCTCondensed;
import org.eurocarbdb.MolecularFramework.sugar.GlycoconjugateException;
import org.eurocarbdb.MolecularFramework.sugar.Sugar;
import org.eurocarbdb.MolecularFramework.util.similiarity.SearchEngine.SearchEngine;
import org.eurocarbdb.MolecularFramework.util.similiarity.SearchEngine.SearchEngineException;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorException;

import edu.uga.ccrc.ontology.glyco.databasebot.data.DatabaseGenerationEvent;
import edu.uga.ccrc.ontology.glyco.databasebot.data.GlycanInformation;
import edu.uga.ccrc.ontology.glyco.databasebot.data.StructureFilter;
import edu.uga.ccrc.ontology.glyco.databasebot.io.DatabaseWriter;
import edu.uga.ccrc.ontology.glyco.databasebot.io.GlycOReportXLS;
import edu.uga.ccrc.simiantools.ms.annotation.structure.GlycanStructure;

public class DBGenerator
{
    private String m_databaseFileName = null;
    private String m_reportFileName = null;
    private String m_databaseDescription = null;
    private boolean m_topology = false;
    private List<StructureFilter> m_structureFilters = new ArrayList<StructureFilter>();
    private String m_databaseName = null;
    private List<GlycanInformation> m_finalList = new ArrayList<GlycanInformation>();
    private HashMap<String, Boolean> m_uniqueStructures = new HashMap<String,Boolean>(); 
    private List<DatabaseGenerationEvent> m_events = new ArrayList<DatabaseGenerationEvent>();
    private InformationGenerator m_infoGen = null;

    public InformationGenerator getInfoGen()
    {
        return m_infoGen;
    }

    public void setInfoGen(InformationGenerator a_infoGen)
    {
        m_infoGen = a_infoGen;
    }

    public List<DatabaseGenerationEvent> getEvents()
    {
        return m_events;
    }

    public void setEvents(List<DatabaseGenerationEvent> a_events)
    {
        m_events = a_events;
    }

    public boolean addEvent(DatabaseGenerationEvent a_event)
    {
        return this.m_events.add(a_event);
    }

    public String getDatabaseFileName()
    {
        return m_databaseFileName;
    }

    public void setDatabaseFileName(String a_databaseFileName)
    {
        m_databaseFileName = a_databaseFileName;
    }

    public String getReportFileName()
    {
        return m_reportFileName;
    }

    public void setReportFileName(String a_reportFileName)
    {
        m_reportFileName = a_reportFileName;
    }

    public boolean isTopology()
    {
        return m_topology;
    }

    public void setTopology(boolean a_topology)
    {
        m_topology = a_topology;
    }

    public List<StructureFilter> getStructureFilters()
    {
        return m_structureFilters;
    }

    public void setStructureFilters(List<StructureFilter> a_structureFilters)
    {
        m_structureFilters = a_structureFilters;
    }

    public String getDatabaseName()
    {
        return m_databaseName;
    }

    public void setDatabaseName(String a_databaseName)
    {
        m_databaseName = a_databaseName;
    }

    public void process(GlycanInformation a_glycanInfo)
    {
        GlycanInformation t_glycanInfo = a_glycanInfo;
        try
        {
            if ( this.validGlycan(t_glycanInfo) )
            {
                try
                {
                    if ( this.m_topology )
                    {
                        t_glycanInfo = this.filterTopology(t_glycanInfo);
                    }
                    if ( this.m_uniqueStructures.get(t_glycanInfo.getGlycoCT()) == null)
                    {
                        this.m_uniqueStructures.put(t_glycanInfo.getGlycoCT(), Boolean.TRUE);
                        this.m_finalList.add(t_glycanInfo);
                    }
                } 
                catch (GlycoVisitorException | SugarImporterException e)
                {
                    DatabaseGenerationEvent t_event = DatabaseGenerationEvent.buildEvent(
                            a_glycanInfo.getId(), 
                            DatabaseGenerationEvent.ERROR, 
                            "Unable to create topology", 
                            e.getMessage());
                    this.m_events.add(t_event);
                }
            }
        }
        catch (SugarImporterException | GlycoVisitorException | GlycoconjugateException | SearchEngineException e)
        {
            DatabaseGenerationEvent t_event = DatabaseGenerationEvent.buildEvent(
                    a_glycanInfo.getId(), 
                    DatabaseGenerationEvent.ERROR, 
                    "Unable to match with filter settings", 
                    e.getMessage());
            this.m_events.add(t_event);
        }
    }

    private GlycanInformation filterTopology(GlycanInformation a_glycanInfo) throws GlycoVisitorException, SugarImporterException
    {
        SugarImporterGlycoCTCondensed t_importerCT = new SugarImporterGlycoCTCondensed();
        Sugar t_sugar = t_importerCT.parse(a_glycanInfo.getGlycoCT());
        GlycoVisitorTopology t_visitorTopology = new GlycoVisitorTopology();
        t_visitorTopology.start(t_sugar);
        SugarExporterGlycoCTCondensed t_exporter = new SugarExporterGlycoCTCondensed();
        t_exporter.start(t_sugar);
        GlycanInformation t_clone = this.m_infoGen.createInfo(a_glycanInfo.getId() + "-T", t_exporter.getHashCode());
        return t_clone;
    }

    private boolean validGlycan(GlycanInformation a_glycanInfo) throws SugarImporterException, GlycoVisitorException, GlycoconjugateException, SearchEngineException
    {
        if ( this.m_structureFilters.size() == 0 )
        {
            return true;
        }
        SugarImporterGlycoCTCondensed t_importer = new SugarImporterGlycoCTCondensed();
        Sugar t_sugar = t_importer.parse(a_glycanInfo.getGlycoCT());
        for (StructureFilter t_filter : this.m_structureFilters)
        {
            Sugar t_subStructure = t_importer.parse(t_filter.getSequence());
            SearchEngine search = new SearchEngine();
            if ( t_filter.getReducing() )
            {
                search.restrictToReducingEnds();
            }
            search.setQueryStructure(t_subStructure);
            search.setQueriedStructure(t_sugar);
            search.match();
            if (search.isExactMatch())
            {
                if ( t_filter.getExclude() )
                {
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    public void finalizeDatabase() throws JAXBException, SugarImporterException, GlycoVisitorException, IOException
    {
        List<GlycanStructure> t_databaseStructures = this.createStructureList();
        DatabaseWriter.writeDatabase(t_databaseStructures, this.m_databaseName, this.m_databaseDescription, new File(this.m_databaseFileName));
        // write report
        GlycOReportXLS t_writer = new GlycOReportXLS();
        t_writer.write(this.m_reportFileName, this.m_finalList);
    }

    private List<GlycanStructure> createStructureList()
    {
        List<GlycanStructure> t_list = new ArrayList<GlycanStructure>();
        for (GlycanInformation t_glycanInformation : this.m_finalList)
        {
            GlycanStructure t_structure = new GlycanStructure();
            t_structure.setId(t_glycanInformation.getId());
            t_structure.setSequence(t_glycanInformation.getGwb());
            t_list.add(t_structure);
        }
        return t_list;
    }

    public String getDatabaseDescription()
    {
        return m_databaseDescription;
    }

    public void setDatabaseDescription(String a_databaseDescription)
    {
        m_databaseDescription = a_databaseDescription;
    }

}