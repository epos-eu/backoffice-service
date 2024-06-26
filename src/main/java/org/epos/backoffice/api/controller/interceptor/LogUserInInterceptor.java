package org.epos.backoffice.api.controller.interceptor;

import org.epos.eposdatamodel.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import usermanagementapis.UserGroupManagementAPI;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

public class LogUserInInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(LogUserInInterceptor.class);


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

        System.out.println(request.toString());
        Map<String, String> allRequestParams = convertQueryParameterFromArrayStringToString(request);
        
        System.out.println(allRequestParams);

        /** TODO: DELETE **/
        /*if (!allRequestParams.containsKey("userId")) {
            allRequestParams.put("userId", "admin");
        }*/

        if (!allRequestParams.containsKey("userId")) {
            String message = "{\"message\": \"The user is not correctly logged in\"}";
            response.setContentType("application/json");
            response.getWriter().write(message);
            response.setStatus(400);
            return false;
        }

        User user = UserGroupManagementAPI.retrieveUserById(allRequestParams.get("userId"));
        if(user == null){

            String message = "{\"message\": \"The user doesn't exists\"}";
            response.setContentType("application/json");
            response.getWriter().write(message);
            response.setStatus(400);
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
