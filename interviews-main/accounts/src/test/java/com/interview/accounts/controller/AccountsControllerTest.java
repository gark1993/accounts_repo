package com.interview.accounts.controller;

import com.interview.accounts.service.AccountService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.accounts.domain.Account;
import com.interview.accounts.model.AccountDTO;
import com.interview.accounts.model.GetAccountsResponseBody;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class AccountsControllerTest {

	@InjectMocks
	private AccountsController accountsController;

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private AccountService accountService;

	private List<AccountDTO> testAccounts;

	@BeforeEach
	public void setUp() {
		testAccounts = new ArrayList<>();
		testAccounts.add(new AccountDTO(10055, "John Doe", 12345));
		testAccounts.add(new AccountDTO(10056, "Jane Smith", 67890));
	}

	@Test
	public void testGetAllAccounts() throws Exception {

		GetAccountsResponseBody getAccountsResponseBody = new GetAccountsResponseBody();
		getAccountsResponseBody.setAccounts(testAccounts);
		getAccountsResponseBody.setTotal(testAccounts.size());
		when(accountService.getAccounts()).thenReturn(getAccountsResponseBody);

		mockMvc.perform(MockMvcRequestBuilders.get("/accounts/all-accounts"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.total").value(testAccounts.size()));

		verify(accountService, times(1)).getAccounts();

	}

	@Test
	public void testGetAccountsPagination() throws Exception {

		Account account1 = new Account(1, 10055, "user1", 1000.0);
		Account account2 = new Account(2, 10056, "user2", 1000.0);

		List<Account> accountsList = List.of(account1, account2);
		when(accountService.getAllAccountsPageable(anyInt(), anyInt())).thenReturn(accountsList);

		int page = 0;
		int size = 2;
		mockMvc.perform(MockMvcRequestBuilders.get("/accounts").param("page", String.valueOf(page)).param("size",
				String.valueOf(size))).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("user1"))
				.andExpect(MockMvcResultMatchers.jsonPath("$[0].number").value(10055))
				.andExpect(MockMvcResultMatchers.jsonPath("$[1].name").value("user2"))
				.andExpect(MockMvcResultMatchers.jsonPath("$[1].number").value(10056));

		verify(accountService, times(1)).getAllAccountsPageable(eq(page), eq(size));

	}

	@Test
	public void testCreateAccount() throws Exception {
		// Prepare the input data for the test
		Account newAccount = new Account();
		newAccount.setId(1);
		newAccount.setName("John Doe");
		newAccount.setNumber(12345);
		newAccount.setBalance(1000.0);

		when(accountService.createAccount(any(Account.class))).thenReturn(newAccount);

		mockMvc.perform(MockMvcRequestBuilders.post("/accounts").contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(newAccount))).andExpect(MockMvcResultMatchers.status().isCreated())
				.andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
				.andExpect(MockMvcResultMatchers.jsonPath("$.name").value("John Doe"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.number").value(12345))
				.andExpect(MockMvcResultMatchers.jsonPath("$.balance").value(1000.0));

		verify(accountService, times(1)).createAccount(eq(newAccount));
	}

	private String asJsonString(Object obj) throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.writeValueAsString(obj);
	}

	@Test
	public void testFilterAccountsByAccountNumber() throws Exception {
		// Prepare the data for the test
		Account account1 = new Account(1, 10055, "user1", 1000.0);
		Account account2 = new Account(2, 10056, "user2", 1000.0);

		// Define the behavior of the mocked service method
		when(accountService.filterAccounts(10055, null)).thenReturn(List.of(account1));

		mockMvc.perform(MockMvcRequestBuilders.get("/accounts/filter").param("accountNumber", "10055"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$[0].number").value(10055));

		verify(accountService, times(1)).filterAccounts(10055, null);
	}

	@Test
	public void testFilterAccountsByAccountName() throws Exception {
		// Prepare the data for the test
		Account account1 = new Account(1, 10055, "user1", 1000.0);
		Account account2 = new Account(2, 10056, "user2", 1000.0);

		// Define the behavior of the mocked service method
		when(accountService.filterAccounts(null, "user2")).thenReturn(List.of(account2));

		mockMvc.perform(MockMvcRequestBuilders.get("/accounts/filter").param("accountName", "user2"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("user2"));

		verify(accountService, times(1)).filterAccounts(null, "user2");
	}

	@Test
	public void testFilterAccountsByAccountNameAndNumber() throws Exception {
		Account account1 = new Account(1, 10055, "user1", 1000.0);
		Account account2 = new Account(2, 10056, "user2", 1000.0);

		when(accountService.filterAccounts(10055, "user1")).thenReturn(List.of(account1));

		mockMvc.perform(MockMvcRequestBuilders.get("/accounts/filter").param("accountName", "user1")
				.param("accountNumber", "10055")).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$[0].number").value(10055))
				.andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("user1"));

		verify(accountService, times(1)).filterAccounts(10055, "user1");
	}

	@Test
	public void testUpdateAccount() throws Exception {

		Account existingAccount = new Account(1, 10055, "user1", 1000.0);

		when(accountService.updateAccount(anyInt(), any(Account.class))).thenReturn(existingAccount);

		mockMvc.perform(MockMvcRequestBuilders.put("/accounts/{id}", 1).contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(existingAccount))).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.number").value(10055))
				.andExpect(MockMvcResultMatchers.jsonPath("$.name").value("user1"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.balance").value(1000.0));

		verify(accountService, times(1)).updateAccount(anyInt(), any(Account.class));
	}

	@Test
	public void testUpdateNonExistingAccount() throws Exception {

		Account accountToUpdate = new Account(1, 10055, "user1", 1000.0);

		when(accountService.updateAccount(anyInt(), any(Account.class))).thenReturn(null);

		mockMvc.perform(MockMvcRequestBuilders.put("/accounts/{id}", 1).contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(accountToUpdate))).andExpect(MockMvcResultMatchers.status().isNotFound());

		verify(accountService, times(1)).updateAccount(anyInt(), any(Account.class));
	}

}