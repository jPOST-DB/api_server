package org.jpostdb.proteome.controller;

import org.jpostdb.proteome.model.response.Response;
import org.jpostdb.proteome.service.SetupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SetupController {
	@Autowired
	private SetupService service;

	@RequestMapping("/setup")
	@ResponseBody
	public Response setup() throws Exception {
		Response response = new Response();
		response.setSuccess(true);

		double start = System.currentTimeMillis();
		this.service.setup();
		double end = System.currentTimeMillis();

		Double time = (end - start) / 1000.0;
		response.setResult(time);

		return response;
	}
}
