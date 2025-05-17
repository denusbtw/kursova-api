package com.kursova.kursovaapi.service;

import com.kursova.kursovaapi.dto.TransportDTO;
import com.kursova.kursovaapi.entity.TransportEntity;
import com.kursova.kursovaapi.repository.TransportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransportServiceTest {

    @Mock
    private TransportRepository repository; // мок репозиторію, що працює з TransportEntity

    @InjectMocks
    private TransportService transportService; // створює об'єкт з мокнутим репозиторієм усередині

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this); // ініціалізація моків
    }

    @Test
    void getById_existingId_returnsDTO() {
        // підготовка: транспорт із ID 1
        TransportEntity entity = new TransportEntity("Bus");
        entity.setId(1);

        // коли викликається findById(1), повернути обгортку з entity
        when(repository.findById(1)).thenReturn(Optional.of(entity));

        // виклик методу сервісу
        TransportDTO result = transportService.getById(1);

        // перевірка, що DTO зібрався правильно
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getName()).isEqualTo("Bus");
    }

    @Test
    void getById_notFound_throwsException() {
        // коли пошук не дає результату
        when(repository.findById(99)).thenReturn(Optional.empty());

        // перевірка, що виняток кинуто
        assertThatThrownBy(() -> transportService.getById(99))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Transport not found");
    }

    @Test
    void getAll_returnsAllTransportsMapped() {
        // два записи в базі
        TransportEntity e1 = new TransportEntity("Bus");
        e1.setId(1);
        TransportEntity e2 = new TransportEntity("Plane");
        e2.setId(2);

        // репозиторій повертає ці сутності
        when(repository.findAll()).thenReturn(List.of(e1, e2));

        // виклик сервісу
        List<TransportDTO> result = transportService.getAll();

        // перевірка розміру і вмісту
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Bus");
        assertThat(result.get(1).getName()).isEqualTo("Plane");
    }

    @Test
    void create_validDTO_savesAndReturnsMappedDTO() {
        // вхідне DTO
        TransportDTO inputDto = new TransportDTO();
        inputDto.setName("Boat");

        // те, що поверне збереження
        TransportEntity savedEntity = new TransportEntity("Boat");
        savedEntity.setId(10);

        when(repository.save(any())).thenReturn(savedEntity);

        // виклик
        TransportDTO result = transportService.create(inputDto);

        // перевірка: ID і назва мають бути збережені
        assertThat(result.getId()).isEqualTo(10);
        assertThat(result.getName()).isEqualTo("Boat");
    }

    @Test
    void delete_existingId_callsRepository() {
        // просто перевірка, що метод deleteById викликається
        transportService.delete(7);
        verify(repository).deleteById(7);
    }

    @Test
    void update_existingId_updatesAndReturnsDTO() {
        // старе значення в базі
        TransportEntity existing = new TransportEntity("Old");
        existing.setId(5);

        // мок: воно знайдене
        when(repository.findById(5)).thenReturn(Optional.of(existing));

        // після оновлення
        TransportEntity updatedEntity = new TransportEntity("New");
        updatedEntity.setId(5);
        when(repository.save(any())).thenReturn(updatedEntity);

        // DTO з новим значенням
        TransportDTO inputDto = new TransportDTO();
        inputDto.setName("New");

        // виклик
        TransportDTO result = transportService.update(5, inputDto);

        // перевірка
        assertThat(result.getId()).isEqualTo(5);
        assertThat(result.getName()).isEqualTo("New");

        verify(repository).save(any());
    }

    @Test
    void update_notFound_throwsException() {
        // якщо ID не знайдено
        when(repository.findById(999)).thenReturn(Optional.empty());

        TransportDTO dto = new TransportDTO();
        dto.setName("Doesn't matter");

        // очікується помилка
        assertThatThrownBy(() -> transportService.update(999, dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid transport ID");

        // збереження не має бути викликане
        verify(repository, never()).save(any());
    }
}
