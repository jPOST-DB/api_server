package org.jpostdb.proteome.repository;

import java.util.List;

import org.jpostdb.proteome.model.entity.CacheFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface CacheFileRepository extends JpaRepository<CacheFile, Integer>,
								QuerydslPredicateExecutor<CacheFile> {
	public List<CacheFile> findByDatasets(String datasets);
}
