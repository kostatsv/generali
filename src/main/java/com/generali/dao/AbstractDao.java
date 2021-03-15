package com.generali.dao;

import com.generali.domain.BaseDomain;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;

import javax.annotation.Resource;
import java.lang.reflect.ParameterizedType;
import java.util.List;

public abstract class AbstractDao<T extends BaseDomain>
{
  @Resource
  private SessionFactory sessionFactory;

  protected Class<T> mClassType;

  @SuppressWarnings("unchecked")
  public AbstractDao()
  {
    Class<?> clazz = getClass();
    while (!(clazz.getGenericSuperclass() instanceof ParameterizedType))
    {
      clazz = clazz.getSuperclass();
    }
    mClassType = (Class<T>) ((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments()[0];
  }
  
  protected final Session getSession()
  {
    return sessionFactory.getCurrentSession();
  }

  public T getById(final Long aId)
  {
    return (T) getSession().get(getClassType(), aId);
  }

  public List<T> getByIds(final List<Long> ids) {
    return (List<T>) getSession()
      .createQuery("from " + getClassName() +
                   " where pk in (:ids)")
      .setParameterList("ids", ids)
      .list();
  }

  @SuppressWarnings("unchecked")
  public final List<T> getAll()
  {
    return (List<T>) getSession().createQuery("from " + getClassName()).list();
  }

  public void makeAllTransient()
  {
    batchMakeTransient(getAll());
  }

  public void saveOrUpdate(T entity) {
    getSession().saveOrUpdate(entity);
  }

  public void batchSaveOrUpdate(List<T> entities) {
    Session session = getSession();
    int i = 0;
    for (T entity : entities) {
      session.saveOrUpdate(entity);
      if (++i % 100 == 0) {
        session.flush();
      }
    }
    session.flush();
  }


  public void makeTransient(T aEntity)
  {
    getSession().delete(aEntity);
  }

  public void batchMakeTransient(final List<T> aEntities)
  {
    Session session = getSession();

    int size = aEntities.size();
    for (int i = 0; i < size; i++)
    {
      session.delete(aEntities.get(i));

      if (i != 0 && i % 50 == 0)
      {
        session.flush();
        session.clear();
      }
    }
  }

  public Long getAllCount()
  {
    return (Long) getSession().createQuery("SELECT COUNT(*) FROM " + getClassName()).uniqueResult();
  }

  protected final String getClassName()
  {
    return mClassType.getSimpleName();
  }

  private final Class<T> getClassType()
  {
    return mClassType;
  }

  protected final Long getCount(Criteria aCriteria)
  {
  	return (Long) aCriteria.setProjection(Projections.rowCount()).uniqueResult();
  }
}
