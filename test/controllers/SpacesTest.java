package controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static play.test.Helpers.callAction;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.fakeGlobal;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.start;
import static play.test.Helpers.status;
import models.Space;

import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import play.mvc.Result;
import utils.LoadData;
import utils.ModelConversion;
import utils.TestConnection;

import com.google.common.collect.ImmutableMap;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class SpacesTest {

	@Before
	public void setUp() {
		start(fakeApplication(fakeGlobal()));
		TestConnection.connectToTest();
		LoadData.load();
	}

	@After
	public void tearDown() {
		TestConnection.close();
	}

	@Test
	public void addSpace() throws IllegalArgumentException, IllegalAccessException, InstantiationException {
		Result result = callAction(controllers.routes.ref.Spaces.add(), fakeRequest().withSession("email", "test1@example.com")
				.withFormUrlEncodedBody(ImmutableMap.of("name", "Test space", "visualization", "Test visualization")));
		assertEquals(200, status(result));
		DBObject foundSpace = TestConnection.getCollection("spaces").findOne(new BasicDBObject("name", "Test space"));
		Space space = ModelConversion.mapToModel(Space.class, foundSpace.toMap());
		assertNotNull(space);
		assertEquals("Test space", space.name);
		assertEquals("test1@example.com", space.owner);
		assertEquals("Test visualization", space.visualization);
		assertEquals(0, space.records.size());
	}

	@Test
	public void renameSpaceSuccess() {
		DBCollection spaces = TestConnection.getCollection("spaces");
		DBObject query = new BasicDBObject("name", new BasicDBObject("$ne", "Test space 2"));
		DBObject space = spaces.findOne(query);
		ObjectId id = (ObjectId) space.get("_id");
		String owner = (String) space.get("owner");
		String spaceId = id.toString();
		Result result = callAction(controllers.routes.ref.Spaces.rename(spaceId), fakeRequest().withSession("email", owner)
				.withFormUrlEncodedBody(ImmutableMap.of("name", "Test space 2")));
		assertEquals(200, status(result));
		BasicDBObject idQuery = new BasicDBObject("_id", id);
		assertEquals("Test space 2", spaces.findOne(idQuery).get("name"));
		assertEquals(owner, spaces.findOne(idQuery).get("owner"));
	}

	@Test
	public void renameSpaceForbidden() {
		DBCollection spaces = TestConnection.getCollection("spaces");
		DBObject query = new BasicDBObject();
		query.put("owner", new BasicDBObject("$ne", "test2@example.com"));
		query.put("name", new BasicDBObject("$ne", "Test space 2"));
		DBObject space = spaces.findOne(query);
		ObjectId id = (ObjectId) space.get("_id");
		String spaceId = id.toString();
		String originalName = (String) space.get("name");
		String originalOwner = (String) space.get("owner");
		Result result = callAction(controllers.routes.ref.Spaces.rename(spaceId), fakeRequest().withSession("email", "test2@example.com")
				.withFormUrlEncodedBody(ImmutableMap.of("name", "Test space 2")));
		assertEquals(403, status(result));
		BasicDBObject idQuery = new BasicDBObject("_id", id);
		assertEquals(originalName, spaces.findOne(idQuery).get("name"));
		assertEquals(originalOwner, spaces.findOne(idQuery).get("owner"));
	}

	@Test
	public void deleteSpaceSuccess() {
		DBCollection spaces = TestConnection.getCollection("spaces");
		long originalCount = spaces.count();
		DBObject space = spaces.findOne();
		ObjectId id = (ObjectId) space.get("_id");
		String owner = (String) space.get("owner");
		String spaceId = id.toString();
		Result result = callAction(controllers.routes.ref.Spaces.delete(spaceId), fakeRequest().withSession("email", owner));
		assertEquals(200, status(result));
		assertNull(spaces.findOne(new BasicDBObject("_id", id)));
		assertEquals(originalCount - 1, spaces.count());
	}

	@Test
	public void deleteSpaceForbidden() {
		DBCollection spaces = TestConnection.getCollection("spaces");
		long originalCount = spaces.count();
		DBObject query = new BasicDBObject();
		query.put("owner", new BasicDBObject("$ne", "test2@example.com"));
		DBObject space = spaces.findOne(query);
		ObjectId id = (ObjectId) space.get("_id");
		String spaceId = id.toString();
		Result result = callAction(controllers.routes.ref.Spaces.rename(spaceId), fakeRequest().withSession("email", "test2@example.com"));
		assertEquals(403, status(result));
		assertNotNull(spaces.findOne(new BasicDBObject("_id", id)));
		assertEquals(originalCount, spaces.count());
	}

}