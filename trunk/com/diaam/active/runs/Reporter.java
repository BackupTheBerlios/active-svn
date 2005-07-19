/*
 * MainCourante.java
 *
 * $Log: Reporter.java,v $
 * Revision 1.3  2005/06/14 18:15:16  herve
 * Meilleure organisation des logs
 *
 * Revision 1.2  2005/05/26 13:42:38  herve
 * Amelioration logs, introduction beans et sure
 *
 * Revision 1.1.1.1  2005/05/10 13:14:00  herve
 * activons nous, mais en reflechissant
 *
 *
 */

package com.diaam.active.runs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import org.apache.log4j.Logger;

/**
 * @author 
 * <a href="mailto:herve.agnoux@diaam-informatique.com">Hervé Agnoux</a>
 *
 *
 */
public final class Reporter extends Leaf
{
  private static ArrayList m_allReporters = new ArrayList();
  
  private Object m_object;
  private HashMap m_logs;
  
  public Reporter(Object pourLui)
  {
    super(pourLui.getClass());
    m_object = pourLui;
    m_logs = new HashMap();
    synchronized (m_allReporters)
    {
      m_allReporters.add(this);
    }
  }
  
  public void warn(String explications, Throwable exception)
  {
    m_log.warn(form(explications), exception);
  }
  
  private String form(String explications)
  {
    return "{"+m_object+"}."+explications;
  }
  
  public Leaf get(Class pourLui)
  {
    Leaf m;
    
    m = (Leaf)m_logs.get(pourLui);
    if (m == null)
    {
      m = new Leaf(pourLui);
      m_logs.put(pourLui, m);
    }
    return m;
  }
  
  public void dispose()
  {
    synchronized (m_allReporters)
    {
      m_allReporters.remove(this);
    }
  }
  
  public Collection reporters()
  {
    synchronized (m_allReporters)
    {
      return (Collection)m_allReporters.clone();
    }
  }
  
  public Object getObject()
  {
    return m_object;
  }
}
