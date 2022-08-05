package edu.uga.ccrc.ontology.glyco.databasebot.util;

import java.util.Comparator;

import edu.uga.ccrc.ontology.glyco.databasebot.data.GlycanInformation;

public class ComperatorGlycanInformationMass implements Comparator<GlycanInformation> 
{
    public int compare(GlycanInformation a_object1, GlycanInformation a_object2) 
    {
        return a_object1.getMass().compareTo(a_object2.getMass());
    }
}
