package life.genny.qwanda.util;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


@ApplicationScoped
public class PersistenceHelper {

  @PersistenceContext(unitName = "genny-persistence-unit")
  private EntityManager em;

  public EntityManager getEntityManager() {
    return em;
  }
}
