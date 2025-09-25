package app.handlers;

import app.core.Router;

public final class RegisterRoutes {
	public static void registerAll(Router router) {
		var users = new UsersHandler();
		router.register("/users", "GET", users::list);
		router.register("/users", "POST", users::create);
		router.register("/users/{id}", "GET", users::getById);
		router.register("/users/{id}", "PUT", users::update);
		router.register("/users/{id}", "DELETE", users::delete);

		var teams = new TeamsHandler();
		router.register("/teams", "GET", teams::list);
		router.register("/teams", "POST", teams::create);
		router.register("/teams/{id}", "GET", teams::getById);
		router.register("/teams/{id}", "PUT", teams::update);
		router.register("/teams/{id}", "DELETE", teams::delete);

		var projects = new ProjectsHandler();
		router.register("/projects", "GET", projects::listByTeam);
		router.register("/projects", "POST", projects::create);
		router.register("/projects/{id}", "GET", projects::getById);
		router.register("/projects/{id}", "PUT", projects::update);
		router.register("/projects/{id}", "DELETE", projects::delete);

		var tasks = new TasksHandler();
		router.register("/tasks", "GET", tasks::listByProject);
		router.register("/tasks", "POST", tasks::create);
		router.register("/tasks/{id}", "GET", tasks::getById);
		router.register("/tasks/{id}", "PUT", tasks::update);
		router.register("/tasks/{id}", "DELETE", tasks::delete);

		var comments = new CommentsHandler();
		router.register("/comments", "GET", comments::listByTask);
		router.register("/comments", "POST", comments::create);
		router.register("/comments/{id}", "GET", comments::getById);
		router.register("/comments/{id}", "PUT", comments::update);
		router.register("/comments/{id}", "DELETE", comments::delete);

		var labels = new LabelsHandler();
		router.register("/labels", "GET", labels::listByTeam);
		router.register("/labels", "POST", labels::create);
		router.register("/labels/{id}", "GET", labels::getById);
		router.register("/labels/{id}", "PUT", labels::update);
		router.register("/labels/{id}", "DELETE", labels::delete);

		var taskLabels = new TaskLabelsHandler();
		router.register("/task-labels", "GET", taskLabels::listByTask);
		router.register("/task-labels", "POST", taskLabels::add);
		router.register("/task-labels/{task_id}/{label_id}", "DELETE", taskLabels::remove);

		var checklists = new ChecklistsHandler();
		router.register("/checklists", "GET", checklists::listByTask);
		router.register("/checklists", "POST", checklists::create);
		router.register("/checklists/{id}", "GET", checklists::getById);
		router.register("/checklists/{id}", "PUT", checklists::update);
		router.register("/checklists/{id}", "DELETE", checklists::delete);

		var checklistItems = new ChecklistItemsHandler();
		router.register("/checklist-items", "GET", checklistItems::listByChecklist);
		router.register("/checklist-items", "POST", checklistItems::create);
		router.register("/checklist-items/{id}", "GET", checklistItems::getById);
		router.register("/checklist-items/{id}", "PUT", checklistItems::update);
		router.register("/checklist-items/{id}", "DELETE", checklistItems::delete);

		var teamMembers = new TeamMembersHandler();
		router.register("/team-members", "GET", teamMembers::listByTeam);
		router.register("/team-members", "POST", teamMembers::add);
		router.register("/team-members/{team_id}/{user_id}", "PUT", teamMembers::updateRole);
		router.register("/team-members/{team_id}/{user_id}", "DELETE", teamMembers::remove);

		var projectMembers = new ProjectMembersHandler();
		router.register("/project-members", "GET", projectMembers::listByProject);
		router.register("/project-members", "POST", projectMembers::add);
		router.register("/project-members/{project_id}/{user_id}", "PUT", projectMembers::updateRole);
		router.register("/project-members/{project_id}/{user_id}", "DELETE", projectMembers::remove);

		var roles = new RolesHandler();
		router.register("/roles", "GET", roles::list);
		router.register("/roles", "POST", roles::create);
		router.register("/roles/{id}", "GET", roles::getById);
		router.register("/roles/{id}", "PUT", roles::update);
		router.register("/roles/{id}", "DELETE", roles::delete);

		var permissions = new PermissionsHandler();
		router.register("/permissions", "GET", permissions::list);
		router.register("/permissions", "POST", permissions::create);
		router.register("/permissions/{id}", "GET", permissions::getById);
		router.register("/permissions/{id}", "PUT", permissions::update);
		router.register("/permissions/{id}", "DELETE", permissions::delete);

		var rolePermissions = new RolePermissionsHandler();
		router.register("/role-permissions", "GET", rolePermissions::listByRole);
		router.register("/role-permissions", "POST", rolePermissions::add);
		router.register("/role-permissions/{role_id}/{permission_id}", "DELETE", rolePermissions::remove);

		var taskAssignees = new TaskAssigneesHandler();
		router.register("/task-assignees", "GET", taskAssignees::listByTask);
		router.register("/task-assignees", "POST", taskAssignees::add);
		router.register("/task-assignees/{task_id}/{user_id}", "DELETE", taskAssignees::remove);
	}
}


