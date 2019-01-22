package com.sapient.io.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class User {

    private Long id;

    private String name;

    private String phone;

    private String opaqueId;
    
    public static User create() {
    	long id =System.currentTimeMillis();
    	return new User(id, "User "+id, id+"", "opaqueId"+id);
    }

}
