package org.epos.backoffice.api.controller.interceptor;

import org.epos.backoffice.bean.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static org.epos.backoffice.bean.RoleEnum.VIEWER;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

public class LogUserInInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(LogUserInInterceptor.class);


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

        Map<String, String> allRequestParams = convertQueryParameterFromArrayStringToString(request);
        
        System.out.println(allRequestParams);

        if (!allRequestParams.containsKey("userId")) {
            String message = "{\"message\": \"The user is not correctly logged in\"}";
            response.setContentType("application/json");
            response.getWriter().write(message);
            response.setStatus(400);
            return false;
        }

        User user = new User();
        user.setEduPersonUniqueId(allRequestParams.get("userId"));

        //for the signup endpoint
        if (request.getMethod().equals("POST") && request.getRequestURI().equals(request.getContextPath() + "/user") && !user.isRegistered()) {
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            return true;
        }

        if (!request.getMethod().equals("POST") && !user.isRegistered()) {
        	System.out.println("ALL REQUEST PARAMETERS: "+allRequestParams.toString());
        	 user.setEmail(allRequestParams.get("email"));
        	 user.setFirstName(allRequestParams.get("firstName"));
        	 user.setLastName(allRequestParams.get("lastName"));
        	 user.setRole(VIEWER);
        	 user.signUp();
        }

        user.signIn();

        if (user.getRole() == null) {
            String message = "{\"message\": \"The user doesn't have the role\"}";
            response.setContentType("application/json");
            response.getWriter().write(message);
            response.setStatus(500);
            return false;
        }


        HttpSession session = request.getSession();
        session.setAttribute("user", user);

        return true;
    }

    private Map<String, String> convertQueryParameterFromArrayStringToString(HttpServletRequest request) {
        Map<String, String> allRequestParams = new TreeMap<>();

        Map<String, String[]> parameterMapArrayString = request.getParameterMap();
        for (String valueParamkey : parameterMapArrayString.keySet()) {
            if (parameterMapArrayString.get(valueParamkey).length > 0)
                allRequestParams.put(valueParamkey, parameterMapArrayString.get(valueParamkey)[0]);
        }
        return allRequestParams;
    }

}
