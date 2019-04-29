package com.revolut.money.rest.handler;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.revolut.money.model.generated.tables.pojos.Account;
import com.revolut.money.service.AccountService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class CreateAccountRequestHandlerUnitTest {
    @Mock
    private AccountService accountService;

    @InjectMocks
    private CreateAccountRequestHandler createAccountRequestHandler;

    @Test
    public void shouldCreateAccount() {
        // given
        int accountId = 1;
        BigDecimal initialSum = BigDecimal.ZERO;
        Account account = new Account(accountId, initialSum);
        given(accountService.createAccount()).willReturn(account);

        // when
        JsonElement jsonElement = createAccountRequestHandler.handle();

        // then
        JsonObject asJsonObject = jsonElement.getAsJsonObject();

        assertThat(asJsonObject.get("id").getAsInt(), is(equalTo(accountId)));
        assertThat(asJsonObject.get("balance").getAsBigDecimal(), is(equalTo(initialSum)));
    }
}