package com.veracode.verademo.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;

import com.veracode.verademo.utils.Constants;
import com.veracode.verademo.utils.User;
import com.veracode.verademo.utils.Utils;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Scope("request")
public class ResetController {
	private static final Logger logger = LogManager.getLogger("VeraDemo:ResetController");

	@Autowired
	ServletContext context;

	private static User[] users;

	static {
		Properties properties = new Properties();
		try (FileReader reader = new FileReader("config.properties")) {
			properties.load(reader);
			users = new User[] {
				User.create(properties.getProperty("user.admin.username"), properties.getProperty("user.admin.password"), "Thats Mr Administrator to you."),
				User.create(properties.getProperty("user.john.username"), properties.getProperty("user.john.password"), "John Smith"),
				User.create(properties.getProperty("user.paul.username"), properties.getProperty("user.paul.password"), "Paul Farrington"),
				User.create(properties.getProperty("user.chrisc.username"), properties.getProperty("user.chrisc.password"), "Chris Campbell"),
				User.create(properties.getProperty("user.laurie.username"), properties.getProperty("user.laurie.password"), "Laurie Mercer"),
				User.create(properties.getProperty("user.nabil.username"), properties.getProperty("user.nabil.password"), "Nabil Bousselham"),
				User.create(properties.getProperty("user.julian.username"), properties.getProperty("user.julian.password"), "Julian Totzek-Hallhuber"),
				User.create(properties.getProperty("user.joash.username"), properties.getProperty("user.joash.password"), "Joash Herbrink"),
				User.create(properties.getProperty("user.andrzej.username"), properties.getProperty("user.andrzej.password"), "Andrzej Szaryk"),
				User.create(properties.getProperty("user.april.username"), properties.getProperty("user.april.password"), "April Sauer"),
				User.create(properties.getProperty("user.armando.username"), properties.getProperty("user.armando.password"), "Armando Bioc"),
				User.create(properties.getProperty("user.ben.username"), properties.getProperty("user.ben.password"), "Ben Stoll"),
				User.create(properties.getProperty("user.brian.username"), properties.getProperty("user.brian.password"), "Brian Pitta"),
				User.create(properties.getProperty("user.caitlin.username"), properties.getProperty("user.caitlin.password"), "Caitlin Johanson"),
				User.create(properties.getProperty("user.christraut.username"), properties.getProperty("user.christraut.password"), "Chris Trautwein"),
				User.create(properties.getProperty("user.christyson.username"), properties.getProperty("user.christyson.password"), "Chris Tyson"),
				User.create(properties.getProperty("user.clint.username"), properties.getProperty("user.clint.password"), "Clint Pollock"),
				User.create(properties.getProperty("user.cody.username"), properties.getProperty("user.cody.password"), "Cody Bertram"),
				User.create(properties.getProperty("user.derek.username"), properties.getProperty("user.derek.password"), "Derek Chowaniec"),
				User.create(properties.getProperty("user.glenn.username"), properties.getProperty("user.glenn.password"), "Glenn Whittemore"),
				User.create(properties.getProperty("user.grant.username"), properties.getProperty("user.grant.password"), "Grant Robinson"),
				User.create(properties.getProperty("user.gregory.username"), properties.getProperty("user.gregory.password"), "Gregory Wolford"),
				User.create(properties.getProperty("user.jacob.username"), properties.getProperty("user.jacob.password"), "Jacob Martel"),
				User.create(properties.getProperty("user.jeremy.username"), properties.getProperty("user.jeremy.password"), "Jeremy Anderson"),
				User.create(properties.getProperty("user.johnny.username"), properties.getProperty("user.johnny.password"), "Johnny Wong"),
				User.create(properties.getProperty("user.kevin.username"), properties.getProperty("user.kevin.password"), "Kevin Rise"),
				User.create(properties.getProperty("user.scottrum.username"), properties.getProperty("user.scottrum.password"), "Scott Rumrill"),
				User.create(properties.getProperty("user.scottsim.username"), properties.getProperty("user.scottsim.password"), "Scott Simpson")
			};
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/reset", method = RequestMethod.GET)
	public String showReset() {
		logger.info("Entering showReset");

		return "reset";
	}

	@RequestMapping(value = "/reset", method = RequestMethod.POST)
	public String processReset(
			@RequestParam(value = "confirm", required = true) String confirm,
			@RequestParam(value = "primary", required = false) String primary,
			Model model) throws IOException {
		logger.info("Entering processReset");

		Connection connect = null;
		PreparedStatement usersStatement = null;
		PreparedStatement listenersStatement = null;
		PreparedStatement blabsStatement = null;
		PreparedStatement commentsStatement = null;
		java.util.Date now = new java.util.Date();

		Random rand = new Random();

		recreateDatabaseSchema();

		try {
			logger.info("Getting Database connection");
			Class.forName("com.mysql.jdbc.Driver");
			connect = DriverManager.getConnection(Constants.create().getJdbcConnectionString());
			logger.info(connect);
			connect.setAutoCommit(false);

			logger.info("Preparing the Stetement for adding users");
			usersStatement = connect.prepareStatement(
					"INSERT INTO users (username, password, password_hint, created_at, last_login, real_name, blab_name) values (?, ?, ?, ?, ?, ?, ?);");
			for (int i = 0; i < users.length; i++) {
				logger.info("Adding user " + users[i].getUserName());
				usersStatement.setString(1, users[i].getUserName());
				usersStatement.setString(2, users[i].getPassword());
				usersStatement.setString(3, users[i].getPasswordHint());
				usersStatement.setTimestamp(4, users[i].getDateCreated());
				usersStatement.setTimestamp(5, users[i].getLastLogin());
				usersStatement.setString(6, users[i].getRealName());
				usersStatement.setString(7, users[i].getBlabName());

				usersStatement.executeUpdate();
			}
			connect.commit();

			logger.info("Preparing the Stetement for adding listeners");
			listenersStatement = connect
					.prepareStatement("INSERT INTO listeners (blabber, listener, status) values (?, ?, 'Active');");
			for (int i = 1; i < users.length; i++) {
				for (int j = 1; j < users.length; j++) {
					if (rand.nextBoolean() && i != j) {
						String blabber = users[i].getUserName();
						String listener = users[j].getUserName();

						logger.info("Adding " + listener + " as a listener of " + blabber);

						listenersStatement.setString(1, blabber);
						listenersStatement.setString(2, listener);

						listenersStatement.executeUpdate();
					}
				}
			}
			connect.commit();

			logger.info("Reading blabs from file");
			String[] blabsContent = loadFile("blabs.txt");

			logger.info("Preparing the Statement for adding blabs");
			blabsStatement = connect
					.prepareStatement("INSERT INTO blabs (blabber, content, timestamp) values (?, ?, ?);");
			for (String blabContent : blabsContent) {
				int randomUserOffset = rand.nextInt(users.length - 2) + 1;
				long vary = rand.nextInt(30 * 24 * 3600);

				String username = users[randomUserOffset].getUserName();
				logger.info("Adding a blab for " + username);

				blabsStatement.setString(1, username);
				blabsStatement.setString(2, blabContent);
				blabsStatement.setTimestamp(3, new Timestamp(now.getTime() - (vary * 1000)));

				blabsStatement.executeUpdate();
			}
			connect.commit();

			logger.info("Reading comments from file");
			String[] commentsContent = loadFile("comments.txt");

			logger.info("Preparing the Statement for adding comments");
			commentsStatement = connect.prepareStatement(
					"INSERT INTO comments (blabid, blabber, content, timestamp) values (?, ?, ?, ?);");
			for (int i = 1; i <= blabsContent.length; i++) {
				int count = rand.nextInt(6);

				for (int j = 0; j < count; j++) {
					int randomUserOffset = rand.nextInt(users.length - 2) + 1;
					String username = users[randomUserOffset].getUserName();

					int commentNum = rand.nextInt(commentsContent.length);
					String comment = commentsContent[commentNum];

					long vary = rand.nextInt(30 * 24 * 3600);

					logger.info("Adding a comment from " + username + " on blab ID " + String.valueOf(i));
					commentsStatement.setInt(1, i);
					commentsStatement.setString(2, username);
					commentsStatement.setString(3, comment);
					commentsStatement.setTimestamp(4, new Timestamp(now.getTime() - (vary * 1000)));

					commentsStatement.executeUpdate();
				}
			}
			connect.commit();
		} catch (SQLException | ClassNotFoundException ex) {
			logger.error(ex);
		} finally {
			try {
				if (usersStatement != null) {
					usersStatement.close();
				}
			} catch (SQLException exceptSql) {
				logger.error(exceptSql);
			}
			try {
				if (listenersStatement != null) {
					listenersStatement.close();
				}
			} catch (SQLException exceptSql) {
				logger.error(exceptSql);
			}
			try {
				if (blabsStatement != null) {
					blabsStatement.close();
				}
			} catch (SQLException exceptSql) {
				logger.error(exceptSql);
			}
			try {
				if (commentsStatement != null) {
					commentsStatement.close();
				}
			} catch (SQLException exceptSql) {
				logger.error(exceptSql);
			}
			try {
				if (connect != null) {
					connect.close();
				}
			} catch (SQLException exceptSql) {
				logger.error(exceptSql);
			}
		}

		return Utils.redirect("reset");
	}

	private String[] loadFile(String filename) {
		return loadFile(filename, new String[0], System.lineSeparator());
	}

	  private void recreateDatabaseSchema() throws IOException {
	        logger.info("Reading database schema from file");
	        String[] schemaSql = loadFile("blab_schema.sql", new String[] { "--", "/*" }, ";");

	        try (Connection connect = DriverManager.getConnection(Constants.create().getJdbcConnectionString());
	             Statement stmt = connect.createStatement()) {

	            logger.info("Getting Database connection");
	            for (String sql : schemaSql) {
	                sql = sql.trim();
	                if (!sql.isEmpty()) {
	                    try {
	                        processSqlStatement(stmt, sql);
	                    } catch (SQLException e) {
	                        logger.error("Error executing SQL statement: " + sql, e);
	                        // Consider rolling back transaction here if using transactions
	                        // throw e; //Or re-throw if you want to stop on the first error
	                    }
	                }
	            }
	        } catch (SQLException ex) {
	            logger.error("Error recreating database schema: ", ex);
	        }

	    }

	    private void processSqlStatement(Statement stmt, String sql) throws SQLException {
	        sql = sql.trim().toUpperCase();
	        if (sql.startsWith("CREATE TABLE")) {
	            // CREATE TABLE statements are generally safe as long as the schema file is trusted.
	            logger.info("Executing CREATE TABLE: " + sql);
	            System.out.println("Executing: " + sql);
	            stmt.executeUpdate(sql);
	        } else if (sql.startsWith("INSERT INTO")) {
	            //Handle INSERT statements safely using PreparedStatement
	            executeInsert(stmt, sql);
	        } else if (sql.startsWith("UPDATE") || sql.startsWith("DELETE")) {
	            //Handle UPDATE and DELETE statements safely using PreparedStatement
	            //  This requires parsing the SQL to extract parameters (complex!)
	            logger.warn("UPDATE/DELETE statements are not safely handled by this method and may cause issues. Please change your schema to use other DDL");
	        } else if (sql.startsWith("CREATE INDEX") || sql.startsWith("ALTER TABLE")) {
	            //Other DDL commands that might be safe (but still need careful review)
	            logger.info("Executing DDL: " + sql);
	            System.out.println("Executing: " + sql);
	            stmt.executeUpdate(sql);
	        } else {
	            logger.warn("Unsupported SQL statement type: " + sql);
	        }
	    }


	    private void executeInsert(Statement stmt, String sql) throws SQLException {
	        //This is a VERY simplified example!  You'll need robust parsing for real-world use.
	        String[] parts = sql.split("\\(", 2);
	        String tableName = parts[0].substring(12).trim();
	        String valuesPart = parts[1].substring(0, parts[1].length() - 1);
	        String[] values = valuesPart.split(",");

	        //  Assume 3 columns for simplicity (adapt as needed!)
	        String insertSql = "INSERT INTO " + tableName + " VALUES (?, ?, ?)";
	        try (PreparedStatement pstmt = stmt.getConnection().prepareStatement(insertSql)) {
	            for (int i = 0; i < values.length; i++) {
	                pstmt.setString(i + 1, values[i].trim()); // VERY basic parameter setting; handle datatypes correctly!
	            }
	            pstmt.executeUpdate();
	        }
	    }

    private boolean isValidSchemaStatement(String sql) {
        if (sql == null || sql.isEmpty()) {
            return false;
        }

        String upperSql = sql.toUpperCase();
        List<String> validPrefixes = List.of("CREATE ", "DROP ", "ALTER ", "INSERT ", "DELETE ");
        return validPrefixes.stream().anyMatch(upperSql::startsWith);
    }

    private String[] loadFile(String filename, String[] skipCharacters, String delimiter) {
        String path = "/app/src/main/resources" + File.separator + filename;

        String regex = skipCharacters.length > 0
                ? "^(?:" + Pattern.quote(String.join("|", skipCharacters)) + ").*?$"
                : "";

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            return br.lines()
                    .filter(line -> !line.matches(regex)) 
                    .collect(Collectors.joining(System.lineSeparator()))
                    .split(delimiter);
        } catch (IOException ex) {
            logger.error("Error reading file: " + path, ex);
            return new String[0];
        }
    }
}
