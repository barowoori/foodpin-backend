package com.barowoori.foodpinbackend.event.controller;


import com.barowoori.foodpinbackend.common.dto.CommonResponse;
import com.barowoori.foodpinbackend.event.command.application.dto.RequestEvent;
import com.barowoori.foodpinbackend.event.command.application.service.EventService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "행사 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/events")
public class EventController {
    private final EventService eventService;

    @PostMapping(value = "/v1")
    public ResponseEntity<CommonResponse<String>> createEvent(@Valid @RequestBody RequestEvent.CreateEventDto createEventDto) {
        eventService.createEvent(createEventDto);
        CommonResponse<String> commonResponse = CommonResponse.<String>builder()
                .data("Event created successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @PutMapping(value = "/v1/{eventId}/info")
    public ResponseEntity<CommonResponse<String>> updateEventInfo(@Valid @PathVariable("eventId") String eventId,
                                                                  @RequestBody RequestEvent.UpdateEventInfoDto updateEventInfoDto) {
        eventService.updateEventInfo(eventId, updateEventInfoDto);
        CommonResponse<String> commonResponse = CommonResponse.<String>builder()
                .data("Event info updated successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @PutMapping(value = "/v1/{eventId}/recruit")
    public ResponseEntity<CommonResponse<String>> updateEventRecruit(@Valid @PathVariable("eventId") String eventId,
                                                                     @RequestBody RequestEvent.UpdateEventRecruitDto updateEventRecruitDto) {
        eventService.updateEventRecruit(eventId, updateEventRecruitDto);
        CommonResponse<String> commonResponse = CommonResponse.<String>builder()
                .data("Event recruit info updated successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @PutMapping(value = "/v1/{eventId}/detail")
    public ResponseEntity<CommonResponse<String>> updateEventDetail(@Valid @PathVariable("eventId") String eventId,
                                                                    @RequestBody RequestEvent.UpdateEventDetailDto updateEventDetailDto) {
        eventService.updateEventDetail(eventId, updateEventDetailDto);
        CommonResponse<String> commonResponse = CommonResponse.<String>builder()
                .data("Event detail info updated successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @PutMapping(value = "/v1/{eventId}/document")
    public ResponseEntity<CommonResponse<String>> updateEventDocument(@Valid @PathVariable("eventId") String eventId,
                                                                      @RequestBody RequestEvent.UpdateEventDocumentDto updateEventDocumentDto) {
        eventService.updateEventDocument(eventId, updateEventDocumentDto);
        CommonResponse<String> commonResponse = CommonResponse.<String>builder()
                .data("Event document info updated successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }
}
