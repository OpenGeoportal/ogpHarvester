/**
 * 
 */
package org.opengeoportal.harvester.api.dao;

import org.opengeoportal.harvester.api.domain.PredefinedRepository;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author <a href="mailto:juanluisrp@geocat.net">Juan Luis Rodríguez</a>
 * 
 * 
 */
public interface PredefinedRepositoryRepository extends
		JpaRepository<PredefinedRepository, Long> {

}
