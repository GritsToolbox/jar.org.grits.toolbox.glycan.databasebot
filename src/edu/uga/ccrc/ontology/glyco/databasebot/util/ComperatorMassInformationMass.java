package edu.uga.ccrc.ontology.glyco.databasebot.util;

import java.util.Comparator;

import edu.uga.ccrc.ontology.glyco.databasebot.data.MassInformation;

public class ComperatorMassInformationMass implements Comparator<MassInformation> 
{
    public int compare(MassInformation a_object1, MassInformation a_object2) 
    {
        return a_object1.getMass().compareTo(a_object2.getMass());
    }
}
