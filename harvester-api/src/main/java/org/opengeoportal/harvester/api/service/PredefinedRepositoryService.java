/**
 *
 */
package org.opengeoportal.harvester.api.service;

import java.util.List;

import org.opengeoportal.harvester.api.domain.CustomRepository;
import org.opengeoportal.harvester.api.domain.PredefinedRepository;

/**
 * Services related with {@link PredefinedRepository}.
 *
 * @author <a href="mailto:juanluisrp@geocat.net">Juan Luis Rodríguez</a>
 *
 */
public interface PredefinedRepositoryService {
    public List<PredefinedRepository> findAll();

    /**
     * Return all {@link PredefinedRepository} that have not an equivalent
     * instance with the same type and URL in {@link CustomRepository}.
     * 
     * @return the list of all {@link PredefinedRepository} with not existing
     *         {@link CustomRepository}.
     */
    public List<PredefinedRepository> findAllNotInCustomRepositories();

}
