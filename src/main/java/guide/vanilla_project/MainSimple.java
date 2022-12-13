package guide.vanilla_project;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.exceptions.JedisException;

import java.time.Duration;

public class MainSimple {
//    private static final int MASTER_PORT = 80;
//    private static final int MASTER_PORT = 6379;
    private static final int MASTER_PORT = 443;
    //private static String MASTER_NAME = "localhost";
//    private static String MASTER_NAME = "192.168.130.101";
   private static String MASTER_NAME = "redis-ms.apps-crc.testing";

    private static JedisPoolConfig buildPoolConfig() {
        final JedisPoolConfig poolConfig = new JedisPoolConfig();
//        poolConfig.setMaxTotal(128);
//        poolConfig.setMaxIdle(128);
//        poolConfig.setMinIdle(16);
//        poolConfig.setTestOnBorrow(true);
//        poolConfig.setTestOnReturn(true);
//        poolConfig.setTestWhileIdle(true);
//        poolConfig.setMinEvictableIdleTimeMillis(Duration.ofSeconds(60).toMillis());
//        poolConfig.setTimeBetweenEvictionRunsMillis(Duration.ofSeconds(30).toMillis());
//        poolConfig.setNumTestsPerEvictionRun(3);
//        poolConfig.setBlockWhenExhausted(true);
        return poolConfig;
    }

    public static void runTestStandalone() throws InterruptedException {
        //boolean writeNext = true;
        //final JedisPoolConfig poolConfig = buildPoolConfig();
        JedisPool jedisPool = new JedisPool(MASTER_NAME, MASTER_PORT);
        try (Jedis jedis = jedisPool.getResource()) {
            int i = 0;
            while (true) {
                try {
                    printer("Fetching connection from pool");
                    printer("Connected");
                    while (true) {
                        String key = "java-key-" + (i % 10);
                        printer("Writing... " + key);
                        jedis.set(key, "_" + i);
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
    }

    private static void printer(String msg) {
        System.out.println(msg);
    }
}
