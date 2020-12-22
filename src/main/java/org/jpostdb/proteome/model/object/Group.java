package org.jpostdb.proteome.model.object;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Group {
	private Set<Peptide> peptideSet;
	private List<Protein> proteins;

	public Group() {
		this.peptideSet = new HashSet<Peptide>();
		this.proteins = new ArrayList<Protein>();
	}

	public List<Protein> getProteins() {
		return proteins;
	}

	public void addProtein(Protein protein) {
		if(!this.proteins.contains(protein)) {
			this.proteins.add(protein);
		}
	}

	public void addPeptide(Peptide peptide) {
		this.peptideSet.add(peptide);
	}

	public boolean hasPeptide(Peptide peptide) {
		return this.peptideSet.contains(peptide);
	}

	public Set<Peptide> getPeptideSet() {
		return this.peptideSet;
	}

	@Override
	public String toString() {
		String string = "Protein Group (" + this.proteins.size() + ")";
		return string;
	}
}
