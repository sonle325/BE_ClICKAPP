package app.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class Json {
	private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

	public static String stringify(Object obj) {
		return gson.toJson(obj);
	}

	public static <T> T parse(String json, Class<T> type) {
		return gson.fromJson(json, type);
	}
}


