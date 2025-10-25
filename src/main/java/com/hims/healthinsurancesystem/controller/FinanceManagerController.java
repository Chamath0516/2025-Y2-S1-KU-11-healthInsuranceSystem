package com.hims.healthinsurancesystem.controller;

import com.hims.healthinsurancesystem.dto.*;
import com.hims.healthinsurancesystem.model.*;
import com.hims.healthinsurancesystem.repository.PremiumPaymentRepository;
import com.hims.healthinsurancesystem.repository.PendingRefundRepository;
import com.hims.healthinsurancesystem.repository.DeniedPaymentRepository;
import com.hims.healthinsurancesystem.repository.DeniedRefundRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/finance")
public class FinanceManagerController {

    @Autowired
    private PremiumPaymentRepository premiumPaymentRepository;

    @Autowired
    private PendingRefundRepository pendingRefundRepository;

    @Autowired
    private DeniedPaymentRepository deniedPaymentRepository;

    @Autowired
    private DeniedRefundRepository deniedRefundRepository;

    @GetMapping("/dashboard")
    public String financeDashboard(Model model) {
        // Add any data needed for the finance dashboard
        // map entities to DTOs and expose as dto.premium_payment
        List<PremiumPayment> recentPayments = premiumPaymentRepository.findAll();

        // Filter to only the earliest payment per customer+month
        Map<String, PremiumPayment> earliestPerCustomerMonth = new HashMap<>();
        for (PremiumPayment p : recentPayments) {
            String customerKey = p.getCustomer() != null && p.getCustomer().getId() != null ? String.valueOf(p.getCustomer().getId()) : "unknown_" + (p.getId() != null ? p.getId() : "n") ;
            String monthKey = p.getMonth() != null ? p.getMonth() : "";
            String key = customerKey + "|" + monthKey;
            PremiumPayment existing = earliestPerCustomerMonth.get(key);
            if (existing == null) {
                earliestPerCustomerMonth.put(key, p);
            } else {
                // compare createdAt, prefer earlier
                if (p.getCreatedAt() != null && (existing.getCreatedAt() == null || p.getCreatedAt().isBefore(existing.getCreatedAt()))) {
                    earliestPerCustomerMonth.put(key, p);
                }
            }
        }
        List<PremiumPayment> filteredPayments = new java.util.ArrayList<>(earliestPerCustomerMonth.values());

        List<PremiumPaymentDto> dtoList = filteredPayments.stream().map(p -> new PremiumPaymentDto(
                p.getId(),
                p.getCustomer() != null ? p.getCustomer().getUsername() : "N/A",
                p.getInsurancePlan() != null ? p.getInsurancePlan().getName() : "N/A",
                p.getPremium(),
                p.getMonth(),
                p.getStatus(),
                p.getCreatedAt()
        )).collect(Collectors.toList());

        // map pending refunds to DTOs for dashboard
        List<PendingRefund> pendingRefunds = pendingRefundRepository.findAll();
        List<PendingRefundDto> pendingDtoList = pendingRefunds.stream().map(r -> new PendingRefundDto(
                r.getId(),
                r.getCustomer() != null ? r.getCustomer().getUsername() : "N/A",
                r.getPremium(),
                r.getMonth(),
                r.getReason(),
                r.getStatus(),
                r.getCreatedAt()
        )).collect(Collectors.toList());

        Map<String, Object> dto = new HashMap<>();
        dto.put("premium_payment", dtoList);
        dto.put("pending_refund", pendingDtoList);
        model.addAttribute("dto", dto);
        return "finance-dashboard";
    }

    @GetMapping("/payments")
    public String financePayments(Model model) {
        List<PremiumPayment> recentPayments = premiumPaymentRepository.findAll();

        // Filter to only the earliest payment per customer+month
        Map<String, PremiumPayment> earliestPerCustomerMonth = new HashMap<>();
        for (PremiumPayment p : recentPayments) {
            String customerKey = p.getCustomer() != null && p.getCustomer().getId() != null ? String.valueOf(p.getCustomer().getId()) : "unknown_" + (p.getId() != null ? p.getId() : "n") ;
            String monthKey = p.getMonth() != null ? p.getMonth() : "";
            String key = customerKey + "|" + monthKey;
            PremiumPayment existing = earliestPerCustomerMonth.get(key);
            if (existing == null) {
                earliestPerCustomerMonth.put(key, p);
            } else {
                if (p.getCreatedAt() != null && (existing.getCreatedAt() == null || p.getCreatedAt().isBefore(existing.getCreatedAt()))) {
                    earliestPerCustomerMonth.put(key, p);
                }
            }
        }
        List<PremiumPayment> filteredPayments = new java.util.ArrayList<>(earliestPerCustomerMonth.values());

        List<PendingRefund> pendingRefunds = pendingRefundRepository.findAll();

        model.addAttribute("recentPayments", filteredPayments);
        model.addAttribute("pendingRefunds", pendingRefunds);
        return "finance-payments";
    }

    @PostMapping("/payments/approve/{id}")
    public String approvePayment(@PathVariable Long id,
                                 @RequestParam(name = "redirect", required = false) String redirect,
                                 RedirectAttributes redirectAttributes) {
        Optional<PremiumPayment> opt = premiumPaymentRepository.findById(id);
        if (opt.isPresent()) {
            PremiumPayment p = opt.get();
            p.setStatus("APPROVED");
            premiumPaymentRepository.save(p);
            redirectAttributes.addFlashAttribute("successMessage", "Payment approved.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Payment not found.");
        }
        if (redirect != null && !redirect.isEmpty()) {
            return "redirect:" + redirect;
        }
        return "redirect:/finance/payments";
    }

    @PostMapping("/payments/deny/{id}")
    public String denyPayment(@PathVariable Long id,
                              @RequestParam(name = "reason", required = false) String reason,
                              @RequestParam(name = "redirect", required = false) String redirect,
                              RedirectAttributes redirectAttributes) {
        Optional<PremiumPayment> opt = premiumPaymentRepository.findById(id);
        if (opt.isPresent()) {
            PremiumPayment p = opt.get();
            DeniedPayment dp = new DeniedPayment();
            dp.setCustomer(p.getCustomer());
            dp.setInsurancePlan(p.getInsurancePlan());
            dp.setPremium(p.getPremium());
            dp.setMonth(p.getMonth());
            dp.setReason(reason != null ? reason : "Denied by finance");
            deniedPaymentRepository.save(dp);
            premiumPaymentRepository.delete(p);
            redirectAttributes.addFlashAttribute("successMessage", "Payment denied and moved to denied payments.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Payment not found.");
        }
        if (redirect != null && !redirect.isEmpty()) {
            return "redirect:" + redirect;
        }
        return "redirect:/finance/payments";
    }

    @PostMapping("/refunds/approve/{id}")
    public String approveRefund(@PathVariable Long id,
                                 @RequestParam(name = "redirect", required = false) String redirect,
                                 RedirectAttributes redirectAttributes) {
        Optional<PendingRefund> opt = pendingRefundRepository.findById(id);
        if (opt.isPresent()) {
            PendingRefund pr = opt.get();
            pr.setStatus("APPROVED");
            pendingRefundRepository.save(pr);
            redirectAttributes.addFlashAttribute("successMessage", "Refund approved.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Pending refund not found.");
        }
        if (redirect != null && !redirect.isEmpty()) {
            return "redirect:" + redirect;
        }
        return "redirect:/finance/dashboard";
    }

    @PostMapping("/refunds/deny/{id}")
    public String denyRefund(@PathVariable Long id,
                              @RequestParam(name = "reason", required = false) String reason,
                              @RequestParam(name = "redirect", required = false) String redirect,
                              RedirectAttributes redirectAttributes) {
        Optional<PendingRefund> opt = pendingRefundRepository.findById(id);
        if (opt.isPresent()) {
            PendingRefund pr = opt.get();
            DeniedRefund dr = new DeniedRefund();
            dr.setCustomer(pr.getCustomer());
            dr.setPremium(pr.getPremium());
            dr.setMonth(pr.getMonth());
            dr.setReason(reason != null ? reason : "Denied by finance");
            deniedRefundRepository.save(dr);
            pendingRefundRepository.delete(pr);
            redirectAttributes.addFlashAttribute("successMessage", "Pending refund denied and moved to denied refunds.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Pending refund not found.");
        }
        if (redirect != null && !redirect.isEmpty()) {
            return "redirect:" + redirect;
        }
        return "redirect:/finance/dashboard";
    }

    @GetMapping("/payments/report")
    public void paymentsReport(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            HttpServletResponse response) throws IOException {

        // normalize dates: if start > end, swap them
        if (startDate.isAfter(endDate)) {
            LocalDate tmp = startDate;
            startDate = endDate;
            endDate = tmp;
        }

        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);

        List<PremiumPayment> allPayments = premiumPaymentRepository.findAll();

        // Apply the same earliest-per-customer+month filter used on the dashboard
        Map<String, PremiumPayment> earliestPerCustomerMonth = new HashMap<>();
        for (PremiumPayment p : allPayments) {
            String customerKey = p.getCustomer() != null && p.getCustomer().getId() != null ? String.valueOf(p.getCustomer().getId()) : "unknown_" + (p.getId() != null ? p.getId() : "n");
            String monthKey = p.getMonth() != null ? p.getMonth() : "";
            String key = customerKey + "|" + monthKey;
            PremiumPayment existing = earliestPerCustomerMonth.get(key);
            if (existing == null) {
                earliestPerCustomerMonth.put(key, p);
            } else {
                if (p.getCreatedAt() != null && (existing.getCreatedAt() == null || p.getCreatedAt().isBefore(existing.getCreatedAt()))) {
                    earliestPerCustomerMonth.put(key, p);
                }
            }
        }
        List<PremiumPayment> filteredPayments = new java.util.ArrayList<>(earliestPerCustomerMonth.values());

        List<PremiumPayment> paymentsInRange = filteredPayments.stream()
                .filter(p -> p.getCreatedAt() != null && !p.getCreatedAt().isBefore(start) && !p.getCreatedAt().isAfter(end))
                .sorted(Comparator.comparing(PremiumPayment::getCreatedAt))
                .collect(Collectors.toList());

        // Generate PDF using PDFBox
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PDPageContentStream cs = null;
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.LETTER);
            doc.addPage(page);

            cs = new PDPageContentStream(doc, page);
            float margin = 50f;
            float yStart = page.getMediaBox().getHeight() - margin;
            float y = yStart;
            float leading = 14f;

            // Title
            cs.beginText();
            cs.setFont(PDType1Font.HELVETICA_BOLD, 16);
            cs.newLineAtOffset(margin, y);
            cs.showText("Payments Report: " + startDate.toString() + " to " + endDate.toString());
            cs.endText();

            y -= leading * 2;

            // Table header
            cs.beginText();
            cs.setFont(PDType1Font.HELVETICA_BOLD, 10);
            cs.newLineAtOffset(margin, y);
            String header = String.format("%-6s %-18s %-18s %-10s %-8s %-20s", "ID", "Customer", "Plan", "Amount", "Month", "Created At");
            cs.showText(header);
            cs.endText();

            y -= leading;

            cs.setFont(PDType1Font.HELVETICA, 10);

            if (paymentsInRange.isEmpty()) {
                cs.beginText();
                cs.newLineAtOffset(margin, y);
                cs.showText("No payments found for the selected date range.");
                cs.endText();
            } else {
                for (PremiumPayment p : paymentsInRange) {
                    if (y < margin + 40) {
                        // finish current content stream and start a new page
                        cs.close();
                        page = new PDPage(PDRectangle.LETTER);
                        doc.addPage(page);
                        cs = new PDPageContentStream(doc, page);
                        y = page.getMediaBox().getHeight() - margin;
                    }
                    cs.beginText();
                    cs.newLineAtOffset(margin, y);
                    String customer = p.getCustomer() != null ? p.getCustomer().getUsername() : "N/A";
                    String plan = p.getInsurancePlan() != null ? p.getInsurancePlan().getName() : "N/A";
                    String amount = p.getPremium() != null ? p.getPremium().toString() : "0.00";
                    String created = p.getCreatedAt() != null ? p.getCreatedAt().toString() : "";
                    String line = String.format("%-6s %-18s %-18s %-10s %-8s %-20s", p.getId(), trim(customer,18), trim(plan,18), amount, p.getMonth() != null ? p.getMonth() : "", created);
                    cs.showText(line);
                    cs.endText();
                    y -= leading;
                }
            }

            // ensure content stream is closed
            if (cs != null) cs.close();
            doc.save(baos);

            // Stream back to client
            response.setContentType("application/pdf");
            String filename = "payments-report-" + startDate.toString() + "_to_" + endDate.toString() + ".pdf";
            response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
            response.setContentLength(baos.size());
            baos.writeTo(response.getOutputStream());
            response.getOutputStream().flush();

        } catch (Exception ex) {
            // log and return 500 with a minimal message
            ex.printStackTrace();
            response.reset();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("text/plain");
            String msg = "Failed to generate PDF: " + ex.getMessage();
            response.getWriter().write(msg);
            response.getWriter().flush();
        } finally {
            try { if (cs != null) cs.close(); } catch (IOException ignored) {}
        }
    }

    private String trim(String s, int max) {
        if (s == null) return "";
        if (s.length() <= max) return s;
        return s.substring(0, max - 3) + "...";
    }
}
