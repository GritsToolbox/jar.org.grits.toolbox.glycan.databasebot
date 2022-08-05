package edu.uga.ccrc.ontology.glyco.databasebot.data;

public class DatabaseGenerationEvent
{
    public static final String ERROR = "error";
    public static final String WARNING = "warning";
    public static final String NOTICE = "notice";
    public static final String SEVERE = "severe";

    private String m_id = null;
    private String m_eventType = null;
    private String m_message = null;
    private String m_description = null;

    public String getId()
    {
        return m_id;
    }
    public void setId(String a_id)
    {
        m_id = a_id;
    }
    public String getEventType()
    {
        return m_eventType;
    }
    public void setEventType(String a_eventType)
    {
        m_eventType = a_eventType;
    }
    public String getMessage()
    {
        return m_message;
    }
    public void setMessage(String a_message)
    {
        m_message = a_message;
    }
    public String getDescription()
    {
        return m_description;
    }
    public void setDescription(String a_description)
    {
        m_description = a_description;
    }

    public static DatabaseGenerationEvent buildEvent(String a_id, String a_eventType, String a_message, String a_description)
    {
        DatabaseGenerationEvent t_event = new DatabaseGenerationEvent();
        t_event.setId(a_id);
        t_event.setDescription(a_description);
        t_event.setEventType(a_eventType);
        t_event.setMessage(a_message);
        return t_event;
    }
}
