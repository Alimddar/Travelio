package org.example.travelio.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OnboardingFunnelResponse {
    private long step1Users;
    private long step2Users;
    private long step3Users;
    private long completedUsers;
    private double dropOffRate;
}