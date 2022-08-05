package edu.uga.ccrc.ontology.glyco.databasebot.listener;

public interface DBStatusListener
{
    public void setNumberStructures(int a_size);

    public void setCurrentStructure(Integer a_number, String a_id);

}
