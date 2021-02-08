package eu.europa.ec.digit.contentmanagement.domain.jpa.access.specific;

import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import eu.europa.ec.digit.contentmanagement.domain.api.access.DataConnectionObject_i;
import eu.europa.ec.digit.contentmanagement.domain.api.access.specific.TypeDefinitionDao_i;
import eu.europa.ec.digit.contentmanagement.domain.api.entities.TypeDefinition_i;
import eu.europa.ec.digit.contentmanagement.domain.api.util.collections.PaginatedList_i;
import eu.europa.ec.digit.contentmanagement.domain.jpa.access.AbstractDaoJpaImpl;
import eu.europa.ec.digit.contentmanagement.domain.jpa.access.DataConnectionObjectJpaImpl;
import eu.europa.ec.digit.contentmanagement.domain.jpa.entities.impl.TypeDefinitionJpaImpl;

/**
 * 
 * @author bentsth
 */
public class TypeDefinitionDaoJpaImpl extends AbstractDaoJpaImpl<TypeDefinition_i, TypeDefinitionJpaImpl>
        implements TypeDefinitionDao_i<TypeDefinitionJpaImpl> {

    public TypeDefinitionDaoJpaImpl(EntityManagerFactory entityManagerFactory) {
        super(entityManagerFactory);
    }


    @Override
    protected Class<TypeDefinition_i> getInterfaceOfEntity() {
        return TypeDefinition_i.class;
    }


    @Override
    protected Class<TypeDefinitionJpaImpl> getImplementingClassOfEntity() {
        return TypeDefinitionJpaImpl.class;
    }


    @Override
    public TypeDefinitionJpaImpl getNewEntityForTest(int seed) {
        return new TypeDefinitionJpaImpl("name_JPA_" + seed, "displayName_" + seed, "description_" + seed);
    }


    @Override
    public PaginatedList_i<? extends TypeDefinition_i> getChildren(DataConnectionObject_i dco, String uuid, int skipItems, int maxItems) throws Exception {
        if(dco == null)
            dco = new DataConnectionObjectJpaImpl(entityManagerFactory.createEntityManager());
        
        TypeDefinition_i parent = retrieve(dco, uuid);
        DataConnectionObjectJpaImpl dco_ = (DataConnectionObjectJpaImpl) dco;
        TypedQuery<TypeDefinitionJpaImpl> typedQuery =
                dco_.getEntityManager().createQuery("SELECT t FROM TypeDefinition t WHERE t.parentTypeDefinition = ?1", TypeDefinitionJpaImpl.class);
        typedQuery.setParameter(1, parent);
        return list(dco, skipItems, maxItems, typedQuery);
    }
}
