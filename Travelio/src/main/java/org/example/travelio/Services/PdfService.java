package org.example.travelio.Services;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import org.example.travelio.DTO.ItineraryDTO;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class PdfService {

    public byte[] generateItineraryPdf(ItineraryDTO.TripPlan plan) {
        // 1. Create a Byte Array Output Stream to hold the PDF data in memory
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            // 2. Create Document instance
            Document document = new Document();
            PdfWriter.getInstance(document, out);

            document.open();

            // 3. Add Title
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
            Paragraph title = new Paragraph("Your Azerbaijan Travel Itinerary", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // 4. Add Summary
            Font summaryFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 12);
            Paragraph summary = new Paragraph(plan.getTripSummary(), summaryFont);
            summary.setSpacingAfter(20);
            document.add(summary);

            // 5. Iterate over Days
            Font dayFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Font slotFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

            for (ItineraryDTO.Day day : plan.getDays()) {
                // Day Header
                Paragraph dayHeader = new Paragraph("Day " + day.getDay() + ": " + day.getCity(), dayFont);
                dayHeader.setSpacingBefore(10);
                dayHeader.setSpacingAfter(10);
                document.add(dayHeader);

                // Time Slots
                for (ItineraryDTO.Slot slot : day.getItinerary()) {
                    Paragraph slotHeader = new Paragraph(slot.getTimeSlot(), slotFont);
                    slotHeader.setIndentationLeft(20);
                    document.add(slotHeader);

                    // Activities
                    com.lowagie.text.List list = new com.lowagie.text.List(com.lowagie.text.List.UNORDERED);
                    list.setIndentationLeft(40);

                    for (ItineraryDTO.Activity activity : slot.getActivities()) {
                        ListItem item = new ListItem(activity.getPlaceName() + ": " + activity.getDescription(), normalFont);
                        list.add(item);
                    }
                    document.add(list);
                }
                document.add(new Paragraph(" ")); // Spacer
            }

            document.close();

        } catch (DocumentException e) {
            throw new RuntimeException("Error generating PDF", e);
        }

        return out.toByteArray();
    }
}