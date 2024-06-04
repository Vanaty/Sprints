# Sprint
 Sprint exam Mr Naina

# Instalation⚒
  Parametre dans `web.xml`
  ### Required☣
   `packageControleur` | package a scanner
  ```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app version="3.1" xmlns="http://xmlns.jcp.org/xml/ns/javaee" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd">
    <context-param>
        <param-name>packageControleur</param-name>
        <param-value>controleur</param-value>
    </context-param>
    <servlet>
        <servlet-name>Controleur</servlet-name>
        <servlet-class>mg.itu.controleur.FrontControleur</servlet-class>
    </servlet>
    <servlet-mapping>
        <servlet-name>Controleur</servlet-name>
        <url-pattern>/</url-pattern>
    </servlet-mapping>
    <session-config>
        <session-timeout>
            30
        </session-timeout>
    </session-config>
</web-app>
 ```
  ### Exemple url-pattern accepted
  /
  /<PATH>/*