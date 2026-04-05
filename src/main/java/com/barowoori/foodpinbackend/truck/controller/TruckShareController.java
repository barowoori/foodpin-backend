package com.barowoori.foodpinbackend.truck.controller;

import com.barowoori.foodpinbackend.file.command.domain.service.ImageManager;
import com.barowoori.foodpinbackend.truck.command.application.service.TruckService;
import com.barowoori.foodpinbackend.truck.command.domain.model.Truck;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class TruckShareController {
    private final TruckService truckService;
    private final ImageManager imageManager;

    public TruckShareController(TruckService truckService, ImageManager imageManager) {
        this.truckService = truckService;
        this.imageManager = imageManager;
    }

    @GetMapping("/share/truck/{id}")
    public String shareTruck(@PathVariable String id, Model model) {
        Truck truck = truckService.getTruckById(id);

        model.addAttribute("title", "푸드핀");
        model.addAttribute("description", truck.getName());
        model.addAttribute("image", getImage(truck.getTruckMainPhotoUrl(imageManager)));
        model.addAttribute("url", "https://dev.barowoori.click/share/truck/" + id);
        model.addAttribute("deepLink", "foodpin://truck?truckId=" + id);
        return "share/default";
    }

    private String getImage(String truckImageUrl) {
        if (truckImageUrl != null) {
            return truckImageUrl;
        }
        return getDefaultImage();
    }

    private String getDefaultImage() {
        return "https://barowoori-bucket.s3.ap-northeast-2.amazonaws.com/default/FoodFinDefaultImage.png";
    }


    @GetMapping("/share/inviting/truck/{id}/code/{code}")
    public String shareInvitingTruck(@PathVariable String id, @PathVariable String code, Model model) {
        Truck truck = truckService.getTruckById(id);

        model.addAttribute("title", "푸드핀");
        model.addAttribute("description", truck.getName() + " 운영자 초대");
        model.addAttribute("image", getDefaultImage());
        model.addAttribute("url", "https://dev.barowoori.click/share/inviting/truck/" + id + "/code/" + code);
        model.addAttribute("deepLink", "foodpin://owner?truckId=" + id + "&" + "code=" + code);
        return "share/default";
    }

}
