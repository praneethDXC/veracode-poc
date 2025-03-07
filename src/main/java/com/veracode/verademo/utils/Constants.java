package com.veracode.verademo.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Constants {
	private final String JDBC_DRIVER = "mysql";
	private final String JDBC_HOSTNAME = "localhost";
	private final String JDBC_PORT = "3306";
	private final String JDBC_DATABASE = "blab";
	private final String JDBC_USER = "blab";

	@Value("${db.password}")
	private String JDBC_PASSWORD;

	private String hostname;
	private String port;
	private String dbname;
	private String username;
	private String password;

	public Constants() {
		String dbnameProp = System.getenv("RDS_DB_NAME");
		this.dbname = (dbnameProp == null) ? JDBC_DATABASE : dbnameProp;
		
		String hostnameProp = System.getenv("RDS_HOSTNAME");
		this.hostname = (hostnameProp == null) ? JDBC_HOSTNAME : hostnameProp;
		
		String portProp = System.getenv("RDS_PORT");
		this.port = (portProp == null) ? JDBC_PORT : portProp;
		
		String userProp = System.getenv("RDS_USERNAME");
		this.username = (userProp == null) ? JDBC_USER : userProp;
		
		String passwordProp = System.getenv("RDS_PASSWORD");
		this.password = (passwordProp == null) ? JDBC_PASSWORD : passwordProp;
	}

	public static final Constants create() {
		return new Constants();
	}

	public final String getJdbcConnectionString() {
		String connection = null;
		try {
			connection = String.format(
					"jdbc:%s://%s:%s/%s?user=%s&password=%s",
					JDBC_DRIVER,
					hostname,
					port,
					dbname,
					URLEncoder.encode(username, "UTF-8"),
					URLEncoder.encode(password, "UTF-8")
			);
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return connection;
	}
}