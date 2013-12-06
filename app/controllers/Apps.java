package controllers;

import models.Record;

import org.bson.types.ObjectId;

import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import utils.DateTimeUtils;
import utils.ModelConversion.ConversionException;
import utils.search.SearchException;

import com.fasterxml.jackson.databind.JsonNode;

// Not secured, accessible from app server
public class Apps extends Controller {

	public static Result getRecord(String recordId) {
		String data = Record.getData(new ObjectId(recordId));

		// allow cross origin request from app server
		response().setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
		return ok(data);
	}

	public static Result checkPreflight() {
		// allow cross origin request from app server
		response().setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
		response().setHeader("Access-Control-Allow-Methods", "POST");
		response().setHeader("Access-Control-Allow-Headers", "Content-Type");
		return ok();
	}

	@BodyParser.Of(BodyParser.Json.class)
	public static Result createRecord() {
		// check whether the request is complete
		JsonNode json = request().body().asJson();
		if (json == null) {
			return badRequest("No json found.");
		} else if (json.get("user") == null) {
			return badRequest("No user found.");
		} else if (json.get("data") == null) {
			return badRequest("No data found.");
		} else if (json.get("name") == null) {
			return badRequest("No name found.");
		} else if (json.get("description") == null) {
			return badRequest("No description found.");
		}

		// parse the header to retrieve app id
		String[] split = request().getHeader("Referer").split("/");
		String appId = split[split.length - 1];

		// save new record with additional metadata
		String user = json.get("user").asText();
		String data = json.get("data").toString();
		String name = json.get("name").asText();
		String description = json.get("description").asText();
		Record record = new Record();
		record.app = new ObjectId(appId);
		record.creator = new ObjectId(user);
		record.owner = new ObjectId(user);
		record.created = DateTimeUtils.getNow();
		record.data = data;
		record.name = name;
		record.description = description;
		String errorMessage;
		try {
			errorMessage = Record.add(record);
		} catch (ConversionException e) {
			return badRequest(e.getMessage());
		} catch (SearchException e) {
			return badRequest(e.getMessage());
		}
		if (errorMessage != null) {
			return badRequest(errorMessage);
		}

		// allow cross origin request from app server
		response().setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
		return ok();
	}

}