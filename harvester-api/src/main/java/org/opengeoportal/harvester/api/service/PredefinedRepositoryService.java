/**
 * 
 */
package org.opengeoportal.harvester.api.service;

import java.util.List;

import org.opengeoportal.harvester.api.domain.PredefinedRepository;


/**
 * Services related with {@link PredefinedRepository}.
 * @author <a href="mailto:juanluisrp@geocat.net">Juan Luis Rodríguez</a>
 *
 */
public interface PredefinedRepositoryService {
	public List<PredefinedRepository> findAll();

}
