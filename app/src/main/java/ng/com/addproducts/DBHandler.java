package ng.com.addproducts;

/**
 * Created by Adeyemi Salau on 4/11/2017.
 */

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.android.internal.http.multipart.MultipartEntity;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class DBHandler extends SQLiteOpenHelper {
//public class DBHandler extends SQLiteAssetHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name

    private static String DB_NAME =  "asknigeria";// Database name
    // Contacts table name
    private static final String TABLE_USER = "user";

    private static final String TABLE_PRODUCT = "products";

    private static boolean sendmail = false;

    private static final String PRODUCT_CATEGORIES = "product_categories";

    private static final String ALL_PRODUCTS = "all_products";

    private static final String TABLE_PRODUCT_REVIEW = "product_review";

    private static final String SYNC = "sync";

    private static final String TITLE = "title";

    private static final String STATE = "state";

    private static final String REVIEWED_TITLE = "product_id";

    private static final String RATING = "rating";

    private static final String ID = "_id";

    private static final String IDENTIFIER = "identifier";

    private static final String USER_ID = "id";
    private static final String USER_EMAIL = "email";
    private static final String USER_PASSWORD = "password";
    private static final String DATE_OF_LOGIN = "date";

    private static final String CATEGORY = "category";

    private static final String C1 = "C1";

    private static final String C2 = "C2";

    private static final String NUMBER = "number";

    public DBHandler(Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE `product_categories` (\n" +
                "\t`_id`\tinteger NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                "\t`category`\tvarchar(255) DEFAULT NULL\n" +
                ");");

        db.execSQL("CREATE TABLE `sync` ( `state` INTEGER DEFAULT '0' )");
        db.execSQL("INSERT INTO `product_categories` (_id,category) VALUES (1,'Consumer Goods'),\n" +
                " (2,'Conglomerates'),\n" +
                " (3,'Financial Services'),\n" +
                " (4,'Services'),\n" +
                " (5,'Industrial Goods'),\n" +
                " (6,'HealthCare'),\n" +
                " (7,'Natural Resources'),\n" +
                " (8,'Oil and Gas'),\n" +
                " (9,'Construction and Real Estate'),\n" +
                " (10,'ICT'),\n" +
                " (11,'Agriculture');\n" +
                "COMMIT");

        db.execSQL("CREATE TABLE `product_review` (\n" +
                "\t`_id`\tinteger NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                "\t`product_id`\tinteger DEFAULT NULL,\n" +
                "\t`product_category`\tinteger DEFAULT NULL,\n" +
                "\t`likes`\tinteger DEFAULT NULL,\n" +
                "\t`dislikes`\tinteger DEFAULT NULL,\n" +
                "\t`rating`\tinteger DEFAULT NULL,\n" +
                "\t`user_comments`\tvarchar(4000) DEFAULT NULL,\n" +
                "\t`user_location_lat`\tfloat DEFAULT NULL,\n" +
                "\t`user_location_lon`\tfloat DEFAULT NULL,\n" +
                "\t`date`\tdatetime DEFAULT NULL,\n" +
                "\t`user`\tvarchar(255) DEFAULT NULL,\n" +
                "\t`identifier`\tTEXT DEFAULT NULL,\n" +
                "\t`synced`\tvarchar(8) DEFAULT 'false'\n" +
                ");");


        db.execSQL("CREATE TABLE `products` (\n" +
                "\t`_id`\tinteger NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                "\t`title`\tvarchar(255) DEFAULT NULL,\n" +
                "\t`category`\tinteger DEFAULT NULL,\n" +
                "\t`manufacturer`\tvarchar(255) DEFAULT NULL,\n" +
                "\t`price`\tREAL DEFAULT NULL,\n" +
                "\t`product_image_1`\tvarchar(255),\n" +
                "\t`product_image_2`\tvarchar(255),\n" +
                "\t`product_image_3`\tvarchar(255),\n" +
                "\t`product_image_4`\tvarchar(255),\n" +
                "\t`product_image_5`\tvarchar(255),\n" +
                "\t`competitor_1`\tvarchar(255) DEFAULT NULL,\n" +
                "\t`competitor_1_manufacturer`\tvarchar(255) DEFAULT NULL,\n" +
                "\t`competitor_2`\tvarchar(255) DEFAULT NULL,\n" +
                "\t`competitor_2_manufacturer`\tvarchar(255) DEFAULT NULL,\n" +
                "\t`competitor_3`\tvarchar(255) DEFAULT NULL,\n" +
                "\t`competitor_3_manufacturer`\tvarchar(255) DEFAULT NULL,\n" +
                "\t`competitor_4`\tvarchar(255) DEFAULT NULL,\n" +
                "\t`competitor_4_manufacturer`\tvarchar(255) DEFAULT NULL,\n" +
                "\t`competitor_5`\tvarchar(255) DEFAULT NULL,\n" +
                "\t`competitor_5_manufacturer`\tvarchar(255) DEFAULT NULL,\n" +
                "\t`competitor_6`\tvarchar(255) DEFAULT NULL,\n" +
                "\t`competitor_6_manufacturer`\tvarchar(255) DEFAULT NULL,\n" +
                "\t`competitor_7`\tvarchar(255) DEFAULT NULL,\n" +
                "\t`competitor_7_manufacturer`\tvarchar(255) DEFAULT NULL,\n" +
                "\t`competitor_8`\tvarchar(255) DEFAULT NULL,\n" +
                "\t`competitor_8_manufacturer`\tvarchar(255) DEFAULT NULL,\n" +
                "\t`competitor_9`\tvarchar(255) DEFAULT NULL,\n" +
                "\t`competitor_9_manufacturer`\tvarchar(255) DEFAULT NULL,\n" +
                "\t`identifier`\tTEXT DEFAULT NULL,\n" +
                "\t`synced`\tvarchar(8) DEFAULT 'false'\n" +
                ");");;

        db.execSQL("CREATE TABLE `user` (\n" +
                "\t`id`\tINTEGER NOT NULL DEFAULT '0' PRIMARY KEY AUTOINCREMENT,\n" +
                "\t`email`\tvarchar(255) DEFAULT NULL,\n" +
                "\t`password`\tvarchar(255) DEFAULT NULL,\n" +
                "\t`date`\tdatetime DEFAULT NULL\n" +
                ");");;

        db.execSQL("CREATE TABLE `users` (\n" +
                "\t`id`\tinteger NOT NULL DEFAULT '0',\n" +
                "\t`firstname`\tvarchar(255) DEFAULT NULL,\n" +
                "\t`lastname`\tvarchar(255) DEFAULT NULL,\n" +
                "\tPRIMARY KEY(`id`)\n" +
                ");");;

        db.execSQL("CREATE TABLE `all_products` ( `_id` integer NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                "`title` varchar(255) DEFAULT NULL, `category` integer DEFAULT NULL, " +
                "`competitor_1` varchar(255) DEFAULT NULL, `competitor_2` varchar(255) DEFAULT NULL," +
                "`about` varchar(255) DEFAULT NULL," +
                "`manufacturer` varchar(255) DEFAULT NULL," +
                "`address` varchar(255) DEFAULT NULL," +
                "`ingredients` varchar(255) DEFAULT NULL," +
                "`updated` integer DEFAULT '0', " +
                "`product_id` integer DEFAULT NULL, " +
                "`product_image_1` varchar(255) DEFAULT NULL, " +
                "`product_image_2` varchar(255) DEFAULT NULL, " +
                "`price` REAL DEFAULT NULL, " +
                "`identifier` varchar(255) DEFAULT NULL " +
                ")");

        db.execSQL("INSERT INTO `all_products` VALUES (7,'Turbor king',1,'Williams','Heineken Beer\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(8,'Williams',1,'Turbor king','Heineken Beer\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(14,'harp',1,'satzenbrau','big stout\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(19,'Baby comfort Diapers\\n',1,'Molfix','baby hugg',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(21,'Darling',1,'X-pression','X-brighter attachment\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(22,'X-brighter attachment\\n',1,'Darling','X-pression',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(28,'Soul mate Hair cream\\n',1,'Dallas','Damatol',NULL,NULL,NULL,NULL,0,62,NULL,NULL,NULL,NULL),(41,'Peak milk\\n',1,'Loya','Dano',NULL,NULL,NULL,NULL,0,49,NULL,NULL,NULL,NULL),(42,'Sprite Beverage',1,'7up Beverage Drink','Limca Beverage Drink\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(43,'7up Beverage Drink',1,'Sprite Beverage','Limca Beverage Drink\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(44,'Limca Beverage Drink\\n',1,'7up Beverage Drink','Sprite Beverage',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(54,'Clair-liss',1,'Caro white','Dove body Cream\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(56,'Dove body Cream\\n',1,'Caro white','Clair-liss',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(57,'Lucky',1,'Leosmart','Bic Pens\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(58,'Leosmart',1,'Lucky','Bic Pens\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(59,'Bic Pens\\n',1,'Leosmart','Lucky',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(60,'Eco',1,'Val-u','FASCO bread\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(61,'Val-u',1,'Eco','FASCO bread\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(62,'FASCO bread\\n',1,'Val-u','Eco',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(63,'Maggi',1,'suppy chicken','knorr chicken Maggi\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(64,'suppy chicken',1,'Maggi','knorr chicken Maggi\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(65,'knorr chicken Maggi\\n',1,'suppy chicken','Maggi',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(68,'Honey well spaghetti\\n',1,'Dangote','Golden penny',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(70,'lady b',1,'Custard','Bolero.\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(71,'Bolero.\\n',1,'lady b','Custard',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(72,'Dstv',4,'star times','Gotv Decoders\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(73,'star times',4,'Dstv','Gotv Decoders\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(74,'Gotv Decoders\\n',4,'star times','Dstv',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(80,'Diva sanitary Pads\\n',1,'lady care','Always Ultra',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(81,'Infinity',1,'nasco corn flakes','Good morning flakes\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(82,'nasco corn flakes',1,'Infinity','Good morning flakes\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(83,'Good morning flakes\\n',1,'nasco corn flakes','Infinity',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(84,'Soo white',1,'Jergens','Rapid White Lotions\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(85,'Jergens',1,'Soo white','Rapid White Lotions\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(86,'Rapid White Lotions\\n',1,'Jergens','Soo white',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(87,'Smart',1,'mascolino','Explore perfumes\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(88,'mascolino',1,'Smart','Explore perfumes\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(89,'Explore perfumes\\n',1,'mascolino','Smart',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(91,'X-Treme',1,'X-Pression','Easy Braid Attachments\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(92,'Easy Braid Attachments\\n',1,'X-Treme','X-Pression',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(94,'Mercy',1,'minimie','Divine grace Chin Chin\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(95,'Divine grace Chin Chin\\n',1,'Mercy','minimie',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(98,'Relax Relaxer\\n',1,'Ozone','mega Growth',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(99,'Rich milk',1,'igloo','fibre active biscuits\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(100,'igloo',1,'Rich milk','fibre active biscuits\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(102,'mama Gold Groundnut Oil',1,'Groundnut Oil','Power Oil\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(103,'Groundnut Oil',1,'mama Gold','Power Oil\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(104,'Power Oil\\n',1,'Groundnut Oil','mama Gold',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(105,'Joy Tomamto',1,'Gino','Rosa Tomato paste\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(107,'Rosa Tomato paste\\n',1,'Gino','Joy Tomamto',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(108,'Drum beans',1,'Honey beans','White Beans\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(109,'Honey beans',1,'Drum beans','White Beans\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(110,'White Beans\\n',1,'Honey beans','Drum beans',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(111,'Mama Gold',1,'Semolina','Semovita\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(112,'Semolina',1,'Mama Gold','Semovita\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(113,'Semovita\\n',1,'Semolina','Mama Gold',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(114,'Caro White',1,'perfect white','cocoa butter Body cream\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(116,'cocoa butter Body cream\\n',1,'perfect white','Caro White',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(120,'butterfly',1,'Cinge','Benbomatic sewing Machines\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(121,'Cinge',5,'butterfly','Benbomatic sewing Machines\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(122,'Benbomatic sewing Machines\\n',5,'Cinge','butterfly',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(123,'Water Gum',5,'Efostic Gum','UHU Gum\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(124,'Efostic Gum',5,'Water Gum','UHU Gum\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(125,'UHU Gum\\n',5,'Efostic Gum','Water Gum',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(126,'Nestle',1,'Good morning','Milo Cornflakes\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(127,'Good morning',1,'Nestle','Milo Cornflakes\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(128,'Milo Cornflakes\\n',1,'Good morning','Nestle',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(129,'Honey well',1,'Dangote','St Louis Sugars\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(131,'St Louis Sugars\\n',1,'Dangote','Honey well',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(132,'Rolex',1,'G-Shock','Casio wrist watches\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(133,'G-Shock',1,'Rolex','Casio wrist watches\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(134,'Casio wrist watches\\n',1,'G-Shock','Rolex',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(135,'Hp',10,'Dell','Toshiba Laptops\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(136,'Dell',10,'Hp','Toshiba Laptops\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(137,'Toshiba Laptops\\n',10,'Dell','Hp',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(141,'Flower Polish\\n',1,'Lude','Bee',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(143,'Tasty Water',1,'Eva','Good win Water\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(144,'Good win Water\\n',1,'Tasty Water','Eva',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(148,'Veleta',1,'Eva','St Lauren Wines\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(150,'St Lauren Wines\\n',1,'Eva','Veleta',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(151,'Fine Coat',5,'Prestige','Blue Boat Paints\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(152,'Prestige',5,'Fine Coat','Blue Boat Paints\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(153,'Blue Boat Paints\\n',5,'Prestige','Fine Coat',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(156,'Miss Bimbo Detergents\\n',1,'Good Mama','Canoe',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(159,'Plus Tissue Papers\\n',1,'Rose','Software',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(160,'Nora',1,'Sunrise','Heinz sweet corn\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(161,'Sunrise',1,'Nora','Heinz sweet corn\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(162,'Heinz sweet corn\\n',1,'Sunrise','Nora',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(163,'Power Oil',1,'Mamador','Kings oil\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(165,'Kings oil\\n',1,'Mamador','Power Oil',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(169,'Mobil',1,'Golden','Total Super Oil',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(170,'Golden',1,'Mobil','Total Super Oil',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(171,'Total Super Oil',1,'Golden','Mobil',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(172,'Guaranty Trust Bank',3,'First Bank','Zenith Bank',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(173,'Zenith Bank',3,'Guaranty Trust Bank','First Bank',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(174,'First Bank',3,'Zenith Bank','Guaranty Trust Bank',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(175,'9mobile Call Services',4,'MTN Call Services','Airtel Call Services',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(176,'MTN Call Services',4,'Airtel Call Services','9mobile Call Services',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(177,'Airtel Call Services',4,'Glo Call Services','MTN Call Services',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(178,'Glo Call Services',4,'MTN Call Services','Airtel Call Services',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(179,'MTN Data Services',4,'Glo Data Services','9mobile Data Services',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(180,'Glo Data Services',4,'9mobile Data Services','MTN Data Services',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(181,'Airtel Data Services',4,'Glo Data Services','9mobile Data Services',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(182,'9mobile Data Services',4,'Airtel Data Services','MTN Data Services',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(183,'United Bank For Africa',3,'Access Bank','Skye Bank',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(184,'Access Bank',3,'Skye Bank','United Bank For Africa',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(185,'Fidelity Bank',3,'Sterling Bank','Diamond Bank',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(186,'Sterling Bank',3,'Diamond Bank','Fidelity Bank',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(187,'Diamond Bank',3,'Fidelity Bank','Sterling Bank',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(188,'Ecobank',3,'Heritage Bank','Wema Bank',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(189,'Heritage Bank',3,'Wema Bank','Ecobank',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(190,'Standard Chartered Bankk',3,'Stanbic IBTC Bank','First City Monument Bank',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(191,'Stanbic IBTC Bank',3,'First City Monument Bank','Standard Chartered Bank',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public Cursor getAllProducts() {
        final SQLiteDatabase db = this.getReadableDatabase();
        Cursor mCursor = db.query(TABLE_PRODUCT, new String[] {"_id", TITLE, IDENTIFIER}, null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public Cursor getAllProductsReviewed() {
        final SQLiteDatabase db = this.getReadableDatabase();
        Cursor mCursor = db.query(TABLE_PRODUCT_REVIEW, new String[] {"_id", REVIEWED_TITLE, RATING}, null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public Cursor getAllProduct() {
        String selectQuery = "SELECT all_products._id AS _id, all_products.title AS title " +
                " FROM " + ALL_PRODUCTS + " WHERE updated = '1'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor;
        //final SQLiteDatabase db = this.getReadableDatabase();
        //Cursor mCursor = db.query(TABLE_PRODUCT_REVIEW, new String[] {"_id", REVIEWED_TITLE, RATING}, null, null, null, null, null);
        //if (mCursor != null) {
        //    mCursor.moveToFirst();
        //}
        //return mCursor;
    }

    public Cursor getAllProductsReviewedNew() {
        String selectQuery = "SELECT product_review._id AS _id, all_products.title AS product_id, product_review.rating AS rating " +
                " FROM " + TABLE_PRODUCT_REVIEW + " JOIN all_products ON product_review.product_id = all_products._id";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor;
        //final SQLiteDatabase db = this.getReadableDatabase();
        //Cursor mCursor = db.query(TABLE_PRODUCT_REVIEW, new String[] {"_id", REVIEWED_TITLE, RATING}, null, null, null, null, null);
        //if (mCursor != null) {
        //    mCursor.moveToFirst();
        //}
        //return mCursor;
    }

    public List<String> getAllCategories() {
        final SQLiteDatabase db = this.getReadableDatabase();
        List<String> results = new ArrayList<String>();
        Cursor mCursor = db.query(PRODUCT_CATEGORIES, new String[] {CATEGORY}, null, null, null, null, null);
        int iCM = mCursor.getColumnIndex(CATEGORY);
        for (mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
            results.add(mCursor.getString(iCM));
        }
        return results;
    }

    public List<String> getAllGlobalProducts() {
        final SQLiteDatabase db = this.getReadableDatabase();
        List<String> results = new ArrayList<String>();
        // Initializing a String Array
        String[] plants = new String[]{
                " ... Select Product ... ",
        };
        results.addAll(Arrays.asList(plants));
        Cursor mCursor = db.query(ALL_PRODUCTS, new String[] {TITLE}, null, null, null, null, null);
        int iCM = mCursor.getColumnIndex(TITLE);
        for (mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
            results.add(mCursor.getString(iCM));
        }
        return results;
    }

    public List<String> getProductsForReview() {
        final SQLiteDatabase db = this.getReadableDatabase();
        List<String> results = new ArrayList<String>();
        // Initializing a String Array
        String[] plants = new String[]{
                "Select Product...",
        };
        results.addAll(Arrays.asList(plants));
        // As Modified on the 19th of August, we now get list of products for reviews from all_products table.
        Cursor mCursor = db.query(ALL_PRODUCTS, new String[] {TITLE}, null, null, null, null, null);
        //Cursor mCursor = db.query(TABLE_PRODUCT, new String[] {TITLE}, null, null, null, null, null);
        int iCM = mCursor.getColumnIndex(TITLE);
        for (mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
            results.add(mCursor.getString(iCM));
        }
        return results;
    }

    public String getUniqueIdentifier(String product) {
        final SQLiteDatabase db = this.getReadableDatabase();
        if (product.contains("'")) {
            product = product.replaceAll("'", "''");
        }
        String[] tableColumns_ = new String[] {"identifier"};
        String whereClause_ = "title = ?";
        String[] whereArgs_ = new String[] {product};
        Cursor mCursor_ = db.query(TABLE_PRODUCT, tableColumns_, whereClause_, whereArgs_, null, null, null);
        int iCM_ = mCursor_.getColumnIndex(IDENTIFIER);
        String results = "";
        for (mCursor_.moveToFirst(); !mCursor_.isAfterLast(); mCursor_.moveToNext()) {
            results = mCursor_.getString(iCM_);
        }
        return results;
    }


    public Cursor getAllDataProductReview() {
        String selectQuery = "SELECT product_id, product_category, likes, dislikes, rating, user_comments,user_location_lat," +
                "user_location_lon, date,user,identifier FROM " + TABLE_PRODUCT_REVIEW;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor;
    }

    public Cursor getAllReviewedProducts() {
        String selectQuery = "select product_review.product_id AS product_id,\n" +
                "product_review.product_category AS product_category,product_review.likes AS likes,\n" +
                "product_review.dislikes AS dislikes,product_review.rating AS rating,\n" +
                "product_review.user_comments AS user_comments, product_review.user_location_lat AS latitude,\n" +
                "product_review.user_location_lon AS longitude, product_review.date AS date_of_review, \n" +
                "product_review.user AS user, product_review.identifier AS identifier from product_review where synced = 'false'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor;
    }

    public Cursor getProducts() {
        String selectQuery = "select _id, about, manufacturer, address, ingredients, product_image_1, product_image_2, price, identifier" +
                " FROM all_products WHERE updated = '1'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor;
    }

    public int getAllProductsToSync() {
        SQLiteDatabase db = this.getReadableDatabase();
        int products = (int) DatabaseUtils.queryNumEntries(db, "product_review", "synced=?", new String[] {"false"});
        int reviews = (int) DatabaseUtils.queryNumEntries(db, "products", "synced=?", new String[] {"false"});
        int total = products+reviews;
        return total;
    }

    public Cursor getAllDataProduct() {
        String selectQuery = "select title,category,manufacturer,price,product_image_1,product_image_2," +
                "competitor_1,competitor_1_manufacturer,competitor_2,competitor_2_manufacturer," +
                "identifier,synced from products where synced = 'false'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor;
    }

    public void synchronize(final View rv) {
        int state = getSync();
        if(state == 0) {
            // Syncing Stopped
            ;
        } else if(state == 1) {
            // Syncing in Progress
            // Review Part Regardless of Product Been Null or Not
            Cursor review = getProducts(); // cursor to hold all review data
            if(review != null) {
                String urlR = "http://104.236.3.3:3008";
                int product_id = review.getColumnIndex("_id");
                int about = review.getColumnIndex("about");
                int manufacturer = review.getColumnIndex("manufacturer");
                int address = review.getColumnIndex("address");
                int ingredients = review.getColumnIndex("ingredients");
                int product_image_1 = review.getColumnIndex("product_image_1");
                int product_image_2 = review.getColumnIndex("product_image_2");
                int price = review.getColumnIndex("price");
                int identifier = review.getColumnIndex("identifier");
                for (review.moveToFirst(); !review.isAfterLast(); review.moveToNext()) {
                    JSONObject json_obj_tempR = new JSONObject();
                    JSONArray arr_tempR = new JSONArray();
                    String st_tempR = null;
                    try{
                        JSONObject jobjR = new JSONObject();
                        jobjR.put("product_id", review.getString(product_id));
                        jobjR.put("about", review.getString(about));
                        jobjR.put("manufacturer", review.getString(manufacturer));
                        jobjR.put("address",review.getString(address));
                        jobjR.put("ingredients",review.getString(ingredients));
                        Uri uriFromPath_1 = Uri.fromFile(new File(review.getString(product_image_1)));
                        Uri uriFromPath_2 = Uri.fromFile(new File(review.getString(product_image_2)));
                        Bitmap bitmap1 = null;
                        Bitmap bitmap2 = null;
                        try {
                            bitmap1 = BitmapFactory.decodeStream(ApplicationName.getAppContext().getContentResolver().openInputStream(uriFromPath_1));
                            bitmap2 = BitmapFactory.decodeStream(ApplicationName.getAppContext().getContentResolver().openInputStream(uriFromPath_2));
                            // Compress
                            ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
                            bitmap1.compress(Bitmap.CompressFormat.JPEG, 100, baos1);
                            byte[] imageBytes1 = baos1.toByteArray();
                            String encodedImage1 = Base64.encodeToString(imageBytes1, Base64.NO_WRAP|Base64.URL_SAFE);
                            jobjR.put("product_image_1", encodedImage1);

                            ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
                            bitmap2.compress(Bitmap.CompressFormat.JPEG, 100, baos2);
                            byte[] imageBytes2 = baos2.toByteArray();
                            String encodedImage2 = Base64.encodeToString(imageBytes2, Base64.NO_WRAP| Base64.URL_SAFE);
                            jobjR.put("product_image_2", encodedImage2);

                            // End Compressing
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        jobjR.put("price",review.getString(price));
                        jobjR.put("identifier",review.getString(identifier));
                        arr_tempR.put(jobjR);
                        json_obj_tempR.put("all_products",arr_tempR);
                        st_tempR = json_obj_tempR.toString();
                        List<NameValuePair> paramsR = new ArrayList<NameValuePair>();
                        paramsR.add(new BasicNameValuePair("data", st_tempR));
                        String resultServerR  = getHttpPost(urlR,paramsR);
                        JSONObject aw = new JSONObject(resultServerR.toString());
                        int response = Integer.parseInt(aw.get("results").toString());
                        if(response == 1) {
                            updateProductStatus(product_id);
                        }

                    } catch (JSONException ex) {
                        ;
                    }
                }
                final TextView stat = (TextView)rv.findViewById(R.id.syncstatus);
                stat.setText("Sync Complete.");
            }
            // Delete All Synced Products First
            /*
            if(removeAllSyncedProductsReviewed()) {
                // Update Application of Sync Complete
                final TextView stat = (TextView)rv.findViewById(R.id.syncstatus);
                stat.setText("Sync Complete.");
            } else {
                // Update Application of Sync Complete
                final TextView stat = (TextView)rv.findViewById(R.id.syncstatus);
                stat.setText("Sync Complete.");
                //stat.setText("Sync couldn't delete reviews.");
            }
            */
        }
    }


    public long updateProductStatus(int pid) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues con = new ContentValues();
        con.put("updated", Integer.valueOf(0));
        return db.update(ALL_PRODUCTS, con, "_id ='" + pid + "'",null);
    }

    public long setSync(int value) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM sync");
        ContentValues con = new ContentValues();
        con.put("state", value);
        long rowInserted = db.insert(SYNC, null, con);
        db.close(); // Closing database connection
        return rowInserted;
    }

    public int getSync() {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] tableColumns_ = new String[] {"state"};
        Cursor mCursor_ = db.query(SYNC, tableColumns_, null, null, null, null, null);
        int iCM_ = mCursor_.getColumnIndex(STATE);
        int state = 0;
        for (mCursor_.moveToFirst(); !mCursor_.isAfterLast(); mCursor_.moveToNext()) {
            state = mCursor_.getInt(iCM_);
        }
        return state;
    }

    public long updateProductsSetSync(String identifier, String sync) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues con = new ContentValues();
        con.put("synced", sync);
        return db.update(TABLE_PRODUCT, con, "identifier ='" + identifier + "'",null);
    }

    public long updateProductReviewSetSync(String identifier, String sync) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues con = new ContentValues();
        con.put("synced", sync);
        return db.update(TABLE_PRODUCT_REVIEW, con, "identifier ='" + identifier + "'",null);
    }

    public void generateNoteOnSD(Context context, String sFileName, String sBody) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "AskNigeria");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();
            Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static HttpResponse makeRequest(String uri, String json) {
        try {
            HttpPost httpPost = new HttpPost(uri);
            httpPost.setEntity(new StringEntity(json));
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            HttpClient client = new DefaultHttpClient();
            HttpResponse response = client.execute(httpPost);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            //return new DefaultHttpClient().execute(httpPost);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void sendUpFile(String url, String json) {
        try {
            HttpURLConnection httpcon = (HttpURLConnection) ((new URL(url).openConnection()));
            httpcon.setDoOutput(true);
            httpcon.setRequestProperty("Content-Type","text/plain");
            httpcon.setRequestProperty("Accept", "application/json");
            httpcon.setRequestProperty("accept-charset", "UTF-8");
            httpcon.setRequestMethod("POST");
            httpcon.connect();

            byte[] outputBytes = json.getBytes("UTF-8");
            OutputStream os = httpcon.getOutputStream();
            os.write(outputBytes);
            os.flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void upFile(String url, String json) {
        try {
            AndroidHttpClient httpClient = AndroidHttpClient.newInstance("User Agent");

            URL urlObj = new URL(url);
            HttpHost host = new HttpHost(urlObj.getHost(), urlObj.getPort(), urlObj.getProtocol());
            AuthScope scope = new AuthScope(urlObj.getHost(), urlObj.getPort());
            HttpContext credContext = new BasicHttpContext();
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new StringEntity(json, "UTF8"));

            // Execute post request and get http response
            HttpResponse httpResponse = httpClient.execute(host, httpPost, credContext);
            httpClient.close();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getHttpPost(String url, List <NameValuePair> params) {
        StringBuilder str = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(params));
            HttpResponse response = client.execute(httpPost);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) { // Status OK
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    str.append(line);
                }
            } else {
                str.append("Failed to download result..");
            }
        } catch (ClientProtocolException e) {
            Log.e("Status Code 2",e.getMessage().toString());
            if(e.getMessage().toString().indexOf("refused") > 0) {
                // Send Mail
                if(!sendmail) {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.setType("message/rfc822");
                    i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"adeyemisalau@gmail.com"});
                    i.putExtra(Intent.EXTRA_SUBJECT, "Product Syncing Server Dead");
                    i.putExtra(Intent.EXTRA_TEXT   , "Either the product syncing server or the review syncing server is refusing connection. Kindly check.");
                    try {
                        ApplicationName.getAppContext().startActivity(Intent.createChooser(i, "Sending mail to Mr. Yemi Server Adminstrator..."));
                        sendmail = true;
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(ApplicationName.getAppContext(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } catch (IOException e) {
            Log.e("Status Code 2",e.getMessage().toString());
            if(e.getMessage().toString().indexOf("refused") > 0) {
                // Send Mail
                if(!sendmail) {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.setType("message/rfc822");
                    i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"adeyemisalau@gmail.com"});
                    i.putExtra(Intent.EXTRA_SUBJECT, "Prodct Syncing Server Dead");
                    i.putExtra(Intent.EXTRA_TEXT   , "Either the product syncing server or the review syncing server is refusing connection. Kindly check.");
                    try {
                        ApplicationName.getAppContext().startActivity(Intent.createChooser(i, "Sending mail to Mr. Yemi Server Adminstrator..."));
                        sendmail = true;
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(ApplicationName.getAppContext(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        return str.toString();
    }

    public List<String> getProductCompetitions(String cat) {
        final SQLiteDatabase db = this.getReadableDatabase();
        if (cat.contains("'")) {
            cat = cat.replaceAll("'", "''");
        }
        List<String> results = new ArrayList<String>();
        String sql = "select product_categories.category AS category, all_products.competitor_1 AS C1, all_products.competitor_2 AS C2 " +
                "FROM all_products JOIN product_categories ON all_products.category = product_categories._id " +
                "WHERE all_products.title = '"+cat+"' LIMIT 1";
        Cursor mCursor = db.rawQuery(sql, null);
        int iCM = mCursor.getColumnIndex(CATEGORY);
        int iC1 = mCursor.getColumnIndex(C1);
        int iC2 = mCursor.getColumnIndex(C2);
        for (mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
            String ct = mCursor.getString(iCM) + "_" + mCursor.getString(iC1) + "_" + mCursor.getString(iC2);
            results.add(ct);
        }
        return results;
    }

    public List<String> getProductCategoryByTitleMod(String cat) {
        final SQLiteDatabase db = this.getReadableDatabase();
        if (cat.contains("'")) {
            cat = cat.replaceAll("'", "''");
        }
        List<String> results = new ArrayList<String>();
        String sql = "select product_categories.category AS category from product_categories JOIN " +
                "products ON product_categories._id = products.category where products.title = '"+cat+"' LIMIT 1";
        Cursor mCursor = db.rawQuery(sql, null);
        int iCM = mCursor.getColumnIndex(CATEGORY);
        for (mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
            results.add(mCursor.getString(iCM));
        }
        return results;
    }

    public List<String> getProductCategoryByTitle(String cat) {
        final SQLiteDatabase db = this.getReadableDatabase();
        if (cat.contains("'")) {
            cat = cat.replaceAll("'", "''");
        }
        List<String> results = new ArrayList<String>();
        String sql = "select product_categories.category AS category from product_categories JOIN " +
                "all_products ON product_categories._id = all_products.category where all_products.title = '"+cat+"' LIMIT 1";
        Cursor mCursor = db.rawQuery(sql, null);
        int iCM = mCursor.getColumnIndex(CATEGORY);
        for (mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
            results.add(mCursor.getString(iCM));
        }
        return results;
    }

    public int getGlobalProductIDByTitle(String cat) {
        final SQLiteDatabase db = this.getReadableDatabase();
        if (cat.contains("'")) {
            cat = cat.replaceAll("'", "''");
        }
        String sql = "SELECT _id FROM all_products WHERE all_products.title = '"+cat+"' LIMIT 1";
        Cursor mCursor = db.rawQuery(sql, null);
        int iCM = mCursor.getColumnIndex(ID);
        int pid = 0;
        for (mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
            pid = mCursor.getInt(iCM);
        }
        return pid;
    }

    public int getProductCategory(String cat) {
        final SQLiteDatabase db = this.getReadableDatabase();
        String[] tableColumns = new String[] {"_id"};
        String whereClause = "category = ?";
        String[] whereArgs = new String[] {cat};
        Cursor mCursor = db.query(PRODUCT_CATEGORIES, tableColumns, whereClause, whereArgs, null, null, null);
        int iCM = mCursor.getColumnIndex(ID);
        int category = 0;
        for (mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
            category = mCursor.getInt(iCM);
        }
        return category;
    }

    public boolean removeAllSyncedProductsReviewed() {
        final SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_PRODUCT_REVIEW, "synced = ?", new String[]{"true"}) > 0;
    }

    public boolean removeAllSyncedProducts() {
        final SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_PRODUCT, "synced = ?", new String[]{"true"}) > 0;
    }

    public boolean removeProductReviewed(String item, String id) {
        int _id = Integer.parseInt(id);
        final SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_PRODUCT_REVIEW, "_id = ?", new String[]{id}) > 0;
    }

    public int canRemoveProduct(String item) {
        final SQLiteDatabase db = this.getWritableDatabase();
        String qu = "select count(*) AS number from product_review where identifier = '" +item+ "'";
        Cursor mCursor = db.rawQuery(qu, null);
        int iCM = mCursor.getColumnIndex(NUMBER);
        int num = 0;
        for (mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
            num = mCursor.getInt(iCM);
        }
        return num;
    }

    public boolean removeProduct(String item, String id) {
        final SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_PRODUCT, "_id = ?", new String[]{id}) > 0;
    }

    public long addReview(String product_name, String product_category, String like, String dislike, String rating, String comments,
                          Double latitude, Double longitude, String date, String user, String identifier) {
        final SQLiteDatabase db = this.getWritableDatabase();
        final ContentValues cv = new ContentValues();
        int pdc = getProductCategory(product_category);
        int product_id = getGlobalProductIDByTitle(product_name);
        cv.put("product_id",product_id);
        cv.put("product_category",pdc);
        cv.put("likes",like);
        cv.put("dislikes",dislike);
        cv.put("rating",rating);
        cv.put("user_comments",comments);
        cv.put("user_location_lat",latitude);
        cv.put("user_location_lon",longitude);
        cv.put("date",date);
        cv.put("user",user);
        cv.put("identifier",identifier);
        long rowInserted = db.insert(TABLE_PRODUCT_REVIEW, null, cv);
        db.close(); // Closing database connection
        return rowInserted;
    }

    public long updateProduct(String product,String product_category_txt,String aboutproduct,
                              String manufacturer,String address,String ingredients,
                              String image1,String image2,String price, String identifier) {
        final SQLiteDatabase db = this.getWritableDatabase();
        int id = getGlobalProductIDByTitle(product);
        ContentValues con = new ContentValues();
        con.put("about",aboutproduct);
        con.put("manufacturer",manufacturer);
        con.put("address",address);
        con.put("ingredients",ingredients);
        con.put("product_image_1",image1);
        con.put("product_image_2",image2);
        con.put("price",price);
        con.put("updated",Integer.valueOf(1));
        con.put("identifier",identifier);
        return db.update(ALL_PRODUCTS, con, "_id ='" + id + "'",null);
    }

    /*public long addProduct(String product_name,String product_category,String product_manufacturer,
                          byte[] image1,byte[] image2,byte[] image3,byte[] image4,byte[] image5,String competitor_one,
                          String competitor_one_man,String competitor_two,String competitor_two_man,
                          String competitor_three,String competitor_three_man,String identifier) {*/
    public long addProduct(String product_name,String product_category,String product_manufacturer,
                           String image1,String image2,String competitor_one,
                           String competitor_one_man,String competitor_two,String competitor_two_man,
                           String identifier,Double price) {
        final SQLiteDatabase db = this.getWritableDatabase();
        final ContentValues cv = new ContentValues();
        int pdc = getProductCategory(product_category);
        cv.put("title",product_name);
        cv.put("category",pdc);
        cv.put("manufacturer",product_manufacturer);
        cv.put("price",price);
        cv.put("product_image_1",image1);
        cv.put("product_image_2",image2);
        //cv.put("product_image_3",image3);
        //cv.put("product_image_4",image4);
        //cv.put("product_image_5",image5);
        cv.put("competitor_1",competitor_one);
        cv.put("competitor_1_manufacturer",competitor_one_man);
        cv.put("competitor_2",competitor_two);
        cv.put("competitor_2_manufacturer",competitor_two_man);
        //cv.put("competitor_3",competitor_three);
        //cv.put("competitor_3_manufacturer",competitor_three_man);
        cv.put("identifier",identifier);
        // Inserting Row
        long rowInserted = db.insert(TABLE_PRODUCT, null, cv);
        db.close(); // Closing database connection
        return rowInserted;
    }

    public long AddUser(User user, final LoginActivity context) {

            final SQLiteDatabase db = this.getWritableDatabase();
            final ContentValues values = new ContentValues();
            values.put(USER_EMAIL, user.getEmail()); // Logged In EMail
            values.put(USER_PASSWORD, user.getPassword()); // Password
            values.put(DATE_OF_LOGIN, user.getDate()); // Date
            // Inserting Row
            long rowInserted = db.insert(TABLE_USER, null, values);
            db.close(); // Closing database connection
            return rowInserted;
    }

    // Getting one user
    public User getUser(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USER, new String[] { USER_ID,
                        USER_EMAIL, USER_PASSWORD, DATE_OF_LOGIN }, USER_EMAIL + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        User contact = new User(cursor.getString(1), cursor.getString(2), cursor.getString(3));
        // return User
        return contact;
    }

    // Deleting a User
    public void deleteUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USER, USER_EMAIL + " = ?",
                new String[] { String.valueOf(user.getEmail()) });
        db.close();
    }

    public void dropTABLE(String table) {
        this.getWritableDatabase().execSQL("DROP TABLE IF EXISTS " + table);
        this.getWritableDatabase().execSQL("DROP TABLE IF EXISTS `products`");
    }

    public void dropTABLEGlobal() {
        this.getWritableDatabase().execSQL("DROP TABLE IF EXISTS `product_review`");
        this.getWritableDatabase().execSQL("DROP TABLE IF EXISTS `products`");
        this.getWritableDatabase().execSQL("DROP TABLE IF EXISTS `product_categories`");
        this.getWritableDatabase().execSQL("DROP TABLE IF EXISTS `sync`");
        this.getWritableDatabase().execSQL("DROP TABLE IF EXISTS `all_products`");
        createTABLE();
    }

    public void createTABLE() {
        this.getWritableDatabase().execSQL("CREATE TABLE `sync` ( `state` INTEGER DEFAULT '0' )");
        this.getWritableDatabase().execSQL("CREATE TABLE `product_categories` (\n" +
                "\t`_id`\tinteger NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                "\t`category`\tvarchar(255) DEFAULT NULL\n" +
                ");");
        this.getWritableDatabase().execSQL("INSERT INTO `product_categories` (_id,category) VALUES (1,'Consumer Goods'),\n" +
                " (2,'Conglomerates'),\n" +
                " (3,'Financial Services'),\n" +
                " (4,'Services'),\n" +
                " (5,'Industrial Goods'),\n" +
                " (6,'HealthCare'),\n" +
                " (7,'Natural Resources'),\n" +
                " (8,'Oil and Gas'),\n" +
                " (9,'Construction and Real Estate'),\n" +
                " (10,'ICT'),\n" +
                " (11,'Agriculture');\n" +
                "COMMIT");
        this.getWritableDatabase().execSQL("CREATE TABLE `product_review` (\n" +
                "\t`_id`\tinteger NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                "\t`product_id`\tinteger DEFAULT NULL,\n" +
                "\t`product_category`\tinteger DEFAULT NULL,\n" +
                "\t`likes`\tinteger DEFAULT NULL,\n" +
                "\t`dislikes`\tinteger DEFAULT NULL,\n" +
                "\t`rating`\tinteger DEFAULT NULL,\n" +
                "\t`user_comments`\tvarchar(4000) DEFAULT NULL,\n" +
                "\t`user_location_lat`\tfloat DEFAULT NULL,\n" +
                "\t`user_location_lon`\tfloat DEFAULT NULL,\n" +
                "\t`date`\tdatetime DEFAULT NULL,\n" +
                "\t`user`\tvarchar(255) DEFAULT NULL,\n" +
                "\t`identifier`\tTEXT DEFAULT NULL,\n" +
                "\t`synced`\tvarchar(8) DEFAULT 'false'\n" +
                ");");
        this.getWritableDatabase().execSQL("CREATE TABLE `products` (\n" +
                "\t`_id`\tinteger NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                "\t`title`\tvarchar(255) DEFAULT NULL,\n" +
                "\t`category`\tinteger DEFAULT NULL,\n" +
                "\t`manufacturer`\tvarchar(255) DEFAULT NULL,\n" +
                "\t`price`\tREAL DEFAULT NULL,\n" +
                "\t`product_image_1`\tvarchar(255),\n" +
                "\t`product_image_2`\tvarchar(255),\n" +
                "\t`product_image_3`\tvarchar(255),\n" +
                "\t`product_image_4`\tvarchar(255),\n" +
                "\t`product_image_5`\tvarchar(255),\n" +
                "\t`competitor_1`\tvarchar(255) DEFAULT NULL,\n" +
                "\t`competitor_1_manufacturer`\tvarchar(255) DEFAULT NULL,\n" +
                "\t`competitor_2`\tvarchar(255) DEFAULT NULL,\n" +
                "\t`competitor_2_manufacturer`\tvarchar(255) DEFAULT NULL,\n" +
                "\t`competitor_3`\tvarchar(255) DEFAULT NULL,\n" +
                "\t`competitor_3_manufacturer`\tvarchar(255) DEFAULT NULL,\n" +
                "\t`competitor_4`\tvarchar(255) DEFAULT NULL,\n" +
                "\t`competitor_4_manufacturer`\tvarchar(255) DEFAULT NULL,\n" +
                "\t`competitor_5`\tvarchar(255) DEFAULT NULL,\n" +
                "\t`competitor_5_manufacturer`\tvarchar(255) DEFAULT NULL,\n" +
                "\t`competitor_6`\tvarchar(255) DEFAULT NULL,\n" +
                "\t`competitor_6_manufacturer`\tvarchar(255) DEFAULT NULL,\n" +
                "\t`competitor_7`\tvarchar(255) DEFAULT NULL,\n" +
                "\t`competitor_7_manufacturer`\tvarchar(255) DEFAULT NULL,\n" +
                "\t`competitor_8`\tvarchar(255) DEFAULT NULL,\n" +
                "\t`competitor_8_manufacturer`\tvarchar(255) DEFAULT NULL,\n" +
                "\t`competitor_9`\tvarchar(255) DEFAULT NULL,\n" +
                "\t`competitor_9_manufacturer`\tvarchar(255) DEFAULT NULL,\n" +
                "\t`identifier`\tTEXT DEFAULT NULL,\n" +
                "\t`synced`\tvarchar(8) DEFAULT 'false'\n" +
                ");");;

        this.getWritableDatabase().execSQL("CREATE TABLE `all_products` ( `_id` integer NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                "`title` varchar(255) DEFAULT NULL, `category` integer DEFAULT NULL, " +
                "`competitor_1` varchar(255) DEFAULT NULL, `competitor_2` varchar(255) DEFAULT NULL," +
                "`about` varchar(255) DEFAULT NULL," +
                "`manufacturer` varchar(255) DEFAULT NULL," +
                "`address` varchar(255) DEFAULT NULL," +
                "`ingredients` varchar(255) DEFAULT NULL," +
                "`updated` integer DEFAULT '0', " +
                "`product_id` integer DEFAULT NULL, " +
                "`product_image_1` varchar(255) DEFAULT NULL, " +
                "`product_image_2` varchar(255) DEFAULT NULL, " +
                "`price` REAL DEFAULT NULL, " +
                "`identifier` varchar(255) DEFAULT NULL " +
                ")");

        this.getWritableDatabase().execSQL("INSERT INTO `all_products` VALUES (7,'Turbor king',1,'Williams','Heineken Beer\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(8,'Williams',1,'Turbor king','Heineken Beer\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(14,'harp',1,'satzenbrau','big stout\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(19,'Baby comfort Diapers\\n',1,'Molfix','baby hugg',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(21,'Darling',1,'X-pression','X-brighter attachment\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(22,'X-brighter attachment\\n',1,'Darling','X-pression',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(28,'Soul mate Hair cream\\n',1,'Dallas','Damatol',NULL,NULL,NULL,NULL,0,62,NULL,NULL,NULL,NULL),(41,'Peak milk\\n',1,'Loya','Dano',NULL,NULL,NULL,NULL,0,49,NULL,NULL,NULL,NULL),(42,'Sprite Beverage',1,'7up Beverage Drink','Limca Beverage Drink\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(43,'7up Beverage Drink',1,'Sprite Beverage','Limca Beverage Drink\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(44,'Limca Beverage Drink\\n',1,'7up Beverage Drink','Sprite Beverage',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(54,'Clair-liss',1,'Caro white','Dove body Cream\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(56,'Dove body Cream\\n',1,'Caro white','Clair-liss',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(57,'Lucky',1,'Leosmart','Bic Pens\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(58,'Leosmart',1,'Lucky','Bic Pens\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(59,'Bic Pens\\n',1,'Leosmart','Lucky',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(60,'Eco',1,'Val-u','FASCO bread\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(61,'Val-u',1,'Eco','FASCO bread\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(62,'FASCO bread\\n',1,'Val-u','Eco',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(63,'Maggi',1,'suppy chicken','knorr chicken Maggi\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(64,'suppy chicken',1,'Maggi','knorr chicken Maggi\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(65,'knorr chicken Maggi\\n',1,'suppy chicken','Maggi',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(68,'Honey well spaghetti\\n',1,'Dangote','Golden penny',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(70,'lady b',1,'Custard','Bolero.\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(71,'Bolero.\\n',1,'lady b','Custard',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(72,'Dstv',4,'star times','Gotv Decoders\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(73,'star times',4,'Dstv','Gotv Decoders\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(74,'Gotv Decoders\\n',4,'star times','Dstv',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(80,'Diva sanitary Pads\\n',1,'lady care','Always Ultra',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(81,'Infinity',1,'nasco corn flakes','Good morning flakes\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(82,'nasco corn flakes',1,'Infinity','Good morning flakes\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(83,'Good morning flakes\\n',1,'nasco corn flakes','Infinity',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(84,'Soo white',1,'Jergens','Rapid White Lotions\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(85,'Jergens',1,'Soo white','Rapid White Lotions\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(86,'Rapid White Lotions\\n',1,'Jergens','Soo white',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(87,'Smart',1,'mascolino','Explore perfumes\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(88,'mascolino',1,'Smart','Explore perfumes\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(89,'Explore perfumes\\n',1,'mascolino','Smart',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(91,'X-Treme',1,'X-Pression','Easy Braid Attachments\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(92,'Easy Braid Attachments\\n',1,'X-Treme','X-Pression',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(94,'Mercy',1,'minimie','Divine grace Chin Chin\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(95,'Divine grace Chin Chin\\n',1,'Mercy','minimie',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(98,'Relax Relaxer\\n',1,'Ozone','mega Growth',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(99,'Rich milk',1,'igloo','fibre active biscuits\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(100,'igloo',1,'Rich milk','fibre active biscuits\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(102,'mama Gold Groundnut Oil',1,'Groundnut Oil','Power Oil\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(103,'Groundnut Oil',1,'mama Gold','Power Oil\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(104,'Power Oil\\n',1,'Groundnut Oil','mama Gold',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(105,'Joy Tomamto',1,'Gino','Rosa Tomato paste\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(107,'Rosa Tomato paste\\n',1,'Gino','Joy Tomamto',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(108,'Drum beans',1,'Honey beans','White Beans\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(109,'Honey beans',1,'Drum beans','White Beans\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(110,'White Beans\\n',1,'Honey beans','Drum beans',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(111,'Mama Gold',1,'Semolina','Semovita\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(112,'Semolina',1,'Mama Gold','Semovita\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(113,'Semovita\\n',1,'Semolina','Mama Gold',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(114,'Caro White',1,'perfect white','cocoa butter Body cream\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(116,'cocoa butter Body cream\\n',1,'perfect white','Caro White',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(120,'butterfly',1,'Cinge','Benbomatic sewing Machines\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(121,'Cinge',5,'butterfly','Benbomatic sewing Machines\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(122,'Benbomatic sewing Machines\\n',5,'Cinge','butterfly',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(123,'Water Gum',5,'Efostic Gum','UHU Gum\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(124,'Efostic Gum',5,'Water Gum','UHU Gum\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(125,'UHU Gum\\n',5,'Efostic Gum','Water Gum',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(126,'Nestle',1,'Good morning','Milo Cornflakes\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(127,'Good morning',1,'Nestle','Milo Cornflakes\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(128,'Milo Cornflakes\\n',1,'Good morning','Nestle',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(129,'Honey well',1,'Dangote','St Louis Sugars\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(131,'St Louis Sugars\\n',1,'Dangote','Honey well',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(132,'Rolex',1,'G-Shock','Casio wrist watches\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(133,'G-Shock',1,'Rolex','Casio wrist watches\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(134,'Casio wrist watches\\n',1,'G-Shock','Rolex',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(135,'Hp',10,'Dell','Toshiba Laptops\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(136,'Dell',10,'Hp','Toshiba Laptops\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(137,'Toshiba Laptops\\n',10,'Dell','Hp',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(141,'Flower Polish\\n',1,'Lude','Bee',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(143,'Tasty Water',1,'Eva','Good win Water\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(144,'Good win Water\\n',1,'Tasty Water','Eva',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(148,'Veleta',1,'Eva','St Lauren Wines\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(150,'St Lauren Wines\\n',1,'Eva','Veleta',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(151,'Fine Coat',5,'Prestige','Blue Boat Paints\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(152,'Prestige',5,'Fine Coat','Blue Boat Paints\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(153,'Blue Boat Paints\\n',5,'Prestige','Fine Coat',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(156,'Miss Bimbo Detergents\\n',1,'Good Mama','Canoe',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(159,'Plus Tissue Papers\\n',1,'Rose','Software',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(160,'Nora',1,'Sunrise','Heinz sweet corn\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(161,'Sunrise',1,'Nora','Heinz sweet corn\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(162,'Heinz sweet corn\\n',1,'Sunrise','Nora',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(163,'Power Oil',1,'Mamador','Kings oil\\n',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(165,'Kings oil\\n',1,'Mamador','Power Oil',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(169,'Mobil',1,'Golden','Total Super Oil',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(170,'Golden',1,'Mobil','Total Super Oil',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(171,'Total Super Oil',1,'Golden','Mobil',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(172,'Guaranty Trust Bank',3,'First Bank','Zenith Bank',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(173,'Zenith Bank',3,'Guaranty Trust Bank','First Bank',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(174,'First Bank',3,'Zenith Bank','Guaranty Trust Bank',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(175,'9mobile Call Services',4,'MTN Call Services','Airtel Call Services',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(176,'MTN Call Services',4,'Airtel Call Services','9mobile Call Services',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(177,'Airtel Call Services',4,'Glo Call Services','MTN Call Services',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(178,'Glo Call Services',4,'MTN Call Services','Airtel Call Services',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(179,'MTN Data Services',4,'Glo Data Services','9mobile Data Services',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(180,'Glo Data Services',4,'9mobile Data Services','MTN Data Services',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(181,'Airtel Data Services',4,'Glo Data Services','9mobile Data Services',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(182,'9mobile Data Services',4,'Airtel Data Services','MTN Data Services',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(183,'United Bank For Africa',3,'Access Bank','Skye Bank',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(184,'Access Bank',3,'Skye Bank','United Bank For Africa',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(185,'Fidelity Bank',3,'Sterling Bank','Diamond Bank',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(186,'Sterling Bank',3,'Diamond Bank','Fidelity Bank',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(187,'Diamond Bank',3,'Fidelity Bank','Sterling Bank',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(188,'Ecobank',3,'Heritage Bank','Wema Bank',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(189,'Heritage Bank',3,'Wema Bank','Ecobank',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(190,'Standard Chartered Bankk',3,'Stanbic IBTC Bank','First City Monument Bank',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL),(191,'Stanbic IBTC Bank',3,'First City Monument Bank','Standard Chartered Bank',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL);");

    }

}
