package life.genny;

import static java.util.Arrays.asList;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import javax.ws.rs.core.Response;
import java.io.IOException;

public class KeycloakImportTest {


  // @Test
  public void keycloakImportTest() {

    final Keycloak kc = KeycloakBuilder.builder() //
        .serverUrl("http://localhost:8180/auth") //
        .realm("wildfly-swarm-keycloak-example")//
        .username("service") //
        .password("password1") //
        .clientId("admin-cli") //
        .resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build()) //
        .build();

    final CredentialRepresentation credential = new CredentialRepresentation();
    credential.setType(CredentialRepresentation.PASSWORD);
    credential.setValue("test123");
    credential.setTemporary(false);

    final UserRepresentation user = new UserRepresentation();
    user.setUsername("testuser");
    user.setFirstName("Test");
    user.setLastName("User");
    user.setCredentials(asList(credential));
    user.setEnabled(true);
    user.setRealmRoles(asList("admin"));

    // Create testuser
    final Response result = kc.realm("wildfly-swarm-keycloak-example").users().create(user);
    if (result.getStatus() != 201) {
      System.err.println("Couldn't create user.");
      System.exit(0);
    }
    System.out.println("Testuser created.... verify in keycloak!");

    System.out.println("Press any key...");
    try {
      System.in.read();
    } catch (final IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    // Delete testuser
    final String locationHeader = result.getHeaderString("Location");
    final String userId = locationHeader.replaceAll(".*/(.*)$", "$1");
    kc.realm("wildfly-swarm-keycloak-example").users().get(userId).remove();
  }



  // final Map<String, Attribute> attributes = new HashMap<String, Attribute>();
  // final String keycloakUrl = "http://localhost:8180";
  // final String realm = "wildfly-swarm-keycloak-example";
  // final String username = "service";
  // final String password = "password1";
  // final String clientid = "security-admin-console";
  // final String secret = "b5f18632-d218-43f6-906b-4a4fd63019d1";
  // try
  // {
  // final List<BaseEntity> users = KeycloakService.importKeycloakUsers(attributes, keycloakUrl,
  // realm, username, password, clientid, secret);
  // System.out.println(users);
  // }catch(IOException|
  // BadDataException e)
  // {
  // // TODO Auto-generated catch block
  // e.printStackTrace();
  // }
  //
  // System.out.println(users);
  //
}


