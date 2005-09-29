/*
 * SureTestCase.java
 *
 * Created on 25 mai 2005, 16:24
 *
 * $Log: SureTestCase.java,v $
 * Revision 1.1  2005/05/25 14:50:25  herve
 * Creation
 *
 *
 */

package com.diaam.test.active.runs;

import com.diaam.active.runs.Sure;
import junit.framework.Assert;

/**
 * Self comprehensible class, I hope.
 *
 * @author
 * <a href="mailto:herve.agnoux@diaam-informatique.com">Hervé Agnoux</a>
 *
 */
public class SureTestCase extends junit.framework.TestCase
{
  public void testReallySure() throws Exception
  {
    Sure suresure;
    
    suresure = new Sure("blabla");
    suresure = new Sure("zme_145");
  }
  
  public void testNotSure() throws Exception
  {
    Sure nosure;

    try {nosure = new Sure("bla bla"); Assert.fail();} 
    catch (Sure.NotSureException ok) {}
    try {nosure = new Sure("blaébla"); Assert.fail();} 
    catch (Sure.NotSureException ok) {}
  }
}
