package com.resumechecker.service;

import java.io.InputStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * FILE PARSER SERVICE
 * Extracts plain text from uploaded resume files.
 * Supports: PDF (.pdf) and Word (.docx) formats.
 *
 * HOW IT WORKS:
 * - PDF: Uses Apache PDFBox to read PDF content
 * - DOCX: Uses Apache POI to read Word document content
 * - Plain text: Just reads the raw text directly
 */
@Service
public class FileParserService {

    /**
     * Main method: takes an uploaded file and returns its text content
     *
     * @param file - the uploaded resume file from the React frontend
     * @return plain text content of the resume
     */
    public String extractText(MultipartFile file) throws Exception {
        String filename = file.getOriginalFilename();

        if (filename == null || filename.isBlank()) {
            throw new IllegalArgumentException("File has no name.");
        }

        String lowerName = filename.toLowerCase();

        // Route to the right parser based on file extension
        if (lowerName.endsWith(".pdf")) {
            return extractFromPdf(file);
        } else if (lowerName.endsWith(".docx")) {
            return extractFromDocx(file);
        } else if (lowerName.endsWith(".txt")) {
            return new String(file.getBytes());
        } else {
            throw new IllegalArgumentException(
                "Unsupported file type: " + filename +
                ". Please upload a PDF, DOCX, or TXT file."
            );
        }
    }

    /**
     * Extract text from a PDF resume using Apache PDFBox
     */
    private String extractFromPdf(MultipartFile file) throws Exception {
    try (InputStream is = file.getInputStream();
         PDDocument document = org.apache.pdfbox.Loader.loadPDF(is.readAllBytes())) {

        PDFTextStripper stripper = new PDFTextStripper();
        String text = stripper.getText(document);

        if (text == null || text.isBlank()) {
            throw new RuntimeException(
                "Could not extract text from PDF. " +
                "The PDF might be image-based (scanned). " +
                "Please use a text-based PDF or paste your resume text instead."
            );
        }

        return text.trim();
    }
}

    /**
     * Extract text from a DOCX resume using Apache POI
     */
    private String extractFromDocx(MultipartFile file) throws Exception {
        try (InputStream is = file.getInputStream();
             XWPFDocument document = new XWPFDocument(is)) {

            StringBuilder text = new StringBuilder();

            // Loop through every paragraph in the Word document
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                String paraText = paragraph.getText();
                if (paraText != null && !paraText.isBlank()) {
                    text.append(paraText).append("\n");
                }
            }

            if (text.isEmpty()) {
                throw new RuntimeException(
                    "Could not extract text from DOCX file. " +
                    "Please try pasting your resume text instead."
                );
            }

            return text.toString().trim();
        }
    }
}
