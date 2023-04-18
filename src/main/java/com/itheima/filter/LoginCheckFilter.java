package com.itheima.filter;

import com.alibaba.fastjson.JSON;
import com.itheima.common.BaseContext;
import com.itheima.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检查用户是否已经登录
 */

@Slf4j
@WebFilter(filterName="loginCheckFilter",urlPatterns="/*")
public class LoginCheckFilter implements Filter {
    //路径匹配，支持通配符;
    public static final AntPathMatcher PATH_MATCHER=new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        //1、获取本次请求的URI
        String requestURI = request.getRequestURI();
        log.info("拦截到请求:{}",requestURI);
        //2、请求不需要处理的请求路径
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg", //移动端发送短信
                "/user/login"  //移动端登录
        };
        //3、判断本次请求是否需要请求
        boolean check = check(urls, requestURI);
        //4、如果不需要处理，则直接放行
        if(check)
        {
            log.info("拦截到请求:{}",requestURI);
            filterChain.doFilter(request,response);
            return;
        }
        //5-1、判断登录状态，如果已经登录，则直接放行
        Long employeeId =(Long) request.getSession().getAttribute("employeeId");
        if(employeeId!=null)
        {
            log.info("用户已登录,用户ID为{}",request.getSession().getAttribute("employeeId"));
            //设置线程的值
            BaseContext.setCurrentId(employeeId);
            long id=Thread.currentThread().getId();
            log.info("线程id为：{}",id);
            filterChain.doFilter(request,response);
            return;
        }
        //5-2、判断移动登录状态，如果已经登录，则直接放行
        Long userId =(Long) request.getSession().getAttribute("userId");
        if(userId!=null)
        {
            log.info("用户已登录,用户ID为{}",request.getSession().getAttribute("userId"));
            //设置线程的值
            BaseContext.setCurrentId(userId);
            long id=Thread.currentThread().getId();
            log.info("线程id为：{}",id);
            filterChain.doFilter(request,response);
            return;
        }
        log.info("用户未登录");
        //6、如果未登录则返回未登录结果，通过输出流方式向客户端页面响应数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
//        log.info("拦截到请求：{}",request.getRequestURI());

    }
    public boolean check(String[] urls,String requestURI)
    {
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if(match==true)
            {
                return true;
            }
        }
        return false;
    }
}
