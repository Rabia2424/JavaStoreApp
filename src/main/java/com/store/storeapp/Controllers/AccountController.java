package com.store.storeapp.Controllers;

import com.store.storeapp.DTOs.AccountStatsDto;
import com.store.storeapp.DTOs.OrderResponseDto;
import com.store.storeapp.DTOs.ProfileDto;
import com.store.storeapp.Models.User;
import com.store.storeapp.Services.AuthService;
import com.store.storeapp.Services.impl.*;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/account")
public class AccountController {
    @Autowired
    private AuthService authService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private AddressService addressService;
    @Autowired
    private UserService userService;
    @Autowired
    private FavoriteService favoriteService;
    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping
    public String accountPage(@CookieValue("jwt") String token, Model model) {

        Long userId = authService.getUserIdFromToken(token);

        User user = userService.getUserById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if(user.getAvatarUrl() != null){
            model.addAttribute("currentAvatarUrl", user.getAvatarUrl());
        }else{
            model.addAttribute("currentUserFirstName", user.getName());
        }
        model.addAttribute("currentUserFullName",
                user.getName() + " " + user.getSurname());

        long addressCount = addressService.countByUserId(userId);
        long favoriteCount = favoriteService.countByUserId(userId);

        AccountStatsDto stats =
                orderService.getAccountStats(userId, addressCount, favoriteCount);
        model.addAttribute("stats", stats);

        model.addAttribute("recentOrders", orderService.getRecentOrders(userId, 3));
        model.addAttribute("favoritePreview", favoriteService.getPreview(userId, 3));

        return "account/index";
    }


    @GetMapping("/section/overview")
    public String overviewFragment(@CookieValue("jwt") String token, Model model) {
        Long userId = authService.getUserIdFromToken(token);

        User user = userService.getUserById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        long addressCount = addressService.countByUserId(userId);
        long favoriteCount = favoriteService.countByUserId(userId);
        AccountStatsDto stats =
                orderService.getAccountStats(userId, addressCount, favoriteCount);
        model.addAttribute("stats", stats);

        model.addAttribute("recentOrders", orderService.getRecentOrders(userId, 3));
        model.addAttribute("favoritePreview", favoriteService.getPreview(userId, 3));

        return "account/overview :: content";
    }

    @GetMapping("/section/orders")
    public String ordersFragment(@CookieValue("jwt") String token, Model model) {
        Long userId = authService.getUserIdFromToken(token);
        List<OrderResponseDto> orders = orderService.getOrdersByUserId(userId);
        model.addAttribute("orders", orders);
        model.addAttribute("hasOrders", !orders.isEmpty());
        return "account/orders :: content";
    }

    @GetMapping("/section/addresses")
    public String addressesFragment(@CookieValue("jwt") String token, Model model) {
        Long userId = authService.getUserIdFromToken(token);
        model.addAttribute("addresses", addressService.listByUser(userId));
        return "account/addresses :: content";
    }

    @GetMapping("/section/favorites")
    public String favoritesFragment(@CookieValue("jwt") String token, Model model) {
        Long userId = authService.getUserIdFromToken(token);
        model.addAttribute("favorites", favoriteService.getAllByUser(userId));
        return "account/favorites :: content";
    }

    @GetMapping("/section/profile")
    public String profileFragment(@CookieValue("jwt") String token, Model model) {
        Long userId = authService.getUserIdFromToken(token);

        User user = userService.getUserById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        ProfileDto dto = new ProfileDto(
                user.getName(),
                user.getSurname(),
                user.getAvatarUrl(),
                user.getUsername(),
                user.getEmail()
        );

        model.addAttribute("profile", dto);
        return "account/profile :: content";
    }

    @PostMapping("/profile")
    public String updateProfile(@CookieValue("jwt") String token,
                                @ModelAttribute ProfileDto profile,
                                @RequestParam(value = "avatar", required = false) MultipartFile avatar,
                                HttpServletResponse response)
            throws IOException {

        Long userId = authService.getUserIdFromToken(token);
        User user = userService.getUserById(userId).orElse(null);

        user.setName(profile.getFirstName());
        user.setSurname(profile.getLastName());
        user.setUsername(profile.getUsername());
        user.setEmail(profile.getEmail());

        if (avatar != null && !avatar.isEmpty()) {
            String url = fileStorageService.store(avatar, "avatars");
            user.setAvatarUrl(url);
        }

        userService.updateUser(user);

        authService.clearAuthCookie(response);

        return "redirect:/auth/login?profileUpdated";
    }

}
