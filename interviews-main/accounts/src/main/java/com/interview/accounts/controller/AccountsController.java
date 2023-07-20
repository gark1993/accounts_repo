package com.interview.accounts.controller;

import com.interview.accounts.model.GetAccountsResponseBody;
import com.interview.accounts.service.AccountService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.interview.accounts.domain.*;

@RestController
@RequestMapping("/accounts")
public class AccountsController {

    @Autowired
    private AccountService accountService;
    

    @GetMapping(value = "/all-accounts")
    public ResponseEntity<GetAccountsResponseBody> getAllAccounts() {
        return ResponseEntity.ok(accountService.getAccounts());
    }
    
    
	@GetMapping
	public ResponseEntity<List<Account>> getAccounts(@RequestParam(name = "page") int page,
			@RequestParam(name = "size") int size) {
		List<Account> pagedAccounts = accountService.getAllAccountsPageable(page, size);

		if (pagedAccounts.isEmpty()) {
			return ResponseEntity.noContent().build();
		}

		return ResponseEntity.ok(pagedAccounts);
	}
	
	
	@GetMapping("/filter")
    public ResponseEntity<List<Account>> filterAccounts(
            @RequestParam(required = false) Integer accountNumber,
            @RequestParam(required = false) String accountName
    ) {
        List<Account> filteredAccounts = accountService.filterAccounts(accountNumber, accountName);

        if (filteredAccounts.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(filteredAccounts);
    }
	
	@PostMapping
    public ResponseEntity<Account> createAccount(@RequestBody Account newAccount) {
        Account createdAccount = accountService.createAccount(newAccount);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAccount);
    }
	
	@PutMapping("/{id}")
    public ResponseEntity<Account> updateAccount(@PathVariable Integer id, @RequestBody Account updatedAccount) {
        Account account = accountService.updateAccount(id, updatedAccount);
        if (account == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(account);
    }
}
