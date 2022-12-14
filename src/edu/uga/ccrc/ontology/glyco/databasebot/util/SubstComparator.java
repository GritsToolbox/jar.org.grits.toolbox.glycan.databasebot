package edu.uga.ccrc.ontology.glyco.databasebot.util;

import java.util.Comparator;

import org.eurocarbdb.MolecularFramework.sugar.SubstituentType;

public class SubstComparator implements Comparator<SubstituentType> 
{
    public int compare(SubstituentType a_subst0, SubstituentType a_subst1) 
    {
        return a_subst0.getName().compareTo(a_subst1.getName());
    }
}
