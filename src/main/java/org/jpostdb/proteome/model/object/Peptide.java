package org.jpostdb.proteome.model.object;

import java.io.Serializable;

public class Peptide implements Serializable {
	private static final long serialVersionUID = 1L;

	private String sequence;
	private String dummy;

	public Peptide(String sequence) {
		this.setSequence(sequence);
	}

	public String getSequence() {
		return sequence;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
		this.setDummy(sequence);
	}

	public void setDummy(String sequence) {
		String dummy = sequence.replace("I", "J");
		dummy = dummy.replace("L", "J");
		this.dummy = dummy;
	}

	public String getDummy() {
		return this.dummy;
	}

	@Override
	public int hashCode() {
		return this.dummy.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		boolean equals = false;
		if(obj instanceof Peptide) {
			Peptide other = (Peptide)obj;
			equals = this.dummy.equals(other.dummy);
		}
		return equals;
	}

	@Override
	public String toString() {
		return this.sequence;
	}
}
