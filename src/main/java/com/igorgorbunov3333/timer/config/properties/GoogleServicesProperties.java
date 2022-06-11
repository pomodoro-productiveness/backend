package com.igorgorbunov3333.timer.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "google-services")
public class GoogleServicesProperties {

    @NotBlank
    private String documentId;
    @NotBlank
    private String credentialsPath;
    @NotBlank
    private String fileName;
    @NotBlank
    private String calendarId;

}
