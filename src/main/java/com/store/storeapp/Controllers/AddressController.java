package com.store.storeapp.Controllers;

import com.store.storeapp.Models.Product;
import com.store.storeapp.Models.UserAddress;
import com.store.storeapp.Services.AuthService;
import com.store.storeapp.Services.impl.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/address")
public class AddressController {
    @Autowired
    private AuthService authService;
    @Autowired
    private AddressService addressService;

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
}
