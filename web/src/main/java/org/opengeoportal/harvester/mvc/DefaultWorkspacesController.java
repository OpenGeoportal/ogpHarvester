package org.opengeoportal.harvester.mvc;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.opengeoportal.harvester.api.domain.DefaultWorkspace;
import org.opengeoportal.harvester.api.service.DefaultWorkspaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * The Class DefaultWorkspacesController.
 */
@Controller
public class DefaultWorkspacesController {

    /** The workspace service. */
    @Autowired
    private DefaultWorkspaceService workspaceService;

    /**
     * Gets the workspaces.
     *
     * @param request the request
     * @param response the response
     * @return the workspaces
     */
    @RequestMapping(value = "/rest/defaultWorkspaces/getdefaultworkspaces", method = RequestMethod.GET)
    @ResponseBody
    public List<DefaultWorkspace> getWorkspaces(
            final HttpServletRequest request,
            final HttpServletResponse response) {

        return this.workspaceService.findAll();
    }

    /**
     * Removes the.
     *
     * @param id the id
     * @param request the request
     * @param response the response
     * @return the list
     */
    @RequestMapping(value = "/rest/defaultWorkspaces/removeworkspace/{id}", method = RequestMethod.POST)
    @ResponseBody
    public List<DefaultWorkspace> remove(
            @PathVariable(value = "id") final long id,
            final HttpServletRequest request,
            final HttpServletResponse response) {

        this.workspaceService.delete(id);

        return this.workspaceService.findAll();
    }

    /**
     * Save.
     *
     * @param name the name
     * @param request the request
     * @param response the response
     * @return the list
     */
    @RequestMapping(value = "/rest/defaultWorkspaces/addworkspace/{name}", method = RequestMethod.POST)
    @ResponseBody
    public List<DefaultWorkspace> save(
            @PathVariable(value = "name") final String name,
            final HttpServletRequest request,
            final HttpServletResponse response) {

        final DefaultWorkspace d = new DefaultWorkspace();

        d.setWorksp(name);

        this.workspaceService.save(d);

        return this.workspaceService.findAll();
    }

}
