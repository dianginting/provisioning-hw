package com.voxloud.provisioning.builder;

import com.voxloud.provisioning.entity.Device;

public class PropertyConfigurationBuilder implements ConfigurationBuilder {

    @Override
    public String buildConfiguration(Device device, String domain, String port, String codecs) {
        // Ensure Desk model is processed.
        if (device.getModel() != Device.DeviceModel.DESK) {
            throw new IllegalArgumentException("Invalid device model for PropertyConfigurationBuilder");
        }

        // Assemble the configuration string.
        return String.format(
                "username=%s%npassword=%s%ndomain=%s%nport=%s%ncodecs=%s",
                device.getUsername(),
                device.getPassword(),
                domain,
                port,
                codecs
        );
    }
}

