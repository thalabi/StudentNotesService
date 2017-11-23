package com.kerneldc.education.studentNotesService.util;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class KdcCollectionUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());

	private KdcCollectionUtils() {
		throw new IllegalStateException("Utility class");
	}

	public static <K,V> Map<K, V> convertToMap(List<V> beanList, String propertyName, Class<K> keyClass) {
		Map<K, V> result = new HashMap<>();
		for (V bean : beanList) {
			Object property = null;
			try {
				property = PropertyUtils.getNestedProperty(bean, propertyName);
			} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				LOGGER.error("Invalid property name '{}'", propertyName);
				e.printStackTrace();
			}
			result.put(keyClass.cast(property), bean);
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static <T,V> List<V> convertToList(Collection<T> collection,String keyPropertyName) {
		List<V> result = new ArrayList<>(); 
		for (T entry : collection) {
			try {
				result.add((V) PropertyUtils.getNestedProperty(entry, keyPropertyName));
			} catch(Exception e) {
				throw new RuntimeException("Cannot convert collection to set with property key: " + keyPropertyName, e);
			}
		}
		return result;
	}
}
