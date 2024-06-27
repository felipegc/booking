package com.felipegc.booking.controllers;

import com.felipegc.booking.dtos.BlockDto;
import com.felipegc.booking.dtos.PropertyDto;
import com.felipegc.booking.models.BlockModel;
import com.felipegc.booking.models.PropertyModel;
import com.felipegc.booking.models.UserModel;
import com.felipegc.booking.services.PropertyService;
import com.felipegc.booking.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/properties")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PropertyController {

    @Autowired
    PropertyService propertyService;

    @Autowired
    UserService userService;

    @PostMapping
    public ResponseEntity<Object> saveProperty(@RequestHeader("Authorization") String token,
                                               @RequestBody PropertyDto propertyDto) {
        Optional<UserModel> userModel = userService.findByToken(UUID.fromString(token));
        if(userModel.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User Not Found.");
        }

        PropertyModel propertyModel = new PropertyModel();
        BeanUtils.copyProperties(propertyDto, propertyModel);
        propertyModel.setOwner(userModel.get());
        return ResponseEntity.status(HttpStatus.CREATED).body(propertyService.save(propertyModel));
    }

    @PutMapping("/{propertyId}/blocks")
    public ResponseEntity<Object> addBlock(@RequestHeader("Authorization") String token,
                                           @RequestBody @Valid BlockDto blockDto,
                                           @PathVariable(value="propertyId") UUID propertyId) {
        Optional<UserModel> userModel = userService.findByToken(UUID.fromString(token));
        if(userModel.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User Not Found.");
        }

        Optional<PropertyModel> propertyModel = propertyService.findById(propertyId);
        if(propertyModel.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Property Not Found.");
        }

        BlockModel blockModel = new BlockModel();
        BeanUtils.copyProperties(blockDto, blockModel);
        blockModel.setProperty(propertyModel.get());

        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(propertyService.addBlock(blockModel, userModel.get()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<Object> getAllProperties() {
        return ResponseEntity.status(HttpStatus.OK).body(propertyService.getAllProperties());
    }
}
