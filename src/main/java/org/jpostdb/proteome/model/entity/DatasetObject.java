package org.jpostdb.proteome.model.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Lob;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name="dataset_objects",
	   indexes = @Index(columnList = "name"))
@NamedQuery(name="DatasetObject.findAll", query="SELECT d FROM DatasetObject d")
public class DatasetObject implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column
	private String name;

	@Lob
	@Column
	private byte[] proteins;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public byte[] getProteins() {
		return proteins;
	}

	public void setProteins(byte[] proteins) {
		this.proteins = proteins;
	}
}
