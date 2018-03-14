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

The jdg-conflict-management quickstart demonstrates how the different spit-brain scenarios behave with partition-handling, ConflictManager
and EntryMergePolicy
- As a clustered standalone application with 2 or more nodes
- As clustered JDG server instances with a simple HotRod client to access the cache


Access the standalone application
---------------------------------

- Start two instances of the standalone application

  cd client
  mvn exec:java

- Add entries to one or both and list it to show it is clustered
- Suspend one instance or force a network split
- Change data on one ot both sides
- Resume the instances or resolve the network split
- See the EntryMergePolicy working
- check the Entries


Prepare and run the JDG server example
--------------------------------------

- Create two copies of a fresh JDG 7.2 server
- Start two JDG servers
- apply the configuration with the provided installation script
- Start the client application and put some data into the cache
- Suspend one instance or force a network split
- Change data on one ot both sides
- Resume the instances or resolve the network split
- See the EntryMergePolicy working
- check the Entries



System requirements
-------------------

All you need to build this project is Java 8.0 (Java SDK 1.8) or better, Maven 3.0 or better.

The application this project produces is designed to be run standalone.

 
Configure Maven
---------------

If you have not yet done so, you must [Configure Maven](https://github.com/jboss-developer/jboss-developer-shared-resources/blob/master/guides/CONFIGURE_MAVEN.md#configure-maven-to-build-and-deploy-the-quickstarts) before testing the quickstarts.

