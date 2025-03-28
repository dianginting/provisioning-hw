package com.voxloud.provisioning.builder;

import com.voxloud.provisioning.entity.Device;

public interface ConfigurationBuilder {
    /**
     * Builds the device configuration based on the provided details.
     *
     * @param device The device entity containing user-specific details.
     * @param domain The domain from application.properties.
     * @param port The port from application.properties.
     * @param codecs The codecs from application.properties.
     * @return The generated configuration as a String.
     */
    String buildConfiguration(Device device, String domain, String port, String codecs);

}
