package eu.europa.ec.digit.contentmanagement.domain.jpa.access.specific;

import javax.persistence.EntityManagerFactory;

import eu.europa.ec.digit.contentmanagement.domain.api.access.specific.RepositoryDao_i;
import eu.europa.ec.digit.contentmanagement.domain.api.entities.Repository_i;
import eu.europa.ec.digit.contentmanagement.domain.jpa.access.AbstractDaoJpaImpl;
import eu.europa.ec.digit.contentmanagement.domain.jpa.entities.impl.RepositoryJpaImpl;

/**
 * 
 * @author bentsth
 */
public class RepositoryDaoJpaImpl extends AbstractDaoJpaImpl<Repository_i, RepositoryJpaImpl>
        implements RepositoryDao_i<RepositoryJpaImpl> {

    public RepositoryDaoJpaImpl(EntityManagerFactory entityManagerFactory) {
        super(entityManagerFactory);
    }


    @Override
    protected Class<Repository_i> getInterfaceOfEntity() {
        return Repository_i.class;
    }


    @Override
    protected Class<RepositoryJpaImpl> getImplementingClassOfEntity() {
        return RepositoryJpaImpl.class;
    }


    @Override
    public Repository_i getNewEntityForTest(int seed) {
        return new RepositoryJpaImpl("name_JPA_" + seed, "description_" + seed, seed);
    }
}
