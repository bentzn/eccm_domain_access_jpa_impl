package eu.europa.ec.digit.contentmanagement.domain.jpa.access;


import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * 
 * @author bentsth
 */
public class WheelsTurning {

	public static void main(String[] args) {
		EntityManager em = null;
		try {
			EntityManagerFactory factory = Persistence.createEntityManagerFactory("testrepo");
			em = factory.createEntityManager();
			System.out.println("EM open: " + em.isOpen());
		}
		catch(Throwable e) {
			e.printStackTrace();
		}
		finally {
			if (em != null && em.isOpen())
				em.close();
		}
		
		System.exit(-1);
	}
}
