package edu.uga.ccrc.ontology.glyco.databasebot.datasource;

import java.util.List;

import org.eurocarbdb.MolecularFramework.io.SugarImporterException;
import org.eurocarbdb.MolecularFramework.io.GlycoCT.SugarExporterGlycoCTCondensed;
import org.eurocarbdb.MolecularFramework.io.Glyde.SugarImporterGlydeII;
import org.eurocarbdb.MolecularFramework.sugar.Sugar;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorException;

import edu.uga.ccrc.ontology.glyco.databasebot.data.DatabaseGenerationEvent;
import edu.uga.ccrc.ontology.glyco.databasebot.data.GlycanInformation;
import edu.uga.ccrc.ontology.glyco.databasebot.util.DBGenerator;
import edu.uga.ccrc.ontology.glyco.databasebot.util.InformationGenerator;
import edu.uga.ccrc.ontology.webapi.client.GlycanClient;
import edu.uga.ccrc.ontology.webapi.client.data.ListObject;
import edu.uga.ccrc.ontology.webapi.client.data.glycan.InformationDetail;
import edu.uga.ccrc.ontology.webapi.client.data.glycan.id.Glycan;
import edu.uga.ccrc.ontology.webapi.client.exception.ErrorMessageException;
import edu.uga.ccrc.ontology.webapi.client.exception.InvalidMessageExpetion;
import edu.uga.ccrc.ontology.webapi.client.exception.WebApiExecutionException;

public class GlycO extends StructureSource
{
    public void start() throws Exception  
    {
        // download the list of structures
        Integer t_counter = 1;
        InformationGenerator t_infoGen = new InformationGenerator();
        for (DBGenerator t_generator : this.m_generators)
        {
            t_generator.setInfoGen(t_infoGen);
        }
        GlycanClient t_glycanClient = new GlycanClient("http://glycomics.ccrc.uga.edu/ontologywebapi/");
        List<ListObject> response = t_glycanClient.getList();
        this.m_listener.setNumberStructures(response.size());
        for (ListObject t_listObject : response)
        {
            if ( t_counter <= GlycO.MAX_TEST_SIZE )
            {
                this.m_listener.setCurrentStructure(t_counter++, t_listObject.getName());
                Glycan t_glycan = null;
                GlycanInformation t_info = null;
                try
                {
                    t_glycan = t_glycanClient.get(t_listObject.getName(),InformationDetail.GLYCAN);
                    t_info = t_infoGen.createInfo(t_glycan.getId(),this.toGlycoCt(t_glycan.getSequence()));
                } 
                catch (ErrorMessageException | InvalidMessageExpetion | WebApiExecutionException e)
                {
                    DatabaseGenerationEvent t_event = DatabaseGenerationEvent.buildEvent(
                            t_listObject.getName(), 
                            DatabaseGenerationEvent.SEVERE, 
                            "Unable to download from GlycO web service", 
                            e.getMessage());
                    for (DBGenerator t_generator : this.m_generators)
                    {
                        t_generator.addEvent(t_event);
                    }
                } 
                catch (GlycoVisitorException | SugarImporterException e)
                {
                    DatabaseGenerationEvent t_event = DatabaseGenerationEvent.buildEvent(
                            t_listObject.getName(), 
                            DatabaseGenerationEvent.ERROR, 
                            "Unable to process glycan", 
                            e.getMessage());
                    for (DBGenerator t_generator : this.m_generators)
                    {
                        t_generator.addEvent(t_event);
                    }
                }
                for (DBGenerator t_generator : this.m_generators)
                {
                    t_generator.process(t_info);
                }
            }
        }
        for (DBGenerator t_generator : this.m_generators)
        {
            t_generator.finalizeDatabase();
        }
    }

    private String toGlycoCt(String a_sequence) throws SugarImporterException, GlycoVisitorException
    {
        SugarImporterGlydeII t_importer = new SugarImporterGlydeII();
        Sugar t_sugar = t_importer.parse(a_sequence);
        SugarExporterGlycoCTCondensed t_exporter = new SugarExporterGlycoCTCondensed();
        t_exporter.start(t_sugar);
        return t_exporter.getHashCode();
    }

}