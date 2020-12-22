package org.jpostdb.proteome.service;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Resource;
import org.jpostdb.proteome.model.Const;
import org.jpostdb.proteome.model.entity.DatasetObject;
import org.jpostdb.proteome.model.object.Peptide;
import org.jpostdb.proteome.model.object.Protein;
import org.jpostdb.proteome.repository.DatasetObjectRepository;
import org.jpostdb.proteome.util.HttpUtil;
import org.jpostdb.proteome.util.SparqlUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;

@Service
public class SetupService {
	@Autowired
	private DatasetObjectRepository repository;

	@Transactional
	public void setup() throws Exception {
		this.init();
		List<String> datasets = this.getDatasets();
		this.saveDataset(datasets.get(0));

 		int count = 1;
		for(String dataset : datasets) {
			String message = String.format(
					"Dataset %s [%d/%d]",
					dataset,
					count,
					datasets.size()
			);
			System.out.println(message);
			this.saveDataset(dataset);
			count++;
		}
	}

	protected void init() {
		this.repository.deleteAll();
	}

	protected List<String> getDatasets() throws Exception {
		List<String> datasets = new ArrayList<String>();

		String url = Const.SPARQLIST + "dbi_dataset_table";
		JsonNode node = HttpUtil.getJson(url, null);

		Iterator<JsonNode> iterator = node.get("results").get("bindings").iterator();;
		while(iterator.hasNext()) {
			JsonNode child = iterator.next();
			String dataset = child.get("dataset_id").get("value").asText();
			datasets.add(dataset);
		}

		return datasets;
	}

	protected String getString(QuerySolution solution, String key) {
		String value = "";
		Literal literal = solution.getLiteral(key);
		if(literal != null) {
			value = literal.getString();
		}
		return value;
	}

	protected String getUrl(QuerySolution solution, String key) {
		String value = "";
		Resource resource = solution.getResource(key);
		if(resource != null) {
			value = resource.getURI().toString();
		}
		return value;
	}

	protected void saveDataset(String dataset) throws Exception {
		List<Protein> proteins = this.getProteins(dataset);
		System.out.println("    " + proteins.size() + " Proteins.");

		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(buffer);
		out.writeObject(proteins);
		out.close();
		buffer.close();

		DatasetObject saveData = new DatasetObject();
		saveData.setName(dataset);
		saveData.setProteins(buffer.toByteArray());

		this.repository.save(saveData);
	}

	protected List<Protein> getProteins(String dataset) throws Exception {
		List<Protein> proteins = new ArrayList<Protein>();

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("dataset", ":" + dataset);

		Map<String, String> mnemonicMap = new HashMap<String, String>();
		Map<String, Protein> proteinMap = new HashMap<String, Protein>();

		SparqlUtil.callSparql(
			Const.ENDPOINT,
			"mnemonic",
			parameters,
			(resultSet) -> {
				while(resultSet.hasNext()) {
					QuerySolution solution = resultSet.next();
					String accession = this.getString(solution, "accession");
					String mnemonic = this.getString(solution, "mnemonic");
					mnemonicMap.put(accession, mnemonic);
				}
			}
		);

		SparqlUtil.callSparql(
			Const.ENDPOINT,
			"proteins",
			parameters,
			(resultSet) -> {
				while(resultSet.hasNext()) {
					QuerySolution solution = resultSet.next();

					String uniprot = this.getString(solution, "uniprot");
					String sequence = this.getString(solution, "pep_seq" );

					String mnemonic = "";
					for(String key : mnemonicMap.keySet()) {
						if(uniprot.startsWith(key)) {
							mnemonic = mnemonicMap.get(key);
						}
					}

					Protein protein = null;
					if(proteinMap.containsKey(uniprot)) {
						protein = proteinMap.get(uniprot);
					}
					else {
						protein = new Protein(uniprot);
						protein.setUniprot(uniprot);
						protein.setMnemonic(mnemonic);
						protein.setPeptides(new ArrayList<Peptide>());
						proteinMap.put(uniprot, protein);
						proteins.add(protein);
					}
					Peptide peptide = new Peptide(sequence);
					peptide.setSequence(sequence);
					protein.getPeptides().add(peptide);
				}
			}
		);

		return proteins;
	}

/*
	private List<String> getDatasets() throws IOException {
		this.repository.deleteAll();

		List<String> datasets = this.getDatasets();
	}

	public List<Peptide> getPeptides(List<String> dataSets) throws IOException {
		List<Peptide> peptides = new ArrayList<Peptide>();

		Map<String, String> parameters = this.getParameter(dataSets);
		SparqlUtil.callSparql(
			Const.ENDPOINT,
			"peptides",
			parameters,
			(resultSet) -> {
				while(resultSet.hasNext()) {
					QuerySolution solution = resultSet.next();
					Literal literal = solution.getLiteral("pep_seq");
					Peptide peptide = new Peptide(literal.getString());
					peptides.add(peptide);
				}
			}
		);

		return peptides;
	}

	public List<Protein> getProteins(List<String> dataSets) throws Exception {

		List<Protein> proteins = new ArrayList<Protein>();
		Map<String, String> parameters = this.getParameter(dataSets);
		SparqlUtil.callSparql(
			Const.ENDPOINT,
			"proteins",
			parameters,
			(resultSet) -> {
				Map<String, Protein> map = new HashMap<String, Protein>();
				while(resultSet.hasNext()) {
					QuerySolution solution = resultSet.next();


					Literal literal = solution.getLiteral("pep_seq");
					Peptide peptide = new Peptide(literal.getString());

					literal = solution.getLiteral("uniprot");
					String uniprot = literal.getString();

					Protein protein = map.get(uniprot);
					if(protein == null) {
						protein = new Protein(uniprot);
						protein.setPeptides(new ArrayList<Peptide>());
						map.put(uniprot, protein);
						proteins.add(protein);
					}

					protein.getPeptides().add(peptide);
				}
			}
		);

		return proteins;
	}

	private Map<String, String> getParameter(List<String> dataSets) {
		String string = "";
		for(String dataSet : dataSets) {
			string = string + " :" + dataSet;
		}

		Map<String, String> map = new HashMap<String, String>();
		map.put("datasets", string);
		return map;
	}


	public void setup() throws Exception {
		List<String> datasets = this.getDatasets();
		int count = 1;
		for(String dataset : datasets) {
			List<String> list = new ArrayList<String>();
			list.add(dataset);

			List<Protein> proteins = this.getProteins(list);
			this.saveDataset(dataset, proteins);


		}
	}

	private void saveDataset(String dataset, List<Protein> proteins) throws IOException {
		List<Protein> list = new ArrayList<Protein>();
		list.addAll(proteins);

		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(buffer);
		out.writeObject(list);
		out.close();
		buffer.close();

		DatasetObject saveData = new DatasetObject();
		saveData.setName(dataset);
		saveData.setProteins(buffer.toByteArray());

		this.repository.save(saveData);
	}
*/
}
