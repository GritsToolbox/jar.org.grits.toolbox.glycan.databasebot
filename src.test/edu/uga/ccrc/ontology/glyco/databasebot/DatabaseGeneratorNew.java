package edu.uga.ccrc.ontology.glyco.databasebot;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import edu.uga.ccrc.ontology.glyco.databasebot.data.StructureFilter;
import edu.uga.ccrc.ontology.glyco.databasebot.data.StructureFilterList;
import edu.uga.ccrc.ontology.glyco.databasebot.datasource.GlycO;
import edu.uga.ccrc.ontology.glyco.databasebot.datasource.StructureSource;
import edu.uga.ccrc.ontology.glyco.databasebot.listener.DBStatusListenerSystemOut;
import edu.uga.ccrc.ontology.glyco.databasebot.util.DBGenerator;

public class DatabaseGeneratorNew
{
    public static void main(String[] args) throws Exception
    {
        long t_startTime = System.currentTimeMillis();
        // get all generators 
        List<DBGenerator> t_generators = DatabaseGeneratorNew.createDatabaseDefinition();
        // download and process structures
//        StructureSource.MAX_TEST_SIZE = 30;
        GlycO t_datasouce = new GlycO();
        t_datasouce.setGenerators(t_generators);
        t_datasouce.setListener(new DBStatusListenerSystemOut());
        t_datasouce.start();
        // finished
        System.out.println("Finished writing files:" + Double.toString( ((double)(System.currentTimeMillis() - t_startTime))/60000D ) + " minutes"); 
    }

    private static List<DBGenerator> createDatabaseDefinition() throws JAXBException
    {
        List<DBGenerator> t_generators = new ArrayList<DBGenerator>();
        t_generators.add(DatabaseGeneratorNew.buildGenerator("All-Glycan", new ArrayList<StructureFilter>(),"All-Glycan.xml","All-Glycan.xls",false, "All structures from the GlycO ontology."));
        t_generators.add(DatabaseGeneratorNew.buildGenerator("N-Glycan", DatabaseGeneratorNew.readDatabaseDefinition("src.test/nglycan.xml"),"N-Glycan.xml","N-Glycan.xls",false, "N-Glycans from the GlycO ontology."));
        t_generators.add(DatabaseGeneratorNew.buildGenerator("O-Glycan", DatabaseGeneratorNew.readDatabaseDefinition("src.test/oglycan.xml"),"O-Glycan.xml","O-Glycan.xls",false, "O-Glycans from the GlycO ontology."));
        t_generators.add(DatabaseGeneratorNew.buildGenerator("GSL-Glycan", DatabaseGeneratorNew.readDatabaseDefinition("src.test/gsl.xml"),"GSL-Glycan.xml","GSL-Glycan.xls",false, "Glycosphingolipid glycans from the GlycO ontology."));
        t_generators.add(DatabaseGeneratorNew.buildGenerator("All-Glycan (Topology)", new ArrayList<StructureFilter>(),"All-Glycan-topology.xml","All-Glycan-topology.xls",true,"Only distinct topologies (no linkage information) from the GlycO ontology."));
        t_generators.add(DatabaseGeneratorNew.buildGenerator("N-Glycan (Topology)", DatabaseGeneratorNew.readDatabaseDefinition("src.test/nglycan.xml"),"N-Glycan-topology.xml","N-Glycan-topology.xls",true, "N-Glycan topologies (no linkage information) from the GlycO ontology."));
        t_generators.add(DatabaseGeneratorNew.buildGenerator("O-Glycan (Topology)", DatabaseGeneratorNew.readDatabaseDefinition("src.test/oglycan.xml"),"O-Glycan-topology.xml","O-Glycan-topology.xls",true, "O-Glycan topologies (no linkage information) from the GlycO ontology."));
        t_generators.add(DatabaseGeneratorNew.buildGenerator("GSL-Glycan (Topology)", DatabaseGeneratorNew.readDatabaseDefinition("src.test/gsl.xml"),"GSL-Glycan-topology.xml","GSL-Glycan-topology.xls",true, "Glycosphingolipid glycans topologies (no linkage information) from the GlycO ontology."));
        return t_generators;
    }

    private static DBGenerator buildGenerator(String a_dbName, List<StructureFilter> a_structureFilters, String a_databaseFileName, String a_reportFileName, boolean a_topology, String a_databaseDescription)
    {
        DBGenerator t_generator = new DBGenerator();
        t_generator.setDatabaseFileName(a_databaseFileName);
        t_generator.setDatabaseName(a_dbName);
        t_generator.setDatabaseDescription(a_databaseDescription);
        t_generator.setReportFileName(a_reportFileName);
        t_generator.setStructureFilters(a_structureFilters);
        t_generator.setTopology(a_topology);
        return t_generator;
    }

    private static List<StructureFilter> readDatabaseDefinition(String a_fileName) throws JAXBException
    {
        File t_file = new File(a_fileName);
        JAXBContext jaxbContext = JAXBContext.newInstance(StructureFilterList.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        return ((StructureFilterList) jaxbUnmarshaller.unmarshal(t_file)).getStructureFilters();
    }
}