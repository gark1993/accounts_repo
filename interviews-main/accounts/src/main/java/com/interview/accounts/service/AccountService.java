package com.interview.accounts.service;

import com.interview.accounts.mapper.AccountsMapper;
import com.interview.accounts.model.GetAccountsResponseBody;
import com.interview.accounts.repo.AccountRepository;
import lombok.RequiredArgsConstructor;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.interview.accounts.domain.*;

@RequiredArgsConstructor
@Service
public class AccountService {

	@Autowired
    private AccountRepository accountRepository;

    public GetAccountsResponseBody getAccounts() {

        return new GetAccountsResponseBody(accountRepository.count(),AccountsMapper.map(accountRepository.findAll()));
    }
    
    public List<Account> getAllAccountsPageable(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Account> paginationRecords = accountRepository.findAll(pageable);   
        return paginationRecords.getContent();
    }
    
    public List<Account> filterAccounts(Integer accountNumber, String accountName) {
        if (accountNumber !=null && accountName != null) {
            return accountRepository.findByNumberOrName(accountNumber, accountName);
        } else if (accountNumber != null) {
            return accountRepository.findByNumber(accountNumber);
        } else if (accountName != null) {
            return accountRepository.findByName(accountName);
        } else {
            return Collections.emptyList();
        }
    }
    
    public Account createAccount(Account account) {
        return accountRepository.save(account);
    }
    
    public Account updateAccount(Integer id, Account updatedAccount) {
        Optional<Account> optionalAccount = accountRepository.findById(id);
        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            // Update the properties of the existing account with the new values
            account.setName(updatedAccount.getName());
            account.setBalance(updatedAccount.getBalance());
            return accountRepository.save(account);
        }
        return null;
    }
}
