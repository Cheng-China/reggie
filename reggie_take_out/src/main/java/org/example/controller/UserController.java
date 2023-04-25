package org.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.example.common.R;
import org.example.entity.User;
import org.example.service.UserService;
import org.example.utils.ValidateCodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 发送验证码
     * @param user
     * @param httpSession
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession httpSession){
        String phone = user.getPhone();
        if (StringUtils.isNotEmpty(phone)){
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info(code);
            //httpSession.setAttribute(phone,code);
            redisTemplate.opsForValue().set(phone,code,5, TimeUnit.MINUTES);
            return R.success("手机验证码发送成工");
        }

        return R.error("发送失败");
    }

    @PostMapping("/login")
    public R<User> login(@RequestBody Map map,HttpSession httpSession){
        log.info("map:{}",map);
        //获取手机号和验证码
        String phone = map.get("phone").toString();
        String code = map.get("code").toString();

        //Object codeInSession = httpSession.getAttribute(phone);
        Object codeInSession = redisTemplate.opsForValue().get(phone);
        //进行验证码校验
        if (codeInSession != null && codeInSession.equals(code)){
            //进入条件内说明验证码正确，判断用户是否为新用户
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);
            User user = userService.getOne(queryWrapper);
            if (user == null){
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            //有点搞不懂这个 user.id 哪里来的
            httpSession.setAttribute("user",user.getId());
            log.info("user.id:{}",user.getId());
            redisTemplate.delete(phone);
            return R.success(user);
        }
        return R.error("登录失败");
    }

}
