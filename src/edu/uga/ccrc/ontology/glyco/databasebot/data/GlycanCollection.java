package edu.uga.ccrc.ontology.glyco.databasebot.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GlycanCollection
{
    private HashMap<String,List<GlycanInformation>> m_collection = new HashMap<String,List<GlycanInformation>>();

    public HashMap<String,List<GlycanInformation>> getCollection()
    {
        return m_collection;
    }

    public void setCollection(HashMap<String,List<GlycanInformation>> a_collection)
    {
        m_collection = a_collection;
    }
    
    public void addGlycan(String a_glycoTree, GlycanInformation t_glycan)
    {
        List<GlycanInformation> t_list = this.m_collection.get(a_glycoTree);
        if ( t_list == null )
        {
            t_list = new ArrayList<GlycanInformation>();
            this.m_collection.put(a_glycoTree, t_list);
        }
        t_list.add(t_glycan);
    }
}
