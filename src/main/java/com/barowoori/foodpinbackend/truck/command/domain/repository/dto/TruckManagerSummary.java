package com.barowoori.foodpinbackend.truck.command.domain.repository.dto;

import com.barowoori.foodpinbackend.file.command.domain.service.ImageManager;
import com.barowoori.foodpinbackend.truck.command.domain.model.TruckManagerRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class TruckManagerSummary {
    private String truckManagerId;
    private String nickname;
    private String phone;
    private String image;
    private TruckManagerRole role;


    public TruckManagerSummary convertToPreSignedUrl(ImageManager imageManager){
        return TruckManagerSummary.builder()
                .truckManagerId(this.truckManagerId)
                .nickname(this.nickname)
                .phone(this.phone)
                .image(imageManager.getPreSignUrl(this.image))
                .role(this.role)
                .build();
    }
}
