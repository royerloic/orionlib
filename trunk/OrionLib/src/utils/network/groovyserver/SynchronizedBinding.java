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

	private void readObject(final ObjectInputStream in)	throws IOException,
																											ClassNotFoundException
	{
		final Map lVariables = (Map) in.readObject();
		this.getVariables().putAll(lVariables);
		in.defaultReadObject();
	}

	private void writeObject(final ObjectOutputStream out) throws IOException
	{
		out.writeObject(this.getVariables());
		out.defaultWriteObject();
	}

	public SynchronizedBinding(final Map pVariables)
	{
		super(pVariables);
		// TODO Auto-generated constructor stub
	}

	public SynchronizedBinding(final String[] pArgs)
	{
		super(pArgs);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object getProperty(final String pArg0)
	{
		synchronized (this)
		{
			return super.getProperty(pArg0);
		}
	}

	@Override
	public Object getVariable(final String pName)
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
	public void setProperty(final String pArg0, final Object pArg1)
	{
		synchronized (this)
		{
			super.setProperty(pArg0, pArg1);
		}
	}

	@Override
	public void setVariable(final String pName, final Object pValue)
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
	public Object invokeMethod(final String pName, final Object pArgs)
	{
		synchronized (this)
		{
			return super.invokeMethod(pName, pArgs);
		}
	}

	@Override
	public void setMetaClass(final MetaClass pMetaClass)
	{
		synchronized (this)
		{
			super.setMetaClass(pMetaClass);
		}
	}

}
