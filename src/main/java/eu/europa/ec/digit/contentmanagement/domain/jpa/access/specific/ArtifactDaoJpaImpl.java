package eu.europa.ec.digit.contentmanagement.domain.jpa.access.specific;

import java.util.*;

import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import org.apache.log4j.Logger;

import eu.europa.ec.digit.contentmanagement.domain.api.access.DataConnectionObject_i;
import eu.europa.ec.digit.contentmanagement.domain.api.access.specific.ArtifactDao_i;
import eu.europa.ec.digit.contentmanagement.domain.api.entities.Artifact_i;
import eu.europa.ec.digit.contentmanagement.domain.api.util.collections.PaginatedListImpl;
import eu.europa.ec.digit.contentmanagement.domain.api.util.collections.PaginatedList_i;
import eu.europa.ec.digit.contentmanagement.domain.jpa.access.AbstractDaoJpaImpl;
import eu.europa.ec.digit.contentmanagement.domain.jpa.access.DataConnectionObjectJpaImpl;
import eu.europa.ec.digit.contentmanagement.domain.jpa.entities.impl.ArtifactJpaImpl;

/**
 * 
 * @author bentsth
 */
public class ArtifactDaoJpaImpl extends AbstractDaoJpaImpl<Artifact_i, ArtifactJpaImpl>
        implements ArtifactDao_i<ArtifactJpaImpl> {
    
    private static final Logger logger = Logger.getLogger(ArtifactDaoJpaImpl.class);

    public ArtifactDaoJpaImpl(EntityManagerFactory entityManagerFactory) {
        super(entityManagerFactory);
    }


    @Override
    protected Class<Artifact_i> getInterfaceOfEntity() {
        return Artifact_i.class;
    }


    @Override
    protected Class<ArtifactJpaImpl> getImplementingClassOfEntity() {
        return ArtifactJpaImpl.class;
    }


    @Override
    public Artifact_i getNewEntityForTest(int seed) {
        return new ArtifactJpaImpl("artifact_JPA_" + seed, null);
    }


    @Override
    public PaginatedList_i<? extends Artifact_i> getChildren(DataConnectionObject_i dco, long id,
            int skipItems, int maxItems) throws Exception {
        
        if (logger.isDebugEnabled())
            logger.debug("Get children, parent id: " + id);
         
        if (dco == null)
            dco = new DataConnectionObjectJpaImpl(entityManagerFactory.createEntityManager());
        
        Artifact_i parent = retrieve(dco, id);
        
        if (logger.isDebugEnabled())
            logger.debug("Found parent: " + parent);
        
        if(parent == null)
            return new PaginatedListImpl<>(skipItems, maxItems, 0);

        DataConnectionObjectJpaImpl dco_ = (DataConnectionObjectJpaImpl) dco;
        TypedQuery<ArtifactJpaImpl> typedQuery = dco_.getEntityManager().createQuery("SELECT a FROM Artifact a WHERE ?1 MEMBER OF a.parents", ArtifactJpaImpl.class);
        typedQuery.setParameter(1, (ArtifactJpaImpl)parent);
        return list(dco_, skipItems, maxItems, typedQuery);
    }


    @Override
    public Set<Artifact_i> getParentsAll(DataConnectionObject_i dco, long id) throws Exception {
        boolean dcoCreated = false;
        if(dco == null) {
            dco = openNewDco();
            dcoCreated = true;
        }
        
        try {
            return getParentsAll(dco, id, null);
        } finally {
            if(dcoCreated)
                dco.close();
        }
    }


    @Override
    public Set<Artifact_i> getParents(DataConnectionObject_i dco, long id) throws Exception {
        boolean dcoCreated = false;
        if(dco == null) {
            dco = openNewDco();
            dcoCreated = true;
        }
        
        try {
            Artifact_i artifact = retrieve(dco, id);
            artifact.getParents().size(); // to avoid problems with closed sessions and lazy loading
            return new HashSet<>(artifact.getParents());
        } finally {
            if(dcoCreated)
                dco.close();
        }
    }


    private Set<Artifact_i> getParentsAll(DataConnectionObject_i dco, long id, Set<Artifact_i> setAllParents) throws Exception {
        if (setAllParents == null)
            setAllParents = new HashSet<>();

        Set<Artifact_i> lstParents = getParents(dco, id);
        setAllParents.addAll(lstParents);
        for (Artifact_i parent : lstParents) {
            getParentsAll(dco, parent.getId(), setAllParents);
        }

        return setAllParents;
    }
}
