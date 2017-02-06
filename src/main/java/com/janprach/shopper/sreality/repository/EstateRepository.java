package com.janprach.shopper.sreality.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

	List<Estate> findAllByStarsGreaterThanAndAddressLike(
			@Param("stars") final Integer stars,
			@Param("address") final String address,
			final Sort sort);

	List<Estate> findAllByDuplicityId(
			@Param("duplicityId") final Long duplicityId,
			final Sort sort);

	List<Estate> findAllByDuplicityIdNotAndAddressLike(
			@Param("duplicityId") final Long duplicityId,
			@Param("address") final String address,
			final Sort sort);

	List<Estate> findAllByActive(final boolean active);

 	@Query(value = "SELECT * FROM estate "
 			+ " WHERE created_at >= DATEADD(MONTH, -3, GETDATE()) "
 			+ " AND address LIKE :address "
 			+ " AND price = :price "
 			+ " AND (area_total = :areaTotal OR area_usable = :areaUsable) ", nativeQuery = true)
	List<Estate> findDuplicatesByAddressAndPriceAndArea(@Param("address") final String address,
			@Param("price") final Long price, @Param("areaTotal") final Integer areaTotal,
			@Param("areaUsable") final Integer areaUsable);

 	@Query(value = "SELECT * FROM estate "
 			+ " WHERE created_at >= DATEADD(MONTH, -3, GETDATE()) "
 			+ " AND price = :price "
 			+ " AND area_total = :areaTotal "
 			+ " AND area_usable = :areaUsable ", nativeQuery = true)
	List<Estate> findDuplicatesByPriceAndArea(@Param("price") final Long price,
			@Param("areaTotal") final Integer areaTotal, @Param("areaUsable") final Integer areaUsable);

 	@Query(value = "SELECT * FROM estate "
 			+ " WHERE created_at >= DATEADD(MONTH, -3, GETDATE()) "
 			+ " AND address LIKE :address "
 			+ " AND area_total = :areaTotal "
 			+ " AND area_usable = :areaUsable ", nativeQuery = true)
	List<Estate> findDuplicatesByAddressAndArea(@Param("address") final String address,
			@Param("areaTotal") final Integer areaTotal, @Param("areaUsable") final Integer areaUsable);

	@Modifying
	@Query("UPDATE Estate e SET e.stars = :stars WHERE e.srealityId = :srealityId")
	int setStarsFor(@Param("srealityId") final long srealityId,
			@Param("stars") final int stars);

	@Modifying
	@Query("UPDATE Estate e SET e.visible = :visible WHERE e.srealityId = :srealityId")
	int setVisibleFor(@Param("srealityId") final long srealityId,
			@Param("visible") final boolean visible);

	@Modifying
	@Query("UPDATE Estate e SET e.note = :note WHERE e.srealityId = :srealityId")
	int setNoteFor(@Param("srealityId") final long srealityId,
			@Param("note") final String note);
}
