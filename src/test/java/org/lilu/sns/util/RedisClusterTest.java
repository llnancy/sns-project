package org.lilu.sns.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.JedisCluster;

/**
 * @Auther: lilu
 * @Date: 2019/2/20
 * @Description:
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisClusterTest {
    @Autowired
    private JedisCluster jedisCluster;

    @Test
    public void test() {
        System.out.println(jedisCluster);
    }
}
