package com.example.demo.api;

import com.example.demo.entity.KoiFishOrder;
import com.example.demo.model.Request.BookingTotalRequest;
import com.example.demo.model.Request.KoiFishOrderTotalRequest;
import com.example.demo.service.BookingService;
import com.example.demo.service.KoiFishOrderService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin("*")
@SecurityRequirement(name = "api")
public class DashBoardAPI {
    @Autowired
    BookingService bookingService;
    @Autowired
    KoiFishOrderService koiFishOrderService;

    @PostMapping("/total-booking")
    public ResponseEntity<Long> getTotalBookings(@RequestBody @Valid BookingTotalRequest bookingTotalRequest) {
        int month = bookingTotalRequest.getMonth();
        int year = bookingTotalRequest.getYear();
        Long totalBookings = bookingService.getTotalBookingsByMonthAndYear(month, year);
        return ResponseEntity.ok(totalBookings);
    }

    @PostMapping("/total-price")
    public ResponseEntity<Long> getTotalPrice(@RequestBody @Valid BookingTotalRequest bookingTotalRequest) {
        int month = bookingTotalRequest.getMonth();
        int year = bookingTotalRequest.getYear();
        Long totalPrice = bookingService.getTotalPriceByMonthAndYear(month, year);
        return ResponseEntity.ok(totalPrice);
    }

    @PostMapping("/total/deleted")
    public ResponseEntity<Long> getTotalDeletedBookings(@RequestBody @Valid BookingTotalRequest bookingTotalRequest) {
        int month = bookingTotalRequest.getMonth();
        int year = bookingTotalRequest.getYear();
        Long totalDeletedBookings = bookingService.getTotalDeletedBookingsByMonthAndYear(month, year);
        return ResponseEntity.ok(totalDeletedBookings);
    }

    @PostMapping("/total-order")
    public ResponseEntity<Double> getTotalOrderAmount(@RequestBody @Valid KoiFishOrderTotalRequest orderTotalRequest) {
        Integer month = orderTotalRequest.getMonth();
        Integer year = orderTotalRequest.getYear();
        Double totalAmount = koiFishOrderService.getTotalOrderAmountByMonthAndYear(month, year);
        return ResponseEntity.ok(totalAmount);
    }

    @PostMapping("/calculateTotalOrderAmount")
    public ResponseEntity<Double> calculateTotalOrderAmountForMonthAndYear(@RequestBody @Valid KoiFishOrderTotalRequest orderTotalRequest){
        Integer month = orderTotalRequest.getMonth();
        Integer year = orderTotalRequest.getYear();
        Double totalAmount = koiFishOrderService.getCalculateTotalOrderAmountForMonthAndYear(month, year);
        return ResponseEntity.ok(totalAmount);
    }

    @PostMapping("/count")
    public ResponseEntity<Long> getTotalOrders(@RequestBody @Valid KoiFishOrderTotalRequest orderTotalRequest) {
        int month = orderTotalRequest.getMonth();
        int year = orderTotalRequest.getYear();
        Long totalOrders = koiFishOrderService.getTotalOrdersByMonthAndYear(month, year);
        return ResponseEntity.ok(totalOrders);
    }

    @PostMapping("/count/deleted")
    public ResponseEntity<Long> getTotalDeletedOrders(@RequestBody @Valid KoiFishOrderTotalRequest orderTotalRequest) {
        int month = orderTotalRequest.getMonth();
        int year = orderTotalRequest.getYear();
        Long totalDeletedOrders = koiFishOrderService.getTotalDeletedOrdersByMonthAndYear(month, year);
        return ResponseEntity.ok(totalDeletedOrders);
    }
}