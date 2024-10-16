package com.example.demo.service;

import com.example.demo.entity.*;
import com.example.demo.exception.NotFoundException;
import com.example.demo.model.Request.KoiFishOrderRequest;
import com.example.demo.model.Response.OrderResponse;
import com.example.demo.repository.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class KoiFishOrderService {

    @Autowired
    private KoiFishOrderRepository koiFishOrderRepository;

    @Autowired
    private KoiRepository koiRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    BreedRepository breedRepository;

    @Autowired
    TransactionsRepository transactionsRepository;

    @Autowired
    private AuthenticationService authenticationService;
    private final ModelMapper modelMapper = new ModelMapper();

    public KoiFishOrder createNewOrder(KoiFishOrderRequest koiFishOrderRequest) {
        KoiFishOrder orders = modelMapper.map(koiFishOrderRequest, KoiFishOrder.class);
        Account currentAccount = authenticationService.getCurrentAccount();
        if (currentAccount == null) {
            throw new RuntimeException("Customer account not found.");
        }
        orders.setCustomer(currentAccount);
        orders.setCreateAt(new Date());
        orders.setId(koiFishOrderRequest.getId());
        KoiFish koi = koiRepository.findKoiById(koiFishOrderRequest.getId());
            orders.setTotal(orders.getTotal());
        return koiFishOrderRepository.save(orders);
    }

    public Double getTotalOrderAmountByMonthAndYear(int month, int year) {
        return koiFishOrderRepository.findTotalOrderAmountByMonthAndYear(month, year);
    }
    public Double getCalculateTotalOrderAmountForMonthAndYear(int month, int year) {
        Double totalAmount = koiFishOrderRepository.findTotalOrderAmountByMonthAndYear(month, year);
        if (totalAmount == null) {
            return 0.0;
        }
        return totalAmount * 0.10;
    }

    public Long getTotalOrdersByMonthAndYear(int month,int year){
        return koiFishOrderRepository.countOrdersByMonthAndYear(month,year);
    }

    public Long getTotalDeletedOrdersByMonthAndYear(int month,int year){
        return koiFishOrderRepository.countDeletedOrdersByMonthAndYear(month,year);
    }

    public OrderResponse getAllOrder(int page, int size){
        Page orderPage = koiFishOrderRepository.findAll(PageRequest.of(page, size));
        List<KoiFishOrder> koiFishOrders = orderPage.getContent();
        List<KoiFishOrder> activeKoiFishOrders = new ArrayList<>();

        for (KoiFishOrder order : koiFishOrders) {
            if(!order.isDeleted()) {
                activeKoiFishOrders.add(order);
            }
        }

        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setListKoiFishOrder(activeKoiFishOrders);
        orderResponse.setPageNumber(orderPage.getNumber());
        orderResponse.setTotalElements(orderPage.getTotalElements());
        orderResponse.setTotalPages(orderPage.getTotalPages());

        return  orderResponse;
    }

    public KoiFishOrder updateOrder(KoiFishOrderRequest shoppingCartRequest, long id) {
        KoiFishOrder oldKoiFishOrder = koiFishOrderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order not found!"));

        KoiFish koi = koiRepository.findKoiById(shoppingCartRequest.getId());

        // Update
        oldKoiFishOrder.setId(shoppingCartRequest.getId());
        oldKoiFishOrder.setTotal(shoppingCartRequest.getTotal());

        return koiFishOrderRepository.save(oldKoiFishOrder);
    }




    public KoiFishOrder deleteOrderCart(long id) {
        KoiFishOrder oldKoiFishOrder = koiFishOrderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order not found!"));

        oldKoiFishOrder.setDeleted(true);
        return koiFishOrderRepository.save(oldKoiFishOrder);
    }


}

