package pos.model;

import java.math.BigDecimal;

import static pos.common.Utils.databaseHost;
import static pos.common.Utils.databaseName;

import java.net.UnknownHostException;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

/**
 * Encapsulates information about a product
 * 
 * @author neopragma
 */
public class Product extends BasicDBObject{
	
	private static final long serialVersionUID = 585718079673038770L;
	private static final String PRODUCTS = "products";
	private static final String ID = "_id";
	private static final String SKU = "sku";
	private static final String DESCRIPTION = "description";
	private static final String UNIT_PRICE = "unit_price";
	private static final String TAXABLE = "taxable";

	String sku;
	String description;
	BigDecimal unitPrice;
	Boolean taxable;
	
	public Product(String sku, String description, BigDecimal unitPrice, Boolean taxable) {
		this.sku = sku;
		this.description = description;
		this.unitPrice = unitPrice;
		this.taxable = taxable;
	}
	
	public static Product newProduct(String sku, String description, BigDecimal unitPrice, Boolean taxable) throws UnknownHostException, DuplicateSkuException {
		Product product = new Product(sku, description, unitPrice, taxable);
		if (exists(sku)) {
			throw new DuplicateSkuException();
		}
		product.put(SKU, sku);
		product.put(DESCRIPTION, description);
		product.put(UNIT_PRICE, unitPrice.doubleValue());
		product.put(TAXABLE, taxable);
		collection().insert(product);
		return product;
	}
	
	public String getSku() {
		return sku;
	}
	
	public void setSku(String sku) {
		this.sku = sku;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public BigDecimal getUnitPrice() {
		return unitPrice;
	}
	
	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}
	
	public Boolean isTaxable() {
		return taxable;
	}
	
	public void setTaxable(Boolean taxable) {
		this.taxable = taxable;
	}
	
	public static boolean exists(String sku) throws UnknownHostException {
		DBObject searchBy = new BasicDBObject();
		searchBy.put(SKU, sku);
		DBObject returnFields = new BasicDBObject();
		returnFields.put(ID, 1 );
		return collection().findOne(searchBy, returnFields) != null;
	}

	private static DBCollection collection() throws UnknownHostException {
		MongoClient mongo = new MongoClient(databaseHost());
		DB db = mongo.getDB(databaseName());
		DBCollection collection = db.getCollection(PRODUCTS);
		return collection;
	}

}
