package com.fleetguard360.adminpanel.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fleetguard360.adminpanel.model.FleetReport;
import com.itextpdf.text.*;
import com.itextpdf.text.DocumentException;

import java.util.List;
// Elimina este import para evitar la ambigüedad
// import com.itextpdf.text.Font;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

@Service
public class ExportService {

    public void exportReportToPdf(FleetReport report, HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=" + report.getReportName().replaceAll("\\s+", "_") + ".pdf";
        response.setHeader(headerKey, headerValue);

        Document document = new Document(PageSize.A4);
        try {
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            // Add title
            // Usa el nombre completo para Font de iText
            com.itextpdf.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph(report.getReportName(), titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(Chunk.NEWLINE);

            // Add report metadata
            // Usa el nombre completo para Font de iText
            com.itextpdf.text.Font metaFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            document.add(new Paragraph("Report Type: " + report.getReportType(), metaFont));
            document.add(new Paragraph("Report Date: " + report.getReportDate().format(formatter), metaFont));
            document.add(new Paragraph("Period: " + report.getStartDate().format(formatter) + " to " + report.getEndDate().format(formatter), metaFont));
            document.add(new Paragraph("Created By: " + report.getCreatedBy().getFullName(), metaFont));
            document.add(Chunk.NEWLINE);

            // Parse JSON data and create table
            try {
                JSONObject jsonData = new JSONObject(report.getReportData());

                // If data contains a table
                if (jsonData.has("tableData") && jsonData.get("tableData") instanceof JSONArray) {
                    JSONArray tableData = jsonData.getJSONArray("tableData");
                    if (tableData.length() > 0) {
                        PdfPTable table = new PdfPTable(tableData.getJSONObject(0).length());
                        table.setWidthPercentage(100);

                        // Add headers
                        JSONObject firstRow = tableData.getJSONObject(0);
                        // Usa el nombre completo para Font de iText
                        com.itextpdf.text.Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
                        for (String key : firstRow.keySet()) {
                            PdfPCell headerCell = new PdfPCell(new Phrase(key, headerFont));
                            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            headerCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                            table.addCell(headerCell);
                        }

                        // Add data rows
                        for (int i = 0; i < tableData.length(); i++) {
                            JSONObject row = tableData.getJSONObject(i);
                            for (String key : row.keySet()) {
                                table.addCell(row.get(key).toString());
                            }
                        }

                        document.add(table);
                    }
                }

                // Add any additional sections from the JSON data
                if (jsonData.has("summary") && jsonData.get("summary") instanceof String) {
                    document.add(Chunk.NEWLINE);
                    // Usa el nombre completo para Font de iText
                    com.itextpdf.text.Font summaryFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
                    document.add(new Paragraph("Summary", summaryFont));
                    document.add(new Paragraph(jsonData.getString("summary"), metaFont));
                }

            } catch (Exception e) {
                document.add(new Paragraph("Error parsing report data: " + e.getMessage(), metaFont));
            }

        } catch (DocumentException e) {
            throw new IOException("Error creating PDF document", e);
        } finally {
            document.close();
        }
    }

    public void exportReportToExcel(FleetReport report, HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=" + report.getReportName().replaceAll("\\s+", "_") + ".xlsx";
        response.setHeader(headerKey, headerValue);

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(report.getReportName());

            // Create header row for metadata
            Row headerRow = sheet.createRow(0);
            CellStyle headerStyle = workbook.createCellStyle();

            // Aquí está el problema - usa Font de Apache POI, no de iText
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            Cell headerCell = headerRow.createCell(0);
            headerCell.setCellValue("Report Name");
            headerCell.setCellStyle(headerStyle);


        }
    }

    public void exportAllReportsToPdf(List<FleetReport> reports, HttpServletResponse response) throws IOException, DocumentException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=all_reports.pdf");

        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        // Título
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        Paragraph title = new Paragraph("Todos los Reportes de Flota", titleFont);
        title.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(title);
        document.add(Chunk.NEWLINE);

        // Para cada reporte, aplicar el mismo formato que en export individual
        for (FleetReport report : reports) {
            // Nombre del reporte
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            Paragraph reportTitle = new Paragraph(report.getReportName(), headerFont);
            document.add(reportTitle);

            document.add(new Paragraph("Report Type: " + report.getReportType()));
            document.add(new Paragraph("Report Date: " + report.getReportDate()));
            document.add(new Paragraph("Period: " + report.getStartDate() + " to " + report.getEndDate()));
            document.add(new Paragraph("Created By: " + report.getCreatedBy().getFullName()));
            document.add(Chunk.NEWLINE);

            // Parsear los datos del reporte
            if (report.getReportData() != null && report.getReportData().contains("tableData")) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode jsonData = objectMapper.readTree(report.getReportData());
                    JsonNode tableData = jsonData.get("tableData");

                    if (tableData != null && tableData.isArray()) {
                        PdfPTable table = new PdfPTable(2);
                        table.setWidthPercentage(100);
                        Stream.of("Placa", "Velocidad").forEach(columnTitle -> {
                            PdfPCell header = new PdfPCell();
                            header.setPhrase(new Phrase(columnTitle));
                            table.addCell(header);
                        });

                        for (JsonNode row : tableData) {
                            table.addCell(row.get("Placa").asText());
                            table.addCell(row.get("Velocidad").asText());
                        }
                        document.add(table);
                    }

                    // Agregar resumen
                    if (jsonData.has("summary")) {
                        Font summaryFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
                        document.add(new Paragraph("Summary", summaryFont));
                        document.add(new Paragraph(jsonData.get("summary").asText()));
                    }

                } catch (Exception e) {
                    document.add(new Paragraph("Error parsing report data"));
                }
            }

            document.add(Chunk.NEWLINE);
            document.add(new LineSeparator());
            document.add(Chunk.NEWLINE);
        }

        document.close();
    }

        }

