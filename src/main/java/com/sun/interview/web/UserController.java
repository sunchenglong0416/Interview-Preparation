package com.sun.interview.web;

import com.sun.interview.common.DistributedLock;
import com.sun.interview.models.Doctor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.Set;

@RestController
public class UserController {

    @Autowired
    DistributedLock distributedLock;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    /**
     * 分布式锁获取
     */
    @PostConstruct
    public void init(){
        String userId ="123";
        String lockKey = "lock_product";
        boolean lockSuccess = distributedLock.tryLock(lockKey, userId, 20000);
        if (lockSuccess){
            try {
                System.out.println("加锁成功-执行业务");
            } catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                distributedLock.releaseDistributedLock(lockKey, userId);
            }
        }
    }

    @PostConstruct
    public void init2(){
        String rankKey ="doctor_rank_key";
        Doctor doctor1 = new Doctor("1003", 23, 8);
        Doctor doctor2 = new Doctor("1002", 30, 7);
        Doctor doctor3 = new Doctor("1001", 30, 7);
        Doctor doctor4 = new Doctor("1004", 30, 8);
        Doctor doctor5 = new Doctor("1005", 30, 8);
        Doctor doctor6 = new Doctor("1006", 30, 9);
        stringRedisTemplate.opsForZSet().add(rankKey, doctor1.getDoctorId(), Double.parseDouble(doctor1.getScore()+"."+doctor1.getNums()));
        stringRedisTemplate.opsForZSet().add(rankKey, doctor2.getDoctorId(), Double.parseDouble(doctor2.getScore()+"."+doctor2.getNums()));
        stringRedisTemplate.opsForZSet().add(rankKey, doctor3.getDoctorId(), Double.parseDouble(doctor3.getScore()+"."+doctor3.getNums()));
        stringRedisTemplate.opsForZSet().add(rankKey, doctor4.getDoctorId(), Double.parseDouble(doctor4.getScore()+"."+doctor4.getNums()));
        stringRedisTemplate.opsForZSet().add(rankKey, doctor5.getDoctorId(), Double.parseDouble(doctor5.getScore()+"."+doctor5.getNums()));
        stringRedisTemplate.opsForZSet().add(rankKey, doctor6.getDoctorId(), Double.parseDouble(doctor6.getScore()+"."+doctor6.getNums()));
        Set<String> range = stringRedisTemplate.opsForZSet().reverseRange(rankKey, 0, 4);
        for (String s : range) {
            System.out.println(s);
        }

    }


}
