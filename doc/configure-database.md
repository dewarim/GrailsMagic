# Howto configure the database for GrailsMagic

Note: with dbCreate field set to "update", Grails will automatically create the necesary tables in the database.

## PostgreSQL

In the database console:

	create user mtg with password 'mtg';
	create database grails_magic owner mtg;

In GrailsMagic /grails-app/conf/DataSource.groovy, change settings to:

    driverClassName = "org.postgresql.Driver"
    username = "mtg"
    password = "mtg"

and change the connection url field in both development and production to:

	url = "jdbc:postgresql://localhost:5432/grails_magic"

In the same place, in BuildConfig.groovy, add

	runtime 'org.postgresql:postgresql:9.3-1100-jdbc41'
	
to dependencies.

## MySQL

(almost the same as PostgreSQL; untested:)

in BuildConfig, use dependency 

	runtime 'mysql:mysql-connector-java:5.1.27'
	
in DataSource, use 

	dataSource {
		pooled = true
		driverClassName = "com.mysql.jdbc.Driver"
		dialect = "org.hibernate.dialect.MySQL5InnoDBDialect"
	}
	
and as connection url, use

	url = "jdbc:mysql://localhost/mtg?useUnicode=yes&characterEncoding=UTF-8"
	
	
