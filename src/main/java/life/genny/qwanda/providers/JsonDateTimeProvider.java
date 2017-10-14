package life.genny.qwanda.providers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

@Provider
public class JsonDateTimeProvider implements ContextResolver<ObjectMapper> {
  private final ObjectMapper mapper;

  public JsonDateTimeProvider() {
    mapper = new ObjectMapper();
    mapper.registerModule(new JSR310Module());
    mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
  }

  @Override
  public ObjectMapper getContext(final Class<?> type) {
    return mapper;
  }
}
