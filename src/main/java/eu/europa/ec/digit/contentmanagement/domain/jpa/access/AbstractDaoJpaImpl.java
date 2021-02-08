package eu.europa.ec.digit.contentmanagement.domain.jpa.access;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.persistence.*;
import javax.persistence.criteria.*;

import org.apache.log4j.Logger;

import eu.europa.ec.digit.contentmanagement.domain.api.access.*;
import eu.europa.ec.digit.contentmanagement.domain.api.entities.AbstractEntity_i;
import eu.europa.ec.digit.contentmanagement.domain.api.util.collections.PaginatedListImpl;
import eu.europa.ec.digit.contentmanagement.domain.api.util.collections.PaginatedList_i;

/**
 * 
 * @author bentsth
 */
public abstract class AbstractDaoJpaImpl<TYPE extends AbstractEntity_i, IMPL extends TYPE>
        extends AbstractDao<EntityManager, TYPE, IMPL> implements EntityDao_i<TYPE, IMPL> {

    private static final Logger logger = Logger.getLogger(AbstractDaoJpaImpl.class);
    protected EntityManagerFactory entityManagerFactory;


    protected AbstractDaoJpaImpl(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }


    protected abstract Class<TYPE> getInterfaceOfEntity();


    protected abstract Class<IMPL> getImplementingClassOfEntity();


    protected String getDataSourceNameOfEntity(Class<?> clazz) {
        for (Annotation ann : clazz.getDeclaredAnnotations()) {
            if (ann instanceof Entity)
                return ((Entity) ann).name();
        }

        return clazz.getSimpleName();
    }


    @Override
    protected EntityManager createConnection() {
        return entityManagerFactory.createEntityManager();
    }


    @Override
    public DataConnectionObjectJpaImpl openNewDco() {
        return new DataConnectionObjectJpaImpl(entityManagerFactory.createEntityManager());
    }


    @Override
    protected DcoWrapper_i<EntityManager> wrap(DataConnectionObject_i dco) {
        return new DcoWrapperJpaImpl(dco);
    }


    protected void createEntity(EntityManager connectionObject, TYPE entity) throws Exception {
        if (entity.getId() > 0)
            throw new Exception("Can't create entity that already has an id");

        connectionObject.persist(entity);
    }


    @Override
    protected TYPE retrieveEntity(EntityManager connectionObject, long id) {
        return connectionObject.find(getImplementingClassOfEntity(), id);
    }


    @Override
    protected TYPE retrieveEntity(EntityManager connectionObject, String uuid) throws Exception {
        try {
            CriteriaBuilder cb = connectionObject.getCriteriaBuilder();
            CriteriaQuery<IMPL> q = cb.createQuery(getImplementingClassOfEntity());
            Root<IMPL> c = q.from(getImplementingClassOfEntity());
            ParameterExpression<String> p = cb.parameter(String.class);
            q.select(c).where(cb.equal(c.get("uuid"), p));
            TypedQuery<IMPL> query = connectionObject.createQuery(q);
            query.setParameter(p, uuid);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }


    @Override
    protected void updateEntity(EntityManager connectionObject, TYPE entity) {
        connectionObject.merge(entity);
    }


    @Override
    protected void deleteEntity(EntityManager connectionObject, TYPE entity) {
        connectionObject.remove(entity);
    }


    protected PaginatedList_i<IMPL> list(DataConnectionObject_i dco, int skipItems, int maxItems,
            TypedQuery<IMPL> typedQuery) throws Exception {
        AtomicBoolean entityManagerCreated = new AtomicBoolean(false);
        DcoWrapperJpaImpl emWrap = new DcoWrapperJpaImpl(dco);
        initConnectionAndTransaction(emWrap, entityManagerCreated, null);

        try {
            return listEntities(emWrap.getConnectionObject(), skipItems, maxItems, typedQuery);
        } finally {
            closeEntityManager(emWrap, entityManagerCreated);
        }
    }


    protected PaginatedList_i<IMPL> listEntities(EntityManager connectionObject, int skipItems, int maxItems)
            throws Exception {
        return listEntities(connectionObject, skipItems, maxItems, null);
    }


    private PaginatedList_i<IMPL> listEntities(EntityManager connectionObject, int skipItems, int maxItems,
            TypedQuery<IMPL> typedQuery) throws Exception {
        if (logger.isDebugEnabled())
            logger.debug("list (" + getTypeName() + "), skipItems: " + skipItems + ", maxItems: " + maxItems);

        if (skipItems < 0)
            throw new Exception("Skip items can not be negative");

        if (maxItems <= 0)
            throw new Exception("Max items must be > 0");

        if (typedQuery == null) {
            // Select all
            CriteriaBuilder criteriaBuilder = connectionObject.getCriteriaBuilder();
            CriteriaQuery<IMPL> criteriaQuery = criteriaBuilder.createQuery(getImplementingClassOfEntity());
            Root<IMPL> root = criteriaQuery.from(getImplementingClassOfEntity());
            CriteriaQuery<IMPL> select = criteriaQuery.select(root);
            typedQuery = connectionObject.createQuery(select);
        }

        typedQuery.setFirstResult(skipItems);
        typedQuery.setMaxResults(maxItems);
        List<IMPL> lstResults = typedQuery.getResultList();

        // Get the total number of results
        int totalItems = 0;
        if (lstResults.size() == maxItems || lstResults.size() == 0) {
            // Page is full or has nothing at all, we have to query
            typedQuery.setFirstResult(0);
            typedQuery.setMaxResults(Integer.MAX_VALUE);
            totalItems = typedQuery.getResultList().size();
        } else {
            // total items can be calculated
            totalItems = skipItems + lstResults.size();
        }

        PaginatedList_i<IMPL> lst = new PaginatedListImpl<>(lstResults, skipItems, maxItems, totalItems);

        if (logger.isDebugEnabled())
            logger.debug(
                    "list (" + getTypeName() + "), returning: " + lst.size() + " records, total count: " + totalItems);

        return lst;
    }


    protected long countEntities(EntityManager connectionObject) {
        CriteriaBuilder builder = connectionObject.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);
        criteriaQuery.select(builder.count(criteriaQuery.from(getImplementingClassOfEntity())));
        return connectionObject.createQuery(criteriaQuery).getSingleResult();
    }
}
