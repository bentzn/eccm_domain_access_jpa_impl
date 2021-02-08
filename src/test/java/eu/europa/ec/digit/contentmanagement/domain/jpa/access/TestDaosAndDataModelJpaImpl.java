package eu.europa.ec.digit.contentmanagement.domain.jpa.access;

import org.junit.BeforeClass;
import org.junit.Test;

import eu.europa.ec.digit.contentmanagement.domain.api.access.*;
import eu.europa.ec.digit.contentmanagement.domain.api.util.EccmUtils;

/**
 * 
 * @author bentsth
 */
public class TestDaosAndDataModelJpaImpl extends TestDaosAndDataModel{
    
    private static DaoModule_i daoModule;
    
    
    @BeforeClass
    public static void beforeClass() throws Exception {
        EccmUtils.deleteAllFiles(".\\resources\\unittest");
        daoModule = new DaoModuleJpaImpl();
        daoModule.init();
    }
   

    @Test
    public void test() throws Exception {
        run(daoModule);
    }
}
