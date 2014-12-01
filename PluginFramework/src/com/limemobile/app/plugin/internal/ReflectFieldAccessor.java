package com.limemobile.app.plugin.internal;

import java.lang.reflect.Field;

public class ReflectFieldAccessor<T> {
	private Object mObject;
	private String mFieldName;

	private boolean isInited;
	private Field mField;

	public ReflectFieldAccessor(Object obj, String fieldName) {
		if (obj == null) {
			throw new IllegalArgumentException("obj cannot be null");
		}
		this.mObject = obj;
		this.mFieldName = fieldName;
	}

	private void prepare() {
		if (isInited)
			return;
		isInited = true;

		Class<?> c = mObject.getClass();
		while (c != null) {
			try {
				Field f = c.getDeclaredField(mFieldName);
				f.setAccessible(true);
				mField = f;
				return;
			} catch (Exception e) {
			} finally {
				c = c.getSuperclass();
			}
		}
	}

	public T get() throws NoSuchFieldException, IllegalAccessException,
			IllegalArgumentException {
		prepare();

		if (mField == null)
			throw new NoSuchFieldException();

		try {
			@SuppressWarnings("unchecked")
			T r = (T) mField.get(mObject);
			return r;
		} catch (ClassCastException e) {
			throw new IllegalArgumentException("unable to cast object");
		}
	}

	public void set(T val) throws NoSuchFieldException, IllegalAccessException,
			IllegalArgumentException {
		prepare();

		if (mField == null)
			throw new NoSuchFieldException();

		mField.set(mObject, val);
	}
}
