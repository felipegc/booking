package com.felipegc.booking.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.felipegc.booking.dtos.BookingDto;
import com.felipegc.booking.models.PropertyModel;
import com.felipegc.booking.models.UserModel;
import com.felipegc.booking.services.BookingService;
import com.felipegc.booking.services.PropertyService;
import com.felipegc.booking.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {BookingController.class})
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PropertyService propertyService;

    @MockBean
    private BookingService bookingService;

    @MockBean
    private UserService userService;

    @Test
    void When_GetBooking_And_BookingNotFound_ThenReturns404() throws Exception {
        when(propertyService.findById(any())).thenReturn(Optional.empty());

        this.mockMvc.perform(get("/bookings/4108e4d7-806c-4fb5-876d-038fae82acce"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Booking not found.")));
    }

    @Test
    void When_SaveBooking_ThenReturns201() throws Exception {
        UUID propertyUUID = UUID.randomUUID();
        PropertyModel propertyModel = new PropertyModel();
        propertyModel.setPropertyId(propertyUUID);

        UUID userIdUUID = UUID.randomUUID();
        UserModel userModel = new UserModel();
        userModel.setUserId(userIdUUID);

        when(propertyService.findById(propertyUUID)).thenReturn(Optional.of(propertyModel));
        when(userService.findById(userIdUUID)).thenReturn(Optional.of(userModel));

        BookingDto bookingDto = new BookingDto();
        bookingDto.setDescription("Booking description");
        bookingDto.setPropertyId(propertyUUID);
        bookingDto.setUserId(userIdUUID);
        ObjectMapper objectMapper = new ObjectMapper();

        this.mockMvc.perform(post("/bookings")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isCreated());
    }
}