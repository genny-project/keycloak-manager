package life.genny.qwanda.endpoint;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;
import life.genny.qwanda.providers.CORSFilter;
import life.genny.qwanda.providers.JsonDateTimeProvider;


@ApplicationPath("")
public class RestApplication extends Application {
  public RestApplication() {

  }

  @Override
  public Set<Class<?>> getClasses() {
    final Set<Class<?>> resources = new HashSet<Class<?>>();
    resources.add(JsonDateTimeProvider.class);
    resources.add(KeycloakEndpoint.class);
    resources.add(CORSFilter.class);

    resources.add(io.swagger.jaxrs.listing.ApiListingResource.class);
    resources.add(io.swagger.jaxrs.listing.SwaggerSerializers.class);

    return resources;
  }

}
