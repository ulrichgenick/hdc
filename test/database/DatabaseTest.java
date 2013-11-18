package database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.fakeGlobal;
import static play.test.Helpers.start;
import models.Circle;
import models.Message;
import models.User;

import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import utils.Connection;
import utils.DateTimeUtils;
import utils.ModelConversion;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class DatabaseTest {

	@Before
	public void setUp() {
		start(fakeApplication(fakeGlobal()));
		Connection.connectToTest();
		Connection.destroy();
	}

	@After
	public void tearDown() {
		Connection.close();
	}

	@Test
	public void createAndSaveObject() {
		DBCollection users = Connection.getCollection("users");
		assertEquals(0, users.count());
		users.insert(new BasicDBObject("name", "Test User"));
		assertEquals(1, users.count());
		DBObject foundObject = users.findOne();
		assertTrue(foundObject.containsField("name"));
		assertEquals("Test User", foundObject.get("name"));
	}

	@Test
	public void createAndSaveUser() throws IllegalArgumentException, IllegalAccessException {
		DBCollection users = Connection.getCollection("users");
		assertEquals(0, users.count());
		User user = new User();
		user.email = "test1@example.com";
		user.name = "Test User";
		user.password = "secret";
		users.insert(new BasicDBObject(ModelConversion.modelToMap(user)));
		assertEquals(1, users.count());
		DBObject foundObject = users.findOne();
		assertTrue(foundObject.containsField("name"));
		assertEquals("Test User", foundObject.get("name"));
	}

	@Test
	public void createAndRetrieveUser() throws IllegalArgumentException, IllegalAccessException, InstantiationException {
		DBCollection users = Connection.getCollection("users");
		assertEquals(0, users.count());
		User user = new User();
		user.email = "test1@example.com";
		user.name = "Test User";
		user.password = "secret";
		users.insert(new BasicDBObject(ModelConversion.modelToMap(user)));
		assertEquals(1, users.count());
		DBObject foundObject = users.findOne();
		User retrievedUser = ModelConversion.mapToModel(User.class, foundObject.toMap());
		assertEquals("Test User", retrievedUser.name);
	}

	@Test
	public void createAndRetrieveMessage() throws IllegalArgumentException, IllegalAccessException,
			InstantiationException {
		DBCollection messages = Connection.getCollection("messages");
		assertEquals(0, messages.count());
		Message message = new Message();
		message.sender = new ObjectId();
		message.receiver = new ObjectId();
		message.created = DateTimeUtils.getNow();
		message.title = "Test";
		message.content = "This is a test message.";
		messages.insert(new BasicDBObject(ModelConversion.modelToMap(message)));
		assertEquals(1, messages.count());
		DBObject foundObject = messages.findOne();
		Message retrievedMessage = ModelConversion.mapToModel(Message.class, foundObject.toMap());
		assertEquals("Test", retrievedMessage.title);
	}

	@Test
	public void createAndRetrieveCircle() throws IllegalArgumentException, IllegalAccessException,
			InstantiationException {
		DBCollection circles = Connection.getCollection("circles");
		assertEquals(0, circles.count());
		Circle circle = new Circle();
		circle.name = "Family";
		circle.owner = new ObjectId();
		circle.members = new BasicDBList();
		circle.members.add("test2@example.com");
		circle.members.add("test3@example.com");
		circle.members.add("test4@example.com");
		circles.insert(new BasicDBObject(ModelConversion.modelToMap(circle)));
		assertEquals(1, circles.count());
		DBObject foundObject = circles.findOne();
		Circle retrievedCircle = ModelConversion.mapToModel(Circle.class, foundObject.toMap());
		assertEquals("Family", retrievedCircle.name);
	}

}