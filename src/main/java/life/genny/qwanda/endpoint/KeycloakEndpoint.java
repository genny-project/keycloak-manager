package life.genny.qwanda.endpoint;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.hibernate.proxy.pojo.javassist.JavassistLazyInitializer;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import life.genny.qwanda.Answer;
import life.genny.qwanda.AnswerLink;
import life.genny.qwanda.Ask;
import life.genny.qwanda.GPS;
import life.genny.qwanda.Question;
import life.genny.qwanda.attribute.Attribute;
import life.genny.qwanda.attribute.EntityAttribute;
import life.genny.qwanda.entity.BaseEntity;
import life.genny.qwanda.message.QDataAskMessage;
import life.genny.qwanda.message.QDataBaseEntityMessage;
import life.genny.qwanda.model.Setup;
import life.genny.qwanda.rule.Rule;
import life.genny.qwanda.service.BaseEntityService;



/**
 * Transactional JAX-RS endpoint
 *
 * @author Adam Crow
 */

@Path("/keycloak")
@Api(value = "/keycloak", description = "Genny Keycloak API", tags = "keycloak")
@Produces(MediaType.APPLICATION_JSON)


@RequestScoped
@Transactional

public class KeycloakEndpoint {

  @Context
  SecurityContext sc;

  @Inject
  private BaseEntityService service;

  public static class HibernateLazyInitializerSerializer
      extends JsonSerializer<JavassistLazyInitializer> {

    @Override
    public void serialize(final JavassistLazyInitializer initializer,
        final JsonGenerator jsonGenerator, final SerializerProvider serializerProvider)
        throws IOException, JsonProcessingException {
      jsonGenerator.writeNull();
    }
  }

  @POST
  @Consumes("application/json")
  @Path("/rules")
  public Response create(final Rule entity) {
    service.insert(entity);
    return Response.created(UriBuilder.fromResource(KeycloakEndpoint.class)
        .path(String.valueOf(entity.getId())).build()).build();
  }


  @POST
  @Consumes("application/json")
  @Path("/attributes")
  public Response create(final Attribute entity) {
    service.insert(entity);
    return Response.created(UriBuilder.fromResource(KeycloakEndpoint.class)
        .path(String.valueOf(entity.getId())).build()).build();
  }

  @POST
  @Consumes("application/json")
  @Path("/questions")
  public Response create(final Question entity) {
    service.insert(entity);
    return Response.created(UriBuilder.fromResource(KeycloakEndpoint.class)
        .path(String.valueOf(entity.getId())).build()).build();
  }

  @POST
  @Consumes("application/json")
  @Path("/asks")
  public Response create(final Ask entity) {
    service.insert(entity);
    return Response.created(UriBuilder.fromResource(KeycloakEndpoint.class)
        .path(String.valueOf(entity.getId())).build()).build();
  }

  @POST
  @Consumes("application/json")
  @Path("/gpss")
  public Response create(final GPS entity) {
    service.insert(entity);
    return Response.created(UriBuilder.fromResource(KeycloakEndpoint.class)
        .path(String.valueOf(entity.getId())).build()).build();
  }


  @POST
  @Consumes("application/json")
  @Path("/answers")
  public Response create(final Answer entity) {

    service.insert(entity);
    return Response.created(UriBuilder.fromResource(KeycloakEndpoint.class)
        .path(String.valueOf(entity.getId())).build()).build();
  }

  @POST
  @Consumes("application/json")
  @Path("/baseentitys")
  public Response create(final BaseEntity entity) {
    service.insert(entity);
    return Response.created(UriBuilder.fromResource(KeycloakEndpoint.class)
        .path(String.valueOf(entity.getId())).build()).build();
  }

  @POST
  @Path("/baseentitys")
  @Consumes("application/json")
  public Response create(@FormParam("name") final String name,
      @FormParam("uniqueCode") final String uniqueCode) {
    final BaseEntity entity = new BaseEntity(uniqueCode, name);
    service.insert(entity);
    return Response.created(UriBuilder.fromResource(KeycloakEndpoint.class)
        .path(String.valueOf(entity.getId())).build()).build();
  }

  @GET
  @Path("/rules/{id}")
  public Response fetchRuleById(@PathParam("id") final Long id) {
    final Rule entity = service.findRuleById(id);
    return Response.status(200).entity(entity).build();
  }

  @GET
  @Path("/baseentitys/{sourceCode}")
  public Response fetchBaseEntityByCode(@Context final ServletContext servletContext,
      @PathParam("sourceCode") final String code) {

    final BaseEntity entity = service.findBaseEntityByCode(code);
    return Response.status(200).entity(entity).build();
  }

  @GET
  @Path("/questions/{id}")
  public Response fetchQuestionById(@PathParam("id") final Long id) {
    final Question entity = service.findQuestionById(id);
    return Response.status(200).entity(entity).build();
  }

  @GET
  @Path("/questions")
  public Response fetchQuestions() {
    final List<Question> entitys = service.findQuestions();
    return Response.status(200).entity(entitys).build();
  }

  @GET
  @Path("/rules")
  public Response fetchRules() {
    final List<Rule> entitys = service.findRules();

    System.out.println(entitys);
    return Response.status(200).entity(entitys).build();
  }

  @GET
  @Path("/asks")
  public Response fetchAsks() {
    final List<Ask> entitys = service.findAsks();

    System.out.println(entitys);
    return Response.status(200).entity(entitys).build();
  }

  @GET
  @Path("/asksmsg")
  public Response fetchAsksMsg() {
    final List<Ask> entitys = service.findAsks();
    final QDataAskMessage qasks = new QDataAskMessage(entitys.toArray(new Ask[0]));
    System.out.println(qasks);
    return Response.status(200).entity(qasks).build();
  }

  @GET
  @Path("/attributes/{id}")
  public Response fetchAttributeById(@PathParam("id") final Long id) {
    final Attribute entity = service.findAttributeById(id);
    return Response.status(200).entity(entity).build();
  }

  @GET
  @Path("/asks/{id}")
  public Response fetchAskById(@PathParam("id") final Long id) {
    final Ask entity = service.findAskById(id);
    return Response.status(200).entity(entity).build();
  }

  @GET
  @Path("/answers/{id}")
  public Response fetchAnswerById(@PathParam("id") final Long id) {
    final Answer entity = service.findAnswerById(id);
    return Response.status(200).entity(entity).build();
  }

  @GET
  @Path("/contexts/{id}")
  public Response fetchContextById(@PathParam("id") final Long id) {
    final life.genny.qwanda.Context entity = service.findContextById(id);
    return Response.status(200).entity(entity).build();
  }

  @GET
  @Path("/baseentitys/{sourceCode}/attributes")
  @ApiOperation(value = "attributes", notes = "BaseEntity Attributes")
  @Produces(MediaType.APPLICATION_JSON)
  public List<EntityAttribute> fetchAttributesByBaseEntityCode(
      @PathParam("sourceCode") final String code) {
    final List<EntityAttribute> entityAttributes = service.findAttributesByBaseEntityCode(code);
    return entityAttributes;
  }

  @GET
  @Path("/baseentitys/{id}/gps")
  @ApiOperation(value = "gps", notes = "Target BaseEntity GPS")
  @Produces(MediaType.APPLICATION_JSON)
  public List<GPS> fetchGPSByTargetBaseEntityId(@PathParam("id") final Long id) {
    final List<GPS> items = service.findGPSByTargetBaseEntityId(id);
    return items;
  }


  @GET
  @Path("/baseentitys/{id}/asks/source")
  @ApiOperation(value = "asks", notes = "Source BaseEntity Asks")
  @Produces(MediaType.APPLICATION_JSON)
  public List<Ask> fetchAsksBySourceBaseEntityId(@PathParam("id") final Long id) {
    final List<Ask> items = service.findAsksBySourceBaseEntityId(id);
    return items;
  }

  @GET
  @Path("/baseentitys/{id}/asks/target")
  @ApiOperation(value = "asks", notes = "BaseEntity Asks about Targets")
  @Produces(MediaType.APPLICATION_JSON)
  public List<Ask> fetchAsksByTargetBaseEntityId(@PathParam("id") final Long id) {
    final List<Ask> items = service.findAsksBySourceBaseEntityId(id);
    return items;
  }

  @GET
  @Path("/baseentitys/{id}/answers")
  @ApiOperation(value = "answers", notes = "BaseEntity AnswerLinks")
  @Produces(MediaType.APPLICATION_JSON)
  public List<AnswerLink> fetchAnswersByTargetBaseEntityId(@PathParam("id") final Long id) {
    final List<AnswerLink> items = service.findAnswersByTargetBaseEntityId(id);
    return items;
  }

  @GET
  @Path("/answers")
  @ApiOperation(value = "answers", notes = "AnswerLinks")
  @Produces(MediaType.APPLICATION_JSON)
  public List<AnswerLink> fetchAnswerLinks() {
    final List<AnswerLink> items = service.findAnswerLinks();
    return items;
  }

  // @GET
  // @Path("/logout")
  // @ApiOperation(value = "Logout", notes = "Logout", response = String.class)
  // @Produces(MediaType.APPLICATION_JSON)
  // public Response logout(@Context final javax.servlet.http.HttpServletRequest request) {
  // try {
  // request.logout();
  // } catch (final ServletException e) {
  // // TODO Auto-generated catch block
  // e.printStackTrace();
  // }
  // return Response.status(200).entity("Logged Out").build();
  // }


  @GET
  @Path("/init")
  @Produces("application/json")
  public Response init() {
    if (sc != null) {
      if (sc.getUserPrincipal() != null) {
        if (sc.getUserPrincipal() instanceof KeycloakPrincipal) {
          final KeycloakPrincipal<KeycloakSecurityContext> kp =
              (KeycloakPrincipal<KeycloakSecurityContext>) sc.getUserPrincipal();

          service.init(kp.getKeycloakSecurityContext());
        }
      }
    }
    return Response.status(200).entity("Initialised").build();
  }

  @GET
  @Path("/baseentitys")
  @Produces("application/json")
  public Response getAll() {
    return Response.status(200).entity(service.getAll()).build();
  }



  @GET
  @Path("/setup")
  @Produces("application/json")
  public Response getSetup() {
    Setup setup = new Setup();
    setup.setLayout("error-layout1");

    // this will set the user id as userName
    if (sc != null) {
      if (sc.getUserPrincipal() != null) {
        sc.getUserPrincipal().getName();

        if (sc.getUserPrincipal() instanceof KeycloakPrincipal) {
          final KeycloakPrincipal<KeycloakSecurityContext> kp =
              (KeycloakPrincipal<KeycloakSecurityContext>) sc.getUserPrincipal();

          // this is how to get the real userName (or rather the login
          // name)

          System.out.println("kc context:" + kp.getKeycloakSecurityContext());
          setup = service.setup(kp.getKeycloakSecurityContext());
        }
      }
    }
    return Response.status(200).entity(setup).build();

  }

  @GET
  @Path("/baseentitys/{sourceCode}/linkcodes/{linkCode}")
  @Produces("application/json")
  public Response getTargets(@PathParam("sourceCode") final String sourceCode,
      @DefaultValue("LNK_CORE") @PathParam("linkCode") final String linkCode) {
    final List<BaseEntity> targets = service.findChildrenByAttributeLink(sourceCode, linkCode);

    BaseEntity[] beArr = new BaseEntity[targets.size()];
    beArr = targets.toArray(beArr);
    final QDataBaseEntityMessage msg = new QDataBaseEntityMessage(beArr, sourceCode, linkCode);

    return Response.status(200).entity(msg).build();
  }

  @POST
  @Path("/baseentitys/uploadcsv")
  @Consumes("multipart/form-data")
  public Response uploadFile(final MultipartFormDataInput input) throws IOException {

    final Map<String, List<InputPart>> uploadForm = input.getFormDataMap();

    // Get file data to save
    final List<InputPart> inputParts = uploadForm.get("attachment");

    for (final InputPart inputPart : inputParts) {
      try {

        final MultivaluedMap<String, String> header = inputPart.getHeaders();
        final String fileName = getFileName(header);

        // convert the uploaded file to inputstream
        final InputStream inputStream = inputPart.getBody(InputStream.class, null);

        // byte[] bytes = IOUtils.toByteArray(inputStream);
        // constructs upload file path
        // writeFile(bytes, fileName);
        service.importBaseEntitys(inputStream, fileName);

        return Response.status(200).entity("Imported file name : " + fileName).build();

      } catch (final Exception e) {
        e.printStackTrace();
      }
    }
    return null;
  }

  private String getFileName(final MultivaluedMap<String, String> header) {

    final String[] contentDisposition = header.getFirst("Content-Disposition").split(";");

    for (final String filename : contentDisposition) {
      if ((filename.trim().startsWith("filename"))) {

        final String[] name = filename.split("=");

        final String finalFileName = name[1].trim().replaceAll("\"", "");
        return finalFileName;
      }
    }
    return "unknown";
  }



}
