package com.aura

import com.aura.data.response.AccountResponse
import com.aura.domain.Account
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AccountResponseTest {

    @Test
    fun toAccountModelTest() {
        val accountResponse = AccountResponse(1, true, 1.00)
        val account = Account(1, true, 1.00)

        assertEquals(account, accountResponse.toAccountModel())
    }
}