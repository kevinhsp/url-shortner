package theBigShortners.urlService;

import com.google.cloud.bigtable.data.v2.models.Row;
import com.google.cloud.bigtable.data.v2.models.Filters;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import util.JwtToken;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static com.google.cloud.bigtable.data.v2.models.Filters.FILTERS;

@Component
public class MyInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = getTokenFromCookie(request);
        if (StringUtils.isBlank(token)) {
            token = request.getHeader("token");
        }
        if (StringUtils.isBlank(token)) {
            token = request.getParameter("token");
        }
        // check token
        Object email = (Object) JwtToken.parseToken(token).get("email");
        Object id = (Object) JwtToken.parseToken(token).get("id");
        Object name = (Object) JwtToken.parseToken(token).get("name");
        if(Objects.equals(id, AccountService.findByEmail(String.valueOf(email)).getId()) && Objects.equals(name, AccountService.findByEmail(String.valueOf(email)).getName()))
            return true;
        return false;
    }

    private String getTokenFromCookie(HttpServletRequest request) {
        String token = null;
        Cookie[] cookies = request.getCookies();
        int len = null == cookies ? 0 : cookies.length;
        if (len > 0) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("token")) {
                    token = cookie.getValue();
                    break;
                }
            }
        }
        return token;
    }
}
