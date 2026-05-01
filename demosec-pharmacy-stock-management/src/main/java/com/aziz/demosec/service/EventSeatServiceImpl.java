package com.aziz.demosec.service;

import com.aziz.demosec.domain.User;
import com.aziz.demosec.dto.SaveSeatRequest;
import com.aziz.demosec.dto.SeatResponse;
import com.aziz.demosec.dto.SeatZoneSummaryResponse;
import com.aziz.demosec.entities.EventSeat;
import com.aziz.demosec.entities.MedicalEvent;
import com.aziz.demosec.entities.SeatStatus;
import com.aziz.demosec.repository.EventSeatRepository;
import com.aziz.demosec.repository.MedicalEventRepository;
import com.aziz.demosec.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventSeatServiceImpl implements IEventSeatService {

    private final EventSeatRepository seatRepository;
    private final MedicalEventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    public List<SeatResponse> getEventSeats(Long eventId) {
        return seatRepository.findByEventId(eventId).stream().map(this::toResponse).toList();
    }

    @Override
    public List<SeatZoneSummaryResponse> getEventSeatSummary(Long eventId) {
        return seatRepository.getSeatSummaryByZone(eventId);
    }

    @Override
    @Transactional
    public void saveSeatsBatch(Long eventId, List<SaveSeatRequest> requests) {
        MedicalEvent event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        for (SaveSeatRequest req : requests) {
            EventSeat seat;
            if (req.getId() != null) {
                seat = seatRepository.findById(req.getId())
                        .orElseThrow(() -> new RuntimeException("Seat not found"));
            } else {
                seat = new EventSeat();
                seat.setEvent(event);
            }
            seat.setZoneName(req.getZoneName());
            seat.setSeatLabel(req.getSeatLabel());
            seat.setPosX(req.getPosX());
            seat.setPosY(req.getPosY());
            
            if (seat.getStatus() == null || seat.getStatus() == SeatStatus.AVAILABLE || seat.getStatus() == SeatStatus.BLOCKED) {
               seat.setStatus(SeatStatus.valueOf(req.getStatus()));
            }

            seatRepository.save(seat);
        }
    }

    @Override
    @Transactional
    public void reserveSeat(Long seatId, String userEmail) {
        EventSeat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new RuntimeException("Seat not found"));
        
        if (seat.getStatus() != SeatStatus.AVAILABLE) {
            throw new RuntimeException("Seat " + seat.getSeatLabel() + " is not available");
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        seat.setStatus(SeatStatus.RESERVED);
        seat.setReservedBy(user);
        seatRepository.save(seat);
    }

    @Override
    @Transactional
    public void releaseSeat(Long seatId) {
        EventSeat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new RuntimeException("Seat not found"));
        
        seat.setStatus(SeatStatus.AVAILABLE);
        seat.setReservedBy(null);
        seatRepository.save(seat);
    }

    private SeatResponse toResponse(EventSeat s) {
        return SeatResponse.builder()
                .id(s.getId())
                .eventId(s.getEvent().getId())
                .zoneName(s.getZoneName())
                .seatLabel(s.getSeatLabel())
                .posX(s.getPosX())
                .posY(s.getPosY())
                .status(s.getStatus())
                .reservedByFullName(s.getReservedBy() != null ? s.getReservedBy().getFullName() : null)
                .build();
    }
}
