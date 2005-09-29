/*
 * ReporterTestCase.java
 *
 * Created on 16 janvier 2005, 08:08
 *
 * $Log: ReporterTestCase.java,v $
 * Revision 1.2  2005/05/26 13:42:38  herve
 * Amelioration logs, introduction beans et sure
 *
 *
 */

package com.diaam.test.active.runs;

import com.diaam.active.runs.Leaf;
import com.diaam.active.runs.Reporter;
import junit.framework.Assert;

/**
 * Self comprehensible class, I hope.
 *
 * @author herve
 */
public class ReporterTestCase extends junit.framework.TestCase
{
  public void testDitTout() throws Exception
  {
    Reporter report;
    Leaf leaf;
    String resul;

    report = new Reporter(this);
    resul = report.say("toto", "val toto").say("tata", "val tata").fini();
    Assert.assertEquals("toto=val toto, tata=val tata, ", resul);
  }
}
