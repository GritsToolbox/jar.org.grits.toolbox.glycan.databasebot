package edu.uga.ccrc.ontology.glyco.databasebot.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CompositionInformation
{
    private String m_compositionString = null;
    private List<GlycanInformation> m_members = new ArrayList<GlycanInformation>();
    private HashMap<String,Integer> m_composition = new HashMap<String,Integer>();
    private Double m_mass = null;
    private Double m_massPme = null;
    public Double getMass()
    {
        return m_mass;
    }
    public void setMass(Double a_mass)
    {
        m_mass = a_mass;
    }
    public Double getMassPme()
    {
        return m_massPme;
    }
    public void setMassPme(Double a_massPme)
    {
        m_massPme = a_massPme;
    }
    public String getCompositionName()
    {
        return m_compositionString;
    }
    public void setCompositionString(String a_compositionString)
    {
        m_compositionString = a_compositionString;
    }
    public List<GlycanInformation> getMembers()
    {
        return m_members;
    }
    public void setMembers(List<GlycanInformation> a_members)
    {
        m_members = a_members;
    }
    public HashMap<String, Integer> getComposition()
    {
        return m_composition;
    }
    public void setComposition(HashMap<String, Integer> a_composition)
    {
        m_composition = a_composition;
    }
    public boolean addMember(GlycanInformation a_glycanInformation)
    {
        return this.m_members.add(a_glycanInformation);
    }
}
