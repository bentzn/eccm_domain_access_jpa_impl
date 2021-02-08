package eu.europa.ec.digit.contentmanagement.domain.jpa.access;

import javax.persistence.EntityManager;

import eu.europa.ec.digit.contentmanagement.domain.api.access.DataConnectionObject_i;

/**
 * 
 * @author bentsth
 */
public class DataConnectionObjectJpaImpl implements DataConnectionObject_i {

    private EntityManager em;


    public DataConnectionObjectJpaImpl(EntityManager em) {
        this.em = em;
    }


    @Override
    public void beginTransaction() {
        em.getTransaction().begin();
    }


    @Override
    public void commitTransaction() {
        em.getTransaction().commit();
    }


    @Override
    public void rollbackTransaction() {
        em.getTransaction().rollback();
    }


    @Override
    public void close() {
        em.close();
    }


    public EntityManager getEntityManager() {
        return em;
    }


    public void setEntityManager(EntityManager em) {
        this.em = em;
    }
}
