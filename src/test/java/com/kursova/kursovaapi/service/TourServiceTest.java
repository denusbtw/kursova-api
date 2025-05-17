package com.kursova.kursovaapi.service;

import com.kursova.kursovaapi.dto.TourDTO;
import com.kursova.kursovaapi.entity.TourEntity;
import com.kursova.kursovaapi.entity.TransportEntity;
import com.kursova.kursovaapi.mapper.TourMapper;
import com.kursova.kursovaapi.repository.TourRepository;
import com.kursova.kursovaapi.repository.TransportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TourServiceTest {

    @Mock
    private TourRepository tourRepository;

    @Mock
    private TransportRepository transportRepository;

    @Mock
    private FavoriteService favoriteService;

    @InjectMocks
    private TourService tourService;

    @BeforeEach
    void setUp() {
        // Ініціалізація моків перед кожним тестом
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createTour_validData_returnsSavedTourDTO() {
        // Підготовка вхідного DTO
        TourDTO input = new TourDTO();
        input.setName("Test Tour");
        input.setTransportId(1);

        // Мокаємо транспорт
        TransportEntity transport = new TransportEntity("Bus");
        transport.setId(1);

        // Мокаємо збережений тур
        TourEntity savedEntity = new TourEntity();
        savedEntity.setName("Test Tour");
        savedEntity.setId(10);
        savedEntity.setTransport(transport);

        when(transportRepository.findById(1)).thenReturn(Optional.of(transport));
        when(tourRepository.save(any())).thenReturn(savedEntity);

        // Виклик методу
        TourDTO result = tourService.createTour(input);

        // Перевірка результату
        assertThat(result.getId()).isEqualTo(10);
        assertThat(result.getName()).isEqualTo("Test Tour");

        verify(tourRepository).save(any());
        verify(transportRepository).findById(1);
    }

    @Test
    void createTour_transportNotFound_throwsException() {
        TourDTO input = new TourDTO();
        input.setTransportId(99);

        when(transportRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tourService.createTour(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid transport ID");

        verify(transportRepository).findById(99);
        verifyNoMoreInteractions(tourRepository); // не має бути збережень
    }

    @Test
    void searchToursWithPaging_filtersApplied_returnsMappedPage() {
        Pageable pageable = PageRequest.of(0, 10);

        TransportEntity transport = new TransportEntity("Bus");
        transport.setId(1);

        // Підготовка сутності туру
        TourEntity entity = new TourEntity();
        entity.setId(1);
        entity.setName("Beach Tour");
        entity.setTransport(transport);

        // Сторінка з одним результатом
        Page<TourEntity> tourEntityPage = new PageImpl<>(List.of(entity), pageable, 1);

        // Фаворити (один тур)
        TourDTO favoriteDto = new TourDTO();
        favoriteDto.setId(1);

        when(favoriteService.getAll()).thenReturn(List.of(favoriteDto));
        when(tourRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(tourEntityPage);

        // Підготовка мапінгу
        TourDTO mappedDto = new TourDTO();
        mappedDto.setId(1);
        mappedDto.setName("Beach Tour");
        mappedDto.setIsFavorite(false); // буде перезаписано вручну

        try (MockedStatic<TourMapper> mocked = mockStatic(TourMapper.class)) {
            mocked.when(() -> TourMapper.toDto(entity)).thenReturn(mappedDto);

            Page<TourDTO> result = tourService.searchToursWithPaging(
                    "Beach", "Vacation", "All inclusive",
                    3, 10,
                    200, 1000,
                    4.0, 5.0,
                    "Bus", pageable
            );

            // Перевірка
            assertThat(result).isNotNull();
            assertThat(result.getTotalElements()).isEqualTo(1);

            TourDTO dto = result.getContent().get(0);
            assertThat(dto.getId()).isEqualTo(1);
            assertThat(dto.getName()).isEqualTo("Beach Tour");
            assertThat(dto.getIsFavorite()).isTrue(); // вручну встановлено в сервісі
        }

        verify(tourRepository).findAll(any(Specification.class), any(Pageable.class));
        verify(favoriteService).getAll();
    }

    @Test
    void searchToursWithPaging_noFilters_returnsAll() {
        Pageable pageable = PageRequest.of(0, 10);

        TourEntity entity = new TourEntity();
        entity.setId(5);

        Page<TourEntity> entityPage = new PageImpl<>(List.of(entity), pageable, 1);

        when(favoriteService.getAll()).thenReturn(List.of());
        when(tourRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(entityPage);

        TourDTO mappedDto = new TourDTO();
        mappedDto.setId(5);
        mappedDto.setName("Test Tour");

        try (MockedStatic<TourMapper> mapperMock = mockStatic(TourMapper.class)) {
            mapperMock.when(() -> TourMapper.toDto(entity)).thenReturn(mappedDto);

            Page<TourDTO> result = tourService.searchToursWithPaging(
                    null, null, null,
                    null, null,
                    null, null,
                    null, null,
                    null, pageable
            );

            assertThat(result.getTotalElements()).isEqualTo(1);

            TourDTO dto = result.getContent().get(0);
            assertThat(dto.getId()).isEqualTo(5);
            assertThat(dto.getIsFavorite()).isFalse(); // тому що список фаворитів — порожній
        }

        verify(favoriteService).getAll();
        verify(tourRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void getAllTypes_delegatesToRepository() {
        List<String> types = List.of("Beach", "Mountain");
        when(tourRepository.findAllDistinctTypes()).thenReturn(types);

        List<String> result = tourService.getAllTypes();

        assertThat(result).isEqualTo(types);
        verify(tourRepository).findAllDistinctTypes();
    }

    @Test
    void getAllMealOptions_delegatesToRepository() {
        List<String> options = List.of("Breakfast", "Full board");
        when(tourRepository.findAllDistinctMealOptions()).thenReturn(options);

        List<String> result = tourService.getAllMealOptions();

        assertThat(result).isEqualTo(options);
        verify(tourRepository).findAllDistinctMealOptions();
    }
}
