package edu.uga.ccrc.simiantools.ms.annotation.structure;


import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlType
public class GlycanStructure
{
    private String m_id = null;    
    private String m_sequence = null;
    private String m_sequenceFormat = null;
    private String m_GWBSequence = null;
    public String getId()
    {
        return m_id;
    }
    
    @XmlAttribute(name="id")
    public void setId(String a_id)
    {
        m_id = a_id;
    }
    
    public String getSequenceFormat()
    {
        return m_sequenceFormat;
    }
    
    @XmlAttribute(name="sequenceFormat")
    public void setSequenceFormat(String a_sequenceFormat)
    {
    	m_sequenceFormat = a_sequenceFormat;
    }
    
    public String getSequence()
    {
        return m_sequence;
    }
    
    @XmlAttribute(name="sequence")
    public void setSequence(String a_sequence)
    {
        m_sequence = a_sequence;
    }
	public String getGWBSequence() {
		return m_GWBSequence;
	}
	
	@XmlAttribute(name="GWBSequence")
	public void setGWBSequence(String GWBSequence) {
		this.m_GWBSequence = GWBSequence;
	}
	
	@Override
	public boolean equals(Object obj) {
		if( ! (obj instanceof GlycanStructure) )
			return false;
		
		GlycanStructure other = (GlycanStructure) obj;
		if( (m_id != null && other.getId() == null) ||
			(m_id == null && other.getId() != null) ||
			! m_id.equals(other.getId()) ) 
			return false;
		
		if( (m_sequence != null && other.getSequence() == null) ||
				(m_sequence == null && other.getSequence() != null) ||
				! m_sequence.equals(other.getSequence()) ) 
				return false;

		if( (m_GWBSequence != null && other.getGWBSequence() == null) ||
				(m_GWBSequence == null && other.getGWBSequence() != null) ||
				! m_GWBSequence.equals(other.getGWBSequence()) ) 
				return false;

		return true;
	}
	
}
