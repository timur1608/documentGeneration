package pdfservice.docapi.common.api.mapper;

import pdfservice.docapi.common.api.dto.OutboxEvent;
import pdfservice.docapi.common.api.dto.OutboxPayload;
import pdfservice.docapi.common.api.dto.OutboxRecord;
import tools.jackson.databind.ObjectMapper;

public class OutboxMapper {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    public static OutboxEvent mapToOutboxEvent(OutboxRecord outboxRecord){
        return OutboxEvent.builder().aggregateId(outboxRecord.aggregateId())
                .eventType(outboxRecord.eventType())
                .aggregatedType(outboxRecord.aggregatedType())
                .payload(MAPPER.readValue(outboxRecord.payload(), OutboxPayload.class))
                .build();
    }
}
