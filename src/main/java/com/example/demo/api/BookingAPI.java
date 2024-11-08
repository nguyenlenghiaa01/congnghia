package com.example.demo.api;

import com.example.demo.entity.Booking;
import com.example.demo.entity.KoiFishOrder;
import com.example.demo.model.Request.BookingRequest;
import com.example.demo.model.Response.BookingResponse;
import com.example.demo.model.Response.BookingResponses;
import com.example.demo.model.Response.BookingsResponse;
import com.example.demo.model.Response.DataResponse;
import com.example.demo.service.BookingService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/booking")
@CrossOrigin("*")
@SecurityRequirement(name="api")
public class BookingAPI {
    @Autowired
    BookingService bookingService;
    @PostMapping("/VNPayAvailableTour")
    public ResponseEntity<?> createVNPay(String id) {
        String vnPayUrl = null;
        try {
            vnPayUrl = bookingService.createUrlAvailableTour(id);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return ResponseEntity.ok(vnPayUrl);
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody BookingRequest bookingRequest) {
        Booking newBooking =bookingService.createNewBooking(bookingRequest);
        return ResponseEntity.ok(newBooking);
    }
//    @PostMapping("/total")
//    public ResponseEntity<Long> getTotalBookings(@RequestBody @Valid BookingTotalRequest bookingTotalRequest) {
//        int month = bookingTotalRequest.getMonth();
//        int year = bookingTotalRequest.getYear();
//        Long totalBookings = bookingService.getTotalBookingsByMonthAndYear(month, year);
//        return ResponseEntity.ok(totalBookings);
//    }
//    @PostMapping("/total-price")
//    public ResponseEntity<Long> getTotalPrice(@RequestBody @Valid BookingTotalRequest bookingTotalRequest) {
//        int month = bookingTotalRequest.getMonth();
//        int year = bookingTotalRequest.getYear();
//        Long totalPrice = bookingService.getTotalPriceByMonthAndYear(month, year);
//        return ResponseEntity.ok(totalPrice);
//    }
//    @PostMapping("/total/deleted")
//    public ResponseEntity<Long> getTotalDeletedBookings(@RequestBody @Valid BookingTotalRequest bookingTotalRequest) {
//        int month = bookingTotalRequest.getMonth();
//        int year = bookingTotalRequest.getYear();
//        Long totalDeletedBookings = bookingService.getTotalDeletedBookingsByMonthAndYear(month, year);
//        return ResponseEntity.ok(totalDeletedBookings);
//    }


    @GetMapping
    public ResponseEntity<DataResponse<BookingsResponse>> get(@RequestParam int page,
                                                              @RequestParam int size) {
        DataResponse<BookingsResponse> bookingResponse = bookingService.getAllBooking(page, size);
        return ResponseEntity.ok(bookingResponse);
    }
    @GetMapping("/idQuotation")
    public ResponseEntity<Booking> get(long id){
        Booking booking = bookingService.getQuotation(id);
        return ResponseEntity.ok(booking);
    }
    @PostMapping("/transaction")
    public ResponseEntity<?> create(@RequestParam String id) throws Exception{
        bookingService.createTransactionId(id);
        return ResponseEntity.ok("Success");
    }

    @GetMapping("/customerId")
    public ResponseEntity<DataResponse<BookingResponses>> getCustomerBooking(@RequestParam int page,
                                                                             @RequestParam int size,
                                                                             @RequestParam String id) {
        DataResponse<BookingResponses> bookingResponse = bookingService.getBookingByCustomer(page, size,id);
        return ResponseEntity.ok(bookingResponse);
    }

    @PutMapping("/updateStatus")
    public ResponseEntity<Booking> updateStatus(String id){
        Booking booking =bookingService.updateStatus(id);
        return  ResponseEntity.ok(booking);
    }

    @GetMapping("/history")
    public ResponseEntity<DataResponse<BookingResponse>> getHistory(@RequestParam int page,
                                                                    @RequestParam int size,
                                                                    @RequestParam long id) {
        DataResponse<BookingResponse> bookingResponse = bookingService.getAllBookingCustomer(page, size, id);
        return ResponseEntity.ok(bookingResponse);
    }
    @GetMapping("/Consulting")
    public ResponseEntity<DataResponse<BookingResponse>> getBooking(@RequestParam int page,
                                                                    @RequestParam int size,
                                                                    @RequestParam long id) {
        DataResponse<BookingResponse> bookingResponse = bookingService.getAllBookingByConsulting(page, size, id);
        return ResponseEntity.ok(bookingResponse);
    }

//    @PutMapping("{id}")
//    public ResponseEntity<?> updateBooking(@Valid @RequestBody BookingRequest booking, @PathVariable long id){//valid kich hoat co che vadilation
//        Booking newBooking = bookingService.updateBooking(booking,id);
//        return ResponseEntity.ok(newBooking);
//    }


    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteBooking(@PathVariable long id){
        Booking newBooking = bookingService.deleteBooking(id);
        return ResponseEntity.ok(newBooking);
    }
}
