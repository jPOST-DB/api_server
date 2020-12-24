package org.jpostdb.proteome.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jpostdb.proteome.model.entity.CacheFile;
import org.jpostdb.proteome.model.entity.DatasetObject;
import org.jpostdb.proteome.model.object.Protein;
import org.jpostdb.proteome.repository.CacheFileRepository;
import org.jpostdb.proteome.repository.DatasetObjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PeptideService {
	@Autowired
	private DatasetObjectRepository datasetRepository;

	@Autowired
	private CacheFileRepository cacheRepository;

	public List<Protein> getProteins(String dataset, double score) throws Exception {
		List<Protein> proteins = new ArrayList<Protein>();
		Set<String> uniprots = new HashSet<String>();

		List<DatasetObject> list = this.datasetRepository.findByName(dataset);
		for(DatasetObject object : list) {
			ObjectInputStream stream = new ObjectInputStream(
				new ByteArrayInputStream(object.getProteins())
			);

			@SuppressWarnings("unchecked")
			List<Protein> subList = (List<Protein>)stream.readObject();
			for(Protein protein : subList) {
				if(!uniprots.contains(protein.getUniprot())) {
					protein.cutOff(score);
					if(protein.getPeptides().size() > 0) {
						proteins.add(protein);
						uniprots.add(protein.getUniprot());
					}
				}
			}
			stream.close();
		}
		proteins.sort(
			(p1, p2) -> {
				int n1 = getProteinNum(p1);
				int n2 = getProteinNum(p2);
				return (n1 - n2);
			}
		);

		return proteins;
	}


	private int getProteinNum(Protein protein) {
		int num = 0;
		String uniprot = protein.getUniprot();
		int index = uniprot.lastIndexOf("-");
		if(index > 0 ) {
			num = Integer.parseInt(uniprot.substring(index + 1));
		}

		if(!protein.getMnemonic().contains("_")) {
			num += 10000;
		}
		return num;
	}


	public List<Protein> getProteins(List<String> datasets, double score) throws Exception {
		List<Protein> result = this.getProteinsFromCache(datasets, score);

		if(result == null) {
			result = new ArrayList<Protein>();
			for(String dataset : datasets) {
				result.addAll(this.getProteins(dataset, score));
			}
			this.saveCache(datasets, result);
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	protected List<Protein> getProteinsFromCache(List<String> datasets, double score) throws Exception {
		List<String> list = new ArrayList<String>();
		list.addAll(datasets);
		list.sort(
			(d1, d2) -> {
				int ret = d1.compareTo(d2);
				return ret;
			}
		);
		String key = String.join(",", list);

		List<Protein> proteins = null;

		List<CacheFile> caches = this.cacheRepository.findByDatasets(key);
		CacheFile cache = null;
		if(caches != null && !caches.isEmpty()) {
			cache = caches.get(0);
		}

		if(cache != null) {
			File cacheFile = new File(cache.getFilePath());
			if(cacheFile.exists()) {
				ObjectInputStream stream = new ObjectInputStream(
						new BufferedInputStream(
								new FileInputStream(cache.getFilePath())
								)
				);
				proteins = (List<Protein>)stream.readObject();
				stream.close();
			}
			else {
				for(CacheFile tmp : caches) {
					this.cacheRepository.delete(tmp);
				}
			}
		}

		List<Protein> cutOffProteins = new ArrayList<Protein>();
		for(Protein protein : proteins) {
			protein.cutOff(score);
			if(protein.getPeptides().size() > 0) {
				cutOffProteins.add(protein);
			}
		}

		return cutOffProteins;
	}

	protected void saveCache(List<String> datasets, List<Protein> proteins) throws Exception {
		List<String> list = new ArrayList<String>();
		list.addAll(datasets);
		list.sort(
			(d1, d2) -> {
				int ret = d1.compareTo(d2);
				return ret;
			}
		);
		String key = String.join(",", list);

		File file = File.createTempFile("cache_file", ".obj");
		ObjectOutputStream stream = new ObjectOutputStream(
				new BufferedOutputStream(
						new FileOutputStream(file.getAbsolutePath())
				)
		);
		stream.writeObject(proteins);
		stream.close();

		CacheFile cache = new CacheFile();
		cache.setDatasets(key);
		cache.setFilePath(file.getAbsolutePath());
		this.cacheRepository.save(cache);
	}
}
