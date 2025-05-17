package com.kursova.kursovaapi.service;

import com.kursova.kursovaapi.dto.TourDTO;
import com.kursova.kursovaapi.entity.FavoriteEntity;
import com.kursova.kursovaapi.entity.TourEntity;
import com.kursova.kursovaapi.repository.FavoriteRepository;
import com.kursova.kursovaapi.repository.TourRepository;
import com.kursova.kursovaapi.mapper.TourMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class FavoriteServiceTest {

    @Mock
    private FavoriteRepository favoriteRepository;  // мок репозиторію для роботи з обраними турами

    @Mock
    private TourRepository tourRepository;  // мок репозиторію для турів (для перевірки валідності ID)

    @InjectMocks
    private FavoriteService favoriteService;  // створює FavoriteService з моками замість реальних бінів

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // ініціалізує моки перед кожним тестом
    }

    // --- addToFavorites() ---

    @Test
    void addToFavorites_tourExistsAndNotFavorite_addsSuccessfully() {
        TourEntity tour = new TourEntity();
        tour.setId(1);

        // мок: знайдено тур за ID
        when(tourRepository.findById(1)).thenReturn(Optional.of(tour));
        // мок: ще не в обраних
        when(favoriteRepository.existsByTour_Id(1)).thenReturn(false);

        // дія: додаємо в обране
        favoriteService.addToFavorites(1);

        // перевірка: викликано збереження
        verify(favoriteRepository).save(any(FavoriteEntity.class));
    }

    @Test
    void addToFavorites_tourDoesNotExist_throwsException() {
        // мок: тур не існує
        when(tourRepository.findById(99)).thenReturn(Optional.empty());

        // дія + перевірка: очікуємо виняток
        assertThatThrownBy(() -> favoriteService.addToFavorites(99))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid tour ID");

        // перевірка: не було спроб збереження
        verifyNoInteractions(favoriteRepository);
    }

    @Test
    void addToFavorites_alreadyFavorite_doesNotAddAgain() {
        TourEntity tour = new TourEntity();
        tour.setId(5);

        // тур знайдено
        when(tourRepository.findById(5)).thenReturn(Optional.of(tour));
        // тур вже в обраних
        when(favoriteRepository.existsByTour_Id(5)).thenReturn(true);

        favoriteService.addToFavorites(5);

        // збереження не викликається повторно
        verify(favoriteRepository, never()).save(any());
    }

    // --- removeByTourId() ---

    @Test
    void removeByTourId_deletesFromFavorites() {
        favoriteService.removeByTourId(3);
        verify(favoriteRepository).deleteByTourId(3);  // перевірка, що виклик був
    }

    // --- getAll() ---

    @Test
    void getAll_returnsAllFavoritesWithIsFavoriteTrue() {
        // створюємо тестовий тур
        TourEntity tour = new TourEntity();
        tour.setId(1);
        tour.setName("Test Tour");

        // і обгортку FavoriteEntity
        FavoriteEntity favorite = new FavoriteEntity(tour);

        when(favoriteRepository.findAll()).thenReturn(List.of(favorite));  // лише один запис

        // готуємо DTO, яке має повернути маппер
        TourDTO dto = new TourDTO();
        dto.setId(1);
        dto.setName("Test Tour");

        // мок static методу TourMapper.toDto(...)
        try (MockedStatic<TourMapper> mapperMock = mockStatic(TourMapper.class)) {
            mapperMock.when(() -> TourMapper.toDto(tour)).thenReturn(dto);

            List<TourDTO> result = favoriteService.getAll();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getId()).isEqualTo(1);
            assertThat(result.get(0).getIsFavorite()).isTrue();  // перевірка, що прапор встановлено
        }
    }

    // --- searchFavorites() ---

    @Test
    void searchFavorites_filtersApplied_returnsFilteredPage() {
        // є один тур в обраних
        TourEntity favoriteTour = new TourEntity();
        favoriteTour.setId(1);
        favoriteTour.setName("Filtered Tour");

        FavoriteEntity favoriteEntity = new FavoriteEntity(favoriteTour);
        when(favoriteRepository.findAll()).thenReturn(List.of(favoriteEntity));

        // і саме його повертає фільтроване запитування
        when(tourRepository.findAll(any(Specification.class))).thenReturn(List.of(favoriteTour));

        // результат, який має повернути TourMapper
        TourDTO dto = new TourDTO();
        dto.setId(1);
        dto.setName("Filtered Tour");

        try (MockedStatic<TourMapper> mapperMock = mockStatic(TourMapper.class)) {
            mapperMock.when(() -> TourMapper.toDto(favoriteTour)).thenReturn(dto);

            Page<TourDTO> result = favoriteService.searchFavorites(
                    "Filtered", null, null,
                    null, null, null, null,
                    null, null, null,
                    PageRequest.of(0, 10)
            );

            assertThat(result.getTotalElements()).isEqualTo(1);
            assertThat(result.getContent().get(0).getId()).isEqualTo(1);
            assertThat(result.getContent().get(0).getIsFavorite()).isTrue();  // прапор встановлено вручну в сервісі
        }
    }

    @Test
    void searchFavorites_noMatch_returnsEmptyPage() {
        // жодного улюбленого туру
        when(favoriteRepository.findAll()).thenReturn(List.of());
        // фільтр теж не дає результатів
        when(tourRepository.findAll(any(Specification.class))).thenReturn(List.of());

        Page<TourDTO> result = favoriteService.searchFavorites(
                null, null, null,
                null, null, null, null,
                null, null, null,
                PageRequest.of(0, 10)
        );

        // результат — порожня сторінка
        assertThat(result.getTotalElements()).isZero();
        assertThat(result.getContent()).isEmpty();
    }
}
