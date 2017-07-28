db-manager-plugin
=============

About
-----

This is a very simple Maven plugin that can create, drop, and dump PostgreSQL and MySql databases.

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
      <version>9.3-1102.jdbc41</version>
    </dependency>
  </dependencies>
</plugin>
```
```xml
<plugin>
  <groupId>com.vecna</groupId>
  <artifactId>db-manager-plugin</artifactId>
  <configuration>
	<url>jdbc:mysql://localhost/mydb</url>
	<username>user</username>
	<password>mypass</password>
  </configuration>
  <dependencies>
    <dependency>
      <groupId>mysql</groupId>
      <artifactId>mysql-connector-java</artifactId>
      <version>5.1.43</version>
    </dependency>
  </dependencies>
</plugin>
```

Goals
------------

* __create:__ creates the database (by running "create database ...").
* __drop:__ drops the database (by running "drop database ...").
* __dump:__ dumps the database to a file (requires pg_dump or mysqldump).

Other DBs
------------

The plugin only works with PostgreSQL and MySql. To support another database, create a new implementation of the DbStrategy interface.
