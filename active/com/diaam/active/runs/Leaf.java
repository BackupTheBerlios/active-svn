/*
 * Main.java
 * * Ce n'est pas Main en anglais, c'est Main en français !
 *
 * $Log: Leaf.java,v $
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

import org.apache.log4j.Logger;

/**
 * @author 
 * <a href="mailto:herve.agnoux@diaam-informatique.com">Hervé Agnoux</a>
 *
 */
public class Leaf extends Memo
{
  protected Logger m_log;
  
  protected Leaf(Class pourElle)
  {
    m_log = Logger.getLogger(pourElle);
  } 
  
  public Leaf intro(String ceci)
  {
    addExpression(ceci);
    return this;
  }  
  
  /** @deprecated in inglish please ! */
  public Leaf dit(String ressource, Object valeur)
  {
    return say(ressource, valeur);
  }
  
  public Leaf say(String ressource, Object valeur)
  {
    add(ressource, valeur);
    return this;
  }  

  /** @deprecated in inglish please ! */
  public Leaf dit(String ressource, int valeur)
  {
    return say(ressource, valeur);
  }
  
  public Leaf say(String ressource, int valeur)
  {
    add(ressource, valeur);
    return this;
  }
  
  /** @deprecated in inglish please ! */
  public Leaf dit(String ressource, boolean valeur)
  {
    return say(ressource, valeur);
  }
  
  public Leaf say(String ressource, boolean valeur)
  {
    add(ressource, valeur);
    return this;
  }
  
  public void warn()
  {
    m_log.warn(m_buffer.toString());
    m_buffer.setLength(0);    
  }
  
  public void warn(Throwable bing)
  {
    m_log.warn(m_buffer.toString(), bing);
    m_buffer.setLength(0);    
  }
  
  public void debug()
  {
    m_log.debug(m_buffer.toString());
    m_buffer.setLength(0);
  }  
  
  public void info()
  {
    m_log.info(m_buffer.toString());
    m_buffer.setLength(0);
  }

  public boolean isDebugEnabled()
  {
    return m_log.isDebugEnabled();
  }
  
  public void outln()
  {
    System.out.println(fini());
  }
}
