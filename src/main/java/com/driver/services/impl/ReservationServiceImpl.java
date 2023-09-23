package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.*;
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
    @Autowired
    PaymentRepository paymentRepository;
    @Override
    public Reservation reserveSpot(Integer userId, Integer parkingLotId, Integer timeInHours, Integer numberOfWheels) throws Exception {
        //Reserve a spot in the given parkingLot such that the total price is minimum. Note that the price per hour for each spot is different
        //Note that the vehicle can only be parked in a spot having a type equal to or larger than given vehicle
        //If parkingLot is not found, user is not found, or no spot is available, throw "Cannot make reservation" exception.
        Optional<User> userOptional = userRepository3.findById(userId);
        Optional<ParkingLot> parkingLotOptional = parkingLotRepository3.findById(parkingLotId);
        if(!userOptional.isPresent() || !parkingLotOptional.isPresent()){
            throw new Exception("Cannot make reservation");
        }

        int minCost=Integer.MAX_VALUE;
        int noOfWheelsAvailable=0;
        Spot assignedSpot=null;
        List<Spot> spots = parkingLotOptional.get().getSpotList();
        for(Spot spot:spots){
            int costOfTheSpot=spot.getPricePerHour();
            if(spot.getSpotType()==SpotType.TWO_WHEELER){
                noOfWheelsAvailable=2;
            }
            else if(spot.getSpotType()==SpotType.FOUR_WHEELER){
                noOfWheelsAvailable=4;
            }
            else{
                noOfWheelsAvailable=Integer.MAX_VALUE;
            }

            if(spot.getPricePerHour()<minCost){
                assignedSpot=spot;
                minCost=spot.getPricePerHour();
            }
        }

        if(noOfWheelsAvailable<numberOfWheels){
            throw new Exception("Cannot make reservation");
        }



        assignedSpot.setOccupied(true);
        Spot savedSpot = spotRepository3.save(assignedSpot);

        Reservation reservation = new Reservation();
        reservation.setSpot(savedSpot);
        reservation.setUser(userOptional.get());
        reservation.setNumberOfHours(timeInHours);

        Reservation savedReservation = reservationRepository3.save(reservation);
        //Payment payment = new Payment();
        //payment.setPaymentCompleted(false);
        //payment.setReservation(savedReservation);
        //paymentRepository.save(payment);
        //savedReservation.setPayment(payment);
        userOptional.get().getReservationList().add(savedReservation);
        userRepository3.save(userOptional.get());
        savedSpot.getReservationList().add(savedReservation);

        return savedReservation;
    }
}
