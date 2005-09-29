/*
 * Memo.java
 *
 * Created on 14 juin 2005, 19:07
 *
 * $Log: Memo.java,v $
 * Revision 1.1  2005/07/19 07:04:45  herve
 * Commit pour passage à SVN.
 *
 *
 */

package com.diaam.active.runs;

/**
 * A sort of string to keep the logs messages.
 *
 * @author
 * <a href="mailto:herve.agnoux@diaam-informatique.com">Hervé Agnoux</a>
 *
 */
public class Memo
{  
  private StringBuffer m_buffer;
  
  public Memo()
  {
    m_buffer = new StringBuffer();
  }
  
  public Memo addExpression(String expression)
  {
    m_buffer.append(expression);
    m_buffer.append(' ');
    return this;    
  }
  
  public Memo add(String ressource, Object valeur)
  {
    m_buffer.append(ressource);
    m_buffer.append('=');
    if (valeur == null)
      m_buffer.append("null");
    else
      m_buffer.append(valeur.toString());
    m_buffer.append(", ");
    return this;
  }  

  public Memo add(String ressource, int valeur)
  {
    m_buffer.append(ressource);
    m_buffer.append('=');
    m_buffer.append(valeur);
    m_buffer.append(", ");
    return this;
  }
  
  public Memo add(String ressource, boolean valeur)
  {
    m_buffer.append(ressource);
    m_buffer.append('=');
    m_buffer.append(valeur);
    m_buffer.append(", ");
    return this;
  }
  
  /**
   * Délivre le message accumulé et vide le conteneur.
   *
   * @deprecated it's in french ! see end.
   */
  public String fini()
  {
    String s;
    
    s = m_buffer.toString();
    m_buffer.setLength(0);
    return s;
  }
  
  /**
   * Give message and clean contener.
   */
  public String end()
  {
    String s;
    
    s = m_buffer.toString();
    m_buffer.setLength(0);
    return s;
  }
}
