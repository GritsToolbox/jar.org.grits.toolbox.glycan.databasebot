package edu.uga.ccrc.ontology.glyco.databasebot.datasource;

import java.util.ArrayList;
import java.util.List;

import edu.uga.ccrc.ontology.glyco.databasebot.listener.DBStatusListener;
import edu.uga.ccrc.ontology.glyco.databasebot.util.DBGenerator;

public abstract class StructureSource
{
    public static Integer MAX_TEST_SIZE = Integer.MAX_VALUE;

    protected List<DBGenerator> m_generators = new ArrayList<DBGenerator>();
    protected DBStatusListener m_listener = null;

    public DBStatusListener getListener()
    {
        return m_listener;
    }

    public void setListener(DBStatusListener a_listener)
    {
        m_listener = a_listener;
    }

    public List<DBGenerator> getGenerators()
    {
        return m_generators;
    }

    public void setGenerators(List<DBGenerator> a_generators)
    {
        m_generators = a_generators;
    }

    public boolean addGenerator(DBGenerator a_generator)
    {
        return this.m_generators.add(a_generator);
    }

    public abstract void start() throws Exception;
}
