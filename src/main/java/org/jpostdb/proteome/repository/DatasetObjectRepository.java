package org.jpostdb.proteome.repository;

import java.util.List;

import org.jpostdb.proteome.model.entity.DatasetObject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface DatasetObjectRepository extends JpaRepository<DatasetObject, Integer>,
												QuerydslPredicateExecutor<DatasetObject> {
	public List<DatasetObject> findByName(String name);
}
