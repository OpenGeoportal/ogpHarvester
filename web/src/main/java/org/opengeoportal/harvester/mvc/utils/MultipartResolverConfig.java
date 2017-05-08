package org.opengeoportal.harvester.mvc.utils;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

@Configuration
public class MultipartResolverConfig {


  @Bean
  public CommonsMultipartResolver multipartResolver() {
      return new CommonsMultipartResolver();
  }

  
}
