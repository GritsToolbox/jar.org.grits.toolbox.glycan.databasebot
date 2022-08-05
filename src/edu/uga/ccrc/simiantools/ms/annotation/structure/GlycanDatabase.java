package edu.uga.ccrc.simiantools.ms.annotation.structure;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name="database")
public class GlycanDatabase
{
    private String m_name = null;
    @XmlElement(name="glycan")
    private List<GlycanStructure> m_structures = new ArrayList<GlycanStructure>();
    private String m_description = null;
    private Integer m_structureCount = null;
    public String getName()
    {
        return m_name;
    }
    @XmlAttribute
    public void setName(String a_name)
    {
        m_name = a_name;
    }
    @XmlTransient
    public List<GlycanStructure> getStructures()
    {
        return m_structures;
    }
    public void setStructures(List<GlycanStructure> a_structures)
    {
        m_structures = a_structures;
    }
    
    public List<String> availableDatabases(){
    	try{
    	final File folder = new File("./databases");
    	List<String> files = new ArrayList<String>();
    	for (final File fileEntry : folder.listFiles()) {
                files.add(fileEntry.getName().substring(0, fileEntry.getName().indexOf(".xml")));
        }
    	return files;
    	}catch(Exception e){
    		e.printStackTrace();
    		return null;
    	}
    }
    public String getDescription()
    {
        return m_description;
    }
    @XmlAttribute
    public void setDescription(String a_description)
    {
        m_description = a_description;
    }
    public Integer getStructureCount()
    {
        return m_structureCount;
    }
    @XmlAttribute
    public void setStructureCount(Integer a_structureCount)
    {
        m_structureCount = a_structureCount;
    }
}
