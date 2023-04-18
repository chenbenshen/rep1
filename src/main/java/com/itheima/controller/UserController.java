package com.itheima.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.common.R;
import com.itheima.entity.User;
import com.itheima.service.UserService;
import com.itheima.utils.SMSUtils;
import com.itheima.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

import static net.sf.jsqlparser.util.validation.metadata.NamedObject.user;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 发送手机短信验证码
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session)
    {
        //获取手机号
        String phone = user.getPhone();
        if(StringUtils.isNotEmpty(phone)) {
            //生成随机的4位验证码
            String  code = ValidateCodeUtils.generateValidateCode(4).toString();

            //调用阿里云提供的短信服务API完成发送短信
//            SMSUtils.sendMessage("FlyPig","SMS_275230461",phone,code);
            log.error("手机号码："+phone);
            log.error("验证码："+code);
            //需要将生成的验证码保存到Session
            session.setAttribute(phone,code);
            return R.success("手机验证短信发送成功");
        }
        return R.error("短信发送失败");
    }

    /**
     * 移动端用户登录
     */
    @PostMapping("login")
    //由于User实体类中没有cope，所以一种是用Map,另一种用Dto接受；
    public R<User> login(@RequestBody Map map,HttpSession session)
    {
        //获取手机号
        String phone = map.get("phone").toString();
        //获取验证码
        String code = map.get("code").toString();
        //从Session中获得保存的验证码
        Object attribute = session.getAttribute(phone);
        //进行验证码的比对
        if(attribute!=null&&attribute.equals(code))
        {
            //如果能够比对成功，说明登录成功
            LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
            userLambdaQueryWrapper.eq(User::getPhone,phone);
            User user = userService.getOne(userLambdaQueryWrapper);
            if(user==null)
            {
                //判断当前手机号对应的用户是否为新用户，如果是就自动完成注册
                user=new User();
                user.setPhone(phone);
                userService.save(user);
            }
            session.setAttribute("userId",user.getId());

            return R.success(user);
        }
        return R.error("登录失败");
    }
}
