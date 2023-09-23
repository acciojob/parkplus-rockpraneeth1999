package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ParkingLotRepository;
import com.driver.repository.ReservationRepository;
import com.driver.repository.SpotRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReservationServiceImpl implements ReservationService {
    @Autowired
    UserRepository userRepository3;
    @Autowired
    SpotRepository spotRepository3;
    @Autowired
    ReservationRepository reservationRepository3;
    @Autowired
    ParkingLotRepository parkingLotRepository3;
    @Override
    public Reservation reserveSpot(Integer userId, Integer parkingLotId, Integer timeInHours, Integer numberOfWheels) throws Exception {

        //check user exists
        Optional<User> optionalUser = userRepository3.findById(userId);
        if(!optionalUser.isPresent()){
            throw new Exception("Cannot make reservation");
        }
        User user=optionalUser.get();

        //check parking lot exists
        Optional<ParkingLot> optionalParkingLot = parkingLotRepository3.findById(parkingLotId);
        if(!optionalParkingLot.isPresent()){
            throw new Exception("Cannot make reservation");
        }
        ParkingLot parkingLot = optionalParkingLot.get();

        //create reservation entity
        Reservation reservation = new Reservation();
        reservation.setNumberOfHours(timeInHours);

        //find spot with minimum cost
        List<Spot> spotList = parkingLot.getSpotList();
        int minCost=Integer.MAX_VALUE;
        Spot minCostSpot=null;

        for(Spot spot:spotList){
            //get number of wheels of each spot
            int numOfWheelsAvailable=0;
            if(spot.getSpotType()== SpotType.TWO_WHEELER){
                numOfWheelsAvailable=2;
            }
            else if(spot.getSpotType()==SpotType.FOUR_WHEELER){
                numOfWheelsAvailable=4;
            }
            else{
                numOfWheelsAvailable=Integer.MAX_VALUE;
            }

            if(numberOfWheels<=numOfWheelsAvailable && spot.getPricePerHour()<minCost){
                minCost=spot.getPricePerHour();
                minCostSpot=spot;
            }
        }

        if (minCostSpot==null){
            throw new Exception("Cannot make reservation");
        }

        //set spot
        reservation.setSpot(minCostSpot);
        //set user
        reservation.setUser(user);

        //save reservation
        reservation = reservationRepository3.save(reservation);

        //add reservation to spot
        minCostSpot.getReservationList().add(reservation);
        //add reservation to user
        user.getReservationList().add(reservation);

        //save user
        user=userRepository3.save(user);
        //save spot
        minCostSpot=spotRepository3.save(minCostSpot);

        return reservation;
    }
}
