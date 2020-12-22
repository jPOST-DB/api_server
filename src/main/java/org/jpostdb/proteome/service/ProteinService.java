package org.jpostdb.proteome.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jpostdb.proteome.model.object.Group;
import org.jpostdb.proteome.model.object.Peptide;
import org.jpostdb.proteome.model.object.Protein;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProteinService {
	private static final int MAX_GROUP_SIZE = 8;

	@Autowired
	private PeptideService pepService;

	public List<String> getProteins(List<String> datasets, boolean accession) throws Exception {
		List<String> answer = new ArrayList<String>();
		List<Protein> proteins = this.pepService.getProteins(datasets);

		Group group = new Group();
		Set<String> proteinSet = new HashSet<String>();
		for(Protein protein : proteins) {
			if(!proteinSet.contains(protein.getUniprot())) {
				group.getProteins().add(protein);
				proteinSet.add(protein.getUniprot());
			}
			group.getPeptideSet().addAll(protein.getPeptides());
		}

		List<Protein> list = getProteinListFromGroup(group);
		for(Protein protein : list) {
			answer.add(protein.getUniprot());
		}

		if(accession) {
			answer = this.getAccessionList(answer);
		}

		return answer;
	}

	private List<String> getAccessionList(List<String> proteins) {
		List<String> array = new ArrayList<String>();
		Set<String> set = new HashSet<String>();

		for(String protein : proteins) {
			String accession = protein;
			int index = accession.lastIndexOf("-");
			if(index > 0) {
				try {
					Integer.parseInt(accession.substring(index + 1));
					accession = accession.substring(0, index);
				}
				catch(Exception e) {
				}
			}

			if(!set.contains(accession)) {
				array.add(accession);
				set.add(accession);
			}
		}
		return array;
	}


	public List<Protein> getProteinListFromGroup(Group group) {
		List<Protein> answer = new ArrayList<Protein>();
		if(group.getProteins().size() == 0 || group.getPeptideSet().size() == 0) {
			return answer;
		}

		List<Group> groups = this.divideGroup(group);
		for(Group subGroup : groups) {
			if(subGroup.getProteins().size() <= MAX_GROUP_SIZE) {
				answer.addAll(this.findProteinList(subGroup));
			}
			else {
				List<Protein> candidate = null;
				List<Protein> minList = this.getMinimumProteinList(subGroup);
				for(Protein protein : minList) {
					List<Protein> list = new ArrayList<Protein>();
					list.add(protein);
					Group removedGroup = this.getRemovedGroup(subGroup, protein);
					list.addAll(this.getProteinListFromGroup(removedGroup));

					if(candidate == null || list.size() < candidate.size()) {
						candidate = list;
					}
				}
				answer.addAll(candidate);
			}
		}
		return answer;
	}

	private Group getRemovedGroup(Group group, Protein protein) {
		Group removedGroup = new Group();

		removedGroup.getProteins().addAll(group.getProteins());
		removedGroup.getPeptideSet().addAll(group.getPeptideSet());

		removedGroup.getProteins().remove(protein);
		removedGroup.getPeptideSet().removeAll(protein.getPeptides());

		List<Protein> removeList = new ArrayList<Protein>();
		for(Protein current : group.getProteins()) {
			boolean found = false;
			for(Peptide peptide : protein.getPeptides()) {
				if(group.getPeptideSet().contains(peptide)) {
					found = true;
				}
			}
			if(!found) {
				removeList.add(current);
			}
		}

		removedGroup.getProteins().removeAll(removeList);

		return removedGroup;
	}

	private List<Protein> getMinimumProteinList(Group group) {
		List<Protein> answer = null;
		Map<Peptide, List<Protein>> map = this.createMap(group);
		for(Peptide peptide : map.keySet()) {
			List<Protein> list = map.get(peptide);
			if(answer == null || list.size() < answer.size()) {
				answer = list;
			}
		}
		return answer;
	}

	private List<Protein> findProteinList(Group group) {
		List<Protein> answer = null;
		for(int num = 1; num <= group.getProteins().size() && answer == null; num++) {
			int[] counters = this.createCounters(num);

			while(answer == null && counters != null) {
				List<Protein> list = new ArrayList<Protein>();
				for(int i = 0; i < counters.length; i++) {
					list.add(group.getProteins().get(counters[i]));
				}
				Set<Peptide> peptideSet = new HashSet<Peptide>(group.getPeptideSet());
				for(Protein protein : list) {
					peptideSet.removeAll(protein.getPeptides());
				}
				if(peptideSet.isEmpty()) {
					answer = list;
				}
				else {
					counters = this.addCounter(counters, group.getProteins().size());
				}
			}
		}
		return answer;
	}

	private List<Group> divideGroup(Group group) {
		List<Group> groups = new ArrayList<Group>();
		Map<Peptide, List<Protein>> map = this.createMap(group);

		while(!map.isEmpty()) {
			Group element = this.extractGroup(map);
			groups.add(element);
		}
		return groups;
	}

	private Map<Peptide, List<Protein>> createMap(Group group) {
		Map<Peptide, List<Protein>> map = new HashMap<Peptide, List<Protein>>();
		for(Protein protein : group.getProteins()) {
			for(Peptide peptide : protein.getPeptides()) {
				if(group.getPeptideSet().contains(peptide)) {
					List<Protein> list = map.get(peptide);
					if(list == null) {
						list = new ArrayList<Protein>();
						map.put(peptide, list);
					}
					list.add(protein);
				}
			}
		}
		return map;
	}

	private Group extractGroup(Map<Peptide, List<Protein>> map) {
		Group group = new Group();

		Peptide peptide = map.keySet().iterator().next();
		group.addPeptide(peptide);
		group.getProteins().addAll(map.get(peptide));
		map.remove(peptide);

		for(int i = 0; i < group.getProteins().size(); i++) {
			Protein protein = group.getProteins().get(i);
			for(Peptide proteinPeptide : protein.getPeptides()) {
				if(map.containsKey(proteinPeptide)) {
					List<Protein> list = map.get(proteinPeptide);
					for(Protein element : list) {
						group.addProtein(element);
					}
					group.addPeptide(proteinPeptide);
					map.remove(proteinPeptide);
				}
			}
		}

		return group;
	}

	private int[] createCounters(int size) {
		int[] counters = new int[size];
		for(int i = 0; i < size; i++) {
			counters[i] = i;
		}
		return counters;
	}

	private int[] addCounter(int[] counters, int upper) {
		int[] newCounters = new int[counters.length];
		boolean changed = false;
		for(int i = 0; i < counters.length; i++) {
			int index = counters.length - 1 - i;
			if(changed) {
				newCounters[index] = counters[index];
			}
			else {
				int count = counters[index] + 1;
				if(count < upper - i) {
					newCounters[index] = count;
					for(int j = 1; j <= i; j++) {
						newCounters[index + j] = count + j;
					}
					changed = true;
				}
			}
		}

		if(!changed) {
			newCounters = null;
		}
		return newCounters;
	}

	public List<Protein> calculateGreedy(List<Peptide> peptides, List<Protein> proteins) {
		List<Protein> answer = new ArrayList<Protein>();
		Map<Protein, List<Peptide>> map = new HashMap<Protein, List<Peptide>>();
		Set<Peptide> peptideSet = new HashSet<Peptide>(peptides);

		for(Protein protein : proteins) {
			List<Peptide> list = new ArrayList<Peptide>(protein.getPeptides());
			map.put(protein, list);
		}

		while(peptideSet.size() > 0) {
			Protein protein = this.selectProtein(map);
			if(protein == null) {
				peptideSet.clear();
			}
			else {
				peptideSet.removeAll(protein.getPeptides());
				answer.add(protein);
			}
		}

		return answer;
	}

	private Protein selectProtein(Map<Protein, List<Peptide>> map) {
		Protein protein = null;
		int count = 0;
		for(Protein current : map.keySet()) {
			if(map.get(current).size() > count) {
				protein = current;
				count = current.getPeptides().size();
			}
		}

		if(protein != null) {
			for(List<Peptide> list : map.values()) {
				list.removeAll(protein.getPeptides());
			}
		}
		return protein;
	}

}
