package com.driver.services.impl;

import com.driver.model.ParkingLot;
import com.driver.model.Spot;
import com.driver.model.SpotType;
import com.driver.repository.ParkingLotRepository;
import com.driver.repository.SpotRepository;
import com.driver.services.ParkingLotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class ParkingLotServiceImpl implements ParkingLotService {
    @Autowired
    ParkingLotRepository parkingLotRepository1;
    @Autowired
    SpotRepository spotRepository1;
    @Override
    public ParkingLot addParkingLot(String name, String address) {
        ParkingLot newParkingLot = new ParkingLot();
        newParkingLot.setName(name);
        newParkingLot.setAddress(address);
        newParkingLot.setSpotList(new ArrayList<>());

        return parkingLotRepository1.save(newParkingLot);
    }

    @Override
    public Spot addSpot(int parkingLotId, Integer numberOfWheels, Integer pricePerHour) {
        SpotType spotType = null;
        if(numberOfWheels <= 2){
            spotType = SpotType.TWO_WHEELER;
        } else if(numberOfWheels == 4 || numberOfWheels == 3){
            spotType = SpotType.FOUR_WHEELER;
        } else {
            spotType = SpotType.OTHERS;
        }
        Optional<ParkingLot> parkingLotOptional = parkingLotRepository1.findById(parkingLotId);
        if(!parkingLotOptional.isPresent()){
            return null;
        }
        Spot spot = new Spot();
        spot.setOccupied(false);
        spot.setPricePerHour(pricePerHour);
        spot.setSpotType(spotType);
        spot.setParkingLot(parkingLotOptional.get());
        spot.setReservationList(new ArrayList<>());

        Spot savedSpot = spotRepository1.save(spot);
        parkingLotOptional.get().getSpotList().add(savedSpot);
        parkingLotRepository1.save(parkingLotOptional.get());

        return savedSpot;
    }

    @Override
    public void deleteSpot(int spotId) {
        spotRepository1.deleteById(spotId);
    }

    @Override
    public Spot updateSpot(int parkingLotId, int spotId, int pricePerHour) {
        Optional<ParkingLot> parkingLotOptional = parkingLotRepository1.findById(parkingLotId);
        ParkingLot parkingLot = parkingLotOptional.get();
        Spot responseSpot = null;

        for(Spot spot : parkingLot.getSpotList()){
            if(spot.getId() == spotId){
                spot.setPricePerHour(pricePerHour);
                responseSpot = spotRepository1.save(spot);
                break;
            }
        }
        parkingLotRepository1.save(parkingLot);
        return responseSpot;
    }

    @Override
    public void deleteParkingLot(int parkingLotId) {
        parkingLotRepository1.deleteById(parkingLotId);
    }
}
