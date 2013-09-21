package com.evinceframework.jpa.hibernate;

import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.TypeDef;
import org.jadira.usertype.dateandtime.joda.PersistentDateTime;
import org.joda.time.DateTime;

/**
 * When Hibernate is configured to scan the com.minus110.support.persistence.hibernate package,
 * then the user types defined in this class using @TypeDef will be registered as user types in 
 * Hibernate.  
 *       
 *	<bean id="entityManagerFactory" class="org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean">
 *		<property name="packagesToScan">
 *			<array>
 * 				<value>com.evinceframework.jpa.hibernate</value>
 * 				...
 * 			</array>
 *		</property>
 *		...
 *	</bean>
 *
 * Hibernate should also be configured with two properties to specify that the timezone of the values 
 * persisted to the database and set onto the Java objects.
 * 
 *  <entry key="jadira.usertype.databaseZone" value="UTC"/>
 *  <entry key="jadira.usertype.javaZone" value="UTC"/>
 *
 * @author Craig M Swing
 */
@MappedSuperclass // hack to register TypeDef
@TypeDef(defaultForType = DateTime.class, typeClass = PersistentDateTime.class)
public class JodaTimeUserTypeHack {}

