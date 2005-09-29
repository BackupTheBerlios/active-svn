/*
 * Rule.java
 *
 * Created on 23 septembre 2005, 15:59
 *
 */

package com.diaam.active.runs;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Yet another <i>if</i> statment. This class is now very complicated, but I
 * hope it will be simplier. <br>
 * There is a generalized way, wich is very complicated, and a specific way, 
 * which is very simple. I hope what you understand.<br>
 * 
 *
 * The use model of the complicated but genralized way is :
 * <code><pre>
 *  Rule myRule;
 *  Rule.Executor myRuleExecutor;
 *  Rule.Result myRuleResult;
 *  
 *  myRuleExecutor = new RuleExecutor(aMethodForTheTest);
 *  myRule = new Rule(myRuleExecutor);
 *  myRuleResult = myRule.evaluateWith(anObjectForTheTest, aValueForTheTest);
 *  if (myRuleResult.isOk())
 *  {
 *    MyClassComponents components;
 *
 *    components = (MyClassComponents)myRuleResult.getComponents();
 *    // ... and so on.
 *  }
 * </pre></code>
 *
 * This class is probably a little complicated. I think the simple use will
 * be the best, but I hesitate. The simple use is by subclassing 
 * the Rule class, for instance :
 * <code><pre>
 * public class RuleShouldBeColored extends Rule
 * {
 *   private Color m_color;
 *
 *   public RuleShouldBeColored match(Object perhaps)
 *   {
 *     RuleShouldBeColored ok;
 *
 *     ok = null;
 *     if (perhaps instanceof Color)
 *     {
 *       m_color = (Color)perhaps;
 *     }
 *     return m_color;
 *   }
 *
 *   public Color getColor()
 *   {
 *     return m_color;
 *   }
 * }
 * </pre></code>
 * And the use :
 * <code><pre>
 * RuleShouldBeColored shouldbe;
 *
 * shouldbe = new RuleShouldBeColored();
 * shouldbe.autoEvaluate(toto);
 * if (shouldbe.isOK())
 * {
 *  Color thegood = shouldbe.getColor();
 *  ... and so on ...
 * }
 * </pre></code>
 *
 * @author
 * <a href="mailto:herve.agnoux@diaam-informatique.com">Hervé Agnoux</a>
 *
 */
public class Rule
{
  /**
   * An executor of a rule.
   */
  public static final class Executor
  {
    private Method m_method;
    
    public Executor(Method method)
    {
      if (method.getParameterTypes().length != 1)
        throw new IllegalArgumentException(
                "The method must have one argument.");
      m_method = method;
    }
    
    public Method getMethod()
    {
      return m_method;
    }
  }
  
  /**
   * The result of a rule.
   */
  public static class Result
  {
    private Object m_components;
    
    Result(Object components)
    {
      m_components = components;
    }
    
    public boolean isOK()
    {
      return m_components != null;
    }
    
    private void setComponents(Object components)
    {
      m_components = components;
    }
    
    public Object getComponents()
    {
      return m_components;
    }
  }
  
  private Executor m_executor;
  private Result m_result;
  private boolean m_forNewResultEachTime;
  
  /**
   * All default things, for easy use by subclasses. The class wrapped 
   * for executor is this.getClass(), the name of the test method is 
   * <i>match</i>, and the parameter class is discovered by reflection.
   */
  public Rule()
  {
    try
    {
      Class[] classparams = {Object.class};
      
      constructHelper(
              new Executor(getClass().getMethod("match", classparams)),
              false);
    }
    catch (NoSuchMethodException nosuch)
    {
      IllegalStateException illegal;
      
      illegal = new IllegalStateException();
      illegal.initCause(nosuch);
      throw illegal;
    }
  }
  
  public Rule(Executor executor)
  {
    this(executor, false);
  }
  
  public Rule(Class classInExecutor, String nameMethod, Class classParameter) 
  throws NoSuchMethodException
  {
    Class[] classparams = {classParameter};
    Executor executor;
    
    constructHelper(
            new Executor(classInExecutor.getMethod(nameMethod, classparams)),
            false);
  }
  
  public Rule(Executor executor, boolean forNewResultEachTime)
  {
    constructHelper(executor, forNewResultEachTime);
  }
  
  private void constructHelper(Executor executor, boolean forNewResultEachTime)
  {
    m_executor = executor;
    m_result = new Result(null);
    m_forNewResultEachTime = forNewResultEachTime;
  }
  
  public Result evaluateWith(Object base, Object expression) 
  throws IllegalAccessException
  {
    try
    {
      Object[] args;
      
      args = new Object[1];
      args[0] = expression;
      if (m_forNewResultEachTime)
        m_result = new Result(null);
      m_result.setComponents(m_executor.getMethod().invoke(base, args));
    }
    catch (InvocationTargetException bad)
    {
      IllegalStateException illegal;
      Memo memo;
      String message;
      
      memo = new Memo();
      message = memo.
              addExpression("evaluate with").
              add("base", base).
              add("expression", expression).
              end();
      illegal = new IllegalStateException(message);
      illegal.initCause(bad);
      throw illegal;
    }
    return m_result;
  }
  
  /**
   * When rule can hold the result. Used by subclasses.
   */
  public void autoEvaluate(Object expression) throws IllegalAccessException
  {
    evaluateWith(this, expression);
  }
  
  public boolean isOK()
  {
    return m_result.isOK();
  }
}
