/**
 * 
 */
package org.opengeoportal.harvester.api.service;

import java.util.List;

import javax.annotation.Resource;

import org.opengeoportal.harvester.api.dao.PredefinedRepositoryRepository;
import org.opengeoportal.harvester.api.domain.PredefinedRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;

/**
 * @author <a href="mailto:juanluisrp@geocat.net">Juan Luis Rodríguez</a>
 * 
 * 
 */
@Service
public class PredefinedRepositoryServiceImpl implements
		PredefinedRepositoryService {

	@Resource
	private PredefinedRepositoryRepository predefinedRepoRepository;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.opengeoportal.harvester.api.service.PredefinedRepositoryService#findAll
	 * ()
	 */
	@Override
	public List<PredefinedRepository> findAll() {
		Sort sort = new Sort(Sort.Direction.ASC,
				Lists.newArrayList(PredefinedRepository.COLUMN_SERVICE_NAME));
		return predefinedRepoRepository.findAll(sort);
	}

}
