package org.jpostdb.proteome.model.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name="cache_files3",
	   indexes = @Index(columnList = "datasets"))
@NamedQuery(name="CacheFile.findAll", query="SELECT c FROM CacheFile c")
public class CacheFile implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(length = 4096)
	private String datasets;

	@Column
	private String filePath;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDatasets() {
		return datasets;
	}

	public void setDatasets(String datasets) {
		this.datasets = datasets;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
}
