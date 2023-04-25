package org.example.filter;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.example.common.BaseContext;
import org.example.common.R;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginFilter implements Filter {
    //一个路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        /*执行过滤功能*/

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String[] urls = new String[]{
                "/front/**",
                "/backend/**",
                "/employee/login",
                "/employee/logout",
                "/common/**",
                "/user/sendMsg",
                "/user/login"
        };

        //获取请求的url，与目标路径进行匹配
        String requestURI = request.getRequestURI();
        //2、判断本次请求是否需要处理
        boolean check = check(urls, requestURI);

        //3、如果不需要处理，则直接放行
        if(check){
            log.info("本次请求{}不需要处理",requestURI);
            filterChain.doFilter(request,response);
            return;
        }

        //4-1、判断登录状态，如果已登录，则直接放行
        if(request.getSession().getAttribute("employee") != null){
            log.info("用户已登录，用户id为：{}",request.getSession().getAttribute("employee"));

            //线程间共享用户id
            Long id = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrent(id);

            filterChain.doFilter(request,response);
            return;
        }

        //4-2、判断移动端登录状态，如果已登录，则直接放行
        if(request.getSession().getAttribute("user") != null){
            log.info("用户已登录，用户id为：{}",request.getSession().getAttribute("user"));

            //线程间共享用户id
            Long id = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrent(id);

            filterChain.doFilter(request,response);
            return;
        }

        log.info("用户未登录");
        //5、如果未登录则返回未登录结果，通过输出流方式向客户端页面响应数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    public boolean check(String[] urls,String requestURI){
        for (String url: urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match){
                return true;
            }
        }
        return false;
    }
}
