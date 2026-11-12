package com.community.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static LoginUser currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof LoginUser loginUser) {
            return loginUser;
        }
        return null;
    }

    public static String currentOperator() {
        LoginUser user = currentUser();
        if (user == null) {
            return "system";
        }
        return StringUtilsSafe(user.getRealName(), user.getUsername());
    }

    private static String StringUtilsSafe(String realName, String username) {
        return realName != null && !realName.isBlank() ? realName : username;
    }
}
