package com.store.storeapp.Controllers;

import com.store.storeapp.Models.Product;
import com.store.storeapp.Models.UserAddress;
import com.store.storeapp.Services.AuthService;
import com.store.storeapp.Services.impl.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/address")
public class AddressController {
    @Autowired
    private AuthService authService;
    @Autowired
    private AddressService addressService;

    @GetMapping("/list")
    public String getList(@CookieValue("jwt") String token, Model model){
        Long userId = authService.getUserIdFromToken(token);
        List<UserAddress> listOfAddress= addressService.listByUser(userId);
        model.addAttribute("listOfAddress", listOfAddress);
        return "account/index";
    }

    @PostMapping("/add")
    public String saveAddress(@ModelAttribute UserAddress address, @CookieValue("jwt") String token) throws IOException {
        Long userId = authService.getUserIdFromToken(token);
        address.setUserId(userId);

        if (address.isDefaultShipping()) {
            addressService.clearDefaultShipping(userId);
        }


        if (address.isDefaultBilling()) {
            addressService.clearDefaultBilling(userId);
        }
        addressService.add(address);
        return "redirect:/order/index";
    }

    // This is for account save address operation
    @PostMapping("/save")
    @ResponseBody
    public ResponseEntity<?> saveAddressJson(@RequestBody UserAddress address, @CookieValue("jwt") String token) {
        try {
            Long userId = authService.getUserIdFromToken(token);
            address.setUserId(userId);

            if (address.isDefaultShipping()) {
                addressService.clearDefaultShipping(userId);
            }

            if (address.isDefaultBilling()) {
                addressService.clearDefaultBilling(userId);
            }

            addressService.add(address);

            return ResponseEntity.ok().body(Map.of("success", true, "message", "Address saved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }


    @PostMapping("/update/{id}")
    @ResponseBody
    public ResponseEntity<?> updateAddress(@PathVariable Long id, @RequestBody UserAddress address, @CookieValue("jwt") String token) {
        try {
            Long userId = authService.getUserIdFromToken(token);
            UserAddress existingAddress = addressService.findById(id);

            if (existingAddress == null || !existingAddress.getUserId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("success", false, "message", "Unauthorized"));
            }

            address.setId(id);
            address.setUserId(userId);

            if (address.isDefaultShipping()) {
                addressService.clearDefaultShipping(userId);
            }

            if (address.isDefaultBilling()) {
                addressService.clearDefaultBilling(userId);
            }

            addressService.update(address);

            return ResponseEntity.ok().body(Map.of("success", true, "message", "Address updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PostMapping("/delete/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteAddress(@PathVariable Long id, @CookieValue("jwt") String token) {
        try {
            Long userId = authService.getUserIdFromToken(token);
            UserAddress address = addressService.findById(id);

            if (address == null || !address.getUserId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("success", false, "message", "Unauthorized"));
            }

            addressService.delete(id);

            return ResponseEntity.ok().body(Map.of("success", true, "message", "Address deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
