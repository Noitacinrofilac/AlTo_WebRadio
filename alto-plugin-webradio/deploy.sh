mvn package
sudo rm /var/lib/tomcat7/webapps/webradio.war
sudo rm -r /var/lib/tomcat7/webapps/webradio/
sudo cp ./target/alto-plugin-webradio-0.0.1-SNAPSHOT.war /var/lib/tomcat7/webapps/webradio.war
sleep 10
sudo rm /var/lib/tomcat7/webapps/webradio/WEB-INF/lib/jersey-core-1.8.jar /var/lib/tomcat7/webapps/webradio/WEB-INF/lib/jersey-server-1.8.jar
sudo service tomcat7 stop
sudo service tomcat7 start
