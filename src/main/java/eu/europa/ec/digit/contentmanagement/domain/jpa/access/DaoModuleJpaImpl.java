package eu.europa.ec.digit.contentmanagement.domain.jpa.access;

import static eu.europa.ec.digit.contentmanagement.domain.jpa.access.EccmConstantsJpaImpl.*;

import java.util.*;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.log4j.Logger;

import eu.europa.ec.digit.contentmanagement.domain.api.access.*;
import eu.europa.ec.digit.contentmanagement.domain.api.access.specific.*;
import eu.europa.ec.digit.contentmanagement.domain.api.query.EccmQuery;
import eu.europa.ec.digit.contentmanagement.domain.api.query.ResultElement;
import eu.europa.ec.digit.contentmanagement.domain.api.util.EccmUtils;
import eu.europa.ec.digit.contentmanagement.domain.jpa.access.specific.*;
import eu.europa.ec.digit.contentmanagement.domain.jpa.entities.impl.RepositoryJpaImpl;
import eu.europa.ec.digit.contentmanagement.domain.jpa.entities.impl.TypeDefinitionJpaImpl;
import eu.europa.ec.digit.contentmanagement.exception.UnimplementedException;

/**
 * 
 * @author bentsth
 */
public class DaoModuleJpaImpl extends AbstractDaoModule {
    private static final Logger logger = Logger.getLogger(DaoModuleJpaImpl.class);
    private EntityManagerFactory entityManagerFactory;


    public DaoModuleJpaImpl() {
    }


    @Override
    public void initSub() throws Exception {
        if (logger.isDebugEnabled())
            logger.debug("Init dao module");

        Properties props = EccmUtils.readEccmPropsFromClasspath();
        String dataSourceName = props.getProperty(PROP_NAME_DATA_SOURCE_NAME, PROP_DEFAULT_DATA_SOURCE_NAME);

        if (logger.isInfoEnabled())
            logger.info("Data source name: " + dataSourceName);

        entityManagerFactory = Persistence.createEntityManagerFactory(dataSourceName);
    }


    @Override
    public void closeSub() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            if (logger.isDebugEnabled())
                logger.debug("Closing entityManagerFactory...");
            entityManagerFactory.close();
        }
    }


    @Override
    public List<EntityDao_i<?, ?>> getDaos() {
        List<EntityDao_i<?, ?>> lst = new LinkedList<>();
        lst.add(getRepositoryDao());
        lst.add(getTypeDefinitionDao());
        lst.add(getArtifactDao());
        return lst;
    }


    @Override
    public RepositoryDao_i<RepositoryJpaImpl> getRepositoryDao() {
        return new RepositoryDaoJpaImpl(entityManagerFactory);
    }


    @Override
    public TypeDefinitionDao_i<TypeDefinitionJpaImpl> getTypeDefinitionDao() {
        return new TypeDefinitionDaoJpaImpl(entityManagerFactory);
    }


    @Override
    public ArtifactDao_i<?> getArtifactDao() {
        return new ArtifactDaoJpaImpl(entityManagerFactory);
    }


    @Override
    public <T> List<T> executeQuery(Class<T> clazz, EccmQuery query) {
        // TODO Auto-generated method stub
        throw new UnimplementedException();
    }


    @Override
    public List<ResultElement> executeQuery(EccmQuery query) {
        // TODO Auto-generated method stub
        throw new UnimplementedException();
    }
}
