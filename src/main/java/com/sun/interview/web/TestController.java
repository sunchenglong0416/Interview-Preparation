package com.sun.interview.web;

import com.alibaba.fastjson2.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {


    @PostMapping("api/v1/permission/api2")
    public JSONObject test(@RequestBody JSONObject e){
        e.put("hello","i am thrid");
        return  e;
    }
}
