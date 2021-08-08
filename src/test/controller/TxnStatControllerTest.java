package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.n26.Application;
import com.n26.model.Txn;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.Instant;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Application.class)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class TxnStatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    public static final String STAT_ENDPOINT = "/statistics";
    public static final String TXN_ENDPOINT = "/transactions";

    @Before
    public void delete_txns() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(TXN_ENDPOINT));
    }

    @Test
    public void add_txn_success() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                .post(TXN_ENDPOINT)
                .content(new ObjectMapper().writeValueAsString(new Txn("44.2", Instant.now().toString())))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    public void add_txn_bad_request_invalid_json() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .post(TXN_ENDPOINT)
                .content("Invalid JSON String")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void add_txn_no_content_old_txn() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .post(TXN_ENDPOINT)
                .content(new ObjectMapper().writeValueAsString(new Txn("43.233434", "2018-07-27T09:59:51.312Z")))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }


    @Test
    public void add_txn_unparsable_invalid_amount() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .post(TXN_ENDPOINT)
                .content(new ObjectMapper().writeValueAsString(new Txn("Invalid Amount", Instant.now().toString())))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void add_txn_unparsable_invalid_timestamp() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .post(TXN_ENDPOINT)
                .content(new ObjectMapper().writeValueAsString(new Txn("143.34", "Invalid Timestamp")))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void add_txn_unparsable_future_date() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .post(TXN_ENDPOINT)
                .content(new ObjectMapper().writeValueAsString(new Txn("223.34", "2022-07-17T09:59:51.312Z")))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void delete_txn_success() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                .delete(TXN_ENDPOINT))
                .andExpect(status().isNoContent());
    }

    @Before
    public void delete_all_txn() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(TXN_ENDPOINT));
    }

    @Test
    public void get_stat_success_with_no_txn() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get(STAT_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.sum").value("0.00"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.avg").value("0.00"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.max").value("0.00"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.min").value("0.00"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.count").value(0));

    }

    @Test
    public void get_stat_success_valid_txns() throws Exception {

        add_txns();

        mockMvc.perform(MockMvcRequestBuilders
                .get(STAT_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.sum").value("49.68"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.avg").value("16.56"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.max").value("34.34"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.min").value("3.00"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.count").value(3));

    }

    @Test
    public void get_stat_return_zero_after_delete() throws Exception {

        add_txns();

        delete_txns();

        mockMvc.perform(MockMvcRequestBuilders
                .get(STAT_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.sum").value("0.00"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.avg").value("0.00"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.max").value("0.00"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.min").value("0.00"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.count").value(0));

    }

    private void add_txns() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .post(TXN_ENDPOINT)
                .content(new ObjectMapper().writeValueAsString(new Txn("34.34", Instant.now().toString())))
                .contentType(MediaType.APPLICATION_JSON));
        mockMvc.perform(MockMvcRequestBuilders
                .post(TXN_ENDPOINT)
                .content(new ObjectMapper().writeValueAsString(new Txn("12.34", Instant.now().toString())))
                .contentType(MediaType.APPLICATION_JSON));
        mockMvc.perform(MockMvcRequestBuilders
                .post(TXN_ENDPOINT)
                .content(new ObjectMapper().writeValueAsString(new Txn("3.00", Instant.now().toString())))
                .contentType(MediaType.APPLICATION_JSON));
    }


}
