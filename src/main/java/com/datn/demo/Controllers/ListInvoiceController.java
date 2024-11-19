package com.datn.demo.Controllers;

import com.datn.demo.Entities.AccountEntity;
import com.datn.demo.Entities.InvoiceEntity;
import com.datn.demo.Services.InvoiceService;
import com.datn.demo.Services.AccountService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("")
public class ListInvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private AccountService accountService;

    @GetMapping("/list")
    public String showListInvoice(Model model, HttpSession session) {
        AccountEntity acc = (AccountEntity) session.getAttribute("acc");

        if (acc != null) {
            Integer accountId = acc.getAccountId();
            List<InvoiceEntity> invoices = invoiceService.getInvoicesByAccountId(accountId);

            // Map để lưu mã QR cho từng hóa đơn
            Map<Integer, String> qrCodeMap = new HashMap<>();

            for (InvoiceEntity invoice : invoices) {
                String encryptedInvoiceId = encryptMD5(invoice.getInvoiceId().toString());
                String qrCodeUrl = "http://localhost:8080/print/" + encryptedInvoiceId;

                // Tạo mã QR cho URL và lưu vào Map
                String qrCodeBase64 = generateQRCodeBase64(qrCodeUrl);
                qrCodeMap.put(invoice.getInvoiceId(), qrCodeBase64);
            }

            model.addAttribute("invoices", invoices);
            model.addAttribute("qrCodeMap", qrCodeMap); // Thêm map chứa mã QR vào model
        } else {
            return "main/user/user-login";
        }

        return "main/user/listInvoice";
    }

    private String encryptMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : messageDigest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private String generateQRCodeBase64(String qrCodeText) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(qrCodeText, BarcodeFormat.QR_CODE, 200, 200);
            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            byte[] pngData = pngOutputStream.toByteArray();

            // Chuyển đổi ảnh mã QR thành chuỗi Base64
            String base64Image = Base64.getEncoder().encodeToString(pngData);

            return base64Image;
        } catch (WriterException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
