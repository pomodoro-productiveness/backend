package com.igorgorbunov3333.timer.backend.service.dayoff;

import com.igorgorbunov3333.timer.backend.model.entity.dayoff.DayOff;
import com.igorgorbunov3333.timer.backend.repository.DayOffRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DayOffSynchronizerTest {

    @InjectMocks
    private DayOffSynchronizer testee;

    @Mock
    private RemoteDayOffProvider remoteDayOffProvider;
    @Mock
    private DayOffRepository dayOffRepository;

    @Test
    void synchronize_WhenInvoked_ThenSynchronize() {
        DayOff dayOffMock = mock(DayOff.class);
        List<DayOff> dayOffs = List.of(dayOffMock);
        when(remoteDayOffProvider.provide()).thenReturn(dayOffs);

        testee.synchronize();

        verify(dayOffRepository).deleteAll();
        verify(dayOffRepository).flush();
        verify(dayOffRepository).saveAll(dayOffs);
    }

}
