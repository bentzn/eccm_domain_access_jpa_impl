package eu.europa.ec.digit.contentmanagement.domain.jpa.access;

import javax.persistence.EntityManager;

import eu.europa.ec.digit.contentmanagement.domain.api.access.DataConnectionObject_i;
import eu.europa.ec.digit.contentmanagement.domain.api.access.DcoWrapper_i;

/**
 * 
 * @author bentsth
 */
class DcoWrapperJpaImpl implements DcoWrapper_i<EntityManager> {

    private DataConnectionObjectJpaImpl dco;


    DcoWrapperJpaImpl(DataConnectionObject_i dco) {
        if(dco == null) 
            this.dco = new DataConnectionObjectJpaImpl(null);
        else 
            this.dco = (DataConnectionObjectJpaImpl) dco;
    }


    public EntityManager getConnectionObject() {
        return dco.getEntityManager();
    }


    public void setConnectionObject(EntityManager em) {
        dco.setEntityManager(em);
    }


    @Override
    public void beginTransaction() {
        dco.getEntityManager().getTransaction().begin();
    }


    @Override
    public void commitTransaction() {
        dco.getEntityManager().getTransaction().commit();
    }


    @Override
    public void rollbackTransaction() {
        dco.getEntityManager().getTransaction().rollback();
    }


    @Override
    public boolean isTransactionActive() {
        return dco.getEntityManager().getTransaction().isActive();
    }


    @Override
    public void close() {
        if(dco.getEntityManager() != null && dco.getEntityManager().isOpen())
            dco.getEntityManager().close();
    }
}
