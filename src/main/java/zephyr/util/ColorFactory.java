package zephyr.util;

import java.awt.Color;
import java.lang.reflect.Type;
import java.util.Map;

import flexjson.ObjectBinder;
import flexjson.ObjectFactory;

public class ColorFactory implements ObjectFactory {

	@Override
	public Object instantiate(ObjectBinder context, Object value, Type targetType,
			@SuppressWarnings("rawtypes") Class targetClass) {
		try {
			Map<?, ?> m = (Map<?, ?>) value;
			return new Color((Integer) m.get("RGB"));
		} catch (ClassCastException e) {
			e.printStackTrace();
		}
		return new Color(255, 255, 255);
	}

}
