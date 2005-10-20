/*
 * BeanUp.java
 *
 * Created on 9 mai 2005, 13:39
 *
 * $Log: BeanUp.java,v $
 * Revision 1.3  2005/07/19 07:04:45  herve
 * Commit pour passage � SVN.
 *
 * Revision 1.2  2005/06/14 18:15:15  herve
 * Meilleure organisation des logs
 *
 * Revision 1.1  2005/05/24 19:52:18  herve
 * Transfert du a mauvaise manipulation
 *
 * Revision 1.1.1.1  2005/05/10 13:14:00  herve
 * activons nous, mais en reflechissant
 *
 *
 */

package com.diaam.active.patterns.beans;


import com.diaam.active.runs.Memo;
import com.diaam.active.runs.Rule;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;



/**
 * <p>A bean which implements an interface at run-time. With that it's possible
 * to change the contract of an interface from version to version, without
 * to be afraid by the old implementation : the implementation is dynamic,
 * and automaticaly follow the accessors presented in the interface.</p>
 *<p>Exemple of use...<br>
 * First an interface :<br>
 * <code><pre>
 * public interface Saleable
 * {
 *  public void setPrice(int price);
 *  public int getPrice();
 * }
 * </pre></code>
 * Without <i>BeanUp</i> you must implements this interface. And that's the
 * miracle ! With <i>BeanUp</i>, you only <i>should</i> implement it. You 
 * can do, in a method :
 * <code><pre>
 *  Saleable bestOffer = BeanUp.getInstance(Saleable.class);
 *  bestOffer.setPrice(9999999999999);
 * </pre></code>
 * ... and <i>bestOffer.getPrice()</i> will give you 9999999999999.
 *</p>
 *
 * @author 
 * <a href="mailto:herve.agnoux@diaam-informatique.com">Hervé Agnoux</a>
 */
public final class BeanUp implements java.lang.reflect.InvocationHandler
{
  /**
   * A class which the properties can be seen by a Map. It's the case of the
   * BeanUp. So if the contract give to the <i>getInstance</i> methods
   * implements or extends this interface, you can get the internaly Map,
   * and use it to influence the comportment of the bean.
   *
   */
  public interface Mapable
  {
    public Map getValues();
  }
  
  private HashMap m_values;
  private Class m_contract;
  private PropertyDescriptor[] am_properties;
  
  private BeanUp(Class contract) throws IntrospectionException
  {
    m_values = new HashMap();
    m_contract = contract;
    am_properties =
            Introspector.getBeanInfo(contract).getPropertyDescriptors();
  }

  /**
   * To get an object of a class, usually an interface.
   *
   */
  public static Object getInstance(Class ofThis)
  {
    Object o;
    
    o = null;
    try
    {
      BeanUp bean;
      
      bean = new BeanUp(ofThis);
      o = getInstance(bean);
    }
    catch (IntrospectionException ie)
    {
      UnsupportedOperationException uoe;
      
      uoe = new UnsupportedOperationException(ie.toString());
      uoe.initCause(ie);
      throw uoe;
    }
    return o;
  }
  
  /**
   * To get an object of a contract, initialised.
   *
   * @param ofThis Usually an interface
   * @param values The initialised values. BeanUp uses a copy of this Map, not
   * this map directly. To get the map internaly used, use the Mapable 
   * interface.
   *
   * @return an object which automatically implements the contract.
   *
   */
  public static Object getInstance(Class ofThis, Map values)
  {
    Object o;
    
    o = null;
    try
    {
      BeanUp bean;
      
      bean = new BeanUp(ofThis);
      bean.m_values.putAll(values);
      o = getInstance(bean);
    }
    catch (IntrospectionException ie)
    {
      UnsupportedOperationException uoe;
      
      uoe = new UnsupportedOperationException(ie.toString());
      uoe.initCause(ie);
      throw uoe;
    }
    return o;
  }
  
  private static Object getInstance(BeanUp forThisBean)
  {
    Object o;
    
    Class[] a_c;
    
    a_c = new Class[1];
    a_c[0] = forThisBean.m_contract;
    o = Proxy.newProxyInstance(
            BeanUp.class.getClassLoader(), a_c, forThisBean);
    return o;
  }
  
  public Object invoke(Object proxy, Method method, Object[] args)
  throws Throwable
  {
    String methname;
    Result result;

    result = new Result();
    try
    {
      methname = method.getName();
      doSpecialMethods(method, args, result);
      if (!result.isDone())
      {
        RuleAccessors rule;
        
        rule = new RuleAccessors();
        rule.autoEvaluate(method);
        if (rule.isOK())
        {
          if (rule.isGet())
          {
            Object o;
            Class returntype;
            
            o = m_values.get(rule.getProperty());
            returntype = method.getReturnType();
            if (o instanceof Table)
            {
              Table t;
              Object[] a_src, a_dest;
              
              t = (Table)o;
              a_src = t.am_vals;
              a_dest = (Object[])Array.newInstance
                      (returntype.getComponentType(), a_src.length);
              System.arraycopy(a_src, 0, a_dest, 0, a_dest.length);
              o = a_dest;
            }
            result.met(o);
            if (!returntype.isAssignableFrom(result.m_value.getClass()))
            {
              Memo memo;
              
              memo = new Memo();
              memo.
                      add("method", method).
                      add("class wait", returntype).
                      add("class get", result.m_value.getClass());
              throw new ClassCastException(memo.fini());
            }
          }
          else if (rule.isSet())
          {
            m_values.put(rule.getProperty(), args[0]);
            result.done();
          }
        }
      }
      if (!result.isDone())
        throw new NoSuchMethodException(method.getName());
    }
    finally
    {
      
    }
    return result.m_value;
  }
    
  /**
   * @todo Sur toString, afficher aussi le contenu du map.
   */
  private void doSpecialMethods(Method method, Object[] args, Result cr)
  {
    String methodname;
    
    methodname = method.getName();
    if (methodname.equals("toString"))
    {
      cr.met("<proxy "+m_contract.getName()+">");
    }
    else if (methodname.equals("equals"))
    {
      if (args.length == 1)
      {
        cr.met(Boolean.FALSE);
        if (args[0] instanceof Proxy)
        {
          InvocationHandler ih;
          
          ih = Proxy.getInvocationHandler(args[0]);
          if (ih instanceof BeanUp)
          {
            BeanUp hpb;
            
            hpb = (BeanUp)ih;
            if (hpb.m_contract == m_contract)
            {
              if (hpb.m_values.equals(m_values))
                cr.met(Boolean.TRUE);
            }
          }
        }
      }
    }
    // for Mapable interface, not very generic...
    else if (methodname.equals("getValues"))
      cr.met(m_values);
  }
  
  private class Result
  {
    private Object m_value;
    private boolean m_done;
    
    private void met(Object value)
    {
      m_value = value;
      m_done = true;
    }
    
    private boolean isDone()
    {
      return m_done;
    }
    
    private void done()
    {
      m_done = true;
    }
  }
  
  private class Table
  {
    private Object[] am_vals;
    
    private Table(int tailleInitiale)
    {
      am_vals = new Object[tailleInitiale];
    }
    
    private void met(int index, Object valeur)
    {
      if (am_vals.length < index + 1)
      {
        Object[] a_o;
        
        a_o = am_vals;
        am_vals = new Object[index+1];
        System.arraycopy(a_o, 0, am_vals, 0, a_o.length);
      }
      am_vals[index] = valeur;
    }
    
    public boolean equals(Object obj)
    {
      boolean resultat;
      
      resultat = false;
      if (obj.getClass() == getClass())
      {
        Table t;
        
        t = (Table)obj;
        if (t.am_vals.length == am_vals.length)
        {
          boolean b;
          
          b = true;
          for (int i = 0; i < am_vals.length; i++)
          {
            if (t.am_vals[i] == null)
            {
              if (am_vals[i] != null)
              {
                b = false;
                break;
              }
            }
            else if (!t.am_vals[i].equals(am_vals[i]))
            {
              b = false;
              break;
            }
          }
          resultat = b;
        }
      }
      return resultat;
    }
  }
  
  public static class RuleAccessors extends com.diaam.active.runs.Rule
  {
    private String m_xet = "";
    private String m_property = "";
    
    public RuleAccessors match(Object perhapsAccessor)
    {
      return match((Method)perhapsAccessor);
    }
    
    public RuleAccessors match(Method perhapsAccessor)
    {
      RuleAccessors ok;
      String name;
      
      ok = null;
      name = perhapsAccessor.getName();
      if (name.startsWith("set")  ||  name.startsWith("get"))
      {
        ok = this;
        m_xet = perhapsAccessor.getName().substring(0, 3);
        m_property = Introspector.decapitalize(name.substring(3));
      }
      return ok;
    }
    
    public boolean isGet()
    {
      return "get".equals(m_xet);
    }
    
    public boolean isSet()
    {
      return "set".equals(m_xet);
    }
    
    public String getProperty()
    {
      return m_property;
    }
  }
}
