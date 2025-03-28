package com.voxloud.provisioning.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.voxloud.provisioning.entity.Device;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Properties;

@Component
public class OverrideFragmentProcessor {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Processes and applies override fragments to the base configuration for a given device model.
     *
     * @param baseConfig      The original configuration as a string.
     * @param overrideFragment The override fragment as a string.
     * @param model           The model of the device (DESK or CONFERENCE).
     * @return The configuration string after applying the override.
     */
    public String processOverride(String baseConfig, String overrideFragment, Device.DeviceModel model) {
        try {
            // Handle configuration based on device model
            if (model == Device.DeviceModel.DESK) {
                return handleDeskDeviceOverride(baseConfig, overrideFragment);
            } else if (model == Device.DeviceModel.CONFERENCE) {
                return handleConferenceDeviceOverride(baseConfig, overrideFragment);
            } else {
                throw new IllegalArgumentException("Unsupported device model");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error applying override fragment", e);
        }
    }

    /**
     * Handles overrides for DESK devices (properties format).
     *
     * @param baseConfig      The base configuration.
     * @param overrideFragment The override fragment.
     * @return The final configuration after applying overrides.
     * @throws Exception If an error occurs during processing.
     */
    private String handleDeskDeviceOverride(String baseConfig, String overrideFragment) throws Exception {
        Properties baseProps = new Properties();
        baseProps.load(new java.io.StringReader(baseConfig)); // Load base configuration as properties

        Properties overrideProps = new Properties();
        overrideProps.load(new java.io.StringReader(overrideFragment)); // Load override fragment as properties

        // Apply overrides
        overrideProps.forEach(baseProps::put);

        // Reconstruct the configuration string
        StringBuilder result = new StringBuilder();
        baseProps.forEach((key, value) -> result.append(key).append("=").append(value).append("\n"));

        return result.toString();
    }

    /**
     * Handles overrides for CONFERENCE devices (JSON format).
     *
     * @param baseConfig      The base configuration.
     * @param overrideFragment The override fragment.
     * @return The final configuration after applying overrides.
     * @throws Exception If an error occurs during processing.
     */
    private String handleConferenceDeviceOverride(String baseConfig, String overrideFragment) throws Exception {
        Map<String, Object> baseJson = objectMapper.readValue(baseConfig, Map.class); // Parse base config as JSON
        Map<String, Object> overrideJson = objectMapper.readValue(overrideFragment, Map.class); // Parse override fragment as JSON

        // Apply overrides
        baseJson.putAll(overrideJson);

        // Serialize back to JSON string
        return objectMapper.writeValueAsString(baseJson);
    }
}


