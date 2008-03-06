package utils.network.groovyserver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

import groovy.lang.Binding;
import groovy.lang.MetaClass;

public class SynchronizedBinding extends Binding implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SynchronizedBinding()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	private void readObject(ObjectInputStream in)	throws IOException,
																								ClassNotFoundException
	{
		Map lVariables = (Map) in.readObject();
		this.getVariables().putAll(lVariables);
		in.defaultReadObject();
	}

	private void writeObject(ObjectOutputStream out) throws IOException
	{
		out.writeObject(this.getVariables());
		out.defaultWriteObject();
	}

	public SynchronizedBinding(Map pVariables)
	{
		super(pVariables);
		// TODO Auto-generated constructor stub
	}

	public SynchronizedBinding(String[] pArgs)
	{
		super(pArgs);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object getProperty(String pArg0)
	{
		synchronized (this)
		{
			return super.getProperty(pArg0);
		}
	}

	@Override
	public Object getVariable(String pName)
	{
		synchronized (this)
		{
			return super.getVariable(pName);
		}
	}

	@Override
	public Map getVariables()
	{
		synchronized (this)
		{
			return super.getVariables();
		}
	}

	@Override
	public void setProperty(String pArg0, Object pArg1)
	{
		synchronized (this)
		{
			super.setProperty(pArg0, pArg1);
		}
	}

	@Override
	public void setVariable(String pName, Object pValue)
	{
		synchronized (this)
		{
			super.setVariable(pName, pValue);
		}
	}

	@Override
	public MetaClass getMetaClass()
	{
		synchronized (this)
		{
			return super.getMetaClass();
		}
	}

	@Override
	public Object invokeMethod(String pName, Object pArgs)
	{
		synchronized (this)
		{
			return super.invokeMethod(pName, pArgs);
		}
	}

	@Override
	public void setMetaClass(MetaClass pMetaClass)
	{
		synchronized (this)
		{
			super.setMetaClass(pMetaClass);
		}
	}

}
