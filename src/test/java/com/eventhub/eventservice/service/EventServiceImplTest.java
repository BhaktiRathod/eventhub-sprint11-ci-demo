package com.eventhub.eventservice.service;



import com.eventhub.eventservice.dto.EventRequestDTO;
import com.eventhub.eventservice.dto.EventResponseDTO;
import com.eventhub.eventservice.entity.Event;
import com.eventhub.eventservice.exception.EventNotFoundException;
import com.eventhub.eventservice.repository.EventRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventServiceImpl eventService;

    @Test
    void shouldCreateEvent() {

        EventRequestDTO request = new EventRequestDTO();
        request.setName("Spring Boot Workshop");
        request.setDescription("Hands-on Spring Boot session");
        request.setEventDate(LocalDate.of(2026, 10, 10));
        request.setVenue("Bengaluru");
        request.setCapacity(100);
        request.setOrganizerId(1L);

        Event savedEvent = new Event();
        savedEvent.setName("Spring Boot Workshop");
        savedEvent.setDescription("Hands-on Spring Boot session");
        savedEvent.setEventDate(LocalDate.of(2026, 10, 10));
        savedEvent.setVenue("Bengaluru");
        savedEvent.setCapacity(100);
        savedEvent.setOrganizerId(1L);

        when(eventRepository.save(any(Event.class)))
                .thenReturn(savedEvent);

        EventResponseDTO result =
                eventService.createEvent(request);

        assertEquals(
                "Docker Workshop",
                result.getName()
        );

        assertEquals("Bengaluru", result.getVenue());
        assertEquals(100, result.getCapacity());
        assertEquals(1L, result.getOrganizerId());

        verify(eventRepository)
                .save(any(Event.class));
    }

    @Test
    void shouldReturnEventById() {

        Event event = new Event();
        event.setName("Java Conference");
        event.setEventDate(
                LocalDate.of(2026, 11, 15)
        );
        event.setVenue("Delhi");
        event.setCapacity(200);
        event.setOrganizerId(2L);

        when(eventRepository.findById(1L))
                .thenReturn(Optional.of(event));

        EventResponseDTO result =
                eventService.getEvent(1L);

        assertEquals(
                "Java Conference",
                result.getName()
        );

        assertEquals(
                "Delhi",
                result.getVenue()
        );

        verify(eventRepository)
                .findById(1L);
    }

    @Test
    void shouldReturnAllEvents() {

        Event event1 = new Event();
        event1.setName("Spring Boot Workshop");
        event1.setOrganizerId(1L);

        Event event2 = new Event();
        event2.setName("Java Conference");
        event2.setOrganizerId(2L);

        when(eventRepository.findAll())
                .thenReturn(List.of(event1, event2));

        List<EventResponseDTO> result =
                eventService.getAllEvents();

        assertEquals(2, result.size());

        assertEquals(
                "Spring Boot Workshop",
                result.get(0).getName()
        );

        assertEquals(
                "Java Conference",
                result.get(1).getName()
        );

        verify(eventRepository)
                .findAll();
    }

    @Test
    void shouldThrowExceptionWhenEventNotFound() {

        when(eventRepository.findById(99L))
                .thenReturn(Optional.empty());

        EventNotFoundException exception =
                assertThrows(
                        EventNotFoundException.class,
                        () -> eventService.getEvent(99L)
                );

        assertEquals(
                "Event not found: 99",
                exception.getMessage()
        );

        verify(eventRepository)
                .findById(99L);
    }
}