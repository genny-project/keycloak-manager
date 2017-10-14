FROM gennyproject/wildfly:latest

RUN env

ENV PROJECT keycloak-manager 
ADD target/$PROJECT $JBOSS_HOME/standalone/deployments/$PROJECT.war

RUN touch $JBOSS_HOME/standalone/deployments/$PROJECT.war.dodeploy
USER root
RUN chown -R jboss:jboss $JBOSS_HOME/standalone/deployments/$PROJECT.war
RUN chmod -Rf 777 $JBOSS_HOME/standalone/deployments/$PROJECT.war

ADD realm $JBOSS_HOME/realm

USER root

