package com.store.storeapp.Controllers;

import com.store.storeapp.Services.AuthService;
import com.store.storeapp.Services.impl.FavoriteService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final AuthService authService;

    public FavoriteController(FavoriteService fs, AuthService as){
        this.favoriteService = fs; this.authService = as;
    }

    private Long requireUser(String token){
        if (token == null || token.isBlank()) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        return authService.getUserIdFromToken(token);
    }

    @PostMapping("/{productId}")
    public Map<String,Object> add(@PathVariable Long productId,
                                  @CookieValue(value="jwt", required=false) String token){
        Long userId = requireUser(token);
        favoriteService.add(userId, productId);
        return Map.of("favorited", true, "count", favoriteService.countForProduct(productId));
    }

    @DeleteMapping("/{productId}")
    public Map<String,Object> remove(@PathVariable Long productId,
                                     @CookieValue(value="jwt", required=false) String token){
        Long userId = requireUser(token);
        favoriteService.remove(userId, productId);
        return Map.of("favorited", false, "count", favoriteService.countForProduct(productId));
    }

}

