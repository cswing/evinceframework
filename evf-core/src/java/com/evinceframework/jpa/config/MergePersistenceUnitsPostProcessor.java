package com.evinceframework.jpa.config;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.spi.PersistenceUnitInfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitPostProcessor;

/**
 * An implementation of the {@link PersistenceUnitPostProcessor} interface, which allows the
 * entities of {@link PersistenceUnitInfo} to exist in multiple jars.
 * 
 * This is an adaptation of the code found at http://forum.springsource.org/showthread.php?t=61763.
 * It has been enhanced to set the additional jar files.  This tells the entity manager to look in 
 * more than one jar when scanning for entity classes.
 * 
 * Eventually only a single {@link MutablePersistenceUnitInfo} will exist per persistence unit name.  
 * This class will prepare all of the persistence units with the same name to be that single instance,
 * as at the time this code executes, there is no way of knowing which will be the surviving 
 * {@link MutablePersistenceUnitInfo}. 
 * 
 * @author Craig Swing
 * @since 1.0
 * @see javax.persistence.spi.PersistenceUnitInfo
 * @see javax.persistence.spi.PersistenceUnitInfo#getPersistenceUnitName()
 * @see org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo
 * @see org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo#getPersistenceUnitRootUrl()
 * @see org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo#getJarFileUrls()
 */
public class MergePersistenceUnitsPostProcessor implements PersistenceUnitPostProcessor {

	private static final Log logger = LogFactory.getLog(MergePersistenceUnitsPostProcessor.class);
	
	Map<String, List<MutablePersistenceUnitInfo>> unitsByName = 
		new HashMap<String, List<MutablePersistenceUnitInfo>>();
	
	Map<String, String> propertyMap = new HashMap<String, String>();
	
	public Map<String, String> getPropertyMap() {
		return propertyMap;
	}

	public void setPropertyMap(Map<String, String> propertyMap) {
		this.propertyMap = propertyMap;
	}

	/**
	 * Merge all {@link MutablePersistenceUnitInfo} units that have the same persistence unit name,
	 */
	@Override
	public void postProcessPersistenceUnitInfo(MutablePersistenceUnitInfo pui) {
		
		String unitName = pui.getPersistenceUnitName();
		
		for (String propName : propertyMap.keySet()) {
			pui.addProperty(propName, propertyMap.get(propName));
		}
		
		List<MutablePersistenceUnitInfo> units = unitsByName.get(unitName);
		if (units == null) {
			units = new ArrayList<MutablePersistenceUnitInfo>();
			unitsByName.put(unitName, units);
		}
	
		for(MutablePersistenceUnitInfo unit : units) {	
			
			// exchange classnames
			pui.getManagedClassNames().addAll(unit.getManagedClassNames());
			unit.getManagedClassNames().addAll(pui.getManagedClassNames());
			
			// exchange urls
			mergeUrl(pui, unit);
			mergeUrl(unit, pui);
		}		
		
		units.add(pui);
	}

	/**
	 * Adds the the root url and all additional urls from the other {@link MutablePersistenceUnitInfo} to
	 * the main {@link MutablePersistenceUnitInfo}.
	 * 
	 * @param main
	 * @param other
	 * @see #addUrl(MutablePersistenceUnitInfo, URL)
	 */
	protected void mergeUrl(MutablePersistenceUnitInfo main, MutablePersistenceUnitInfo other) {
		
		if (main == null || other == null)
			return;
		
		addUrl(main, other.getPersistenceUnitRootUrl());
		
		for (URL url : other.getJarFileUrls()) {
			addUrl(main, url);
		}
	}
	
	/**
	 * Adds the url to the {@link MutablePersistenceUnitInfo#getJarFileUrls()} property if the url is not the
	 * root url and not already in the {@link MutablePersistenceUnitInfo#getJarFileUrls()} collection.
	 * 
	 * @param unit
	 * @param url
	 * @see org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo#getJarFileUrls()
	 * @see org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo#getPersistenceUnitRootUrl()
	 */
	protected void addUrl(MutablePersistenceUnitInfo unit, URL url) {
		
		if (unit.getPersistenceUnitRootUrl().equals(url))
			return;
		
		if (unit.getJarFileUrls().contains(url))
			return;
		
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("Adding jar [%s] to persistence unit [%s]."));
		}
		
		unit.addJarFileUrl(url);	
	}
}