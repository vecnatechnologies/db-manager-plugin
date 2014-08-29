db-manager-plugin
=============

About
-----

This is a very simple Maven that can create, drop, and dump PostgreSQL databases.

Usage
-----

The plugin requires the JDBC url, username, and password to be specified. Add the appropriate version of the JDBC driver as a dependency.

```xml
<plugin>
  <groupId>com.vecna</groupId>
  <artifactId>db-manager-plugin</artifactId>
  <configuration>
	<url>jdbc:postgresql://localhost/mydb</url>
	<username>user</username>
	<password>mypass</password>
  </configuration>
  <dependencies>
    <dependency>
      <groupId>postgresql</groupId>
      <artifactId>postgresql</artifactId>
      <version>8.4</version>
    </dependency>
  </dependencies>
</plugin>
```

Goals
------------

* __create:__ creates the database (by running "create database ...").
* __drop:__ drops the database (by running "drop database ...").
* __dump:__ dumps the database to a file (requires pg_dump).

Other DBs
------------

The plugin only works with PostgreSQL. To support another database, create a new implementation of the DbStrategy interface.
