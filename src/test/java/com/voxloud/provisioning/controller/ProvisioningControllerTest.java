package com.voxloud.provisioning.controller;

import com.voxloud.provisioning.service.ProvisioningService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(ProvisioningController.class)
class ProvisioningControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProvisioningService provisioningService;

    @BeforeEach
    void setUp() {
        Mockito.reset(provisioningService);
    }

    @Test
    void provisionDevice_Success_ReturnsConfiguration() throws Exception {
        // Arrange
        String macAddress = "00:11:22:33:44:55";
        String expectedConfiguration = "device configuration string";

        when(provisioningService.provisionDevice(macAddress)).thenReturn(expectedConfiguration);

        // Act & Assert
        mockMvc.perform(get("/api/v1/provisioning/{mac}", macAddress)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedConfiguration));

        // Verify interaction
        verify(provisioningService, times(1)).provisionDevice(macAddress);
    }

    @Test
    void provisionDevice_InvalidMac_ReturnsBadRequest() throws Exception {
        // Arrange
        String invalidMac = "invalid-mac";
        String errorMessage = "Invalid MAC address";

        doThrow(new IllegalArgumentException(errorMessage)).when(provisioningService).provisionDevice(invalidMac);

        // Act & Assert
        mockMvc.perform(get("/api/v1/provisioning/{mac}", invalidMac)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(errorMessage));

        // Verify interaction
        verify(provisioningService, times(1)).provisionDevice(invalidMac);
    }

    @Test
    void provisionDevice_DeviceNotFound_ReturnsNotFound() throws Exception {
        // Arrange
        String macAddress = "00:11:22:33:44:55";
        String errorMessage = "Device not found";

        doThrow(new IllegalStateException(errorMessage)).when(provisioningService).provisionDevice(macAddress);

        // Act & Assert
        mockMvc.perform(get("/api/v1/provisioning/{mac}", macAddress)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(errorMessage));

        // Verify interaction
        verify(provisioningService, times(1)).provisionDevice(macAddress);
    }
}

