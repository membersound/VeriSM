A.1	Deployment

A.1.1	Preparation and installation
- Install the latest Java Runtime Environment (JRE) or, if you want to continue the development, latest Java Development Kit (JDK) (http://www.oracle.com/technetwork/ java/javase/downloads/index.html)

A.1.2	Webserver setup and configuration
- Download the webserver Apache Tomcat TomEE Plus (http://tomee.apache.org/ downloads.html), and extract it preferably to a path without whitespaces.
- Copy the file "startup.bat" from the CD to the root of the extracted Tomcat server folder.
- Edit the "startup.bat" and adapt the environment variables with the path to your JDK installation and to you Tomcat folder. The path "CATALINE_HOME" is the root folder of the webserver.

A.1.3	Webserver deployment
- From the CD, copy the precompiled application file "verism-1.0-SNAPSHOT.war" to "CATALINE_HOME\webapps". Tomcat will deploy the application automatically on launch. Rename the file as the filename will define the absolute path to the application, eg "verism". Thus it is important to rename the file before launching the server. 
- Copy the library "hibernate-jpa-2.1-api-1.0.0.Draft-16.jar" from the "\lib" on the CD to "CATALINE_HOME\lib". All other libraries are initially included in Tomcat.
- Start the server by executing the "startup.bat". The project will be automatically deployed.
- Open the URL appending the name you chose for the application file. By default: http://localhost:8080/verism
- For enabling access to the application within the local network, apply firewall rules accordingly. Contact your network administrator to assist you with this.
- To redeploy/undeploy the application during running webserver mode: You first have to define an admin user for Tomcat application management. Go to "CATALINE_HOME\conf\tomcat-users.xml" and uncomment the last entry for "tomee-admin". You might also want to define your own password. Default is "tomee" for both username and pass. Restart the Server. Go to http://localhost:8080/manager. To login, use the tomcat user credentials configured above.


A.2	Development

A.2.1	Project setup and configuration
- Download and extract latest Eclipse IDE for JEE (http://www.eclipse.org/downloads/)
- Run Eclipse > Help > Eclipse Marketplace > Search for the following plugins and install them: "Google Plugin for Eclipse", "Maven Integration for Eclipse WTP". Chose the version that matches your Eclipse installation.
- Help > Install New Software > Add the source for "m2e-wtp": http://download.-eclipse.org/m2e-wtp/releases/juno/ > Check all items for "Maven Integration for Eclipse" > install.
- Open Eclipse > Import > General > Existing projects into workspace > Choose the root directory of the project on the CD > Check: Copy project into workspace > Finish.
- Project properties > Java Build Path > Order and Export > Make sure that "Maven Dependencies" are marked for export.

A.2.2	Project compilation
- Project properties > Run As > Maven Build... > Enter: "dependency:resolve" > Run. 
This will download and resolve all Maven defined dependencies inside the Maven configuration file "pom.xml" to a local repository.
- Project properties > Run As > Maven Build... > Enter: "package" > Run. This will compile the "\client" folder to JavaScript and the "\server" classes to bytecode. All permutations for the different browsers are created. Also any dependencies are copied from the local repository to the target "\lib" folder. In reference to chapter 3.4 (Projektstruktur), notice that the "\lib" folder in the development tree "src\main\webapp\WEB-INF\lib" is empty. Thus the project remains free from specific dependencies. Here Maven uses the "convention over configuration" principle explained in chapter 3.3 (Google Web Toolkit).
- Project properties > Run As > Maven Build... > Enter: "gwt:run". This will copy all static resources like images, layout and configuration files to the target folder and run the application on the GWT-provided jetty server > Click on "Launch Default Browser". This will launch the app in development mode. Make sure you have the "GWT Developer Plugin" installed for your browser (FF: https://dl-ssl.google.com/gwt/ plugins/firefox/gwt-dev-plugin.xpi, Chrome: https://chrome.google.com/webstore/detail /gwt-developer-plugin).
- As a result of both Maven commands above, the compiled *.war application file is created in the "target" folder of the project. To deploy this on the standalone Tomcat webserver, continue as already advised in section A.1.3 (Webserver deployment.
- To continue the development without having to use Maven commands all the time: Project properties > Run As > Web Application. Doubleclick the URL shown in the "Development Mode" tab to open the application. Press F5 inside the browser to refresh changes on the client side code. Press "Reload web server" on the "Developoment Mode" tab in Eclipse to refresh changes on the server side.
