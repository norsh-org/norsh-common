package org.norsh.util;

import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

/**
 * Factory for creating a custom {@link TypeAdapter} to serialize {@link Throwable} instances into JSON format.
 * <p>
 * This adapter ensures that exceptions are serialized with their type, message, cause, and suppressed exceptions.
 * Deserialization is explicitly unsupported to prevent security risks.
 * </p>
 *
 * @since 1.0.0
 * @version 1.0.0
 * @author Danthur Lice
 * @see <a href="https://docs.norsh.org">Norsh Documentation</a>
 */
public final class LogThrowableTypeAdapter implements TypeAdapterFactory {
	/**
	 * Creates a {@link TypeAdapter} for serializing {@link Throwable} objects.
	 *
	 * @param gson the Gson instance
	 * @param type the type token representing the class
	 * @param <T>  the type parameter
	 * @return a custom {@code TypeAdapter} for {@link Throwable}, or {@code null} if the type is not applicable
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
		return Throwable.class.isAssignableFrom(type.getRawType()) ? (TypeAdapter<T>) new ThrowableTypeAdapter() : null;
	}

	/**
	 * Custom {@link TypeAdapter} for serializing {@link Throwable} objects.
	 */
	private static final class ThrowableTypeAdapter extends TypeAdapter<Throwable> {

		@Override
		public void write(JsonWriter out, Throwable value) throws IOException {
			if (value == null) {
				out.nullValue();
				return;
			}

			out.beginObject();
			writeField(out, "type", value.getClass().getSimpleName());
			writeField(out, "message", value.getMessage());

			if (value.getCause() != null) {
				out.name("cause");
				write(out, value.getCause());
			}

			if (value.getSuppressed().length > 0) {
				out.name("suppressed");
				out.beginArray();
				for (Throwable suppressed : value.getSuppressed()) {
					write(out, suppressed);
				}
				out.endArray();
			}

			out.endObject();
		}

		@Override
		public Throwable read(JsonReader in) {
			throw new UnsupportedOperationException("Deserialization of Throwable is not supported.");
		}

		/**
		 * Writes a field to the JSON output if the value is not null.
		 *
		 * @param out   the JSON writer
		 * @param name  the field name
		 * @param value the field value
		 * @throws IOException if an error occurs during writing
		 */
		private void writeField(JsonWriter out, String name, String value) throws IOException {
			if (value != null) {
				out.name(name).value(value);
			}
		}
	}
}
