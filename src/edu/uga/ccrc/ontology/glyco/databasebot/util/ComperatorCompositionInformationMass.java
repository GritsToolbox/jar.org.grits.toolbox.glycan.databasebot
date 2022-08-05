package edu.uga.ccrc.ontology.glyco.databasebot.util;

import java.util.Comparator;

import edu.uga.ccrc.ontology.glyco.databasebot.data.CompositionInformation;

public class ComperatorCompositionInformationMass implements Comparator<CompositionInformation> 
{
    public int compare(CompositionInformation a_object1, CompositionInformation a_object2) 
    {
        return a_object1.getMass().compareTo(a_object2.getMass());
    }
}
