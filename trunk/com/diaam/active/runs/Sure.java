/*
 * Sure.java
 *
 * Created on 25 mai 2005, 15:59
 *
 * $Log: Sure.java,v $
 * Revision 1.2  2005/06/14 18:15:16  herve
 * Meilleure organisation des logs
 *
 * Revision 1.1  2005/05/25 14:49:54  herve
 * Creation
 *
 *
 */

package com.diaam.active.runs;

import java.util.regex.Pattern;

/**
 * Un identificateur robuste. Souvent il est nécessaire de disposer
 * d'identificateurs qui ne comportent pas d'espace, pas d'accents, ou
 * d'autres caractères hésotériques.
 *
 * @author 
 * <a href="mailto:herve.agnoux@diaam-informatique.com">Hervé Agnoux</a>
 *
 */
public class Sure
{
  public final static Pattern m_patternSure = Pattern.compile("\\w+");
  
  private String m_tag;
  
  /** Creates a new instance of Sure */
  public Sure(String tag)
  {
    if (!m_patternSure.matcher(tag).matches())
      throw new NotSureException(tag);
    m_tag = tag;
  }
  
  public String getTag()
  {
    return m_tag;
  }
  
  public static class NotSureException extends java.lang.RuntimeException
  {
    private NotSureException(String noSure)
    {
      super(noSure);
    }
  }
  
  public String toString()
  {
    return super.toString()+"(m_tag="+m_tag+")";
  }
}
