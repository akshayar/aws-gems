package com.sapient.io.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class Transaction {
    private Long id;

    private String requestId;

    private String opaqueId;
    
    public static Transaction create() {
    	long id =System.currentTimeMillis();
    	return new Transaction(id, "RequestId "+id,  "opaqueId"+id);
    }

}
