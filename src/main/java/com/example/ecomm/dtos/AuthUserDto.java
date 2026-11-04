package com.example.ecomm.dtos;

import lombok.Data;

@Data
public class AuthUserDto {
    private String email;
    private String role;
}

/*

POST /orders -> should it charge the user 2 times?


20 delete requests
- 6 got through
- 14 failed - retry them -> ~4 requests will succeed
- 10 failed - retry them -> ~3 requests will succeed

Let's add retry functionality will exponential backoff and jitter
 */
