package edu.uga.ccrc.ontology.glyco.databasebot.data;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlType
public class StructureFilter
{
    private String m_name = null;
    private String m_description = null;
    private String m_sequence = null;
    private Boolean m_reducing = Boolean.FALSE;
    private Boolean m_include = Boolean.FALSE;
    private Boolean m_exclude = Boolean.FALSE;
    
    public String getName()
    {
        return m_name;
    }
    @XmlAttribute(name = "name")
    public void setName(String a_name)
    {
        m_name = a_name;
    }
    public String getDescription()
    {
        return m_description;
    }
    @XmlAttribute(name = "description")
    public void setDescription(String a_description)
    {
        m_description = a_description;
    }
    public String getSequence()
    {
        return m_sequence;
    }
    @XmlValue
    public void setSequence(String a_sequence)
    {
        m_sequence = a_sequence;
    }
    public Boolean getReducing()
    {
        return m_reducing;
    }
    @XmlAttribute(name = "reducing")
    public void setReducing(Boolean a_reducing)
    {
        m_reducing = a_reducing;
    }
    public Boolean getInclude()
    {
        return m_include;
    }
    @XmlAttribute(name = "include")
    public void setInclude(Boolean a_include)
    {
        m_include = a_include;
    }
    public Boolean getExclude()
    {
        return m_exclude;
    }
    @XmlAttribute(name = "exclude")
    public void setExclude(Boolean a_exclude)
    {
        m_exclude = a_exclude;
    }
}
