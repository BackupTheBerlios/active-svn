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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import org.apache.log4j.Logger;

/**
 * A logger for an object with a long cycle of life. Be careful : I supose
 * this class will become a real reporter, and not only a logger.
 *
 * @author 
 * <a href="mailto:herve.agnoux@diaam-informatique.com">Hervé Agnoux</a>
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
      m_allReporters.add(new WeakReference(this));
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

  /**
   * If this reporter is no longuer in use. The reporters use a WeakReference
   * to conserve the list of reporters, so this method is not strictly 
   * obligatory.
   */
  public void dispose()
  {
    synchronized (m_allReporters)
    {
      WeakReference[] refs;
      
      refs = (WeakReference[])m_allReporters.toArray(
              new WeakReference[m_allReporters.size()]);
      for (int i = 0; i < refs.length; i++)
      {
        WeakReference ref;
        Object obj;
        
        ref = refs[i];
        obj = ref.get();
        if (obj == null)
          m_allReporters.remove(ref);
        else if (obj == this)
          m_allReporters.remove(ref);
      }
    }
  }
  
  public Collection reporters()
  {
    ArrayList list;
    
    list = new ArrayList();
    synchronized (m_allReporters)
    {
      WeakReference[] refs;
      
      refs = (WeakReference[])m_allReporters.toArray(
              new WeakReference[m_allReporters.size()]);
      for (int i = 0; i < refs.length; i++)
      {
        Object obj;
        
        obj = refs[i].get();
        if (obj == null)
          m_allReporters.remove(refs[i]);
        else
          list.add(obj);
      }
      return list;
    }
  }
  
  public Object getObject()
  {
    return m_object;
  }
}
