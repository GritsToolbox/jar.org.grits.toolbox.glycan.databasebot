package edu.uga.ccrc.ontology.glyco.databasebot.data;

import java.util.HashMap;

public class GlycanInformation
{
    private String m_id = null;
    private HashMap<String, Integer> m_composition = null;
    private Double m_mass = null;
    private Double m_massPme = null;
    private String m_compositionName = null;
    private String m_glycoCT = null;
    private String m_gwb = null;

    public String getId()
    {
        return this.m_id;
    }
    public void setId(String a_id)
    {
        this.m_id = a_id;
    }
    public HashMap<String, Integer> getComposition()
    {
        return this.m_composition;
    }
    public void setComposition(HashMap<String, Integer> a_composition)
    {
        this.m_composition = a_composition;
    }
    public void setMass(Double mass) {
        m_mass = mass;
    }
    public Double getMass() {
        return m_mass;
    }
    public Double getMassPme()
    {
        return m_massPme;
    }
    public void setMassPme(Double massPme)
    {
        this.m_massPme = massPme;
    }
    public String getCompositionName()
    {
        return m_compositionName;
    }
    public void setCompositionName(String a_compositionName)
    {
        m_compositionName = a_compositionName;
    }
    public String getGwb()
    {
        return m_gwb;
    }
    public void setGwb(String a_gwb)
    {
        m_gwb = a_gwb;
    }

    public GlycanInformation clone()
    {
        GlycanInformation t_result = new GlycanInformation();
        t_result.setComposition(this.m_composition);
        t_result.setCompositionName(this.m_compositionName);
        t_result.setGwb(this.m_gwb);
        t_result.setId(this.m_id);
        t_result.setMass(this.m_mass);
        t_result.setMassPme(this.m_massPme);
        t_result.setGlycoCT(this.m_glycoCT);
        return t_result;
    }
    public String getGlycoCT()
    {
        return m_glycoCT;
    }
    public void setGlycoCT(String a_glycoCT)
    {
        m_glycoCT = a_glycoCT;
    }
}
