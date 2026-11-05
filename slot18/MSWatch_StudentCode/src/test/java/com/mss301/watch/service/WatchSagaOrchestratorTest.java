package com.mss301.watch.service;

import com.mss301.watch.client.BrandClient;
import com.mss301.watch.dto.WatchRequest;
import com.mss301.watch.entity.*;
import com.mss301.watch.exception.SagaExecutionException;
import com.mss301.watch.saga.*;
import org.junit.jupiter.api.*;
import org.springframework.test.util.ReflectionTestUtils;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class WatchSagaOrchestratorTest {
    private WatchLocalService local;
    private BrandClient brands;
    private SagaTracker tracker;
    private WatchSagaOrchestrator orchestrator;

    @BeforeEach void setUp() {
        local = mock(WatchLocalService.class); brands = mock(BrandClient.class); tracker = mock(SagaTracker.class);
        orchestrator = new WatchSagaOrchestrator(local, brands, tracker);
        ReflectionTestUtils.setField(orchestrator, "faultEnabled", true);
        ReflectionTestUtils.setField(orchestrator, "maxAttempts", 3);
    }

    @Test void createCompensatesInReverseOrderAfterSecondStep() {
        Watch watch = mock(Watch.class); WatchCategory category = mock(WatchCategory.class);
        when(watch.getWatchId()).thenReturn(11); when(watch.getCategory()).thenReturn(category); when(category.getCategoryId()).thenReturn(1);
        when(watch.getName()).thenReturn("Valid Watch Name"); when(watch.getBrandId()).thenReturn(1);
        when(watch.getCaseMaterial()).thenReturn("Steel"); when(watch.getStrapMaterial()).thenReturn("Leather");
        when(watch.getWaterResistance()).thenReturn("100 meters"); when(watch.getPrice()).thenReturn(new BigDecimal("100"));
        when(watch.getReleaseDate()).thenReturn(LocalDate.now()); when(watch.getStock()).thenReturn(1);
        when(local.create(any())).thenReturn(watch);

        assertThatThrownBy(() -> orchestrator.create(request(1), 2)).isInstanceOf(SagaExecutionException.class);
        var order = inOrder(brands, local);
        order.verify(brands).assign(1, 11);
        order.verify(brands).unassign(11);
        order.verify(local).deleteIfExists(11);
    }

    @Test void deleteSkipsFaultAfterPivot() {
        when(local.snapshot(5)).thenReturn(new WatchData(5, "Valid Watch Name", 1, 2, "Steel", "Leather", "100 meters", new BigDecimal("100"), LocalDate.now(), 1));
        assertThatCode(() -> orchestrator.delete(5, 2)).doesNotThrowAnyException();
        verify(brands).unassign(5); verify(local).delete(5); verify(brands, never()).assign(anyInt(), anyInt());
    }

    @Test void deleteNeverCompensatesAfterPivotCommit() {
        when(local.snapshot(5)).thenReturn(new WatchData(5, "Valid Watch Name", 1, 2, "Steel", "Leather", "100 meters", new BigDecimal("100"), LocalDate.now(), 1));
        doThrow(new IllegalStateException("log unavailable")).when(tracker)
            .log(anyString(), eq("DELETE_WATCH"), eq("PIVOT"), eq("COMPLETED"), anyString());

        assertThatCode(() -> orchestrator.delete(5, null)).doesNotThrowAnyException();
        verify(local).delete(5);
        verify(brands, never()).assign(anyInt(), anyInt());
    }

    private WatchRequest request(int brandId) {
        return new WatchRequest("Valid Watch Name", 1, brandId, "Steel", "Leather", "100 meters", new BigDecimal("100"), LocalDate.now(), 1);
    }
}
