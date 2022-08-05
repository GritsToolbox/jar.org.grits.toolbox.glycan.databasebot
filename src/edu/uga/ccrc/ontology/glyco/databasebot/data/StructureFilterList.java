package edu.uga.ccrc.ontology.glyco.databasebot.data;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "structure_filter_list")
public class StructureFilterList
{
    private List<StructureFilter> m_structureFilters = new ArrayList<StructureFilter>();

    public List<StructureFilter> getStructureFilters()
    {
        return m_structureFilters;
    }
    @XmlElement(name = "structure_filter")
    public void setStructureFilters(List<StructureFilter> a_structureFilters)
    {
        m_structureFilters = a_structureFilters;
    }
}
