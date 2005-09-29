/*
 * RuleTestCase.java
 *
 * Created on 23 septembre 2005, 17:34
 *
 */

package com.diaam.test.active.runs;

import com.diaam.active.runs.Rule;
import junit.framework.Assert;

/**
 * Self comprehensible class, I hope.
 *
 * @author
 * <a href="mailto:herve.agnoux@diaam-informatique.com">Herv� Agnoux</a>
 *
 */
public class RuleTestCase extends junit.framework.TestCase
{
  public RuleTestCase()
  {
  }
  
  public void testOKIsOk() throws Exception
  {
    Rule rule;
    Rule.Result result;
    Class[] paramsClass = {Object.class};
    
    rule = new Rule(
            new Rule.Executor(getClass().getMethod("toSayOK", paramsClass)));
    result = rule.evaluateWith(this, "goood");
    Assert.assertTrue(result.isOK());
    Assert.assertTrue(result.getComponents() instanceof Integer);
  }
  
  public Integer toSayOK(Object notUsed)
  {
    return new Integer(2); 
  }
}
