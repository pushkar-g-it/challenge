package com.db.awmd.challenge;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import com.db.awmd.challenge.service.AccountsService;
import com.db.awmd.challenge.service.EmailNotificationService;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class TransferControllerTest {

	private MockMvc mockMvc;

	@Autowired
	private AccountsService accountsService;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@MockBean
	private EmailNotificationService emailNotificationService;

	@Before
	public void prepareMockMvc() {
		this.mockMvc = webAppContextSetup(this.webApplicationContext).build();

		// Reset the existing accounts before each test.
		accountsService.getAccountsRepository().clearAccounts();
	}

	@Test
	public void transferAmountWithNoAccountId() throws Exception {
		this.mockMvc.perform(post("/v1/transfer").contentType(MediaType.APPLICATION_JSON).content("{\"amount\":1000}"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void transferZeroAmount() throws Exception {
		this.mockMvc
				.perform(post("/v1/transfer").contentType(MediaType.APPLICATION_JSON)
						.content("{\"fromAccountId\":\"Id-123\",\"toAccountId\":\"Id-124\",\"amount\":0}"))
				.andExpect(status().isUnprocessableEntity());
	}
	@Test
	public void transferNonNumericalAmount() throws Exception {
		this.mockMvc
				.perform(post("/v1/transfer").contentType(MediaType.APPLICATION_JSON)
						.content("{\"fromAccountId\":\"Id-123\",\"toAccountId\":\"Id-124\",\"amount\":\"$mhd\"}"))
				.andExpect(status().isBadRequest());
	}
	@Test
	public void transferNegativeAmount() throws Exception {
		this.mockMvc
				.perform(post("/v1/transfer").contentType(MediaType.APPLICATION_JSON)
						.content("{\"fromAccountId\":\"Id-123\",\"toAccountId\":\"Id-124\",\"amount\":-1000}"))
				.andExpect(status().isUnprocessableEntity());
	}

	@Test
	public void transferAmountWithEmptyReceiverIdAccountId() throws Exception {
		this.mockMvc
				.perform(post("/v1/transfer").contentType(MediaType.APPLICATION_JSON)
						.content("{\"fromAccountId\":\"Id-123\",\"toAccountId\":\"\",\"amount\":1000}"))
				.andExpect(status().isBadRequest());
	}
	
	@Test
	public void transferAmountWithEmptySenderIdAccountId() throws Exception {
		this.mockMvc
				.perform(post("/v1/transfer").contentType(MediaType.APPLICATION_JSON)
						.content("{\"fromAccountId\":\"\",\"toAccountId\":\"Id-124\",\"amount\":1000}"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void transferAmount() throws Exception {
		this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
				.content("{\"accountId\":\"Id-125\",\"balance\":1000}")).andExpect(status().isCreated());
		this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
				.content("{\"accountId\":\"Id-126\",\"balance\":1000}")).andExpect(status().isCreated());

		this.mockMvc
				.perform(post("/v1/transfer").contentType(MediaType.APPLICATION_JSON)
						.content("{\"fromAccountId\":\"Id-125\",\"toAccountId\":\"Id-126\",\"amount\":1000}"))
				.andExpect(status().isOk());

		this.mockMvc.perform(get("/v1/accounts/Id-126")).andExpect(status().isOk())
				.andExpect(content().string("{\"accountId\":\"Id-126\",\"balance\":2000}"));
		this.mockMvc.perform(get("/v1/accounts/Id-125")).andExpect(status().isOk())
				.andExpect(content().string("{\"accountId\":\"Id-125\",\"balance\":0}"));

	}

	@Test
	public void transferAmount_InsufficientBalance() throws Exception {
		this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
				.content("{\"accountId\":\"Id-125\",\"balance\":0}")).andExpect(status().isCreated());
		this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
				.content("{\"accountId\":\"Id-126\",\"balance\":1000}")).andExpect(status().isCreated());

		this.mockMvc
				.perform(post("/v1/transfer").contentType(MediaType.APPLICATION_JSON)
						.content("{\"fromAccountId\":\"Id-125\",\"toAccountId\":\"Id-126\",\"amount\":1000}"))
				.andExpect(status().isUnprocessableEntity());

	}

	@Test
	public void transferAmount_AccountDoesntExist() throws Exception {
		this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
				.content("{\"accountId\":\"Id-126\",\"balance\":1000}")).andExpect(status().isCreated());

		this.mockMvc
				.perform(post("/v1/transfer").contentType(MediaType.APPLICATION_JSON)
						.content("{\"fromAccountId\":\"Id-125\",\"toAccountId\":\"Id-126\",\"amount\":1000}"))
				.andExpect(status().isBadRequest());

	}

}
