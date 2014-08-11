package zephyr.serialization;

import java.awt.Color;

import zephyr.util.ColorFactory;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

public class Serializer<T> {

	public static final int NORMAL = 0;

	public static final int DEEP = 1;

	private final JSONSerializer JSON_SERIALIZER;

	private final JSONDeserializer<T> JSON_DESERIALIZER;

	public Serializer() {
		JSON_SERIALIZER = new JSONSerializer().prettyPrint(true);
		JSON_DESERIALIZER = new JSONDeserializer<T>().use(Color.class, new ColorFactory());
	}

	public String toJSON(Object src) {
		return toJSON(src, new String());
	}

	public String toJSON(Object src, String... exclude) {
		return toJSON(src, NORMAL, exclude);
	}

	public String toJSON(Object src, int serializationMode) {
		return toJSON(src, serializationMode, new String());
	}

	public String toJSON(Object src, int serializationMode, String... exclude) {
		String json = null;
		switch (serializationMode) {
		case DEEP:
			json = JSON_SERIALIZER.exclude(exclude).deepSerialize(src);
			break;
		case NORMAL:
		default:
			json = JSON_SERIALIZER.exclude(exclude).serialize(src);
			break;
		}
		System.out.print(json);
		return json;
	}

	public T fromJSON(String json) {
		return JSON_DESERIALIZER.deserialize(json);
	}
}
