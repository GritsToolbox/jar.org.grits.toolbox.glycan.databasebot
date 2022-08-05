package edu.uga.ccrc.ontology.glyco.databasebot.data;

import java.util.ArrayList;
import java.util.List;

public class MassInformation
{
    private List<GlycanInformation> m_members = new ArrayList<GlycanInformation>();
    private Double m_massMin = null;
    private Double m_massMax = null;
    private Double m_mass = null;
    private Double m_massPme = null;
    
    public MassInformation(double a_mass, double a_defiationPPM)
    {
        this.m_mass = a_mass;
        this.m_massMin = a_mass - a_mass * a_defiationPPM / 1000000D;
        this.m_massMax = a_mass + a_mass * a_defiationPPM / 1000000D;
    }
    public Double getMassMin()
    {
        return m_massMin;
    }
    public void setMassMin(Double a_massMin)
    {
        m_massMin = a_massMin;
    }
    public Double getMassMax()
    {
        return m_massMax;
    }
    public void setMassMax(Double a_massMax)
    {
        m_massMax = a_massMax;
    }
    public List<GlycanInformation> getMembers()
    {
        return m_members;
    }
    public void setMembers(List<GlycanInformation> a_members)
    {
        m_members = a_members;
    }
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
    public boolean isInRange(Double a_mass)
    {
        if ( a_mass < this.m_massMin || a_mass > this.m_massMax )
        {
            return false;
        }
        return true;
    }
    public boolean addMember(GlycanInformation a_member)
    {
        return this.m_members.add(a_member);
    }
}
