package org.sdet.testing.app;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@JsonIgnoreProperties(ignoreUnknown = true)
record LoginResponse(String token,User user)
{
    @JsonIgnoreProperties(ignoreUnknown = true)
    record User(int id, String email, String role, String name){}
}
