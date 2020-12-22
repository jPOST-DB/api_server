package org.jpostdb.proteome.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;

public class SparqlUtil {
	public static interface ResultGetter {
		public void getResult(ResultSet resultSet);
	}

	public static String getSparql(String template, Map<String, String> parameters) throws IOException {
		InputStream stream = SparqlUtil.class.getResourceAsStream("/templates/sparql/" + template + ".sparql");
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		StringWriter string = new StringWriter();
		PrintWriter writer = new PrintWriter(string);

		String line = null;
		while((line = reader.readLine()) != null) {
			if(parameters != null) {
				for(String key : parameters.keySet()) {
					String value = parameters.get(key);
					line = line.replace("{{" + key + "}}", value);
				}
			}
			writer.println(line);
		}
		reader.close();
		writer.close();
		string.close();

		return string.toString();
	}

	public static void callSparql(
			String endpoint,
			String template,
			Map<String, String> parameters,
			ResultGetter getter
	) throws IOException {
		String sparql = SparqlUtil.getSparql(template, parameters);
		Query query = QueryFactory.create(sparql);
		QueryExecution execution = QueryExecutionFactory.sparqlService(endpoint, query);
		ResultSet resultSet = execution.execSelect();

		getter.getResult(resultSet);

		execution.close();
	}
}
