package com.example.demo.service;

import com.example.demo.entity.KoiFish;
import com.example.demo.exception.DuplicateEntity;
import com.example.demo.exception.NotFoundException;
import com.example.demo.model.Request.KoiFishRequest;
import com.example.demo.model.Response.KoiFishResponse;
import com.example.demo.repository.BreedRepository;
import com.example.demo.repository.FarmRepository;
import com.example.demo.repository.KoiRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Service // danh dau day la mot lop xu ly logic
public class KoiFishService {
    // xu ly nhung logic lien qua
    private ModelMapper modelMapper = new ModelMapper();
    @Autowired
    KoiRepository koiRepository;

    @Autowired
    BreedRepository breedRepository;

    @Autowired
    FarmRepository farmRepository;

    public KoiFish createNewKoi(KoiFishRequest koiFishRequest) {
        // Tạo đối tượng KoiFish mới
        KoiFish koiFish = new KoiFish();

        // Thiết lập thuộc tính breed và farm
        koiFish.setBreed(breedRepository.findById(koiFishRequest.getBreedId())
                .orElseThrow(() -> new NotFoundException("Breed not exist")));

        koiFish.setFarm(farmRepository.findById(koiFishRequest.getFarmId())
                .orElseThrow(() -> new NotFoundException("Farm not exist")));

         koiFish.setName(koiFishRequest.getName());
         koiFish.setImage(koiFishRequest.getImage());
         koiFish.setDescription(koiFishRequest.getDescription());

        try {
            // Lưu và trả về KoiFish mới
            return koiRepository.save(koiFish);
        } catch (Exception e) {
            throw new DuplicateEntity("Duplicate Koi id !");
        }
    }


    public KoiFishResponse getAllKoi(@RequestParam int page, @RequestParam int size){
        Page fishPage = koiRepository.findAll(PageRequest.of(page, size));
        List<KoiFish> koiFishes = fishPage.getContent();
        List<KoiFish> activeKoiFish = new ArrayList<>();

        for(KoiFish fish : koiFishes) {
            if(!fish.isDeleted()) {
                activeKoiFish.add(fish);
            }
        }

        KoiFishResponse koiFishResponse = new KoiFishResponse();

        koiFishResponse.setListFish(activeKoiFish);
        koiFishResponse.setPageNumber(fishPage.getNumber());
        koiFishResponse.setTotalElements(fishPage.getTotalElements());
        koiFishResponse.setTotalPages(fishPage.getTotalPages());

        return koiFishResponse;
    }
    public KoiFish updateKoiFish(KoiFishRequest koi, long id){
        // buoc 1: tim toi thang student co id nhu la FE cung cap
        KoiFish oldKoi = koiRepository.findKoiById(id);
        if(oldKoi ==null){
            throw new NotFoundException("Koi not found !");//dung viec xu ly ngay tu day
        }
        //=> co farm co ton tai
        oldKoi.setFarm(farmRepository.findById(koi.getFarmId()).orElseThrow(() -> new NotFoundException("Farm not exist")));
        oldKoi.setBreed(breedRepository.findById(koi.getBreedId()).orElseThrow(() -> new NotFoundException("Breed not exist")));
        oldKoi.setName(koi.getName());
        oldKoi.setImage(koi.getImage());
        return koiRepository.save(oldKoi);
    }
    public KoiFish deleteKoi(long id){
        KoiFish oldKoi = koiRepository.findKoiById(id);
        if(oldKoi ==null){
            throw new NotFoundException("Koi not found !");//dung viec xu ly ngay tu day
        }
        oldKoi.setDeleted(true);
        return koiRepository.save(oldKoi);
    }
}
