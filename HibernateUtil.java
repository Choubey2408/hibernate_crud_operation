package net.javaguides.hibernate.util;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;

import net.javaguides.hibernate.model.Student;

import java.util.Properties;


public class HibernateUtil {
	
	private static SessionFactory sessionFactory; 
	public static SessionFactory getSessionFactory() { 
		if (sessionFactory == null) { 
			try { 
				Configuration con = new Configuration()  
				.setProperty(Environment.DIALECT, "org.hibernate.dialect.MySQL5Dialect") 
				.setProperty(Environment.URL, "jdbc:mysql://127.0.0.1:3306/school_management") 
				.setProperty("hibernate.show_sql", "true") 
				.setProperty("hibernate.connection.username", "root") 
				// .setProperty(Environment.HBM2DDL AUTO, "create-drop")
				.setProperty("hibernate.connection.password", "Tan@bha24");
 
				con.addAnnotatedClass(Student.class); 
				ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder() 
						.applySettings(con.getProperties()).build(); 
				sessionFactory = con.buildSessionFactory(serviceRegistry); 
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
		return sessionFactory;
		
	}
}

