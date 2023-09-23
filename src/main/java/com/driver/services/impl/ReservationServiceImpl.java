package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.*;
import com.driver.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
            return null;
        }
        List<Spot> spots = parkingLotOptional.get().getSpotList();
        List<Spot> spotsWithGivenType = new ArrayList<>();
        for(Spot spot : spots){
            if(numberOfWheels <= 2){
                spotsWithGivenType.add(spot);
            } else if((numberOfWheels == 4) || (numberOfWheels == 3)){
                if (spot.getSpotType() == SpotType.FOUR_WHEELER || spot.getSpotType() == SpotType.OTHERS){
                    spotsWithGivenType.add(spot);
                }
            } else {
                if(spot.getSpotType() == SpotType.OTHERS) {
                    spotsWithGivenType.add(spot);
                }
            }
        }
        int minPrice = Integer.MAX_VALUE;
        Spot perfectSpot = null;
        for(Spot spot : spotsWithGivenType){
            if(!spot.getOccupied() && spot.getPricePerHour() < minPrice){
                minPrice = spot.getPricePerHour();
                perfectSpot = spot;
            }
        }
        if (perfectSpot == null) {
            return null;
        }

        perfectSpot.setOccupied(true);
        Spot savedSpot = spotRepository3.save(perfectSpot);

        Reservation reservation = new Reservation();
        reservation.setSpot(savedSpot);
        reservation.setUser(userOptional.get());
        reservation.setNumberOfHours(timeInHours);

        Reservation savedReservation = reservationRepository3.save(reservation);
        Payment payment = new Payment();
        payment.setPaymentCompleted(false);
        payment.setReservation(savedReservation);
        paymentRepository.save(payment);
        savedReservation.setPayment(payment);
        userOptional.get().getReservationList().add(savedReservation);
        userRepository3.save(userOptional.get());
        savedSpot.getReservationList().add(savedReservation);

        return savedReservation;
    }
}
