package com.felipegc.booking.controllers;

import com.felipegc.booking.dtos.BlockDto;
import com.felipegc.booking.dtos.PropertyDto;
import com.felipegc.booking.models.BlockModel;
import com.felipegc.booking.models.PropertyModel;
import com.felipegc.booking.models.UserModel;
import com.felipegc.booking.services.PropertyService;
import com.felipegc.booking.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
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

    @Operation(summary = "Save a new property.",
            description = "Save a new property. The response is the property saved containing its detailed information.")
    @ApiResponses(
            value = {
            @ApiResponse(responseCode = "201", description = "The property is created."),
            @ApiResponse(responseCode = "404", description = "The user by the provided token is not found.")
    })
    @PostMapping
    public ResponseEntity<Object> saveProperty(@RequestHeader("token") String token,
                                               @RequestBody PropertyDto propertyDto) {
        Optional<UserModel> userModel = userService.findByToken(UUID.fromString(token));
        if (userModel.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User Not Found.");
        }

        PropertyModel propertyModel = new PropertyModel();
        BeanUtils.copyProperties(propertyDto, propertyModel);
        propertyModel.setOwner(userModel.get());
        return ResponseEntity.status(HttpStatus.CREATED).body(propertyService.save(propertyModel));
    }

    @Operation(summary = "Add a block to a property.",
            description = "Add a block. The response is the block added to the property.")
    @ApiResponses(
            value = {
            @ApiResponse(responseCode = "200", description = "The block is created."),
            @ApiResponse(responseCode = "400", description = "The block is not created."),
            @ApiResponse(responseCode = "404", description = "The property or user by the provided token is not found.")
    })
    @PostMapping("/{propertyId}/blocks")
    public ResponseEntity<Object> addBlock(@RequestHeader("token") String token,
                                           @RequestBody @Valid BlockDto blockDto,
                                           @PathVariable(value = "propertyId") UUID propertyId) {
        Optional<UserModel> userModel = userService.findByToken(UUID.fromString(token));
        if (userModel.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User Not Found.");
        }

        Optional<PropertyModel> propertyModel = propertyService.findById(propertyId);
        if (propertyModel.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Property Not Found.");
        }

        BlockModel blockModel = new BlockModel();
        BeanUtils.copyProperties(blockDto, blockModel);
        blockModel.setProperty(propertyModel.get());

        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(propertyService.saveBlock(blockModel, userModel.get()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(summary = "Update a block.",
            description = "Update a block. The response is the block updated.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The block is updated."),
            @ApiResponse(responseCode = "400", description = "The block is not updated."),
            @ApiResponse(responseCode = "404", description = "The property, block or user by the provided token is not found.")
    })
    @PutMapping("/{propertyId}/blocks/{blockId}")
    public ResponseEntity<Object> updateBlock(@RequestHeader("token") String token,
                                              @RequestBody @Valid BlockDto blockDto,
                                              @PathVariable(value = "propertyId") UUID propertyId,
                                              @PathVariable(value = "blockId") UUID blockId) {
        Optional<UserModel> userModel = userService.findByToken(UUID.fromString(token));
        if (userModel.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User Not Found.");
        }

        Optional<PropertyModel> propertyModel = propertyService.findById(propertyId);
        if (propertyModel.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Property Not Found.");
        }

        Optional<BlockModel> blockModelOptional = propertyModel.get().getBlocks().stream()
                .filter(block -> block.getBlockId().equals(blockId))
                .findFirst();
        if (blockModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Block Not Found.");
        }

        BlockModel blockModel = blockModelOptional.get();
        BeanUtils.copyProperties(blockDto, blockModel);
        blockModel.setProperty(propertyModel.get());

        try {
            return ResponseEntity.status(HttpStatus.OK).body(propertyService.saveBlock(blockModel, userModel.get()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(summary = "Delete a block.",
            description = "Delete a block by its property and id. The response is the status of the operation.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The block is deleted."),
            @ApiResponse(responseCode = "400", description = "The block is not deleted."),
            @ApiResponse(responseCode = "404", description = "The property, block or user by the provided token is not found.")
    })
    @DeleteMapping("/{propertyId}/blocks/{blockId}")
    public ResponseEntity<Object> deleteBlock(@RequestHeader("token") String token,
                                              @PathVariable(value = "propertyId") UUID propertyId,
                                              @PathVariable(value = "blockId") UUID blockId) {
        Optional<UserModel> userModel = userService.findByToken(UUID.fromString(token));
        if (userModel.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User Not Found.");
        }

        Optional<PropertyModel> propertyModel = propertyService.findById(propertyId);
        if (propertyModel.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Property Not Found.");
        }

        Optional<BlockModel> blockModelOptional = propertyModel.get().getBlocks().stream()
                .filter(block -> block.getBlockId().equals(blockId))
                .findFirst();
        if (blockModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Block Not Found.");
        }

        try {
            propertyService.deleteBlock(blockModelOptional.get(), userModel.get());
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(summary = "Get all properties.",
            description = "Get all properties. The response is a list of properties.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All properties."),
    })
    @GetMapping
    public ResponseEntity<Object> getAllProperties() {
        return ResponseEntity.status(HttpStatus.OK).body(propertyService.getAllProperties());
    }
}
