package org.jpostdb.proteome.model;

public class PeptideSequence {
	private String sequence;
	private String dummy;

	public PeptideSequence(String sequence) {
		this.setSequence(sequence);
	}

	public String getSequence() {
		return sequence;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
		this.setDummy(sequence);
	}

	private void setDummy(String sequence) {
		String dummy = sequence.replace("I", "J");
		dummy = dummy.replace("L", "J");
		this.dummy = dummy;
	}

	@Override
	public int hashCode() {
		return this.dummy.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		boolean equals = false;
		if(obj instanceof PeptideSequence) {
			PeptideSequence other = (PeptideSequence)obj;
			equals = this.dummy.equals(other.dummy);
		}
		return equals;
	}

	@Override
	public String toString() {
		return this.sequence;
	}
}
