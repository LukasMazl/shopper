package com.janprach.shopper.sreality.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.janprach.shopper.sreality.entity.Estate;

@RepositoryRestResource(path = "estates")
public interface EstateRepository extends JpaRepository<Estate, Long> {
	Estate findByAddressLike(final String address);

	Estate findBySrealityId(final long srealityId);

	Page<Estate> findAllByActiveAndVisibleAndAddressLike(
			@Param("active") final boolean active,
			@Param("visible") final boolean visible,
			@Param("address") final String address, final Pageable pageable);

	List<Estate> findAllByActive(final boolean active);

	@Modifying
	@Query("UPDATE Estate e SET e.stars = :stars WHERE e.srealityId = :srealityId")
	int setStarsFor(@Param("srealityId") final long srealityId,
			@Param("stars") final int stars);
}
