/*
Copyright 2004 		Philip Jacob <phil@whirlycott.com>
				  	Seth Fitzsimmons <seth@note.amherst.edu>

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

/*
 * Created on Jun 6, 2004 by pjacob
 *
 */
package com.whirlycott.cache.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.opensymphony.oscache.base.Cache;
import com.opensymphony.oscache.general.GeneralCacheAdministrator;

/**
 * @author pjacob
 *
 */
public class BenchmarkOscache implements Runnable {
    private static Log log = LogFactory.getLog(BenchmarkOscache.class);
    
    private static Cache c = new GeneralCacheAdministrator().getCache();
    
    public static void main(String[] args) throws Exception {
        log.debug("Single threaded tests...");
        BenchmarkOscache oscacheTest = new BenchmarkOscache();
        oscacheTest.doPutTest();
        oscacheTest.doGetTest();
        
        Thread[] t = new Thread[Constants.THREAD_COUNT];
        long beginGets = System.currentTimeMillis();
        for (int i=0; i<Constants.THREAD_COUNT;i++) {
            log.debug("Starting thread " + i);
            t[i] = new Thread(new BenchmarkOscache());
            t[i].start();
        }
        
        for (int i=0;i<Constants.THREAD_COUNT;i++)
            t[i].join();
        
        long endGets = System.currentTimeMillis();
        
        log.debug("Total mx-thread time w/ concurrency level  " + Constants.THREAD_COUNT + " was " + (endGets - beginGets));
        
        new GeneralCacheAdministrator().destroy();
    }
    
    /**
     * 
     */
    private void doGetTest() throws Exception {
        //BEGIN GETS
        long startGet = System.currentTimeMillis();
        for (int loop=0; loop <Constants.RETRIEVE_COUNT;loop++) {
            //log.debug("Looping: " + loop );
            for (int i = 0; i < Constants.STORE_COUNT; i++) {
                Object o = c.getFromCache(new Integer(i).toString());
                String s = (String)o;
/*                if (!s.equals((String)o)) {
                    log.error(s + " doesn't equal");
                    throw new Exception("Mismatch");
                }*/
            }
        }
        long endGet = System.currentTimeMillis();       
        log.debug("Total GET time was: " + (endGet - startGet));
        //END GETS
    }

    private void doPutTest() throws Exception {
       
        //BEGIN PUTS
        long start = System.currentTimeMillis();
        for (int i = 0; i < Constants.STORE_COUNT; i++) {
            c.putInCache(new Integer(i).toString(), "value" + i);
        }
        long end = System.currentTimeMillis();       
        log.debug("Total PUT time was: " + (end - start));
        //END PUTS
        
        //--------------------------------------------
        
        log.debug("Cache size is: " + null);
    }

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run() {
        try {
            doGetTest();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
}
