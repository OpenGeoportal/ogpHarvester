/**
 * 
 */
package org.opengeoportal.harvester.api.dao;

import java.util.List;

import org.opengeoportal.harvester.api.domain.PredefinedRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * @author <a href="mailto:juanluisrp@geocat.net">Juan Luis Rodríguez</a>
 * 
 * 
 */
public interface PredefinedRepositoryRepository extends
		JpaRepository<PredefinedRepository, Long> {

	/**
	 * Return all the predefined repositories that has not been added yet to
	 * custom repositories;
	 * 
	 * @return
	 */
	@Query("select pr from PredefinedRepository pr where not exists (select "
			+ "cr from CustomRepository cr where cr.serviceType = pr.serviceType "
			+"and cr.url = pr.url and cr.deleted=false) "
			+ "order by pr.name asc")
	List<PredefinedRepository> findAllNotInCustomRepositories();

}
