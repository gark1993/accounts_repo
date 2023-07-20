package com.interview.accounts.repo;

import com.interview.accounts.domain.Account;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
	
	List<Account> findByNumber(int accountNumber);
	
	List<Account> findById(int id);

    List<Account> findByName(String accountName);

    List<Account> findByNumberOrName(int accountNumber, String accountName);

}
