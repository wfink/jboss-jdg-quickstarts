eap-cluster-app: Example how to use JDG cache from a EAP application
====================================================================
Author: Wolf-Dieter Fink
Level: Advanced
Technologies: Infinispan, CDI, EAP
Summary: Shows how to use Infinispan from a EAP application in embedded mode
Target Product: JDG
Product Versions: EAP 6.x, JDG 6.3
Source: <https://github.com/infinispan/jdg-quickstart>

What is it?
-----------

EAP-cluster-app is a simple application that uses Infinispan Cache in embedded mode

TODO

System requirements
-------------------

All you need to build this project is Java 6.0 (Java SDK 1.6) or better, Maven 3.0 or better.

The application this project produces is designed to be run on Red Hat JBoss Enterprise Application Platform (EAP) 6.1 or later.

 
Configure Maven
---------------

If you have not yet done so, you must [Configure Maven](../../README.md#configure-maven) before testing the quickstarts.


Configure and start EAP instances in standanlone mode
---------

1. Copy an EAP server to 4 different directories EAP_NODE[1-4].
2. Open a command line for each of the 4 nodes and navigate to the root of the EAP server directory.
3. The following shows the command line to start the different servers:

        For Linux:   $EAP_NODE1_HOME/bin/standalone.sh -Djboss.node.name=node1
                     $EAP_NODE2_HOME/bin/standalone.sh -Djboss.node.name=node2 -Djboss.socket.binding.port-offset=100
                     $EAP_NODE3_HOME/bin/standalone.sh -Djboss.node.name=node3 -Djboss.socket.binding.port-offset=200 -c standalone-ha.xml
                     $EAP_NODE4_HOME/bin/standalone.sh -Djboss.node.name=node4 -Djboss.socket.binding.port-offset=300 -c standalone-ha.xml
        For Windows: %EAP_NODE1_HOME%\bin\standalone.bat -Djboss.node.name=node1
                     %EAP_NODE2_HOME%\bin\standalone.sh -Djboss.node.name=node2 -Djboss.socket.binding.port-offset=100
                     %EAP_NODE3_HOME%\bin\standalone.sh -Djboss.node.name=node3 -Djboss.socket.binding.port-offset=200 -c standalone-ha.xml
                     %EAP_NODE4_HOME%\bin\standalone.sh -Djboss.node.name=node4 -Djboss.socket.binding.port-offset=300 -c standalone-ha.xml

4. Add a user to each server:

        For Linux:   $EAP_NODE#_HOME/bin/add-user.sh -a -u quickuser -p quick-123
        For Windows: %EAP_NODE#_HOME%\bin\standalone.bat -a -u quickuser -p quick-123

5. Add the configuration for node1 (AppOne) to use EJB server-to-server invocation:

        For Linux:   $EAP_NODE1_HOME/bin/jboss-cli.sh -c --file=QUICKSTART_HOME/install-appOne-standalone.cli
        For Windows: %EAP_NODE1_HOME%\bin\standalone.bat -c --file=QUICKSTART_HOME/install-appOne-standalone.cli


Build and Deploy the Application
------------------------------------------------

_NOTE: The following build command assumes you have configured your Maven user settings. If you have not, you must include Maven setting arguments on the command line. See [Build and Deploy the Quickstarts](../README.md#build-and-deploy-the-quickstarts) for complete instructions and additional options._

1. Open a command line and navigate to the root directory of this quickstart.
2. Type this command to build and deploy the archive:

        mvn clean install
        
3. Copy the application to the appropriate server

        cp adminApp/ear/target/jboss-eap-application-adminApp.ear EAP_NODE1/standalone/deployments
        cp cp appOne/ear/target/jboss-eap-application-AppOne.ear EAP_NODE2/standalone/deployments
        cp cp appTwo/ear/target/jboss-eap-application-AppTwo.ear EAP_NODE3/standalone/deployments
        cp appTwo/ear/target/jboss-eap-application-AppTwo.ear EAP_NODE4/standalone/deployments
 

Access the application
---------------------


TODO split and add explanations what happen

1. Navigate to the client directory of the quickstart
2. Access the Admin application to add values to the different caches and validate that they are replicated

        mvn -Dexec.mainClass=org.jboss.as.quickstarts.datagrid.eap.app.AdminClient exec:java

2. Access the AppOneClient application to add values with the Admin node to the AppTwo cache and validate that they are replicated
   by using the AppOne application which use clustered EJB invocation to the EJB AppTwoCacheAccess to validate the value

        mvn -Dexec.mainClass=org.jboss.as.quickstarts.datagrid.eap.app.ClientAppOne exec:java


Undeploy the Archive
--------------------


1. Remove the deployables from the different standalone/deployments folders.

        mvn jboss-as:undeploy


Debug the Application
---------------------

If you want to debug the source code or look at the Javadocs of any library in the project, run either of the following commands to pull them into your local repository. The IDE should then detect them.

        mvn dependency:sources
        mvn dependency:resolve -Dclassifier=javadoc

