package org.jpostdb.proteome.model.object;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Protein implements Serializable {
	private static final long serialVersionUID = 1L;

	private String uniprot;
	private String mnemonic;
	private List<Peptide> peptides;

	public Protein(String uniprot) {
		this.setUniprot(uniprot);
		this.setPeptides(new ArrayList<Peptide>());
	}
	public String getUniprot() {
		return uniprot;
	}
	public void setUniprot(String uniprot) {
		this.uniprot = uniprot;
	}
	public String getMnemonic() {
		return mnemonic;
	}

	public void setMnemonic(String mnemonic) {
		this.mnemonic = mnemonic;
	}

	public List<Peptide> getPeptides() {
		return peptides;
	}
	public void setPeptides(List<Peptide> peptides) {
		this.peptides = peptides;
	}

	public void cutOff(double score) {
		List<Peptide> list = new ArrayList<Peptide>();
		for(Peptide peptide : this.peptides) {
			if(peptide.getScore() >= score) {
				list.add(peptide);
			}
		}
		this.peptides = list;
	}

	@Override
	public String toString() {
		String string = "Protein(" + this.uniprot + ")";
		return string;
	}


}
