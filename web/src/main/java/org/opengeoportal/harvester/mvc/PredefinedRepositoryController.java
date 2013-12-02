/**
 * 
 */
package org.opengeoportal.harvester.mvc;

import java.util.List;

import javax.annotation.Resource;

import org.opengeoportal.harvester.api.domain.PredefinedRepository;
import org.opengeoportal.harvester.api.service.PredefinedRepositoryService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author <a href="mailto:juanluisrp@geocat.net">Juan Luis Rodríguez</a>
 * 
 */
@Controller
public class PredefinedRepositoryController {
	@Resource
	private PredefinedRepositoryService predefinedRepositoryService;

	@RequestMapping("/rest/predefinedRepositories/notInCustomRepos")
	public @ResponseBody List<PredefinedRepository> getPredefinedRepoList() {
		List<PredefinedRepository> result = predefinedRepositoryService
				.findAllNotInCustomRepositories();

		return result;
	}

}
