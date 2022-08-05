package edu.uga.ccrc.ontology.glyco.databasebot.listener;

public class DBStatusListenerSystemOut implements DBStatusListener
{
    private Integer m_structureCount = 0;
    
    public void setNumberStructures(int a_size)
    {
        this.m_structureCount = a_size;
    }

    public void setCurrentStructure(Integer a_number, String a_id)
    {
        System.out.println("Processing " + a_number.toString() + " of " + this.m_structureCount.toString() + ": " + a_id);
    }

}
