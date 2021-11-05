package com.notenoughrunes.types;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import java.lang.reflect.Type;

public class BooleanTypeAdapter implements JsonDeserializer<Boolean>
{
	public Boolean deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
	{
		if (((JsonPrimitive) json).isBoolean())
		{
			return json.getAsBoolean();
		}

		if (((JsonPrimitive) json).isString())
		{
			String jsonValue = json.getAsString();
			if (jsonValue.equalsIgnoreCase("yes"))
			{
				return true;
			}
			else if (jsonValue.equalsIgnoreCase("no"))
			{
				return false;
			}
			else
			{
				return null;
			}
		}

		int code = json.getAsInt();
		return code != 0 && (code == 1 ? true : null);
	}
}
