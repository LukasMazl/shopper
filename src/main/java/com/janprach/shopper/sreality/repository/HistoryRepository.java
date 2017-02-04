package com.janprach.shopper.sreality.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.janprach.shopper.sreality.entity.History;

@RepositoryRestResource(exported = false)
public interface HistoryRepository extends JpaRepository<History, Long> {
}
