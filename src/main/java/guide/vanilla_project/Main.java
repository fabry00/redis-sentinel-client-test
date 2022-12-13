package guide.vanilla_project;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.exceptions.JedisException;

import java.nio.charset.Charset;
import java.util.*;

public class Main {
    private static final String MASTER_NAME = "mymaster";
    private static final Set sentinels;

    static {
        sentinels = new HashSet();
//        sentinels.add("192.168.122.105:26379");
//        sentinels.add("192.168.122.129:26379");
//        sentinels.add("192.168.122.144:26379");
        sentinels.add("127.0.0.1:26379");
    }

    public static void main(String[] args) throws InterruptedException {
        runTest();
    }

    public static void runTest() throws InterruptedException {
        boolean writeNext = true;
        JedisSentinelPool pool = new JedisSentinelPool(MASTER_NAME, sentinels);
        Jedis jedis = null;
        int i = 0;
        while (true) {
            try {
                printer("Fetching connection from pool");
                jedis = pool.getResource();
                printer("Authenticating...");
                //jedis.auth(PASSWORD);
                printer("auth complete...");
                printer("Connected");
                while (true) {
                    printer("Connected to sentinel: "+pool.getCurrentHostMaster());
                    String key = "java-key-" + (i % 10);
                    printer("Writing... " +key);
                    jedis.set(key, "_"+i);
                    key = jedis.randomKey();
                    printer("Reading... " + key);
                    String val = jedis.get(key);
                    printer("Value... " + val);
                    i++;
                    Thread.sleep(500);
                }
            } catch (JedisException e) {
                printer("Connection error of some sort!");
                printer(e.getMessage());
                Thread.sleep(2 * 1000);
            } finally {
                if (jedis != null) {
                    jedis.close();
                }
            }
        }
    }

    private static void printer(String msg) {
        System.out.println(msg);
    }
}
