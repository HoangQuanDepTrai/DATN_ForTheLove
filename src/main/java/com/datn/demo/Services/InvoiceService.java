package com.datn.demo.Services;

import com.datn.demo.Entities.InvoiceEntity;
import com.datn.demo.Repositories.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    public List<InvoiceEntity> findAllWithDetails() {
        return invoiceRepository.findAllWithDetails();
    }

    public List<InvoiceEntity> findAll() {
        return invoiceRepository.findAll();
    }

    // Phương thức để lấy danh sách hóa đơn theo accountId và đảo ngược danh sách
    public List<InvoiceEntity> getInvoicesByAccountId(Integer accountId) {
        List<InvoiceEntity> invoices = invoiceRepository.findByAccount_AccountId(accountId);
        Collections.reverse(invoices); // Đảo ngược danh sách
        return invoices;
    }
}