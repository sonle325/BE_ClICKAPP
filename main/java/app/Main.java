package app;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.Statement;

import com.sun.net.httpserver.HttpServer;

import app.core.Db;
import app.core.Router;
import app.handlers.RegisterRoutes;

public class Main {
	public static void main(String[] args) throws Exception {
		String dbUrl = "jdbc:postgresql://localhost:5432/local";
		String dbUser = "Sonkdhy2005";
		String dbPassword = "Sonkdhy2005@";
		Db.init(dbUrl, dbUser, dbPassword);
		runMigrations("schema.sql");

		HttpServer server = HttpServer.create(new InetSocketAddress(8082), 0);
		Router router = new Router(server);

		router.register("/health", "GET", exchange -> Router.okJson(exchange, "{\"ok\":true}"));

		// Swagger endpoints
		setupSwaggerEndpoints(router);

		RegisterRoutes.registerAll(router);

		server.setExecutor(java.util.concurrent.Executors.newCachedThreadPool());
		server.start();
		System.out.println("Server started at http://localhost:8082");
		System.out.println("API Documentation: http://localhost:8082/api-docs");
	}

	private static void setupSwaggerEndpoints(Router router) {
		// Swagger JSON endpoint
		router.register("/swagger", "GET", exchange -> {
			String swaggerJson = createSwaggerJson();
			Router.okJson(exchange, swaggerJson);
		});

		// Swagger UI endpoint
		router.register("/api-docs", "GET", exchange -> {
			String swaggerUIHtml = createSwaggerUI();
			exchange.getResponseHeaders().add("Content-Type", "text/html");
			exchange.sendResponseHeaders(200, swaggerUIHtml.getBytes().length);
			try (OutputStream os = exchange.getResponseBody()) {
				os.write(swaggerUIHtml.getBytes());
			}
		});
	}

	private static String createSwaggerJson() {
		return """
			{
			  "openapi": "3.0.1",
			  "info": {
			    "title": "ClickApp API",
			    "description": "API documentation for ClickApp project management system",
			    "version": "1.0.0",
			    "contact": {
			      "name": "API Support",
			      "email": "support@clickapp.com"
			    }
			  },
			  "servers": [
			    {
			      "url": "http://localhost:8082",
			      "description": "Development server"
			    }
			  ],
			  "paths": {
			    "/health": {
			      "get": {
			        "summary": "Health check",
			        "description": "Check if the server is running",
			        "responses": {
			          "200": {
			            "description": "Server is healthy",
			            "content": {
			              "application/json": {
			                "schema": {
			                  "type": "object",
			                  "properties": {
			                    "ok": {
			                      "type": "boolean"
			                    }
			                  }
			                }
			              }
			            }
			          }
			        }
			      }
			    },
			    "/users": {
			      "get": {
			        "summary": "Get all users",
			        "description": "Retrieve a list of all users",
			        "responses": {
			          "200": {
			            "description": "List of users",
			            "content": {
			              "application/json": {
			                "schema": {
			                  "type": "array",
			                  "items": {
			                    "type": "object"
			                  }
			                }
			              }
			            }
			          }
			        }
			      },
			      "post": {
			        "summary": "Create new user",
			        "description": "Create a new user",
			        "requestBody": {
			          "required": true,
			          "content": {
			            "application/json": {
			              "schema": {
			                "type": "object",
			                "properties": {
			                  "name": {
			                    "type": "string"
			                  },
			                  "email": {
			                    "type": "string"
			                  }
			                }
			              }
			            }
			          }
			        },
			        "responses": {
			          "201": {
			            "description": "User created successfully"
			          }
			        }
			      }
			    },
			    "/tasks": {
			      "get": {
			        "summary": "Get tasks by project",
			        "description": "Retrieve tasks for a specific project",
			        "parameters": [
			          {
			            "name": "project_id",
			            "in": "query",
			            "required": true,
			            "schema": {
			              "type": "string"
			            }
			          }
			        ],
			        "responses": {
			          "200": {
			            "description": "List of tasks",
			            "content": {
			              "application/json": {
			                "schema": {
			                  "type": "array",
			                  "items": {
			                    "type": "object"
			                  }
			                }
			              }
			            }
			          }
			        }
			      },
			      "post": {
			        "summary": "Create new task",
			        "description": "Create a new task",
			        "requestBody": {
			          "required": true,
			          "content": {
			            "application/json": {
			              "schema": {
			                "type": "object",
			                "properties": {
			                  "project_id": {
			                    "type": "string"
			                  },
			                  "title": {
			                    "type": "string"
			                  },
			                  "status": {
			                    "type": "string"
			                  },
			                  "priority": {
			                    "type": "string"
			                  }
			                }
			              }
			            }
			          }
			        },
			        "responses": {
			          "201": {
			            "description": "Task created successfully"
			          }
			        }
			      }
			    }
			  }
			}
			""";
	}

	private static String createSwaggerUI() {
		return """
			<!DOCTYPE html>
			<html>
			<head>
			    <title>ClickApp API Documentation</title>
			    <link rel="stylesheet" type="text/css" href="https://unpkg.com/swagger-ui-dist@4.15.5/swagger-ui.css" />
			    <style>
			        html {
			            box-sizing: border-box;
			            overflow: -moz-scrollbars-vertical;
			            overflow-y: scroll;
			        }
			        *, *:before, *:after {
			            box-sizing: inherit;
			        }
			        body {
			            margin:0;
			            background: #fafafa;
			        }
			    </style>
			</head>
			<body>
			    <div id="swagger-ui"></div>
			    <script src="https://unpkg.com/swagger-ui-dist@4.15.5/swagger-ui-bundle.js"></script>
			    <script src="https://unpkg.com/swagger-ui-dist@4.15.5/swagger-ui-standalone-preset.js"></script>
			    <script>
			        window.onload = function() {
			            const ui = SwaggerUIBundle({
			                url: '/swagger',
			                dom_id: '#swagger-ui',
			                deepLinking: true,
			                presets: [
			                    SwaggerUIBundle.presets.apis,
			                    SwaggerUIStandalonePreset
			                ],
			                plugins: [
			                    SwaggerUIBundle.plugins.DownloadUrl
			                ],
			                layout: "StandaloneLayout"
			            });
			        };
			    </script>
			</body>
			</html>
			""";
	}

	private static void runMigrations(String schemaFile) throws Exception {
		String sql = Files.readString(Path.of(schemaFile));
		try (Connection c = Db.get(); Statement st = c.createStatement()) {
			st.executeUpdate("PRAGMA foreign_keys = ON;");
			for (String stmt : sql.split(";\\s*\\n")) {
				String trimmed = stmt.trim();
				if (!trimmed.isEmpty()) {
					st.executeUpdate(trimmed + ";");
				}
			}
		}
	}
}


