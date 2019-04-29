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
public class GetAccountRequestHandlerUnitTest {
    @Mock
    private AccountService accountService;

    @InjectMocks
    private GetAccountRequestHandler getAccountRequestHandler;

    @Test
    public void shouldReturnAccount() {
        // given
        int accountId = 1;
        BigDecimal initialSum = BigDecimal.ONE;
        Account account = new Account(accountId, initialSum);

        given(accountService.findAccount(accountId)).willReturn(account);

        // when
        JsonElement jsonElement = getAccountRequestHandler.handle(accountId);

        // then
        JsonObject asJsonObject = jsonElement.getAsJsonObject();

        assertThat(asJsonObject.get("id").getAsInt(), is(equalTo(accountId)));
        assertThat(asJsonObject.get("balance").getAsBigDecimal(), is(equalTo(initialSum)));
    }
}