/*
 * copyright (C) reserved to CaprusIT (I) Pvt. Ltd. 2018 - 2018 All rights reserved.
 *
 * This Software is licensed under the CaprusIT private license version 1.0
 * any breach or unauthorized reutilization of this will be strictly prohibited and may leads to leagal issue.  
 */
package com.caprus.cupcake.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.EntityType;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

// TODO: Auto-generated Javadoc
/**
 * The Class DBService.
 * @author - VijayaSaradhi R
 */
@Component
@Service
/*
 * @NamedNativeQuery( name="findStudentPercentile",
 * query="SELECT * FROM STUDENTS" )
 */
/*
 * @NamedQueries({
 * 
 * @NamedQuery(name="findStudentPercentile", query="SELECT s FROM students s")
 * })
 */

public class DBService {

	/** The entity manager. */
	/*
	 * @Autowired SessionFactory sessionFactory;
	 */
	@Autowired
	EntityManager entityManager;

	/** The entities map. */
	private static Map<String, Object> entitiesMap = new HashMap<String, Object>();
	
	/** The initialize. */
	static boolean initialize = false;

	/**
	 * Gets the entity manager.
	 *
	 * @return the entity manager
	 */
	private EntityManager getEntityManager() {
		/*
		 * AnnotationConfigApplicationContext context = new
		 * AnnotationConfigApplicationContext(DBConfig.class);
		 * EntityManagerFactory emf =
		 * context.getBean(EntityManagerFactory.class); return
		 * emf.createEntityManager();
		 */
		return entityManager;
	}

	/**
	 * Process data bean.
	 *
	 * @param bean the bean
	 */
	@Transactional
	public void processDataBean(Object bean) {

		getEntityManager().persist(bean);
		// entityManager.remove(bean);

	}

	/**
	 * Gets the all entities.
	 *
	 * @return the all entities
	 */
	private Set<EntityType<?>> getAllEntities() {
		return getEntityManager().getMetamodel().getEntities();
	}

	/*
	 * public List<Object> getStudents() { Query q =
	 * entityManager.createNativeQuery("SELECT * FROM students s"); return
	 * q.getResultList(); }
	 */

	/**
	 * Register entity beans.
	 */
	public void registerEntityBeans() {
		if (!initialize) {
			for (EntityType<?> entity : getAllEntities())
				entitiesMap.put(entity.getName(), entity.getJavaType());

			System.out.println("Entity cache initialized !!");
			initialize = true;
		}
	}

	/**
	 * Gets the bean.
	 *
	 * @param table the table
	 * @return the bean
	 */
	public Object getBean(String table) {
		registerEntityBeans();
		Class entityClass = (Class) entitiesMap.get(table);
		try {
			if (entityClass != null)
				return entityClass.newInstance();
		} catch (Exception e) {
			System.out.println("Error while loading bean definitions !!! " + e.getMessage());
		}

		return entityClass;
	}

	/**
	 * Gets the all bean names.
	 *
	 * @return the all bean names
	 */
	public Set<String> getAllBeanNames() {
		registerEntityBeans();

		return entitiesMap.keySet();
	}

	/**
	 * Evict.
	 *
	 * @param bean the bean
	 */
	public void evict(Object bean) {
		getEntityManager().detach(bean);
	}
}
