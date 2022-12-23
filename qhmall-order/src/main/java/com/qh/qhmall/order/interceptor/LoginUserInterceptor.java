package com.qh.qhmall.order.interceptor;


import com.qh.common.constant.AuthServerConstant;
import com.qh.common.vo.MemberResponseVo;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 登录用户拦截器
 *
 * @author 清欢
 * @date 2022/12/03  15:31:05
 */
@Component
public class LoginUserInterceptor implements HandlerInterceptor {
    public static ThreadLocal<MemberResponseVo> loginUser = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        boolean match = antPathMatcher.match("/order/order/status/**", uri);
        boolean match1 = antPathMatcher.match("/payed/notify", uri);
        boolean match2 = antPathMatcher.match("/swagger-ui.html", uri);
        if (match || match1 || match2) {
            return true;
        }
        MemberResponseVo attribute = (MemberResponseVo) request.getSession().getAttribute(AuthServerConstant.LOGIN_USER);
        if (attribute != null) {
            loginUser.set(attribute);
            return true;
        } else {
            //跳转登录页
            request.getSession().setAttribute("msg", "请先登录");
            response.sendRedirect("http://auth.qhmall.com/login.html");
            return false;
        }
    }


}
