package org.jpostdb.proteome.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.jpostdb.proteome.service.ProteinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ProteinController {
	@Autowired
	private ProteinService service;

	@RequestMapping("/proteins")
	@ResponseBody
	@CrossOrigin
	public List<String> getProteins(
			@RequestParam(value = "datasets", required = false) String datasets,
			@RequestParam(value = "accession", required = false) String accession,
			@RequestParam(value  ="score", required = false) String scoreString
	) throws Exception {
		boolean isAccession = false;
		if(accession != null) {
			try {
				isAccession = Boolean.parseBoolean(accession);
			}
			catch(Exception e) {
			}
		}

		double score = 0.0;
		if(scoreString != null) {
			try {
				score = Double.parseDouble(scoreString);
			}
			catch(Exception e) {
			}
		}

		List<String> list = new ArrayList<String>();
		if(datasets == null || datasets.isEmpty()) {
			String[] array = {
				"DS796_1", "DS796_2", "DS796_3", "DS796_4", "DS796_5",
				"DS796_6", "DS797_1", "DS797_2", "DS797_3", "DS797_4",
				"DS797_5", "DS798_1", "DS798_2", "DS798_3", "DS798_4",
				"DS798_5", "DS799_1", "DS799_2", "DS800_1", "DS800_2",
				"DS801_1", "DS802_1", "DS803_1", "DS804_1", "DS805_1",
				"DS806_1", "DS806_2", "DS807_1", "DS807_2", "DS808_1",
				"DS808_2", "DS810_1", "DS810_2", "DS810_3", "DS811_1",
				"DS811_2", "DS811_3", "DS812_1", "DS812_2", "DS812_3",
				"DS813_1", "DS813_2", "DS813_3", "DS814_1", "DS814_2",
				"DS814_3", "DS815_1", "DS815_2", "DS815_3", "DS816_1",
				"DS816_2", "DS816_3", "DS817_1", "DS817_2", "DS817_3",
				"DS820_1", "DS820_10", "DS820_2", "DS820_3", "DS820_4",
				"DS820_5", "DS820_6", "DS820_7", "DS820_8", "DS820_9",
				"DS822_1", "DS822_2", "DS822_3", "DS822_4", "DS823_1",
				"DS823_2", "DS823_3", "DS823_4", "DS824_1", "DS824_2",
				"DS824_3", "DS824_4", "DS869_1", "DS870_1", "DS870_2",
				"DS871_1"
			};
			for(String dataSet : array) {
				list.add(dataSet);
			}
		}
		else {
			StringTokenizer tokenizer = new StringTokenizer(datasets);
			while(tokenizer.hasMoreTokens()) {
				String token = tokenizer.nextToken().trim();
				list.add(token);
			}
		}

		long start = System.currentTimeMillis();

		List<String> result = this.service.getProteins(list, score, isAccession);

		long end = System.currentTimeMillis();
		double time = (double)(end - start) / 1000.0;

		System.out.println("Elepsed Time: " + time);
		return result;
	}
}
