package com.dubboss.seckillconsumer.conf;

import com.dubboss.seckillapi.entity.SkUser;
import com.dubboss.seckillapi.service.UserService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @ClassName UserArgumentResolver
 * @Description TODO
 * @Author yjy
 * @Date 2020/5/15 22:46
 * @Vertion 1.0
 **/
@Service
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    @Reference
    private UserService userService;

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        Class<?> clazz = methodParameter.getParameterType();
        return clazz == SkUser.class;
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        HttpServletResponse httpServletResponse = nativeWebRequest.getNativeResponse(HttpServletResponse.class);
        HttpServletRequest httpServletRequest = nativeWebRequest.getNativeRequest(HttpServletRequest.class);
        String paramToken = httpServletRequest.getParameter(UserService.COOKIE_NAME_TOKEN);
        String cookieToken = getCookieValue(httpServletRequest, UserService.COOKIE_NAME_TOKEN);
        if (StringUtils.isEmpty(paramToken) && StringUtils.isEmpty(cookieToken)) {
            return null;
        }
        // 优先取 paramcookie
        String token = StringUtils.isEmpty(paramToken) ? cookieToken : paramToken;
        SkUser skUser = userService.getByToken(httpServletResponse, token);
        return skUser;
    }

    private String getCookieValue(HttpServletRequest httpServletRequest, String cookieNameToken) {
        Cookie[] cookies = httpServletRequest.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(cookieNameToken)) {
                return cookie.getValue();
            }
        }
        return null;
    }

}
