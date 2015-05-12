package com.janprach.shopper.sreality.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.janprach.shopper.sreality.entity.RawResponse;

public interface RawResponseRepository extends JpaRepository<RawResponse, Long> {
	@Query("select rr from RawResponse rr, Estate e where rr.estate = e and e.srealityId = :srealityId")
	RawResponse findLatestBySrealityId(@Param("srealityId") final long srealityId);
}
