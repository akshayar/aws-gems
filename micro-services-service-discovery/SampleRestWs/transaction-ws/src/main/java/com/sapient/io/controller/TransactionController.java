package com.sapient.io.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sapient.io.bean.Transaction;

@RestController
public class TransactionController {


    @GetMapping(value = "/")
    public List<Transaction> getTransactions(){
        return Arrays.asList(Transaction.create());
    }
    @GetMapping(value = "/ping")
    public String ping() {
    	return "Transaction OK";
    }
}
