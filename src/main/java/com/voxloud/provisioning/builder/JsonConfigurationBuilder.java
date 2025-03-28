package com.voxloud.provisioning.builder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.voxloud.provisioning.entity.Device;

import java.util.HashMap;
import java.util.Map;

public class JsonConfigurationBuilder implements ConfigurationBuilder {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String buildConfiguration(Device device, String domain, String port, String codecs) {
        Map<String, Object> config = new HashMap<>();
        config.put("username", device.getUsername());
        config.put("password", device.getPassword());
        config.put("domain", domain);
        config.put("port", port);
        config.put("codecs", codecs.split(","));

        try {
            return objectMapper.writeValueAsString(config);
        } catch (Exception e) {
            throw new RuntimeException("Error generating JSON configuration", e);
        }
    }
}

