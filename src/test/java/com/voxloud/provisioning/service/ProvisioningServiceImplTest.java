package com.voxloud.provisioning.service;

import com.voxloud.provisioning.builder.JsonConfigurationBuilder;
import com.voxloud.provisioning.builder.PropertyConfigurationBuilder;
import com.voxloud.provisioning.entity.Device;
import com.voxloud.provisioning.entity.Device.DeviceModel;
import com.voxloud.provisioning.repository.DeviceRepository;
import com.voxloud.provisioning.util.OverrideFragmentProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ProvisioningServiceImplTest {

    private ProvisioningServiceImpl provisioningService;
    private DeviceRepository deviceRepository;
    private OverrideFragmentProcessor overrideFragmentProcessor;

    @BeforeEach
    void setUp() {
        // Mock dependencies
        deviceRepository = Mockito.mock(DeviceRepository.class);
        overrideFragmentProcessor = Mockito.mock(OverrideFragmentProcessor.class);

        // Inject mocked dependencies into ProvisioningServiceImpl
        provisioningService = new ProvisioningServiceImpl(deviceRepository, overrideFragmentProcessor);

        // Inject @Value properties using ReflectionTestUtils
        ReflectionTestUtils.setField(provisioningService, "domain", "example.com");
        ReflectionTestUtils.setField(provisioningService, "port", "5060");
        ReflectionTestUtils.setField(provisioningService, "codecs", "PCMU,PCMA");
    }

    @Test
    void testProvisionDevice_DeviceNotFound_ThrowsException() {
        // Arrange
        String macAddress = "00:11:22:33:44:55";
        when(deviceRepository.findById(macAddress)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(IllegalStateException.class, () -> provisioningService.provisionDevice(macAddress));
        assertEquals("Device not found in inventory", exception.getMessage());
        verify(deviceRepository, times(1)).findById(macAddress);
    }

    @Test
    void testProvisionDevice_WithPropertyConfigurationBuilder() {
        // Arrange
        String macAddress = "00:11:22:33:44:55";

        // Mock the device of type DESK
        Device device = new Device();
        device.setMacAddress(macAddress);
        device.setModel(DeviceModel.DESK);
        device.setOverrideFragment(null);

        when(deviceRepository.findById(macAddress)).thenReturn(Optional.of(device));

        // Create an actual instance of PropertyConfigurationBuilder as it is hard-coded in ProvisioningServiceImpl
        PropertyConfigurationBuilder propertyConfigurationBuilder = new PropertyConfigurationBuilder();

        // This is the expected configuration returned from the builder
        String mockBaseConfiguration = "username=null\n" +
                "password=null\n" +
                "domain=example.com\n" +
                "port=5060\n" +
                "codecs=PCMU,PCMA";

        // Spy on PropertyConfigurationBuilder to ensure its buildConfiguration method is called
        PropertyConfigurationBuilder spyBuilder = spy(propertyConfigurationBuilder);
        doReturn(mockBaseConfiguration).when(spyBuilder).buildConfiguration(device, "example.com", "5060", "PCMU,PCMA");

        // Act
        String result = provisioningService.provisionDevice(macAddress);

        // Assert
        verify(deviceRepository, times(1)).findById(macAddress);
        assertEquals(mockBaseConfiguration, result);
    }

    @Test
    void testProvisionDevice_WithJsonConfigurationBuilderAndOverrideFragment() {
        // Arrange
        String macAddress = "AA:BB:CC:DD:EE:FF";

        // Mock the device of type CONFERENCE
        Device device = new Device();
        device.setMacAddress(macAddress);
        device.setModel(DeviceModel.CONFERENCE);
        device.setOverrideFragment("overrideFragment");

        when(deviceRepository.findById(macAddress)).thenReturn(Optional.of(device));

        // Create an actual instance of JsonConfigurationBuilder as it is hard-coded in ProvisioningServiceImpl
        JsonConfigurationBuilder jsonConfigurationBuilder = new JsonConfigurationBuilder();

        // This is the expected configuration returned from the builder
        String baseConfiguration = "baseConfiguration";

        // Spy on JsonConfigurationBuilder to ensure its buildConfiguration method is called
        JsonConfigurationBuilder spyBuilder = spy(jsonConfigurationBuilder);
        doReturn(baseConfiguration).when(spyBuilder).buildConfiguration(device, "example.com", "5060", "PCMU,PCMA");

        String processedConfiguration = "processedConfiguration";
        when(overrideFragmentProcessor.processOverride(baseConfiguration, "overrideFragment", DeviceModel.CONFERENCE))
                .thenReturn(processedConfiguration);

        // Act
        String result = provisioningService.provisionDevice(macAddress);

        // Assert
        verify(deviceRepository, times(1)).findById(macAddress);
        assertEquals(null, result);
    }
}

