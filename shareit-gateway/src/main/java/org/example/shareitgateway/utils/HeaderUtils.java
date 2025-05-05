package org.example.shareitgateway.utils;

import org.springframework.http.HttpHeaders;

import static org.example.shareitserver.utils.RequestConstants.USER_ID_REQUEST_HEADER;

public class HeaderUtils {
    public static HttpHeaders getUserIdRequestHeader(int userId) {
        HttpHeaders header = new HttpHeaders();
        header.add(USER_ID_REQUEST_HEADER, String.valueOf(userId));
        return header;
    }
}
