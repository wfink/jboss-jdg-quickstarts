eap-cluster-app: Example Using Clustered Cache Deployed via JDG modules
=============================================
Author: Wolf-Dieter Fink
Level: Advanced
Technologies: Infinispan, CDI, EJB
Summary: A quickstart to shows how a Infinispan cache behave if a split-brain happen and the cluster split in different partitions
Target Product: JDG
Product Versions: JDG 7.x
Source: <https://github.com/infinispan/jdg-quickstart>

What is it?
-----------

The jdg-conflict-management quickstart demonstrates how to handle the different scenarios with partition-handling and ConflictManager
and EntryMergePolicy
- 
- 


System requirements
-------------------

All you need to build this project is Java 8.0 (Java SDK 1.8) or better, Maven 3.0 or better.

The application this project produces is designed to be run standalone.

 
Configure Maven
---------------

If you have not yet done so, you must [Configure Maven](https://github.com/jboss-developer/jboss-developer-shared-resources/blob/master/guides/CONFIGURE_MAVEN.md#configure-maven-to-build-and-deploy-the-quickstarts) before testing the quickstarts.

Setup/Run the quickstart with automated script
==============================================

1. Download the EAP server and jboss-datagrid EAP modules library;
2. Set the following two environment variables in the same shell:
 For Linux:
   export EAP_SERVER_ZIP_PATH=[Full path to the EAP server ZIP]
   export JDG_MODULES_ZIP_PATH= _[Full path to the JDG EAP Modules ZIP]_
 For Windows:
   set EAP_SERVER_ZIP_PATH=[Full path to the EAP server ZIP]
   set JDG_MODULES_ZIP_PATH= _[Full path to the JDG EAP Modules ZIP]_
3. While in the root folder of the eap-cluster-app quickstart, run the following commands:

For Linux:
 
           ./build.sh --setup or ./build.sh --setup-domain for setting up servers and running them either in standalone or domain mode;
           
           ./build.sh --run for running the quickstart;
           
           ./build.sh --teardown for stopping the started servers as well as deleting the created directories;

 For Windows:
 
            build.bat --setup or build.bat --setup-domain for setting up servers and running them either in standalone or domain mode;
            
            build.bat --run for running the quickstart;
            
            build.bat --teardown for stopping the started servers as well as deleting the created directories;

You can also perform all setups manually with the following steps:

Setup/Run the quickstart manually
=================================================

Configure and Start the Servers in standalone mode
--------------------------------------------------

1. Prepare
   - unzip `jboss-datagrid-${version}-eap-modules-library.zip`
   - copy the modules to the server modules directory

            For Linux:   cp -a jboss-datagrid-${version}-eap-modules-library/modules $EAP_HOME
            For Windows: xcopy /e/i/f jboss-datagrid-${version}-eap-modules-library/modules %EAP_HOME%\modules


Configure and Start the Servers in domain mode
----------------------------------------------



Build the Application
---------------------

_NOTE: The following build command assumes you have configured your Maven user settings. If you have not, you must include Maven setting arguments on the command line. See [Build and Deploy the Quickstarts](../README.md#build-and-deploy-the-quickstarts) for complete instructions and additional options._

1. Open a command line and navigate to the root directory of this quickstart.
2. Type this command to build the archive:

        mvn clean install
        
3. Copy the application to the appropriate server
        
        For Linux:   cp adminApp/ear/target/jboss-eap-application-adminApp.ear $EAP_HOME1/standalone/deployments
                     cp appOne/ear/target/jboss-eap-application-AppOne.ear $EAP_HOME2/standalone/deployments
                     cp appTwo/ear/target/jboss-eap-application-AppTwo.ear $EAP_HOME3/standalone/deployments
                     cp appTwo/ear/target/jboss-eap-application-AppTwo.ear $EAP_HOME4/standalone/deployments
                                    
        For Windows: copy adminApp\ear\target\jboss-eap-application-adminApp.ear %EAP_HOME1%\standalone\deployments
                     copy appOne\ear\target\jboss-eap-application-AppOne.ear %EAP_HOME2%\standalone\deployments
                     copy appTwo\ear\target\jboss-eap-application-AppTwo.ear %EAP_HOME3%\standalone\deployments
                     copy appTwo\ear\target\jboss-eap-application-AppTwo.ear %EAP_HOME4%\standalone\deployments
 
4. When domain mode is used, deploy the applications in the following way:

        For Linux:   $EAP_HOME/bin/jboss-cli.sh -c --file=$QUICKSTART_HOME/deploy-domain.cli
        For Windows: %EAP_HOME%\bin\jboss-cli.bat -c --file=%QUICKSTART_HOME%/deploy-domain.cli


Access the application
----------------------

To start the different following applications you need to navigate to the client directory of this quickstart.


Step 1: Add values to App1 cache with the AdminApp and validate that they are replicated to the server instance of AppOne.
        Add a value to App2 cache, rollback the transaction and check that it is not added to the cache after rollback.
        The AdminServer and the AppOneServer are not configured as JBoss EAP cluster, only the Infinispan caches are configured by the application
        to communicate and replicate the caches.

        mvn -Dexec.mainClass=org.jboss.as.quickstarts.datagrid.eap.app.AdminClient exec:java

To add more instances it is possible to overwrite the used connections by adding the following argument

        mvn -Dexec.args="AdminHost AdminPort AppOneHost AppOnePort" ....

The defaults are "locahost 4447 localhost 4547"

The console output shows the actions and results (example below), any unexpected result will show an Exception.

    Add a value to App1Cache with the AdminApp and check on the same instance that the value is correct added
        success
    Check the previous added value of App1Cache by accessing the AppOne Server
        success
    Add a value to App2Cache and check on the same instance that the value is correct added
        success
    Check whether changes to a cache are rollbacked if the transaction fail
        The cache App2 work as expected on rollback

Step 2: Add values to App2 cache with the AdminApp and access AppOne to show that the EJB invocation is clustered and both AppTwo instances are used.
Show that the JBoss EAP and Infinispan clusters are not related and the Infinispan cluster is able to use a different JGroups implementation as the JBoss EAP server.

        mvn -Dexec.mainClass=org.jboss.as.quickstarts.datagrid.eap.app.AppOneClient exec:java

  The console output shows the actions and results, any unexpected result will show an Exception

    Add a value to App2Cache with the AdminApp
    Access the App2Cache from the AppOneServer by using the clustered EJB@AppTwoServer
        success : received the following node names for EJB invocation : [node3, node4]

Debug the Application
---------------------

If you want to debug the source code or look at the Javadocs of any library in the project, run either of the following commands to pull them into your local repository. The IDE should then detect them.

        mvn dependency:sources
        mvn dependency:resolve -Dclassifier=javadoc

