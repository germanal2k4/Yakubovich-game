JAVAFX_LIB="/Users/germanalbershteyn/Desktop/javafx-sdk-22.0.1/lib/"

java -Dprism.order=sw -Djava.awt.headless=true --module-path $JAVAFX_LIB:jars/Server-1.jar -m com.example.server/com.example.server.ServerConfigWindow