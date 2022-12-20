package com.igorgorbunov3333.timer.console.service.google.util;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GoogleServiceUtil {

    public static final String APPLICATION_NAME = "timer application";
    public static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

}
