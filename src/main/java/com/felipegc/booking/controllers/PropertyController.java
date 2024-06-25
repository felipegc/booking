package com.felipegc.booking.controllers;

import com.felipegc.booking.dtos.PropertyDto;
import com.felipegc.booking.models.PropertyModel;
import com.felipegc.booking.services.PropertyService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/properties")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PropertyController {

    @Autowired
    PropertyService propertyService;

    @PostMapping
    public ResponseEntity<Object> saveProperty(@RequestBody PropertyDto propertyDto) {
        PropertyModel propertyModel = new PropertyModel();
        BeanUtils.copyProperties(propertyDto, propertyModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(propertyService.save(propertyModel));
    }

    @GetMapping
    public ResponseEntity<Object> getAllProperties() {
        return ResponseEntity.status(HttpStatus.OK).body(propertyService.getAllProperties());
    }
}
