package life.genny.qwanda.util;


import org.json.JSONException;
import org.json.JSONObject;
import org.keycloak.adapters.KeycloakConfigResolver;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.KeycloakDeploymentBuilder;
import org.keycloak.adapters.OIDCHttpFacade;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import life.genny.qwandautils.KeycloakUtils;



public class PathBasedKeycloakConfigResolver implements KeycloakConfigResolver {

  private static Map<String, String> keycloakJsonMap = new HashMap<String, String>();


  private final Map<String, KeycloakDeployment> cache =
      new ConcurrentHashMap<String, KeycloakDeployment>();

  @Override
  public KeycloakDeployment resolve(final OIDCHttpFacade.Request request) {
    URL aURL = null;
    String realm = "wildfly-swarm-keycloak-example";
    String username = null;

    if (request != null) {
      // System.out.println("Keycloak Deployment Path incoming request:" + request);
      try {
        System.out.println("Keycloak Deployment Path incoming request URI:" + request.getURI());
        // Now check for a token

        if (keycloakJsonMap.isEmpty()) {
          readFilenamesFromDirectory("./realm", keycloakJsonMap);
          System.out.println("filenames loaded ...");
        } else {
          System.out.println("filenames already loaded ...");
        }


        if (request.getHeader("Authorization") != null) {
          // extract the token
          final String authTokenHeader = request.getHeader("Authorization");
          System.out.println("authTokenHeader:" + authTokenHeader);
          final String bearerToken = authTokenHeader.substring(7);
          System.out.println("bearerToken:" + bearerToken);
          // now extract the realm

          final JSONObject jsonObj = KeycloakUtils.getDecodedToken(bearerToken);

          System.out.println("******" + jsonObj);
          try {
            username = (String) jsonObj.get("preferred_username");
            realm = (String) jsonObj.get("aud");
          } catch (final JSONException e1) {
            System.out
                .println("no customercode incuded with token for " + username + ":" + jsonObj);
          } catch (final NullPointerException e2) {
            System.out.println("NullPointerException for " + bearerToken + "::::::" + username);
          }

        } else {

          aURL = new URL(request.getURI());
          final String url = aURL.getHost();
          System.out.println("received KeycloakConfigResolver url:" + url);


          final String keycloakJsonText = keycloakJsonMap.get(url);
          System.out.println("Selected KeycloakJson:[" + keycloakJsonText + "]");

          // extract realm
          final JSONObject json = new JSONObject(keycloakJsonText);
          System.out.println("json:" + json);
          realm = json.getString("realm");
        }


      } catch (final Exception e) {
        System.out.println("Error in accessing request.getURI , spi issue?");
      }
    }


    System.out.println(">>>>> INCOMING REALM IS " + realm);

    KeycloakDeployment deployment = cache.get(realm);

    if (null == deployment) {
      System.getenv("JBOSS_HOME");
      // is = new FileInputStream(fileName);
      // if (is == null) {
      // throw new IllegalStateException("Not able to find the file /" + realm + ".json");
      // }
      InputStream is;
      try {
        is = new ByteArrayInputStream(
            keycloakJsonMap.get(realm).getBytes(StandardCharsets.UTF_8.name()));
        System.out.println("Building deployment ");
        deployment = KeycloakDeploymentBuilder.build(is);
        cache.put(realm, deployment);
      } catch (final UnsupportedEncodingException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }


    } else {
      System.out.println("Deployment fetched from cache");
    }

    if (deployment != null) {
      System.out.println("Deployment is not null ");
      System.out.println("accountUrl:" + deployment.getAccountUrl());
      System.out.println("realm:" + deployment.getRealm());
      System.out.println("resource name:" + deployment.getResourceName());


    }

    return deployment;
  }

  private static void readFilenamesFromDirectory(final String rootFilePath,
      final Map<String, String> keycloakJsonMap) {
    final File folder = new File(rootFilePath);
    final File[] listOfFiles = folder.listFiles();
    final String localIP = System.getenv("HOSTIP");
    System.out.println("Loading Files! with HOSTIP=" + localIP);

    for (int i = 0; i < listOfFiles.length; i++) {
      if (listOfFiles[i].isFile()) {
        System.out.println("File " + listOfFiles[i].getName());
        try {
          String keycloakJsonText = getFileAsText(listOfFiles[i]);
          // Handle case where dev is in place with localhost

          // if (!"localhost.json".equalsIgnoreCase(listOfFiles[i].getName())) {
          keycloakJsonText = keycloakJsonText.replaceAll("localhost", localIP);

          // }
          final String key = listOfFiles[i].getName().replaceAll(".json", "");
          System.out.println("keycloak key:" + key + "," + keycloakJsonText);

          keycloakJsonMap.put(key, keycloakJsonText);
        } catch (final IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }

      } else if (listOfFiles[i].isDirectory()) {
        System.out.println("Directory " + listOfFiles[i].getName());
        readFilenamesFromDirectory(listOfFiles[i].getName(), keycloakJsonMap);
      }
    }
  }

  private static String getFileAsText(final File file) throws IOException {
    final BufferedReader in = new BufferedReader(new FileReader(file));
    String ret = "";
    String line = null;
    while ((line = in.readLine()) != null) {
      ret += line;
    }
    in.close();

    return ret;
  }
}

