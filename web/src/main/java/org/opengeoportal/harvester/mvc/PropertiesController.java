package org.opengeoportal.harvester.mvc;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Component
public class PropertiesController {
  
 
  @Value("#{setup['dataingest.api.url']}")
  private String dataIngestUrl;
  
  @RequestMapping(value = "/rest/dataIngest/endPoint", method = RequestMethod.GET)
  @ResponseBody
  public String getDataIngestURL() {
      
      return dataIngestUrl;
  }

}
