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
 * The Class PredefinedRepositoryController.
 *
 * @author <a href="mailto:juanluisrp@geocat.net">Juan Luis Rodríguez</a>
 */
@Controller
public class PredefinedRepositoryController {
    
    /** The predefined repository service. */
    @Resource
    private PredefinedRepositoryService predefinedRepositoryService;

    /**
     * Gets the predefined repo list.
     *
     * @return the predefined repo list
     */
    @RequestMapping("/rest/predefinedRepositories/notInCustomRepos")
    public @ResponseBody List<PredefinedRepository> getPredefinedRepoList() {
        final List<PredefinedRepository> result = this.predefinedRepositoryService
                .findAllNotInCustomRepositories();

        return result;
    }

}
