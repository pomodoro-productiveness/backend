package com.igorgorbunov3333.timer.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "google-drive")
public class GoogleDriveProperties {

    @NotBlank
    private String documentId;
    @NotBlank
    private String credentialsPath;
    @NotBlank
    private String fileName;

}
