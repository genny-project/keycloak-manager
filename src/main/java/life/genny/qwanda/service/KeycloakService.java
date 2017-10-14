package life.genny.qwanda.service;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
// import org.apache.http.HttpEntity;
// import org.apache.http.HttpResponse;
// import org.apache.http.NameValuePair;
// import org.apache.http.client.HttpClient;
// import org.apache.http.client.entity.UrlEncodedFormEntity;
// import org.apache.http.client.methods.HttpGet;
// import org.apache.http.client.methods.HttpPost;
// import org.apache.http.impl.client.DefaultHttpClient;
// import org.apache.http.message.BasicNameValuePair;
//// import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.common.util.KeycloakUriBuilder;
import org.keycloak.constants.ServiceUrlConstants;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.util.JsonSerialization;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import life.genny.qwanda.attribute.Attribute;
import life.genny.qwanda.entity.BaseEntity;
import life.genny.qwanda.entity.Person;
import life.genny.qwanda.exception.BadDataException;



public class KeycloakService {

  String keycloakUrl = null;
  String realm = null;
  String username = null;
  String password = null;

  String clientid = null;
  String secret = null;

  AccessTokenResponse accessToken = null;
  Keycloak keycloak = null;

  public KeycloakService(final String keycloakUrl, final String realm, final String username,
      final String password, final String clientid, final String secret) throws IOException {


    this.keycloakUrl = keycloakUrl;
    this.realm = realm;
    this.username = username;
    this.password = password;
    this.clientid = clientid;
    this.secret = secret;


    accessToken = getToken();

  }

  //
  private AccessTokenResponse getToken() throws IOException {

    final HttpClient httpClient = new DefaultHttpClient();

    try {
      final HttpPost post = new HttpPost(KeycloakUriBuilder.fromUri(keycloakUrl + "/auth")
          .path(ServiceUrlConstants.TOKEN_PATH).build(realm));

      post.addHeader("Content-Type", "application/x-www-form-urlencoded");

      final List<NameValuePair> formParams = new ArrayList<NameValuePair>();
      formParams.add(new BasicNameValuePair("username", username));
      formParams.add(new BasicNameValuePair("password", password));
      formParams.add(new BasicNameValuePair(OAuth2Constants.GRANT_TYPE, "password"));
      formParams.add(new BasicNameValuePair(OAuth2Constants.CLIENT_ID, "security-admin-console"));
      formParams.add(new BasicNameValuePair(OAuth2Constants.CLIENT_SECRET, secret));
      final UrlEncodedFormEntity form = new UrlEncodedFormEntity(formParams, "UTF-8");

      post.setEntity(form);

      final HttpResponse response = httpClient.execute(post);

      final int statusCode = response.getStatusLine().getStatusCode();
      final HttpEntity entity = response.getEntity();
      String content = null;
      if (statusCode != 200) {
        content = getContent(entity);
        throw new IOException("" + statusCode);
      }
      if (entity == null) {
        throw new IOException("Null Entity");
      } else {
        content = getContent(entity);
      }
      return JsonSerialization.readValue(content, AccessTokenResponse.class);
    } finally {
      httpClient.getConnectionManager().shutdown();
    }
  }

  public static String getContent(final HttpEntity httpEntity) throws IOException {
    if (httpEntity == null)
      return null;
    final InputStream is = httpEntity.getContent();
    try {
      final ByteArrayOutputStream os = new ByteArrayOutputStream();
      int c;
      while ((c = is.read()) != -1) {
        os.write(c);
      }
      final byte[] bytes = os.toByteArray();
      final String data = new String(bytes);
      return data;
    } finally {
      try {
        is.close();
      } catch (final IOException ignored) {

      }
    }

  }

  public List<LinkedHashMap> fetchKeycloakUsers() {
    final HttpClient client = new DefaultHttpClient();
    try {
      final HttpGet get =
          new HttpGet(this.keycloakUrl + "/auth/admin/realms/" + this.realm + "/users");
      get.addHeader("Authorization", "Bearer " + this.accessToken.getToken());
      try {
        final HttpResponse response = client.execute(get);
        if (response.getStatusLine().getStatusCode() != 200) {
          throw new IOException();
        }
        final HttpEntity entity = response.getEntity();
        final InputStream is = entity.getContent();
        try {
          return JsonSerialization.readValue(is, (new ArrayList<UserRepresentation>()).getClass());
        } finally {
          is.close();
        }
      } catch (final IOException e) {
        throw new RuntimeException(e);
      }
    } finally {
      client.getConnectionManager().shutdown();
    }
  }

  static public List<BaseEntity> importKeycloakUsers(final Map<String, Attribute> attributes)
      throws IOException, BadDataException {
    final Map<String, String> envParams = System.getenv();
    String keycloakUrl = envParams.get("KEYCLOAKURL");
    System.out.println("Keycloak URL=[" + keycloakUrl + "]");
    keycloakUrl = keycloakUrl.replaceAll("'", "");
    final String realm = envParams.get("KEYCLOAK_REALM");
    final String username = envParams.get("KEYCLOAK_USERNAME");
    final String password = envParams.get("KEYCLOAK_PASSWORD");
    final String clientid = envParams.get("KEYCLOAK_CLIENTID");
    final String secret = envParams.get("KEYCLOAK_SECRET");


    return importKeycloakUsers(attributes, keycloakUrl, realm, username, password, clientid,
        secret);
  }



  static public List<BaseEntity> importKeycloakUsers(final Map<String, Attribute> attributes,
      final String keycloakUrl, final String realm, final String username, final String password,
      final String clientid, final String secret) throws IOException, BadDataException {

    final List<BaseEntity> bes = new ArrayList<BaseEntity>();

    final KeycloakService kcs =
        new KeycloakService(keycloakUrl, realm, username, password, clientid, secret);

    final List<LinkedHashMap> users = kcs.fetchKeycloakUsers();
    for (final LinkedHashMap user : users) {
      final String name = user.get("firstName") + " " + user.get("lastName");
      final Person newUser = new Person(name);
      final String keycloakUUID = (String) user.get("id");
      newUser.setCode(Person.getDefaultCodePrefix() + keycloakUUID.toUpperCase());
      newUser.setName(name);
      newUser.addAttribute(attributes.get("PRI_NAME"), 1.0, name);
      newUser.addAttribute(attributes.get("PRI_FIRSTNAME"), 1.0, user.get("firstName"));
      newUser.addAttribute(attributes.get("PRI_LASTNAME"), 1.0, user.get("lastName"));
      newUser.addAttribute(attributes.get("PRI_UUID"), 1.0, user.get("id"));
      newUser.addAttribute(attributes.get("PRI_EMAIL"), 1.0, user.get("email"));
      newUser.addAttribute(attributes.get("PRI_USERNAME"), 1.0, user.get("username"));
      System.out.println("Code=" + newUser.getCode());;
      bes.add(newUser);
    }

    return bes;
  }

}
