package org.jpostdb.proteome.util;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class HttpUtil {
	public static JsonNode getJson(String url,  Map<String, String> parameters) throws Exception {
		URIBuilder builder = new URIBuilder(url);
		if(parameters != null) {
			for(String key : parameters.keySet()) {
				String value = parameters.get(key);
				builder.setParameter(key,  value);
			}
		}

		HttpGet request = new HttpGet(builder.build());

		HttpClient client = HttpClients.createDefault();
		HttpResponse response = client.execute(request);
		String content = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

		ObjectMapper mapper = new ObjectMapper();
		JsonNode node = mapper.readTree(content);

		return node;
	}
}
