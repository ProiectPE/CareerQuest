package ro.unibuc.careerquest.service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import ro.unibuc.careerquest.data.EmployerEntity;
import ro.unibuc.careerquest.data.EmployerRepository;
import ro.unibuc.careerquest.dto.Employer;
import ro.unibuc.careerquest.exception.EntityNotFoundException;

@ExtendWith(SpringExtension.class)
public class EmployerServiceTest {

    @Mock
    private EmployerRepository employerRepository;

    @InjectMocks
    private EmployerService employerService;

    private EmployerEntity employerEntity1;
    private EmployerEntity employerEntity2;
    private Employer employerDto1;
    private Employer employerDto2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        employerEntity1 = new EmployerEntity();
        employerEntity1.setId("1");
        employerEntity1.setName("Test Employer 1");
        employerEntity1.setEmail("test1@email.com");
        employerEntity1.setPhone("1234567890");
        employerEntity1.setCompany("Company A");
        employerEntity1.setLastPaymentDate(LocalDate.of(2025, 1, 13));
        employerEntity1.setPremium(true);

        employerEntity2 = new EmployerEntity();
        employerEntity2.setId("2");
        employerEntity2.setName("Test Employer 2");
        employerEntity2.setEmail("test2@email.com");
        employerEntity2.setPhone("0987654321");
        employerEntity2.setCompany("Company B");
        employerEntity2.setLastPaymentDate(LocalDate.of(2025, 1, 15));
        employerEntity2.setPremium(false);

        employerDto1 = new Employer("1", "Test Employer 1", "test1@email.com", "1234567890", "Company A", LocalDate.of(2025, 1, 13), true);
        employerDto2 = new Employer("2", "Test Employer 2", "test2@email.com", "0987654321", "Company B", LocalDate.of(2025, 1, 15), false);
    }

    @Test
    public void testGetAllEmployers() {
        List<EmployerEntity> employerEntities = Arrays.asList(employerEntity1, employerEntity2);

        when(employerRepository.findAll()).thenReturn(employerEntities);

        List<Employer> employers = employerService.getAllEmployers();

        assertEquals(2, employers.size());
        assertEquals("Test Employer 1", employers.get(0).getName());
        assertEquals("Test Employer 2", employers.get(1).getName());
    }

    @Test
    public void testGetEmployerById() {
        String employerId = "1";
        when(employerRepository.findById(employerId)).thenReturn(Optional.of(employerEntity1));

        Employer employer = employerService.getEmployerById(employerId);

        assertEquals("1", employer.getId());
        assertEquals("Test Employer 1", employer.getName());
        assertEquals("Company A", employer.getCompany());
    }

    @Test
    public void testGetEmployerByIdThrowsException() {
        String employerId = "3";
        when(employerRepository.findById(employerId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> employerService.getEmployerById(employerId));
    }

    @Test
    public void testSaveEmployer() {
        when(employerRepository.save(any(EmployerEntity.class))).thenReturn(employerEntity1);

        Employer savedEmployer = employerService.saveEmployer(employerDto1);

        assertEquals("1", savedEmployer.getId());
        assertEquals("Test Employer 1", savedEmployer.getName());
        assertEquals("Company A", savedEmployer.getCompany());
    }

    @Test
    public void testUpdateEmployer() {
        String employerId = "1";
        Employer updatedEmployer = new Employer("1", "Updated Employer", "updated@email.com", "1122334455", "Updated Company", LocalDate.now(), false);

        when(employerRepository.findById(employerId)).thenReturn(Optional.of(employerEntity1));
        when(employerRepository.save(any(EmployerEntity.class))).thenReturn(employerEntity1);

        Employer result = employerService.updateEmployer(employerId, updatedEmployer);

        assertEquals("Updated Employer", result.getName());
        assertEquals("Updated Company", result.getCompany());
        assertEquals("updated@email.com", result.getEmail());
    }

    @Test
    public void testUpdateEmployerThrowsException() {
        String employerId = "3";
        Employer updatedEmployer = new Employer("3", "Updated Employer", "updated@email.com", "1122334455", "Updated Company", LocalDate.now(), false);

        when(employerRepository.findById(employerId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> employerService.updateEmployer(employerId, updatedEmployer));
    }

    @Test
    public void testDeleteEmployer() {
        String employerId = "1";
        when(employerRepository.findById(employerId)).thenReturn(Optional.of(employerEntity1));

        String result = employerService.deleteEmployer(employerId);

        assertEquals("Employer by id=1 was deleted.", result);
        verify(employerRepository, times(1)).delete(employerEntity1);
    }

    @Test
    public void testDeleteEmployerThrowsException() {
        String employerId = "3";
        when(employerRepository.findById(employerId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> employerService.deleteEmployer(employerId));
    }

    @Test
    public void testDeleteAllEmployers() {
        employerService.deleteAllEmplyers();

        verify(employerRepository, times(1)).deleteAll();
    }

    @Test
    public void testUpdatePayment() {
        String employerId = "1";
        when(employerRepository.findById(employerId)).thenReturn(Optional.of(employerEntity1));
        when(employerRepository.save(any(EmployerEntity.class))).thenReturn(employerEntity1);

        Employer updatedEmployer = employerService.updatePayment(employerId);

        assertEquals("1", updatedEmployer.getId());
        assertTrue(updatedEmployer.isPremium());
        assertEquals(LocalDate.now(), updatedEmployer.getLastPaymentDate());
    }

    @Test
    public void testUpdatePaymentThrowsException() {
        String employerId = "3";
        when(employerRepository.findById(employerId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> employerService.updatePayment(employerId));
    }
}
