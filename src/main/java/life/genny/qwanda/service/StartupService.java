package life.genny.qwanda.service;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import life.genny.qwanda.entity.BaseEntity;

/**
 * This Service bean demonstrate various JPA manipulations of {@link BaseEntity}
 *
 * @author Adam Crow
 */
@Singleton
@Startup
public class StartupService {



  @Inject
  private BaseEntityService service;

  @PostConstruct
  public void init() {


  }
}
