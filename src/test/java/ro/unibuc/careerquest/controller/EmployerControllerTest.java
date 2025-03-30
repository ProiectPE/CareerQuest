package ro.unibuc.careerquest.controller;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import ro.unibuc.careerquest.dto.Employer;
import ro.unibuc.careerquest.exception.EntityNotFoundException;
import ro.unibuc.careerquest.service.EmployerService;

public class EmployerControllerTest {

    @Mock
    private EmployerService empsService;

    @InjectMocks
    private EmployerController employerController;

    private MockMvc mockMvc;

    private Employer employer_sample = new Employer("1", "Test Employer", "test@email.com", "123456789", "Test Company", LocalDate.now(), true);

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(employerController).build();
    }

    @Test
    public void test_getAllEmployers() throws Exception {
        List<Employer> employers = Arrays.asList(
            new Employer("1", "Test Employer 1", "email1@email.com", "123456", "Company 1", LocalDate.now(), true),
            new Employer("2", "Test Employer 2", "email2@email.com", "654321", "Company 2", LocalDate.now(), false)
        );

        when(empsService.getAllEmployers()).thenReturn(employers);

        mockMvc.perform(get("/employer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].name").value("Test Employer 1"))
                .andExpect(jsonPath("$[1].id").value("2"))
                .andExpect(jsonPath("$[1].name").value("Test Employer 2"));
    }

    @Test
    public void test_getEmployerById() throws Exception {
        String id = "1";
        Employer employer = new Employer("1", "Test Employer", "test@email.com", "123456789", "Test Company", LocalDate.now(), true);

        when(empsService.getEmployerById(id)).thenReturn(employer);

        mockMvc.perform(get("/employer/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("Test Employer"))
                .andExpect(jsonPath("$.email").value("test@email.com"))
                .andExpect(jsonPath("$.phone").value("123456789"))
                .andExpect(jsonPath("$.company").value("Test Company"))
                .andExpect(jsonPath("$.premium").value(true));
    }

    @Test
    public void test_createEmployer() throws Exception {
        when(empsService.saveEmployer(any(Employer.class))).thenReturn(employer_sample);

        mockMvc.perform(post("/employer")
                .content("{\"id\":\"1\",\"name\":\"Test Employer\",\"email\":\"test@email.com\",\"phone\":\"123456789\",\"company\":\"Test Company\",\"lastPaymentDate\":\"2023-03-31\",\"premium\":true}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("Test Employer"))
                .andExpect(jsonPath("$.email").value("test@email.com"))
                .andExpect(jsonPath("$.phone").value("123456789"))
                .andExpect(jsonPath("$.company").value("Test Company"))
                .andExpect(jsonPath("$.premium").value(true));
    }

    @Test
    public void test_updateEmployer() throws Exception {
        String id = "1";
        Employer updatedEmployer = new Employer("1", "Updated Employer", "updated@email.com", "987654321", "Updated Company", LocalDate.now(), false);

        when(empsService.updateEmployer(eq(id), any(Employer.class))).thenReturn(updatedEmployer);

        mockMvc.perform(put("/employer/{id}", id)
                .content("{\"name\":\"Updated Employer\",\"email\":\"updated@email.com\",\"phone\":\"987654321\",\"company\":\"Updated Company\",\"lastPaymentDate\":\"2023-03-31\",\"premium\":false}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("Updated Employer"))
                .andExpect(jsonPath("$.email").value("updated@email.com"))
                .andExpect(jsonPath("$.phone").value("987654321"))
                .andExpect(jsonPath("$.company").value("Updated Company"))
                .andExpect(jsonPath("$.premium").value(false));
    }

    @Test
    public void test_updateEmployer_NotFound() throws Exception {
        String id = "1";
        when(empsService.updateEmployer(eq(id), any(Employer.class))).thenThrow(EntityNotFoundException.class);

        mockMvc.perform(put("/employer/{id}", id)
                .content("{\"name\":\"Updated Employer\",\"email\":\"updated@email.com\",\"phone\":\"987654321\",\"company\":\"Updated Company\",\"lastPaymentDate\":\"2023-03-31\",\"premium\":false}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void test_deleteEmployer() throws Exception {
        String id = "1";

        when(empsService.deleteEmployer(id)).thenReturn("Employer deleted");

        mockMvc.perform(delete("/employer/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().string("Employer deleted"));

        verify(empsService, times(1)).deleteEmployer(id);
    }

    @Test
    void test_deleteEmployer_NotFound() throws Exception {
        String id = "1";
        when(empsService.deleteEmployer(id)).thenThrow(EntityNotFoundException.class);

        mockMvc.perform(delete("/employer/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    public void test_payForPremium() throws Exception {
        String id = "1";
        when(empsService.updatePayment(id)).thenReturn(employer_sample);

        mockMvc.perform(put("/employer/{id}/pay", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("Test Employer"))
                .andExpect(jsonPath("$.premium").value(true));
    }
}
