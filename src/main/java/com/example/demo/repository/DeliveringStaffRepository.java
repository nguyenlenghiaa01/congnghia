package com.example.demo.repository;

import com.example.demo.entity.DeliveringStaff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeliveringStaffRepository extends JpaRepository <DeliveringStaff, String> {
    DeliveringStaff findDeliveringStaffById(String DeliveringId);
    List<DeliveringStaff> findDeliveringStaffsByIsDeletedFalse();
}