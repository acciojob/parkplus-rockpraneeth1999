package com.driver.services.impl;

import com.driver.model.Payment;
import com.driver.model.PaymentMode;
import com.driver.model.Reservation;
import com.driver.model.Spot;
import com.driver.repository.PaymentRepository;
import com.driver.repository.ReservationRepository;
import com.driver.repository.SpotRepository;
import com.driver.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    ReservationRepository reservationRepository2;
    @Autowired
    PaymentRepository paymentRepository2;

    @Autowired
    SpotRepository spotRepository;

    @Override
    public Payment pay(Integer reservationId, int amountSent, String mode) throws Exception {
        //check if reservation exists
        Optional<Reservation> optionalReservation = reservationRepository2.findById(reservationId);
        if(!optionalReservation.isPresent()){
            throw new Exception("Invalid reservationId");
        }
        Reservation reservation=optionalReservation.get();

        //check mode of payment is valid
        PaymentMode paymentMode;
        if(mode.equals("cash")){
            paymentMode=PaymentMode.CASH;
        }
        else if(mode.equals("card")){
            paymentMode=PaymentMode.CARD;
        }
        else if(mode.equals("upi")){
            paymentMode=PaymentMode.UPI;
        }
        else{
            throw new Exception("Payment mode not detected");
        }

        //make payment entity
        Payment payment = new Payment();
        payment.setPaymentMode(paymentMode);

        //check amount is enough
        int pricePerHour=reservation.getSpot().getPricePerHour();
        int numberOfHours=reservation.getNumberOfHours();
        int amountRequired=pricePerHour*numberOfHours;
        if(amountSent<amountRequired){
            throw new Exception("Insufficient Amount");
        }

        //set reservation to payment
        payment.setReservation(reservation);

        //get spot
        Spot spot = reservation.getSpot();
        //set spot is occupied
        spot.setOccupied(true);

        //set payment in reservation
        reservation.setPayment(payment);

        //set payment status in payment
        payment.setPaymentCompleted(true);

        payment=paymentRepository2.save(payment);

        reservation=reservationRepository2.save(reservation);

        spot=spotRepository.save(spot);

        return payment;
    }
}
