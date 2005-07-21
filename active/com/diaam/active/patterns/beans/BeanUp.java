/*
 * BeanUp.java
 *
 * Created on 9 mai 2005, 13:39
 *
 * $Log: BeanUp.java,v $
 * Revision 1.3  2005/07/19 07:04:45  herve
 * Commit pour passage à SVN.
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


import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;



/**
 *
 * @author 
 * <a href="mailto:herve.agnoux@diaam-informatique.com">Hervé Agnoux</a>
 */
public class BeanUp implements java.lang.reflect.InvocationHandler
{
  private HashMap m_values;
  private Class m_contract;
  private PropertyDescriptor[] am_properties;
  
  /**
  * @todo réaliser un beanup à l'aide d'un contrat + map (des propriétés), de façon que l'on puisse initialiser un bean
  * par des propriétés. On peut imaginer renvoyer ce map par un accesseur.
  */
  protected BeanUp(Class contract) throws IntrospectionException
  {
    m_values = new HashMap();
    m_contract = contract;
    am_properties =
            Introspector.getBeanInfo(contract).getPropertyDescriptors();
  }
  
  public static Object getInstance(Class ofThis)
  {
    Object o;
    
    o = null;
    try
    {
      Class[] a_c;
      
      a_c = new Class[1];
      a_c[0] = ofThis;
      o = Proxy.newProxyInstance(
              BeanUp.class.getClassLoader(), 
              a_c, 
              new BeanUp(ofThis));
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
      if (methname.startsWith("set")  ||  methname.startsWith("get"))
      {
      String propname;
      
      propname = methname.substring(3);
      if (!result.isDone())
      {
        if (estLecture(method))
        {
          Object o;
          
          o = m_values.get(propname);
          if (o instanceof Table)
          {
            Table t;
            Object[] a_src, a_dest;
            
            t = (Table)o;
            a_src = t.am_vals;
            a_dest = (Object[])Array.newInstance
                    (method.getReturnType().getComponentType(), a_src.length);
            System.arraycopy(a_src, 0, a_dest, 0, a_dest.length);
            o = a_dest;
          }
          result.met(o);
        }
        else if (estEcriture(method))
        {
          m_values.put(propname, args[0]);
          result.done();
        }
      }
      if (!result.isDone())
      {
        if ((args.length >= 1)  &&  (args[0] instanceof Integer))
        {
          if (methname.startsWith("set"))
          {
            if (args.length > 1)
            {
              int tailleListe;
              Integer i;
              Table t;
              
              t = (Table)m_values.get(propname);
              i = (Integer)args[0];
              tailleListe = i.intValue() + 1;
              if (t == null)
                t = new Table(tailleListe);
              t.met(i.intValue(), args[1]);
              m_values.put(propname, t);
              result.done();
            }
          }
          else if (methname.startsWith("get"))
          {
            Table a_liste;
            Integer i;
            
            i = (Integer)args[0];
            a_liste = (Table)m_values.get(propname);
            result.met(a_liste.am_vals[i.intValue()]);
          }
        }
      }
      }
    if (!result.isDone())
      throw new NoSuchMethodException(method.getName());
    }
//    catch (InvocationTargetException ite)
//    {
//      throw ite.getTargetException();
//    }
    finally
    {
      
    }
    return result.m_value;
  }
  
  private boolean estLecture(Method elle)
  {
    boolean b;
    
    b = false;
    for (int i = 0; i < am_properties.length; i++)
    {
      Method m;
      
      m = am_properties[i].getReadMethod();
      if ((m != null)  &&  m.equals(elle))
      {
        b = true;
        break;
      }
    }
    return b;
  }
  
  private boolean estEcriture(Method elle)
  {
    boolean b;
    
    b = false;
    for (int i = 0; i < am_properties.length; i++)
    {
      Method m;
      
      m = am_properties[i].getWriteMethod();
      if ((m != null)  &&  m.equals(elle))
      {
        b = true;
        break;
      }
    }
    return b;
  }
  
  private void doSpecialMethods(Method method, Object[] args, Result cr)
  {
    if (method.getName().equals("toString"))
    {
      cr.met("<proxy "+m_contract.getName()+">");
    }
    else if (method.getName().equals("equals"))
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
}
