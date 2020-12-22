package org.jpostdb.proteome;

import java.util.HashMap;
import java.util.Map;

import org.jpostdb.proteome.util.StringUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JpostProteomeApplication {

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(JpostProteomeApplication.class);

		String port = StringUtil.getOption(args, "port");
		port = StringUtil.nvl(port);
		if(port.isEmpty()) {
			port = "8081";
		}

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("server.port", port);
		map.put(
			"spring.datasource.url",
			"jdbc:h2:./proteome-" + port
		);
		application.setDefaultProperties(map);

		application.run(args);
	}

}
