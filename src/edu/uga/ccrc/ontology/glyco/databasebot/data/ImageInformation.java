package edu.uga.ccrc.ontology.glyco.databasebot.data;

public class ImageInformation
{
    private byte[] m_image = null;
    private Integer m_width = null;
    private Integer m_height = null;
    public byte[] getImage()
    {
        return m_image;
    }
    public void setImage(byte[] a_image)
    {
        m_image = a_image;
    }
    public Integer getWidth()
    {
        return m_width;
    }
    public void setWidth(Integer a_width)
    {
        m_width = a_width;
    }
    public Integer getHeight()
    {
        return m_height;
    }
    public void setHeight(Integer a_height)
    {
        m_height = a_height;
    }
}
