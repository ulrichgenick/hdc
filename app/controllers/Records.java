package controllers;

import models.App;
import models.ModelException;

import org.bson.types.ObjectId;

import play.Play;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.dialogs.createrecords;

@Security.Authenticated(Secured.class)
public class Records extends Controller {

	public static Result create(String appIdString) {
		ObjectId appId = new ObjectId(appIdString);
		App app;
		try {
			app = App.find(appId);
		} catch (ModelException e) {
			return internalServerError(e.getMessage());
		}
		String localhost = Play.application().configuration().getString("external.host");
		return ok(createrecords.render(app, localhost, new ObjectId(request().username())));
	}

}
