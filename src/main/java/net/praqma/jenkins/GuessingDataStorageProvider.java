/*
 * The MIT License
 *
 * Copyright 2014 Mads.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package net.praqma.jenkins;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import java.net.UnknownHostException;
import org.mongojack.DBCursor;
import org.mongojack.JacksonDBCollection;
import org.mongojack.WriteResult;


/**
 *
 * @author Mads
 */
public class GuessingDataStorageProvider {
    
    private static GuessingDataStorageProvider instance;
    private static final String COLLECTIONNAME = "guesses";
    private static final int PORT = 27017;
    private final MongoClient client;
    
    
    private GuessingDataStorageProvider() throws UnknownHostException {
        client = new MongoClient("localhost", PORT);        
    }
    
    public static GuessingDataStorageProvider getInstance() throws UnknownHostException {
        
        if(instance == null) {
            instance = new GuessingDataStorageProvider();
        }
        
        return instance;
    }
    
    private DB getDb() {
        return client.getDB(COLLECTIONNAME);
    }
    
    public int count() {
        JacksonDBCollection<GuessingBuildAction, String> coll = JacksonDBCollection.wrap(getDb().getCollection(COLLECTIONNAME), GuessingBuildAction.class, String.class);        
        return coll.find().count();
    }
    
    public int countCorrect() {
        int count = 0;
        JacksonDBCollection<GuessingBuildAction, String> coll = JacksonDBCollection.wrap(getDb().getCollection(COLLECTIONNAME), GuessingBuildAction.class, String.class);        
        DBCursor<GuessingBuildAction> actions = coll.find(new BasicDBObject("correct", true));
        count = actions.count();
        actions.close();
        return count;
    }
    
    public int countIncorrect() {
        int count = 0;
        JacksonDBCollection<GuessingBuildAction, String> coll = JacksonDBCollection.wrap(getDb().getCollection(COLLECTIONNAME), GuessingBuildAction.class, String.class);        
        DBCursor<GuessingBuildAction> actions = coll.find(new BasicDBObject("correct", false));
        count = actions.count();
        actions.close();
        return count;
    }
    
    public void store(GuessingBuildAction action) {
        JacksonDBCollection<GuessingBuildAction, String> coll = JacksonDBCollection.wrap(getDb().getCollection(COLLECTIONNAME), GuessingBuildAction.class, String.class);
        WriteResult<GuessingBuildAction, String> result = coll.insert(action);                                
    }
    
}
