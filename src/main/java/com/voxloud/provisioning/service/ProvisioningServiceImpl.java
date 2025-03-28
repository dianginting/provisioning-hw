package com.voxloud.provisioning.service;

import com.voxloud.provisioning.builder.ConfigurationBuilder;
import com.voxloud.provisioning.builder.JsonConfigurationBuilder;
import com.voxloud.provisioning.builder.PropertyConfigurationBuilder;
import com.voxloud.provisioning.entity.Device;
import com.voxloud.provisioning.repository.DeviceRepository;
import com.voxloud.provisioning.util.OverrideFragmentProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProvisioningServiceImpl implements ProvisioningService {

    private final DeviceRepository deviceRepository;
    private final OverrideFragmentProcessor overrideFragmentProcessor;

    @Value("${provisioning.domain}")
    private String domain;

    @Value("${provisioning.port}")
    private String port;

    @Value("${provisioning.codecs}")
    private String codecs;

    public ProvisioningServiceImpl(DeviceRepository deviceRepository, OverrideFragmentProcessor overrideFragmentProcessor) {
        this.deviceRepository = deviceRepository;
        this.overrideFragmentProcessor = overrideFragmentProcessor;
    }

    @Override
    public String provisionDevice(String macAddress) {
        // Ensure the device exists in the repository
        Optional<Device> optionalDevice = deviceRepository.findById(macAddress);

        if (optionalDevice.isEmpty()) {
            throw new IllegalStateException("Device not found in inventory");
        }

        // Retrieve the device
        Device device = optionalDevice.get();

        // Select and use the appropriate ConfigurationBuilder
        ConfigurationBuilder builder = getBuilder(device.getModel());
        String baseConfiguration = builder.buildConfiguration(device, domain, port, codecs);

        // Check for override fragment and process it
        if (device.getOverrideFragment() != null) {
            baseConfiguration = overrideFragmentProcessor.processOverride(baseConfiguration, device.getOverrideFragment(), device.getModel());
        }

        return baseConfiguration;
    }

    private ConfigurationBuilder getBuilder(Device.DeviceModel model) {
        // Return the appropriate ConfigurationBuilder based on the device model
        return switch (model) {
            case DESK -> new PropertyConfigurationBuilder();
            case CONFERENCE -> new JsonConfigurationBuilder();
        };
    }
}

