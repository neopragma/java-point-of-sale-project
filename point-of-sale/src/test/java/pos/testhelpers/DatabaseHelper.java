package pos.testhelpers;

import static pos.utils.Utils.databaseHost;
import static pos.utils.Utils.databaseName;

import java.io.IOException;
import java.net.UnknownHostException;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

public class DatabaseHelper {
	
	public void clearCollection(String collectionName) throws IOException {
		collection(collectionName).remove(new BasicDBObject());
	}
	
	public void loadProducts() throws IOException {
		
	}

	private DBCollection collection(String collectionName) throws IOException {
		MongoClient mongo = new MongoClient(databaseHost());
		DB db = mongo.getDB(databaseName());
		return db.getCollection(collectionName);		
	}
	
}
