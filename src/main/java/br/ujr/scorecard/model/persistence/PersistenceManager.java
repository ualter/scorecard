package br.ujr.scorecard.model.persistence;


/**
 * 
 * @author <a href="ualter@uol.com.br">Ualter Otoni Azambuja Junior</a>  
 */
public class PersistenceManager {

	/*Logger logger;
	PersistenceBroker persistenceBroker;

	public PersistenceManager()
	{
		this.init();
	}

	private void init()
	{
		try 
		{
			 this.logger            = ServiceLocator.getLogger(PersistenceManager.class);
			 this.persistenceBroker = ServiceLocator.getPersistenceBroker();
		} catch (Exception e)
		{
			this.logger.error(e.toString());
			e.printStackTrace();
		}
	}

	public void beginTransaction()
	{
		this.persistenceBroker.beginTransaction();
	}
	
	public void commitTransaction()
	{
		this.persistenceBroker.commitTransaction();
	}
	
	public void abortTransaction()
	{
	    this.persistenceBroker.abortTransaction();
	}

	public void save(Object obj)
	{
		try 
		{
			  this.persistenceBroker.store(obj);
		} catch ( Throwable e )
		{
			this.logger.error(e.toString());
			e.printStackTrace();
		}
	}
	
	public void delete(Object obj)
	{
		try 
		{
			  this.persistenceBroker.delete(obj);
		} catch ( Throwable e )
		{
			this.logger.error(e.toString());
			e.printStackTrace();
		}
	}
	
	public Object getObjectById(Class clazz, long id)
	{
	    Object[] pks = {new Long(id)};
	    return this.getObjectById(clazz, pks);
	}
	public Object getObjectById(Class clazz, int id)
	{
	    Object[] pks = {new Integer(id)};
	    return this.getObjectById(clazz, pks);
	}
	public Object getObjectById(Class clazz, Object[] pks)
	{
		Object obj = null;
		try
		{
			Identity identity = new Identity(clazz,clazz, pks);
			obj = this.persistenceBroker.getObjectByIdentity(identity);
		} catch (Throwable e)
		{
			this.logger.error(e.toString());
			e.printStackTrace();
		}
		return obj;
	}

	public List getObjects(Class clazz)
	{
		List result = null;
		try 
		{
			Criteria criteria = new Criteria();
			Query    query    = new QueryByCriteria(clazz);
			result            = (List)this.persistenceBroker.getCollectionByQuery(query);
		} catch ( Throwable e)
		{
			this.logger.error(e.toString());
			e.printStackTrace();
		}
		return result;
	}
	
	public List getObjectsByCriteria(Class clazz, Criteria criteria)
	{
		List result = null;
		try 
		{
			Query    query    = new QueryByCriteria(clazz,criteria);
			result            = (List)this.persistenceBroker.getCollectionByQuery(query);
		} catch ( Throwable e)
		{
			this.logger.error(e.toString());
			e.printStackTrace();
		}
		return result;
	}
	
	public PersistenceBroker getPersistenceBroker()
	{
		return this.persistenceBroker;
	}
	
	public Object[] getNextIdentity(Object obj)
	{
	    Identity oid   = new Identity(obj,this.persistenceBroker);
		return oid.getPrimaryKeyValues();
	}*/
	
	
}
