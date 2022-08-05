package edu.uga.ccrc.ontology.glyco.databasebot.io;

import java.io.File;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import edu.uga.ccrc.simiantools.ms.annotation.structure.GlycanDatabase;
import edu.uga.ccrc.simiantools.ms.annotation.structure.GlycanStructure;

public class DatabaseWriter
{

    public static void writeDatabase(List<GlycanStructure> a_structures, String a_databaseName, String a_description, File a_file) throws JAXBException
    {
        JAXBContext t_context = JAXBContext.newInstance(GlycanDatabase.class);
        Marshaller t_marshaller = t_context.createMarshaller();

        GlycanDatabase t_db = new GlycanDatabase();
        t_db.setName(a_databaseName);
        t_db.setDescription(a_description);
        t_db.setStructureCount(a_structures.size());
        t_db.setStructures(a_structures);
        t_context = JAXBContext.newInstance(GlycanDatabase.class);
        t_marshaller = t_context.createMarshaller();

        // output pretty printed
        t_marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        t_marshaller.marshal(t_db, a_file);
    }

}
